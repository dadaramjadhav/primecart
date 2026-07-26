#!/usr/bin/env bash

set -euo pipefail

GIT_SHA="${1:-}"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/../.." && pwd)"
KUSTOMIZATION="${PROJECT_ROOT}/k8s/overlays/local/applications/kustomization.yaml"

if [[ ! "${GIT_SHA}" =~ ^[0-9a-fA-F]{40}$ ]]; then
  echo "Usage: $0 <40-character-git-sha>"
  exit 1
fi

TEMP_FILE="$(mktemp "${KUSTOMIZATION}.tmp.XXXXXX")"
trap 'rm -f "${TEMP_FILE}"' EXIT

awk -v git_sha="${GIT_SHA}" '
  /newTag:/ {
    sub(/newTag:.*/, "newTag: " git_sha)
  }
  {
    print
  }
' "${KUSTOMIZATION}" >"${TEMP_FILE}"

mv "${TEMP_FILE}" "${KUSTOMIZATION}"
trap - EXIT

echo "Application image version updated to ${GIT_SHA}."
echo

kubectl kustomize "$(dirname "${KUSTOMIZATION}")" |
  awk '/image:/ { print }'
