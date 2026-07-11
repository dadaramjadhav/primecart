# Keycloak Setup with MySQL using Docker

## Prerequisites

* MySQL Server is running locally.
* Docker is installed and running.

## Step 1: Create the Keycloak Database

Create a new database in MySQL:

```sql
CREATE DATABASE keycloak;
```

> **Note:** You do **not** need to create any tables manually. Keycloak automatically creates all required tables during startup.

## Step 2: Start the Keycloak Container

Run the following Docker command:

```bash
docker run -d \
  --name keycloak \
  -p 8090:8080 \
  -e KC_DB=mysql \
  -e KC_DB_URL=jdbc:mysql://host.docker.internal:3306/keycloak \
  -e KC_DB_USERNAME=root \
  -e KC_DB_PASSWORD=Admin@12345 \
  -e KC_BOOTSTRAP_ADMIN_USERNAME=admin \
  -e KC_BOOTSTRAP_ADMIN_PASSWORD=admin \
  quay.io/keycloak/keycloak:latest start-dev
```

## Step 3: Verify the Installation

Once the container starts successfully, open your browser and navigate to:

```
http://localhost:8090
```

Log in using the bootstrap administrator credentials:

* **Username:** `admin`
* **Password:** `admin`

Keycloak will automatically initialize the database and create all necessary tables on the first startup.
