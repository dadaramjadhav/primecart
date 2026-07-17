import { useCallback, useEffect, useMemo, useState } from "react"
import ThemeContext from "./ThemeContext"

function getInitialTheme() {
  const savedTheme = localStorage.getItem("theme")

  if (savedTheme === "light" || savedTheme === "dark") {
    return savedTheme
  }

  // Checks whether the user's device or operating system currently prefers dark mode.
  const prefersDarkMode = window.matchMedia("(prefers-color-scheme: dark)").matches

  return prefersDarkMode ? "dark" : "light"
}

function ThemeProvider({ children }) {
  const [theme, setTheme] = useState(getInitialTheme)

  useEffect(() => {
    // Tailwind activates dark: styles when the root <html> element has the "dark" class.
    if (theme === "dark") {
      // Changes <html> to <html class="dark">.
      document.documentElement.classList.add("dark")
    } else {
      // Removes the "dark" class from <html> to activate light-mode styles.
      document.documentElement.classList.remove("dark")
    }

    localStorage.setItem("theme", theme)
  }, [theme])

  const toggleTheme = useCallback(() => {
    setTheme((currentTheme) => (currentTheme === "light" ? "dark" : "light"))
  }, [])

  const value = useMemo(
    () => ({
      theme,
      toggleTheme,
    }),
    [theme, toggleTheme],
  )

  return <ThemeContext.Provider value={value}>{children}</ThemeContext.Provider>
}

export default ThemeProvider
