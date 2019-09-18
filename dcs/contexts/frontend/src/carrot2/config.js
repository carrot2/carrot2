export const dcsConfig = {
  dcsServiceUrl: process.env.REACT_APP_DCS_SERVICE_URL || "http://localhost:8080/service/cluster?template=frontend-default"
};

export const isCarrot2Distribution = () => false;//process.env.REACT_APP_DISTRIBUTION !== "lingo3g";

