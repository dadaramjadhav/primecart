echo "Restarting PrimeCart platform..."
"${SCRIPT_DIR}/platform/restart-platform.sh"

echo "Waiting for PrimeCart platform..."
TIMEOUT="${TIMEOUT}" \
  "${SCRIPT_DIR}/platform/wait-platform.sh"

echo "Restarting PrimeCart applications..."
"${SCRIPT_DIR}/apps/restart-primecart-apps.sh"

echo "Waiting for PrimeCart applications..."
TIMEOUT="${TIMEOUT}" \
  "${SCRIPT_DIR}/apps/wait-primecart-apps.sh"

echo "PrimeCart restart completed."
"${SCRIPT_DIR}/status-primecart.sh"