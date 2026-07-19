function getSafeErrorDetails(error) {
  return {
    name: error?.name ?? "Error",
    message: error?.message ?? "Unknown error",
    status: error?.response?.status ?? error?.status ?? null,
    code: error?.response?.data?.code ?? error?.code ?? null,
  }
}

export function logSafeError(operation, error) {
  console.error(operation, getSafeErrorDetails(error))
}
