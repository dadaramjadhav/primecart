import Keycloak from "keycloak-js"

const keycloak = new Keycloak({
  url: "http://localhost:8080",
  realm: "primecart",
  clientId: "primecart-react",
})

export default keycloak
