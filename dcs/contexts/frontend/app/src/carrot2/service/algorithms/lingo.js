import descriptor from "./descriptors/org.carrot2.clustering.lingo.LingoClusteringAlgorithm.json";

import { persistentStore } from "@carrotsearch/ui/store/persistent-store.js";
import { storeAccessors } from "@carrotsearch/ui/settings/Setting.js";

import {
  advanced,
  collectDefaults,
  createExcludedLabelsSetting,
  createExcludedWordsSetting,
  getDescriptorsById,
  settingFromDescriptor,
  settingFromDescriptorRecursive,
  settingFromFilterDescriptor
} from "./attributes.js";
import { createLanguageSetting } from "./language.js";

const descriptorsById = getDescriptorsById(descriptor);

const settingFrom = (id, overrides) =>
  settingFromDescriptor(descriptorsById, id, overrides);
const settingFromRecursive = (id, getterProvider, overrides) =>
  settingFromDescriptorRecursive(
    descriptorsById,
    id,
    getterProvider,
    overrides
  );
const settingFromFilter = (id, getterProvider) =>
  settingFromFilterDescriptor(descriptorsById, id, getterProvider);

const getterProvider = () => getter;
const clusterSettings = [
  settingFrom("desiredClusterCount"),
  settingFrom("preprocessing.documentAssigner.minClusterSize"),
  ...settingFromRecursive(
    "clusterBuilder.labelAssigner",
    getterProvider,
    () => ({ ui: "radio" })
  ),
  settingFrom("preprocessing.documentAssigner.exactPhraseAssignment"),
  advanced(settingFrom("clusterBuilder.clusterMergingThreshold")),
  advanced(settingFrom("scoreWeight"))
];
const labelSettings = [
  settingFrom("clusterBuilder.phraseLabelBoost"),
  advanced(settingFrom("clusterBuilder.phraseLengthPenaltyStart")),
  advanced(settingFrom("clusterBuilder.phraseLengthPenaltyStop")),
  ...settingFromFilter(
    "preprocessing.labelFilters.completeLabelFilter",
    getterProvider
  ),
  ...settingFromFilter(
    "preprocessing.labelFilters.genitiveLabelFilter",
    getterProvider
  ),
  ...settingFromFilter(
    "preprocessing.labelFilters.minLengthLabelFilter",
    getterProvider
  ),
  ...settingFromFilter(
    "preprocessing.labelFilters.numericLabelFilter",
    getterProvider
  ),
  ...settingFromFilter(
    "preprocessing.labelFilters.queryLabelFilter",
    getterProvider
  ),
  ...settingFromFilter(
    "preprocessing.labelFilters.stopLabelFilter",
    getterProvider
  ),
  ...settingFromFilter(
    "preprocessing.labelFilters.stopWordLabelFilter",
    getterProvider
  )
];
const languageModelSettings = [
  ...settingFromRecursive("matrixBuilder.termWeighting", getterProvider),
  settingFrom("matrixBuilder.boostFields"),
  settingFrom("matrixBuilder.boostedFieldWeight"),
  advanced(settingFrom("preprocessing.phraseDfThreshold")),
  advanced(settingFrom("preprocessing.wordDfThreshold")),
  advanced(settingFrom("matrixBuilder.maxWordDf")),
  ...settingFromRecursive("matrixReducer.factorizationFactory", getterProvider),
  settingFrom("matrixBuilder.maximumMatrixSize")
];

const dictionariesSettings = [
  createExcludedLabelsSetting("lingo"),
  createExcludedWordsSetting("lingo")
];

const parameterStore = persistentStore(
  "parameters:algorithm:lingo",
  Object.assign(
    { language: "English" },
    collectDefaults(descriptorsById, [
      clusterSettings,
      labelSettings,
      languageModelSettings,
      dictionariesSettings
    ])
  )
);

// Add language setting separately, so that it doesn't participate in the process of
// collecting default values (language is not a parameter).
languageModelSettings.unshift(
  createLanguageSetting(
    "lingo",
    "Lingo",
    storeAccessors(parameterStore, "language")
  )
);

const getter = setting => parameterStore[setting.id];
const settings = [
  {
    id: "lingo",
    type: "group",
    settings: [
      {
        id: "lingo:clusters",
        type: "group",
        label: "Clusters",
        settings: clusterSettings,
        description:
          "Parameters affecting the number, structure and content of clusters."
      },
      {
        id: "lingo:labels",
        type: "group",
        label: "Cluster labels",
        settings: labelSettings,
        description: "Customization of cluster labels."
      },
      {
        id: "lingo:dictionaries",
        type: "group",
        label: "Dictionaries",
        settings: dictionariesSettings,
        description: "Label and word exclusion dictionaries."
      },
      {
        id: "lingo:languageModel",
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

export const lingo = {
  label: "Lingo",
  description: "Well-described flat clusters.",
  descriptionHtml:
    "creates well-described flat clusters. Does not scale beyond a few thousand search results. Available as part of the open source <a href='http://project.carrot2.org' target='_blank'>Carrot<sup>2</sup> framework</a>.",
  tag: "open source",
  getSettings: () => settings,
  getLanguage: () => parameterStore.language,
  getDefaults: parameterStore.getDefaults,
  resetToDefaults: parameterStore.resetToDefaults,
  applyQueryHint: (params, query) => (params.queryHint = query)
};
