import { isCarrot2Distribution } from "./config.js";

import { lingo } from "./service/algorithms/lingo.js";
import { stc } from "./service/algorithms/stc.js";
import { kmeans } from "./service/algorithms/kmeans.js";

const lingo3gAlgorithm = {
  "Lingo3G": {
    label: "Lingo3G",
    description: "Meaningful, well-described hierarchical clusters. Very fast, many tuning options.",
    descriptionHtml: "produces meaningful, well-described hierarchical clusters. Very fast, scalable, many tuning options (not exposed in this demo). Commercially available from <a href='https://carrotsearch.com' target='_blank'>Carrot Search</a>.",
    tag: "commercial",
    getSettings: () => {
      return [];
    }
  }
};

const opensourceAlgorithms = {
  "Lingo": lingo,
  "STC": stc,
  "Bisecting K-Means": kmeans
};

export const algorithms = isCarrot2Distribution() ? opensourceAlgorithms : lingo3gAlgorithm;
