import Keycloak from "keycloak-js"

const keycloak = new Keycloak({
  url: "http://localhost:8090",
  realm: "primecart",
  clientId: "primecart-react",
})

export default keycloak
