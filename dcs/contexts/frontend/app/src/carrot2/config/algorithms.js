import { lingo } from "../service/algorithms/lingo.js";
import { stc } from "../service/algorithms/stc.js";
import { kmeans } from "../service/algorithms/kmeans.js";

const opensourceAlgorithms = {
  Lingo: lingo,
  STC: stc,
  "Bisecting K-Means": kmeans
};

export const algorithms = opensourceAlgorithms;
