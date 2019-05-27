import { etools } from "./service/sources/etools.js";
import { pubmed } from "./service/sources/pubmed.js";

export const sources = {
  "web": {
    label: "Web",
    source: etools
  },
  "pubmed": {
    label: "PubMed",
    source: pubmed
  }
};