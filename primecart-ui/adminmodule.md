Yes—an admin module is a good place to learn these concepts without disturbing the working user module.
The key is to give each library a distinct responsibility:
TanStack Query: backend/server state
Redux Toolkit: global client-side state
React Hook Form: form state and validation
shadcn/ui: reusable UI components
React Router: admin navigation and route protection
Proposed structure
src/
├── module/
│ ├── user/
│ └── admin/
│ ├── components/
│ ├── hooks/
│ ├── pages/
│ ├── routes/
│ ├── services/
│ ├── store/
│ │ └── slices/
│ └── validation/
│
├── shared/
│ ├── components/
│ ├── layouts/
│ ├── ui/
│ └── utils/
│
├── routes/
│ └── AppRoutes.jsx
│
└── store/
└── store.js
Phase 1: Admin routing and authorization
Create the initial admin module:
src/module/admin/
├── pages/
│ └── AdminDashboard.jsx
├── routes/
│ ├── AdminRoutes.jsx
│ └── AdminProtectedRoute.jsx
└── components/
Add:
/admin
Admin dashboard placeholder
Admin-specific protected route
Role checking from Keycloak
A separate admin layout
Lazy-load AdminRoutes
Important: hiding an admin route in React is not security. Your backend must also verify the admin role.
Phase 2: Admin layout with shadcn/ui
Install and configure shadcn/ui for the existing Vite application.
Build:
Admin sidebar
Header
Dashboard cards
Buttons
Tables
Dropdown menus
Dialogs
Badges
Loading skeletons
Place generic generated components under:
src/shared/ui/
Place admin-specific combinations under:
src/module/admin/components/
For example, a generic Button is shared, while AdminSidebar belongs to the admin module.
shadcn/ui places component source code inside your application, so you can inspect and modify it while learning. Its current Vite setup also recommends configuring an @ import alias. Official Vite guide
Phase 3: Redux Toolkit foundations
Install:
@reduxjs/toolkit
react-redux
Create:
src/store/store.js
src/module/admin/store/slices/adminUiSlice.js
Start with appropriate client state:
sidebarOpen
selectedProductIds
productTableView
activeFilters
bulkActionSelection
Learn:
configureStore
Redux <Provider>
createSlice
Actions
Reducers
useSelector
useDispatch
Redux DevTools
Redux Toolkit’s official quick start recommends configureStore, <Provider>, createSlice, and React-Redux hooks as the core setup. Redux Toolkit Quick Start
Do not initially move products, orders, or API responses into Redux. They are server state and should remain in TanStack Query.
Phase 4: Product management
Create an admin product list:
/admin/products
Features:
Product table
Search
Category filter
Stock filter
Sort controls
Pagination
Edit button
Delete confirmation dialog
Multi-product selection
Responsibilities:
TanStack Query fetches products.
Redux keeps table preferences and selections.
shadcn/ui supplies the table, inputs, buttons and dialogs.
This phase demonstrates the difference between server state and client state.
Phase 5: React Hook Form basics
Create:
/admin/products/new
Build a product form with:
Name
Description
Price
Category
Stock quantity
Image URL
Active status
Learn:
useForm
register
handleSubmit
formState.errors
Field validation
Default values
Form reset
Submission state
React Hook Form is designed primarily for local form state and reducing unnecessary rerenders. It should not be stored in Redux. React Hook Form
Phase 6: Form validation
After learning React Hook Form’s built-in validation, introduce a schema validator such as Zod.
Create:
src/module/admin/validation/productSchema.js
Learn:
Required fields
Numeric validation
Minimum and maximum values
URL validation
Cross-field validation
Reusable validation schemas
Displaying errors through shadcn form components
Learn native React Hook Form validation first, then add Zod. That makes the purpose of each layer easier to understand.
Phase 7: Edit-product form
Create:
/admin/products/:id/edit
Learn:
Fetching initial data with TanStack Query
Supplying asynchronous default values
Using reset() when data arrives
Sharing the create/edit form component
Updating through useMutation
Invalidating the product query
Showing success and failure notifications
One reusable component can support both modes:
ProductForm
├── create mode
└── edit mode
Phase 8: Order management
Create:
/admin/orders
/admin/orders/:id
Features:
Order table
Status filter
Date filter
Customer search
Order details
Change-order-status form
Confirmation dialog
This reinforces:
Controlled shadcn components with React Hook Form’s Controller
Mutations and query invalidation
Redux filters or persisted table preferences
Reusable status badges
Phase 9: Advanced Redux Toolkit concepts
After the basic admin screens work, add:
Memoized selectors
Multiple slices
Redux middleware
State persistence for UI preferences
Resetting state at logout
Normalized client-only data if genuinely needed
Avoid RTK Query initially because your application already uses TanStack Query. Using both for the same API data would add duplication and make the architecture harder to understand.
Phase 10: Quality and performance
Finish with:
Lazy-loaded admin module
Page-level lazy loading where useful
Error boundaries
Loading skeletons
Empty states
Accessible forms
Unit tests for reducers
Form validation tests
Component tests
Admin authorization tests
Build, lint and test CI
Recommended learning order
Admin folders and routes
Admin role protection
Admin layout
shadcn/ui setup
Redux store and one small UI slice
Product list
React Hook Form product creation
Validation
Product editing
Order management
Testing and lazy loading
The first implementation step should be the basic admin folder, AdminRoutes, AdminProtectedRoute, AdminLayout, and an empty dashboard. Do not install all three libraries at once—we can introduce each when its first real use case appears.
