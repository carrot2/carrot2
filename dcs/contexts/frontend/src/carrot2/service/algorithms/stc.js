import descriptor from "./descriptors/org.carrot2.clustering.stc.STCClusteringAlgorithm.json";
import { persistentStore } from "../../../carrotsearch/store/persistent-store.js";
import { collectDefaults, getDescriptorsById, settingFromDescriptor } from "./attributes.js";

const descriptorsById = getDescriptorsById(descriptor);

const settingFrom = (id, overrides) => settingFromDescriptor(descriptorsById, id, overrides);

const clusterSettings = [
  settingFrom("maxClusters"),
  settingFrom("maxBaseClusters"),
  settingFrom("minBaseClusterScore"),
  settingFrom("minBaseClusterSize"),
  settingFrom("documentCountBoost"),
  settingFrom("mergeStemEquivalentBaseClusters"),
  settingFrom("mergeThreshold"),
  settingFrom("scoreWeight")
];
const labelSettings = [
  settingFrom("singleTermBoost"),
  settingFrom("optimalPhraseLength"),
  settingFrom("optimalPhraseLengthDev"),
  settingFrom("maxWordsPerLabel"),
  settingFrom("maxPhrasesPerLabel"),
  settingFrom("maxPhraseOverlap"),
  settingFrom("mostGeneralPhraseCoverage"),
];
const languageModelSettings = [
  settingFrom("ignoreWordIfInHigherDocsPercent"),
  settingFrom("preprocessing.wordDfThreshold"),
];

const parameterStore = persistentStore(
    "parameters:algorithm:stc",
    collectDefaults(descriptorsById, [
      clusterSettings,
      labelSettings,
      languageModelSettings
    ])
);
const getter = setting => parameterStore[setting.id];
const settings = [
  {
    id: "stc",
    type: "group",
    settings: [
      {
        id: "stc:clusters",
        type: "group",
        label: "Clusters",
        settings: clusterSettings,
        description: "Parameters affecting the number, structure and content of clusters."
      },

      {
        id: "stc:labels",
        type: "group",
        label: "Cluster labels",
        settings: labelSettings,
        description: "Customization of cluster labels."
      },

      {
        id: "stc:languageModel",
        type: "group",
        label: "Language model",
        settings: languageModelSettings,
        description: "Parameters of the document representation used by the clustering algorithm."
      }
    ],
    get: getter,
    set: (setting, val) => parameterStore[setting.id] = val
  }
];

export const stc = {
  label: "STC",
  description: "Flat clusters, fast algorithm.",
  descriptionHtml: "the classic search results clustering algorithm. Produces flat cluster with adequate description, very fast. Available as part of the open source <a href='http://project.carrot2.org' target='_blank'>Carrot<sup>2</sup> framework</a>",
  tag: "open source",
  getSettings: () => settings
};