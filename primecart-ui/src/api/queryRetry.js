const NON_RETRYABLE_STATUSES = [400, 401, 403, 404, 409]

// retrying for selected status code
export function shouldRetryQuery(failureCount, error) {
  const status = error?.status

  if (NON_RETRYABLE_STATUSES.includes(status)) {
    return false
  }

  return failureCount < 3
}
