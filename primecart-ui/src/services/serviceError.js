import axios from "axios"

export class ServiceError extends Error {
  constructor(message, status, details = null) {
    super(message)

    this.name = "ServiceError"
    this.status = status
    this.details = details
  }
}

export function normalizeServiceError(error, fallbackMessage = "Something went wrong.") {
  if (!axios.isAxiosError(error)) {
    return new ServiceError(error?.message ?? fallbackMessage, null)
  }

  const status = error.response?.status
  const backendData = error.response?.data

  let message = backendData?.message ?? fallbackMessage

  if (!error.response) {
    message = "Unable to connect to the server."
  } else if (status === 400) {
    message = backendData?.message ?? "Invalid request."
  } else if (status === 401) {
    message = "Your session has expired."
  } else if (status === 403) {
    message = "You are not allowed to perform this action."
  } else if (status === 404) {
    message = backendData?.message ?? "Resource not found."
  } else if (status === 409) {
    message = backendData?.message ?? "The request conflicts with the current data."
  } else if (status >= 500) {
    message = "The server is currently unable to process the request."
  }

  return new ServiceError(message, status, backendData)
}
