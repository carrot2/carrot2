export const dcsServiceUrl = () => {
  const base = process.env.NODE_ENV === "production" ? window.location : "http://localhost:8080";
  return new URL(process.env.REACT_APP_DCS_SERVICE_URL, base);
};

export const isCarrot2Distribution = () => process.env.REACT_APP_DISTRIBUTION !== "lingo3g";

