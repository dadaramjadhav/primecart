import { StrictMode } from "react"
import { createRoot } from "react-dom/client"
import { MutationCache, QueryCache, QueryClient, QueryClientProvider } from "@tanstack/react-query"
import { ReactQueryDevtools } from "@tanstack/react-query-devtools"
import { ToastContainer } from "react-toastify"
import { shouldRetryQuery } from "./api/queryRetry"
import ErrorBoundary from "./module/user/components/ErrorBoundary"

import App from "./App"
import "./index.css"
import keycloak from "./auth/keycloak"
import AuthProvider from "./context/AuthProvider"
import ThemeProvider from "./context/ThemeProvider"
const queryClient = new QueryClient({
  queryCache: new QueryCache({
    onError: (error, query) => {
      console.error("Query failed", {
        queryKey: query.queryKey,
        error,
      })
    },
  }),

  mutationCache: new MutationCache({
    onError: (error, variables, context, mutation) => {
      console.error("Mutation failed", {
        mutationKey: mutation.options.mutationKey,
        variables,
        error,
      })
    },
  }),

  defaultOptions: {
    queries: {
      retry: shouldRetryQuery,
      staleTime: 10_000,
      refetchOnWindowFocus: false,
    },
    mutations: {
      retry: false,
    },
  },
})
keycloak
  .init({
    onLoad: "check-sso",
    checkLoginIframe: false,
    pkceMethod: "S256",
  })
  .then(() => {
    createRoot(document.getElementById("root")).render(
      <QueryClientProvider client={queryClient}>
        <StrictMode>
          <AuthProvider>
            <ThemeProvider>
              <ErrorBoundary>
                <App />
              </ErrorBoundary>
              <ToastContainer position="bottom-center" autoClose={2500} limit={3} />
            </ThemeProvider>
          </AuthProvider>
        </StrictMode>
        <ReactQueryDevtools initialIsOpen={false} />
      </QueryClientProvider>,
    )
  })
  .catch(console.error)
