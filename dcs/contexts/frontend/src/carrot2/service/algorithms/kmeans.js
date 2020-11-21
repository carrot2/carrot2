import descriptor from "./descriptors/org.carrot2.clustering.kmeans.BisectingKMeansClusteringAlgorithm.json";
import { persistentStore } from "../../../carrotsearch/store/persistent-store.js";
import {
  advanced,
  collectDefaults,
  getDescriptorsById,
  settingFromDescriptor,
  settingFromDescriptorRecursive
} from "./attributes.js";

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

const getterProvider = () => getter;
const clusterSettings = [
  settingFrom("clusterCount"),
  settingFrom("maxIterations"),
  settingFrom("partitionCount")
];
const labelSettings = [settingFrom("labelCount")];
const languageModelSettings = [
  ...settingFromRecursive("matrixBuilder.termWeighting", getterProvider),
  settingFrom("matrixBuilder.boostFields"),
  settingFrom("matrixBuilder.boostedFieldWeight"),
  advanced(settingFrom("matrixBuilder.maxWordDf")),
  advanced(settingFrom("preprocessing.wordDfThreshold")),
  settingFrom("useDimensionalityReduction"),
  ...settingFromRecursive("matrixReducer.factorizationFactory", getterProvider),
  settingFrom("matrixBuilder.maximumMatrixSize")
];

const parameterStore = persistentStore(
  "parameters:algorithm:kmeans",
  collectDefaults(descriptorsById, [
    clusterSettings,
    labelSettings,
    languageModelSettings
  ])
);
const getter = setting => parameterStore[setting.id];
const settings = [
  {
    id: "kmeans",
    type: "group",
    settings: [
      {
        id: "kmeans:clusters",
        type: "group",
        label: "Clusters",
        settings: clusterSettings,
        description:
          "Parameters affecting the number, structure and content of clusters."
      },

      {
        id: "kmeans:labels",
        type: "group",
        label: "Cluster labels",
        settings: labelSettings,
        description: "Customization of cluster labels."
      },

      {
        id: "kmeans:languageModel",
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

export const kmeans = {
  label: "k-means",
  description: "Base line algorithm, bag-of-words labels.",
  descriptionHtml:
    "base line clustering algorithm, produces bag-of-words style cluster descriptions. Available as part of the open source <a href='http://project.carrot2.org' target='_blank'>Carrot<sup>2</sup> framework</a>",
  tag: "open source",
  getSettings: () => settings,
  resetToDefaults: parameterStore.resetToDefaults
};
