# Product Image Upload with Amazon S3

## Overview

PrimeCart uploads product images directly from the administration UI to an
Amazon S3 bucket using a short-lived presigned URL. Product Service does not
receive or proxy the image bytes.

After the upload succeeds, the frontend creates or updates the product and
sends the image URL as part of the product JSON. Only that URL is stored in
the MySQL `products.image_url` column.

```text
Admin UI
   |
   | 1. Request a presigned PUT URL
   v
Product Service
   |
   | 2. Return objectKey and short-lived uploadUrl
   v
Admin UI ---------------- PUT image ----------------> Amazon S3
   |
   | 3. Create/update product with the resulting image URL
   v
Product Service -------------------------------> MySQL
                                                   image_url only
```

This avoids:

- Storing image binary data in MySQL.
- Sending large image bodies through API Gateway and Product Service.
- Exposing AWS credentials to the browser.
- Giving the browser general permission to write to the S3 bucket.

## AWS Resources

The development configuration uses:

```text
Bucket: primecart-products
Region: eu-north-1
Object prefix: products/temporary/
```

The bucket was created with:

- ACLs disabled.
- Versioning enabled.
- Default S3 encryption enabled.
- CORS configured for the PrimeCart frontend origin.
- An IAM principal restricted to the required bucket and object operations.

During development, objects under `products/temporary/` have temporary public
read access so the normal S3 URL can be rendered by the frontend. Public write
and delete access are not enabled.

CloudFront with Origin Access Control should replace direct public S3 access
before production. Once CloudFront is configured, remove the temporary public
read bucket policy and re-enable every S3 Block Public Access option.

## Backend Changes

### Maven dependencies

Product Service imports the AWS SDK 2.x BOM and includes the S3 SDK:

```xml
<properties>
    <aws-sdk.version>2.31.78</aws-sdk.version>
</properties>
```

```xml
<dependency>
    <groupId>software.amazon.awssdk</groupId>
    <artifactId>s3</artifactId>
</dependency>
```

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>software.amazon.awssdk</groupId>
            <artifactId>bom</artifactId>
            <version>${aws-sdk.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

### Storage properties

`ProductImageStorageProperties` maps the storage configuration:

```java
@ConfigurationProperties(prefix = "primecart.storage")
public record ProductImageStorageProperties(
        String endpoint,
        String region,
        String bucket,
        boolean pathStyleAccess,
        String accessKey,
        String secretKey,
        Duration presignedUrlDuration,
        DataSize maxFileSize
) {
}
```

The AWS configuration is equivalent to:

```yaml
primecart:
  storage:
    endpoint: ""
    region: ${PRODUCT_IMAGE_STORAGE_REGION:eu-north-1}
    bucket: ${PRODUCT_IMAGE_STORAGE_BUCKET:primecart-products}
    access-key: ${AWS_ACCESS_KEY_ID}
    secret-key: ${AWS_SECRET_ACCESS_KEY}
    path-style-access: false
    presigned-url-duration: 5m
    max-file-size: 5MB
```

An empty endpoint allows the SDK to use the normal AWS S3 endpoint. Path-style
access is disabled for AWS.

Credentials must come from environment variables or a secret manager. They
must never be committed to `application.yml`. In an AWS production runtime,
prefer an IAM role and the AWS default credentials provider chain instead of
long-lived access keys.

### AWS client configuration

`ProductImageStorageConfig` creates:

- `S3Client` for S3 operations.
- `S3Presigner` for short-lived signed operations.
- An AWS region and credentials provider.
- Optional endpoint override and path-style access for S3-compatible local
  storage.

For the current AWS deployment, no endpoint override is supplied and
`pathStyleAccess` is `false`.

### Upload request and response

The frontend requests upload authorization with:

```json
{
  "fileName": "product.png",
  "contentType": "image/png",
  "fileSize": 425600
}
```

`CreateProductImageUploadRequest` validates that all three values are present
and that the size is positive.

Product Service returns:

```json
{
  "objectKey": "products/temporary/7fa0d52c-a118-4701-8910-f7e0e8615521.png",
  "uploadUrl": "https://primecart-products.s3.eu-north-1.amazonaws.com/...",
  "expiresAt": "2026-07-24T12:30:00Z"
}
```

The `uploadUrl` includes an AWS signature and expires after five minutes. It is
used only to upload the object. It is not stored in the database because its
signature expires.

### Presigned upload generation

`ProductImageStorageServiceImpl`:

1. Allows only JPEG, PNG, and WebP.
2. Rejects a file larger than the configured maximum.
3. Maps the content type to a safe extension.
4. Generates a server-controlled UUID object key.
5. Creates a `PutObjectRequest` containing the bucket, key, content type,
   content length, and sanitized original filename.
6. Presigns the PUT request for the configured duration.
7. Returns the key, signed URL, and expiry time.

The generated key has this format:

```text
products/temporary/<UUID>.<validated-extension>
```

The original filename is not used as the object key. This prevents filename
collisions and unsafe path values.

### Upload endpoint security

The endpoint is:

```http
POST /api/product-images/upload-url
```

It is protected with the Keycloak `ADMIN` role:

```java
@PreAuthorize("hasRole('ADMIN')")
```

The Spring Security request rules also restrict the endpoint to `ADMIN`.

### Product persistence

The existing product request contains:

```java
String imageUrl
```

`ProductMapper` assigns it to the entity:

```java
product.setImageUrl(request.imageUrl());
```

The database stores it in:

```sql
image_url VARCHAR(500)
```

Only the URL string is stored:

```text
https://primecart-products.s3.eu-north-1.amazonaws.com/products/temporary/<UUID>.png
```

The image bytes remain in S3.

## Frontend Changes

### File input

`ProductForm.jsx` provides a file selector restricted to supported image
types:

```jsx
<Input
  id="productImage"
  type="file"
  accept="image/jpeg,image/png,image/webp"
  onChange={(event) => {
    setImageFile(event.target.files?.[0] ?? null)
    setUploadError("")
  }}
/>
```

The form tracks:

```javascript
const [imageFile, setImageFile] = useState(null)
const [isUploading, setIsUploading] = useState(false)
const [uploadError, setUploadError] = useState("")
```

### Requesting upload authorization

`adminProductService.js` sends file metadata to Product Service:

```javascript
export async function createProductImageUpload(file) {
  const response = await api.post("/api/product-images/upload-url", {
    fileName: file.name,
    contentType: file.type,
    fileSize: file.size,
  })

  return response.data
}
```

This request uses the normal API client, so it includes the Keycloak access
token.

### Uploading directly to S3

The image is uploaded directly to the returned presigned URL:

```javascript
export async function uploadProductImage(uploadUrl, file) {
  await axios.put(uploadUrl, file, {
    headers: {
      "Content-Type": file.type,
      "x-amz-meta-original-filename": file.name,
    },
  })
}
```

Plain Axios is intentionally used for this request. The frontend must not send
the PrimeCart Keycloak authorization header to Amazon S3.

The request headers must match the headers signed by Product Service.

### Creating the product

After S3 confirms the upload, the frontend builds the temporary public image
URL from the returned object key:

```javascript
const upload = await createProductImageUpload(imageFile)

await uploadProductImage(upload.uploadUrl, imageFile)

const imageUrl =
  `https://primecart-products.s3.eu-north-1.amazonaws.com/${upload.objectKey}`
```

It then sends the product JSON:

```javascript
await onSubmit({
  ...productData,
  imageUrl,
})
```

Example request:

```json
{
  "name": "prod4",
  "description": "Product description",
  "price": 1,
  "imageUrl": "https://primecart-products.s3.eu-north-1.amazonaws.com/products/temporary/7fa0d52c-a118-4701-8910-f7e0e8615521.png",
  "sku": "skfdasf",
  "stock": 1,
  "categoryId": 4,
  "brandId": 4,
  "active": true
}
```

For an edit with no new image, the existing URL is retained. When a new image
is selected, it is uploaded first and the replacement URL is sent with the
update request.

## End-to-End Sequence

```text
1. Administrator selects an image.
2. Frontend validates the form.
3. Frontend requests POST /api/product-images/upload-url.
4. Product Service validates metadata and generates a signed PUT URL.
5. Frontend uploads the file directly to S3.
6. S3 returns a successful response.
7. Frontend constructs the non-expiring object URL from objectKey.
8. Frontend sends POST /api/products or PUT /api/products/{id}.
9. Product Service stores the URL in products.image_url.
10. Product responses return imageUrl and the UI renders it in an <img>.
```

The product request must not be submitted if the S3 upload fails.

## Verification

Confirm that the object exists:

```text
AWS Console
→ S3
→ primecart-products
→ products
→ temporary
```

Confirm the database value:

```sql
SELECT id, name, sku, image_url
FROM products
ORDER BY id DESC;
```

Confirm that MySQL contains only a URL and not image bytes:

```text
https://primecart-products.s3.eu-north-1.amazonaws.com/products/temporary/<UUID>.png
```

Test these failure cases:

- Unsupported file type.
- Empty file.
- File larger than 5 MB.
- Missing or expired Keycloak token.
- User without the `ADMIN` role.
- Expired presigned upload URL.
- S3 unavailable.
- Product creation attempted after a failed upload.

## Current Limitations and Follow-Up Work

- Replace temporary public S3 reads with CloudFront and Origin Access Control.
- Replace the hard-coded S3 hostname in the frontend with configuration or a
  backend-provided delivery URL.
- Move successfully attached images from `products/temporary/` to a permanent
  product prefix, or record their attachment state.
- Delete abandoned temporary uploads with an S3 lifecycle rule.
- Delete or archive the previous object when a product image is replaced.
- Validate the uploaded object's existence before saving a product.
- Validate the actual image signature/content rather than trusting only the
  browser-provided content type.
- Consider decoding and re-encoding images and generating thumbnail/WebP
  variants asynchronously.
- Add a `product_images` table when multiple images, primary-image selection,
  alt text, and display order are required.
- Prefer storing `objectKey` rather than the complete delivery URL in a future
  schema. That allows migration from S3 URLs to CloudFront without rewriting
  product rows.
