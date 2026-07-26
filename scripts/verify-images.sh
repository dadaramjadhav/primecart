#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"
IMAGE_LIST="$(mktemp)"
FAILED=0

trap 'rm -f "${IMAGE_LIST}"' EXIT

{
  kubectl kustomize \
    "${PROJECT_ROOT}/k8s/overlays/local/applications"

  kubectl kustomize \
    "${PROJECT_ROOT}/k8s/overlays/local/platform"
} |
awk '/image:/ { print $2 }' |
sort --unique >"${IMAGE_LIST}"

while IFS= read -r image; do
  echo "Checking ${image}..."

  if docker buildx imagetools inspect "${image}" >/dev/null 2>&1; then
    printf '%-6s %s\n' "PASS" "${image}"
  else
    printf '%-6s %s\n' "FAIL" "${image}"
    FAILED=1
  fi
done <"${IMAGE_LIST}"

if ((FAILED)); then
  echo "One or more Docker images are unavailable."
  exit 1
fi

echo "All Docker images are available."