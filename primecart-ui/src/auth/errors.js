export class SessionExpiredError extends Error {
  constructor(options = {}) {
    super("Your authentication session has expired.", options)

    this.name = "SessionExpiredError"
    this.code = "SESSION_EXPIRED"
    this.status = 401
  }
}
