const head = document.getElementsByTagName("head")[0];
const script = document.createElement("script");
script.type = "text/javascript";
script.src = "https://stats.carrot2.org/js/index.js";
script.dataset.domain = "search.carrot2.org";
head.appendChild(script);

export default app => {
  app.on("clusteringRequested", e => {
    const plausible = window["plausible"];
    if (plausible) {
      plausible("ClusteringRequested", {
        props: {
          source: e.source,
          docs: Math.ceil(e.docs / 50) * 50, // bucket with the resolution of 50
          app: e.app,
          view: e.view
        }
      });
    }
  });
};