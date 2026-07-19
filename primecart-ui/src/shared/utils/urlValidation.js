export function isSafeHttpUrl(value) {
  if (typeof value !== "string" || !value.trim()) {
    return false
  }

  try {
    const url = new URL(value)

    return ["http:", "https:"].includes(url.protocol)
  } catch {
    return false
  }
}
