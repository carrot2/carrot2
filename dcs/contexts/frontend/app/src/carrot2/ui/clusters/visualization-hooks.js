import { autoEffect, clearEffect } from "@risingstack/react-easy-state";
import { useState, useEffect, useRef } from "react";

const EMPTY_OBJECT = {};

/**
 * Manages the process of setting new dataObject instances
 * on the visualization, ensuring that:
 *
 * - new dataObject is set when clusters change
 * - new dataObject is set only when the visualization panel is visible,
 *   if the panel is not visible, the new dataObject is set when the visualization
 *   becomes visible. This is to avoid slow-down caused by, for example,
 *   treemap computation when only pie chart is visible.
 * - an empty dataObject is set on an invisible visualization when clusters
 *   change. This is to avoid a flash of old content when the user switches
 *   between visualizations.
 */
export const useDataObject = (clusters, documents, visible, includeResults) => {
  const [dataObject, setDataObject] = useState(EMPTY_OBJECT);
  const [dataObjectInternal, setDataObjectInternal] = useState(EMPTY_OBJECT);
  const prevDataObjectInternal = useRef(dataObjectInternal);

  // Get references to arrays before setting up the side effect.
  // If we referenced clusterStore inside the effect function,
  // the effect might run with an array that is different from
  // the one passed in the inputs parameters.

  // Builds an internal dataObject when clusters or documents change.
  useEffect(() => {
    let groupId = 0;
    setDataObjectInternal({
      groups: clusters.map(function clusters(c) {
        return {
          id: (groupId++).toString(),
          cluster: c,
          label: `${c.labels.join(", ")} (${c.size})`,
          weight: c.unclustered ? 0 : c.size,
          groups: (includeResults ? c.documents : [])
            .map(d => {
              let document = documents[d];
              return {
                id: (groupId++).toString(),
                document: document,
                label: document && document.title,
                rank: document && document.__rank
              };
            })
            .concat((c.clusters || []).map(clusters))
        };
      })
    });
  }, [clusters, documents, includeResults]);

  // Transfers the internal dataObject to the visualization, if the visualization
  // panel is visible. If the panel is not visible, an empty dataObject is set
  // to avoid a flash of old content when the visualization becomes visible.
  useEffect(() => {
    if (visible) {
      setDataObject(dataObjectInternal);
      prevDataObjectInternal.current = dataObjectInternal;
    } else {
      if (prevDataObjectInternal.current !== dataObjectInternal) {
        setDataObject(EMPTY_OBJECT);
        prevDataObjectInternal.current = dataObjectInternal;
      }
    }
  }, [dataObjectInternal, visible]);

  return [dataObject];
};

export const useSelection = (
  clusterSelectionStore,
  documentSelectionStore,
  dataObject
) => {
  const [selection, setSelection] = useState([]);
  useEffect(() => {
    const updateSelection = () => {
      const groups = dataObject.groups;
      if (groups) {
        const toSelect = [];
        groups.forEach(function collect(group) {
          if (
            (group.cluster &&
              clusterSelectionStore.selected.has(group.cluster)) ||
            (group.document &&
              documentSelectionStore.selected.has(group.document))
          ) {
            toSelect.push(group);
          }
          if (group.groups) {
            group.groups.forEach(collect);
          }
        });
        setSelection(toSelect);
      }
    };

    autoEffect(updateSelection);

    return () => {
      clearEffect(updateSelection);
    };
  }, [clusterSelectionStore, documentSelectionStore, dataObject]);

  return [selection, setSelection];
};
