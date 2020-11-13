import descriptor from "./descriptors/org.carrot2.clustering.lingo.LingoClusteringAlgorithm.json";
import { persistentStore } from "../../../carrotsearch/store/persistent-store.js";
import {
  collectDefaults,
  getDescriptorsById,
  settingFromDescriptor,
  settingFromDescriptorRecursive, settingFromFilterDescriptor
} from "./attributes.js";

const descriptorsById = getDescriptorsById(descriptor);

const settingFrom = (id, overrides) => settingFromDescriptor(descriptorsById, id, overrides);
const settingFromRecursive = (id, getterProvider, overrides) =>
    settingFromDescriptorRecursive(descriptorsById, id, getterProvider, overrides);
const settingFromFilter = (id, getterProvider) =>
    settingFromFilterDescriptor(descriptorsById, id, getterProvider);

const getterProvider = () => getter;
const clusterSettings = [
  settingFrom("desiredClusterCount"),
  settingFrom("preprocessing.documentAssigner.minClusterSize"),
  ...settingFromRecursive("clusterBuilder.labelAssigner", getterProvider, () => ({ ui: "radio" })),
  settingFrom("preprocessing.documentAssigner.exactPhraseAssignment"),
  settingFrom("clusterBuilder.clusterMergingThreshold"),
  settingFrom("scoreWeight", { label: "Size-score sorting ratio" }),
];
const labelSettings = [
  settingFrom("clusterBuilder.phraseLabelBoost"),
  settingFrom("clusterBuilder.phraseLengthPenaltyStart"),
  settingFrom("clusterBuilder.phraseLengthPenaltyStop"),
  ...settingFromFilter("preprocessing.labelFilters.completeLabelFilter", getterProvider),
  ...settingFromFilter("preprocessing.labelFilters.genitiveLabelFilter", getterProvider),
  ...settingFromFilter("preprocessing.labelFilters.minLengthLabelFilter", getterProvider),
  ...settingFromFilter("preprocessing.labelFilters.numericLabelFilter", getterProvider),
  ...settingFromFilter("preprocessing.labelFilters.queryLabelFilter", getterProvider),
  ...settingFromFilter("preprocessing.labelFilters.stopLabelFilter", getterProvider),
  ...settingFromFilter("preprocessing.labelFilters.stopWordLabelFilter", getterProvider),
];
const languageModelSettings = [
  ...settingFromRecursive("matrixBuilder.termWeighting", getterProvider),
  settingFrom("matrixBuilder.boostFields"),
  settingFrom("matrixBuilder.boostedFieldWeight"),
  settingFrom("preprocessing.phraseDfThreshold"),
  settingFrom("preprocessing.wordDfThreshold"),
  settingFrom("matrixBuilder.maxWordDf"),
  ...settingFromRecursive("matrixReducer.factorizationFactory", getterProvider),
  settingFrom("matrixBuilder.maximumMatrixSize")
];

const parameterStore = persistentStore(
    "parameters:algorithm:lingo",
    collectDefaults(descriptorsById, [
      clusterSettings,
      labelSettings,
      languageModelSettings
    ])
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
        description: "Parameters affecting the number, structure and content of clusters."
      },
      {
        id: "lingo:labels",
        type: "group",
        label: "Cluster labels",
        settings: labelSettings,
        description: "Customization of cluster labels."
      },
      {
        id: "lingo:languageModel",
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

export const lingo = {
  label: "Lingo",
  description: "Well-described flat clusters.",
  descriptionHtml: "creates well-described flat clusters. Does not scale beyond a few thousand search results. Available as part of the open source <a href='http://project.carrot2.org' target='_blank'>Carrot<sup>2</sup> framework</a>.",
  tag: "open source",
  getSettings: () => settings
};

