import descriptor from "./descriptors/org.carrot2.clustering.lingo.LingoClusteringAlgorithm.json";
import { persistentStore } from "../../util/persistent-store.js";
import { getDescriptorsById, settingFrom as settingFromDescriptor } from "./attributes.js";

const descriptorsById = getDescriptorsById(descriptor);
console.log(descriptorsById);

const settingFrom = (id, overrides) => settingFromDescriptor(descriptorsById, id, overrides);

const clusterSettings = [
  settingFrom("desiredClusterCount"),
  settingFrom("clusterBuilder.clusterMergingThreshold"),
  settingFrom("scoreWeight", { label: "Size-score sorting ratio" }),
  settingFrom("preprocessing.documentAssigner.exactPhraseAssignment")
];
const labelSettings = [
  settingFrom("clusterBuilder.phraseLabelBoost"),
  settingFrom("clusterBuilder.phraseLengthPenaltyStart"),
  settingFrom("clusterBuilder.phraseLengthPenaltyStop")
];

const defaults = [ clusterSettings, labelSettings ].flat().reduce((defs, setting) => {
  defs[setting.id] = descriptorsById.get(setting.id).value;
  return defs;
}, {});

const parameterStore = persistentStore("parameters:algorithm:lingo", defaults);
export const lingo = {
  label: "Lingo",
  description: "Well-described flat clusters.",
  descriptionHtml: "creates well-described flat clusters. Does not scale beyond a few thousand search results. Available as part of the open source <a href='http://project.carrot2.org' target='_blank'>Carrot<sup>2</sup> framework</a>.",
  tag: "open source",
  getSettings: () => {
    return [
      {
        id: "lingo",
        type: "group",
        settings: [
          {
            id: "lingo:clusters",
            type: "group",
            label: "Clusters",
            settings: clusterSettings
          },
          {
            id: "lingo:labels",
            type: "group",
            label: "Cluster labels",
            settings: labelSettings
          }
        ],
        get: setting => parameterStore[setting.id],
        set: (setting, val) => parameterStore[setting.id] = val
      }
    ];
  }
};

