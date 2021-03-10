import descriptor from "./descriptors/org.carrot2.clustering.stc.STCClusteringAlgorithm.json";

import { persistentStore } from "@carrotsearch/ui/store/persistent-store.js";
import { storeAccessors } from "@carrotsearch/ui/settings/Setting.js";

import {
  advanced,
  collectDefaults,
  getDescriptorsById,
  settingFromDescriptor
} from "./attributes.js";
import { createLanguageSetting } from "./language.js";

const descriptorsById = getDescriptorsById(descriptor);

const settingFrom = (id, overrides) =>
  settingFromDescriptor(descriptorsById, id, overrides);

const clusterSettings = [
  settingFrom("maxClusters"),
  settingFrom("maxBaseClusters"),
  settingFrom("minBaseClusterScore"),
  settingFrom("minBaseClusterSize"),
  settingFrom("documentCountBoost"),
  advanced(settingFrom("mergeStemEquivalentBaseClusters")),
  advanced(settingFrom("mergeThreshold")),
  advanced(settingFrom("scoreWeight"))
];
const labelSettings = [
  settingFrom("singleTermBoost"),
  settingFrom("optimalPhraseLength"),
  settingFrom("optimalPhraseLengthDev"),
  settingFrom("maxWordsPerLabel"),
  settingFrom("maxPhrasesPerLabel"),
  advanced(settingFrom("maxPhraseOverlap")),
  advanced(settingFrom("mostGeneralPhraseCoverage"))
];
const languageModelSettings = [
  advanced(
    settingFrom("ignoreWordIfInHigherDocsPercent", {
      label: "Max relative word DF"
    })
  ),
  advanced(settingFrom("preprocessing.wordDfThreshold"))
];

const parameterStore = persistentStore(
  "parameters:algorithm:stc",
  Object.assign(
    { language: "English" },
    collectDefaults(descriptorsById, [
      clusterSettings,
      labelSettings,
      languageModelSettings
    ])
  )
);

// Add language setting separately, so that it doesn't participate in the process of
// collecting default values (language is not a parameter).
languageModelSettings.unshift(
  createLanguageSetting(
    "stc",
    "STC",
    storeAccessors(parameterStore, "language")
  )
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
        description:
          "Parameters affecting the number, structure and content of clusters."
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
        description:
          "Parameters of the document representation used by the clustering algorithm."
      }
    ],
    get: getter,
    set: (setting, val) => (parameterStore[setting.id] = val)
  }
];

export const stc = {
  label: "STC",
  description: "Flat clusters, fast algorithm.",
  descriptionHtml:
    "the classic search results clustering algorithm. Produces flat cluster with adequate description, very fast. Available as part of the open source <a href='http://project.carrot2.org' target='_blank'>Carrot<sup>2</sup> framework</a>",
  tag: "open source",
  getSettings: () => settings,
  getLanguage: () => parameterStore.language,
  getDefaults: parameterStore.getDefaults,
  resetToDefaults: parameterStore.resetToDefaults,
  applyQueryHint: (params, query) => (params.queryHint = query)
};
