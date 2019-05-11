import { store } from "react-easy-state";
import { observe } from '@nx-js/observer-util';

import { clusterStore } from "./services";

export const clusterSelectionStore = store({
  selected: new Set(),
  toggleSelection: function (cluster, keepSelection) {
    const selected = clusterSelectionStore.selected;

    if (!keepSelection) {
      if (selected.size === 1 && selected.has(cluster)) {
        // See the comment in the clear() method for why
        // we don't use selected.clear() here.
        selected.delete(cluster);
        return;
      }

      clusterSelectionStore.clear();
    }

    if (clusterSelectionStore.isSelected(cluster)) {
      selected.delete(cluster);
    } else {
      selected.add(cluster);
    }
  },
  isSelected: function (cluster) {
    return clusterSelectionStore.selected.has(cluster);
  },
  clear: function () {
    // Normally, selected.clear() would be cleaner here, but
    // react-easy-state is smart enough to track operations
    // on individual set members and redraw only the affected
    // components. Therefore, if we remove individual clusters,
    // only the corresponding elements would re-render.
    // If we cleared the whole set, all cluster components
    // would have to re-render.
    const selected = clusterSelectionStore.selected;
    for (const c of Array.from(selected.values())) {
      selected.delete(c);
    }
  }
});

export const documentVisibilityStore = store({
  visibleDocumentIds: new Set(),
  isVisible: function (document) {
    return documentVisibilityStore.visibleDocumentIds.size === 0 ?
      true : documentVisibilityStore.visibleDocumentIds.has(document.id);
  }
});

observe(function () {
  const visibleDocumentIds = documentVisibilityStore.visibleDocumentIds;

  // A sequence of visibleDocumentIds.clear() and adding documents
  // from the new cluster would do here. Instead, we avoid useless re-rendering
  // when we remove and add documents to the set as appropriate.
  // See the comment in the clear() method of clusterSelectionStore for justification.

  const newVisibleDocuments = new Set();
  addDocumentsFromClusters(clusterSelectionStore.selected, newVisibleDocuments);

  for (const oldDocId of visibleDocumentIds) {
    if (!newVisibleDocuments.has(oldDocId)) {
      visibleDocumentIds.delete(oldDocId);
    }
  }

  for (const newDocId of newVisibleDocuments) {
    visibleDocumentIds.add(newDocId);
  }

  function addDocumentsFromClusters(clusters, set) {
    if (clusters) {
      for (let cluster of clusters) {
        for (let docId of cluster.documents) {
          set.add(docId);
        }
        addDocumentsFromClusters(cluster.clusters, set);
      }
    }
  }
});

let previousClusters = undefined;
observe(function () {
  if (clusterStore.clusters !== previousClusters) {
    clusterSelectionStore.clear();
    previousClusters = clusterStore.clusters;
  }
});