import { store, autoEffect } from "@risingstack/react-easy-state";

import { clusterStore } from "./services.js";

const itemSelectionStore = () => {
  const itemSelectionStore = store({
    selected: new Set(),
    toggleSelection: function (item, keepSelection) {
      const selected = itemSelectionStore.selected;

      if (!keepSelection) {
        if (selected.size === 1 && selected.has(item)) {
          // See the comment in the clear() method for why
          // we don't use selected.clear() here.
          selected.delete(item);
          return;
        }

        itemSelectionStore.clear();
      }

      if (itemSelectionStore.isSelected(item)) {
        selected.delete(item);
      } else {
        selected.add(item);
      }
    },
    replaceSelection: function (items) {
      const itemSet = new Set(items);
      const selected = itemSelectionStore.selected;

      for (const c of Array.from(selected.values())) {
        if (!itemSet.has(c)) {
          selected.delete(c);
        }
      }
      items.forEach(i => {
        selected.add(i);
      });
    },
    isSelected: function (item) {
      return itemSelectionStore.selected.has(item);
    },
    clear: function () {
      // Normally, selected.clear() would be cleaner here, but
      // react-easy-state is smart enough to track operations
      // on individual set members and redraw only the affected
      // components. Therefore, if we remove individual clusters,
      // only the corresponding elements would re-render.
      // If we cleared the whole set, all cluster components
      // would have to re-render.
      const selected = itemSelectionStore.selected;
      for (const c of selected.values()) {
        selected.delete(c);
      }
    }
  });
  return itemSelectionStore;
};

export const clusterSelectionStore = itemSelectionStore();
export const documentSelectionStore = itemSelectionStore();

export const documentVisibilityStore = store({
  allDocumentsVisible: true,
  visibleDocumentIds: new Set(),
  isVisible: function (document) {
    return documentVisibilityStore.visibleDocumentIds.has(document.__id);
  },
  replaceVisible: function (newVisibleDocumentIdsSet) {
    const visibleDocumentIds = documentVisibilityStore.visibleDocumentIds;

    // A sequence of visibleDocumentIds.clear() and adding documents
    // from the new cluster would do here. Instead, we avoid useless re-rendering
    // when we remove and add documents to the set as appropriate.
    // See the comment in the clear() method of clusterSelectionStore for justification.
    for (const oldDocId of visibleDocumentIds) {
      if (!newVisibleDocumentIdsSet.has(oldDocId)) {
        visibleDocumentIds.delete(oldDocId);
      }
    }

    for (const newDocId of newVisibleDocumentIdsSet) {
      visibleDocumentIds.add(newDocId);
    }

    //
    documentVisibilityStore.allDocumentsVisible = visibleDocumentIds.size === 0;
  }
});

autoEffect(function () {
  const newVisibleDocuments = new Set();
  addDocumentsFromClusters(clusterSelectionStore.selected, newVisibleDocuments);
  for (let doc of documentSelectionStore.selected) {
    newVisibleDocuments.add(doc.__id);
  }
  documentVisibilityStore.replaceVisible(newVisibleDocuments);

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
autoEffect(function () {
  if (clusterStore.clusters !== previousClusters) {
    clusterSelectionStore.clear();
    previousClusters = clusterStore.clusters;
  }
});
