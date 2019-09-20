export const dcsServiceUrl = () => {
  const env = process.env.REACT_APP_DCS_SERVICE_URL || "auto";
  if (env === "auto") {
    const url = new URL(window.location);
    return `${url.protocol}//${url.hostname}:8080/service/cluster?template=frontend-default` ;
  } else {
    return env;
  }
};

export const isCarrot2Distribution = () => process.env.REACT_APP_DISTRIBUTION !== "lingo3g";

