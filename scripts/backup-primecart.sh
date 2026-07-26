#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"
TIMESTAMP="$(date '+%Y%m%d-%H%M%S')"
BACKUP_DIR="${PROJECT_ROOT}/backups/primecart-${TIMESTAMP}"

mkdir -p "${BACKUP_DIR}/mysql" "${BACKUP_DIR}/keycloak"

echo "Backing up MySQL..."

kubectl exec mysql-0 \
  --namespace primecart-infra \
  --container mysql \
  -- bash -c \
  'MYSQL_PWD="$(cat /opt/bitnami/mysql/secrets/mysql-root-password)" mysqldump --user=root --all-databases --single-transaction --routines --events --triggers' |
  gzip >"${BACKUP_DIR}/mysql/all-databases.sql.gz"

echo "Backing up Keycloak realm..."

kubectl exec deployment/keycloak \
  --namespace primecart-infra \
  --container keycloak \
  -- sh -c \
  '/opt/keycloak/bin/kcadm.sh config credentials \
    --server http://localhost:8080 \
    --realm master \
    --user "$KC_BOOTSTRAP_ADMIN_USERNAME" \
    --password "$KC_BOOTSTRAP_ADMIN_PASSWORD" >/dev/null &&
   /opt/keycloak/bin/kcadm.sh get realms/primecart' \
  >"${BACKUP_DIR}/keycloak/primecart-realm.json"

test -s "${BACKUP_DIR}/mysql/all-databases.sql.gz"
test -s "${BACKUP_DIR}/keycloak/primecart-realm.json"

echo "Backup completed: ${BACKUP_DIR}"

du -h \
  "${BACKUP_DIR}/mysql/all-databases.sql.gz" \
  "${BACKUP_DIR}/keycloak/primecart-realm.json"
