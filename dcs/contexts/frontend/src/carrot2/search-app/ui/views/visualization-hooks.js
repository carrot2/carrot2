import { unobserve } from "@nx-js/observer-util";
import { useState, useEffect } from "react";
import { observeBatched } from "../../../util/batch-observe.js";

export const useDataObject = clusterStore => {
  const [ dataObject, setDataObject ] = useState({});

  // Get references to arrays before setting up the side effect.
  // If we referenced clusterStore inside the effect function,
  // the effect might run with an array that is different from
  // the one passed in the inputs parameters.
  const clusters = clusterStore.clusters;
  const documents = clusterStore.documents;

  useEffect(() => {
    let groupId = 0;
    setDataObject({
      groups: clusters.map(function clusters(c) {
        return {
          id: (groupId++).toString(),
          cluster: c,
          label: `${c.labels.join(", ")} (${c.size})`,
          weight: c.size,
          groups: c.documents.map(d => {
            let document = documents[d];
            return {
              id: (groupId++).toString(),
              document: document,
              label: document && document.title,
              rank: document && document.rank
            }
          }).concat((c.clusters || []).map(clusters))
        }
      })
    });
  }, [ clusters, documents ]);

  return [ dataObject, setDataObject ];
};

export const useSelection = (clusterSelectionStore, documentSelectionStore, dataObject) => {
  const [ selection, setSelection ] = useState([]);
  useEffect(() => {
    const updateSelection = () => {
      const groups = dataObject.groups;
      if (groups) {
        const toSelect = [];
        groups.forEach(function collect(group) {
          if ((group.cluster && clusterSelectionStore.selected.has(group.cluster)) ||
            (group.document && documentSelectionStore.selected.has(group.document))) {
            toSelect.push(group);
          }
          if (group.groups) {
            group.groups.forEach(collect);
          }
        });
        setSelection(toSelect);
      }
    };

    observeBatched(updateSelection);

    return () => { unobserve(updateSelection); };
  }, [ clusterSelectionStore, documentSelectionStore, dataObject ]);

  return [ selection, setSelection ];
};