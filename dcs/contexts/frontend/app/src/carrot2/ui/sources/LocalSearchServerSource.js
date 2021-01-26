import { autoEffect } from "@risingstack/react-easy-state";

import { persistentStore } from "@carrotsearch/ui/store/persistent-store.js";
import { createStateStore } from "@carrotsearch/ui/settings/ServiceUrlSetting.js";
import { storeAccessors } from "@carrotsearch/ui/settings/Setting.js";

import { workbenchSourceStore } from "../../apps/workbench/store/source-store.js";
import {
  createFieldChoiceSetting,
  createSchemaExtractorStores,
  createSource
} from "./CustomSchemaSource.js";
import { createResultConfigStore } from "./CustomSchemaResult.js";

export const createLocalSearch = ({
  id,
  serviceName,
  configOverrides,
  querySetting,
  fetchCollections,
  fetchResultsForSchemaInference
}) => {
  const { schemaInfoStore, resultHolder } = createSchemaExtractorStores(id);

  const resultConfigStore = createResultConfigStore(id);

  const serviceConfigStore = persistentStore(
    `workbench:source:${id}:serviceConfig`,
    Object.assign(
      {
        collection: undefined,
        maxResults: 100
      },
      configOverrides
    )
  );

  const serviceStateStore = createStateStore({
    isUrlValid: () => serviceStateStore.status === "ok",
    checkServiceUrl: async url => {
      serviceStateStore.status = "loading";
      serviceStateStore.message = "";
      try {
        const collectionIds = await fetchCollections(url);

        serviceConfigStore.serviceUrl = url;
        serviceStateStore.status = "ok";

        serviceStateStore.collections = collectionIds;
        if (collectionIds.indexOf(serviceConfigStore.collection) < 0) {
          serviceConfigStore.collection = collectionIds[0];
        }
      } catch (e) {
        serviceStateStore.status = "error";
        serviceStateStore.message = e instanceof Error ? e.toString() : e;
      }
    },
    collections: []
  });

  const isSearchPossible = () =>
    serviceStateStore.isUrlValid() && !!serviceConfigStore.collection;

  // Check service URL when the page loads.
  autoEffect(() => {
    if (workbenchSourceStore.source === id) {
      serviceStateStore.checkServiceUrl(serviceConfigStore.serviceUrl);
    }
  });
  autoEffect(() => {
    if (workbenchSourceStore.source === id && isSearchPossible()) {
      schemaInfoStore.load(async () => {
        const result = await fetchResultsForSchemaInference();
        resultHolder.documents = result.documents;
        return result;
      });
    }
  });

  const settings = [
    {
      id: `${id}:serviceUrl`,
      type: "service-url",
      label: `${serviceName} service URL`,
      urlStore: serviceStateStore,
      get: () => serviceConfigStore.serviceUrl,
      stateStore: serviceStateStore,
      checkUrl: serviceStateStore.checkServiceUrl
    },

    {
      id: `${id}:collection`,
      type: "enum",
      ui: "select",
      label: `${serviceName} collection to search`,
      noOptionsMessage: "Collection list is empty, no content to search.",
      options: () => serviceStateStore.collections.map(c => ({ value: c })),
      visible: () => serviceStateStore.isUrlValid(),
      get: () => serviceConfigStore.collection,
      set: (sett, collection) => (serviceConfigStore.collection = collection)
    },

    createFieldChoiceSetting(id, schemaInfoStore, {
      visible: () => isSearchPossible()
    }),

    querySetting(id),

    {
      id: `${id}:maxResults`,
      type: "number",
      label: "Max results",
      min: 0,
      max: 1000,
      step: 10,
      description: `<p>The number of search results to fetch.</p>`,
      visible: () => isSearchPossible(),
      ...storeAccessors(serviceConfigStore, "maxResults")
    }
  ];

  const afterSuccessfulSearch = () => {
    resultConfigStore.load(schemaInfoStore.fieldStats, resultHolder);
  };

  const createLocalSearchSource = base => {
    return createSource(schemaInfoStore, resultConfigStore, base);
  };

  return {
    serviceConfigStore,
    serviceStateStore,
    isSearchPossible,
    settings,
    afterSuccessfulSearch,
    createLocalSearchSource
  };
};
