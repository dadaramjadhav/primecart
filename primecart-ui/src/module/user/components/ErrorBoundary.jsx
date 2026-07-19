import { Component } from "react"
import { logSafeError } from "@/shared/utils/safeLogger"

// error boundary for exception handling
class ErrorBoundary extends Component {
  constructor(props) {
    super(props)

    this.state = {
      hasError: false,
      error: null,
    }
  }

  static getDerivedStateFromError(error) {
    return {
      hasError: true,
      error,
    }
  }

  componentDidCatch(error) {
    logSafeError("Unexpected React rendering error", error)
  }

  handleReload = () => {
    window.location.reload()
  }

  handleGoHome = () => {
    window.location.href = "/"
  }

  render() {
    if (this.state.hasError) {
      return (
        <div className="flex min-h-screen items-center justify-center bg-slate-50 px-4 dark:bg-slate-950">
          <div className="w-full max-w-lg rounded-xl bg-white p-8 text-center shadow-lg dark:bg-slate-900">
            <h1 className="text-3xl font-bold text-red-600">Something went wrong</h1>

            <p className="mt-4 text-slate-600 dark:text-slate-400">
              An unexpected error occurred while displaying this page.
            </p>

            {import.meta.env.DEV && this.state.error && (
              <pre className="mt-4 overflow-auto rounded bg-slate-100 p-3 text-left text-sm text-red-600 dark:bg-slate-800">
                {this.state.error.message}
              </pre>
            )}

            <div className="mt-6 flex justify-center gap-3">
              <button
                type="button"
                onClick={this.handleReload}
                className="rounded-lg bg-blue-600 px-5 py-2 text-white hover:bg-blue-700">
                Reload
              </button>

              <button
                type="button"
                onClick={this.handleGoHome}
                className="rounded-lg border border-slate-300 px-5 py-2 dark:border-slate-700">
                Go Home
              </button>
            </div>
          </div>
        </div>
      )
    }

    return this.props.children
  }
}

export default ErrorBoundary
