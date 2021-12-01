export const dcsServiceUrl = () =>
  new URL(process.env.REACT_APP_DCS_SERVICE_URL, window.location);
