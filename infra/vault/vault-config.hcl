ui = true
disable_mlock = true

storage "raft" {
  path    = "/vault/data"
  node_id = "primecart-vault-1"
}

listener "tcp" {
  address     = "0.0.0.0:8200"
  tls_disable = true
}

api_addr     = "http://localhost:8200"
cluster_addr = "http://primecart-vault:8201"
