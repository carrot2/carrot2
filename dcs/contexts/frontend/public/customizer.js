export default app => {
  app.on("clusteringSuccessful", e => {
    console.log("clusteringSuccessful", e);
  });
};