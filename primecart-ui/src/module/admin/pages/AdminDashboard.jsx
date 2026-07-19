import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/shared/ui/card"

function AdminDashboard() {
  const dashboardItems = [
    {
      title: "Total Products",
      value: 0,
      description: "Products available in PrimeCart",
    },
    {
      title: "Total Orders",
      value: 0,
      description: "Orders placed by customers",
    },
    {
      title: "Pending Orders",
      value: 0,
      description: "Orders waiting to be processed",
    },
    {
      title: "Total Revenue",
      value: "₹0",
      description: "Revenue generated from orders",
    },
  ]

  return (
    <div>
      <div className="mb-8">
        <h1 className="text-3xl font-bold">Admin Dashboard</h1>

        <p className="mt-2 text-muted-foreground">
          Overview of the PrimeCart administration area.
        </p>
      </div>

      <div className="grid gap-6 sm:grid-cols-2 lg:grid-cols-4">
        {dashboardItems.map((item) => (
          <Card key={item.title}>
            <CardHeader>
              <CardTitle>{item.title}</CardTitle>
              <CardDescription>{item.description}</CardDescription>
            </CardHeader>

            <CardContent>
              <p className="text-3xl font-bold">{item.value}</p>
            </CardContent>
          </Card>
        ))}
      </div>
    </div>
  )
}


export default AdminDashboard
