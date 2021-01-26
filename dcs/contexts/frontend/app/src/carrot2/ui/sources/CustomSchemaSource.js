import React from "react";

import "./CustomSchemaSource.css";

import { autoEffect, store, view } from "@risingstack/react-easy-state";
import { Checkbox } from "@blueprintjs/core";

import { ButtonLink } from "@carrotsearch/ui/ButtonLink.js";
import { ArrayLogger, LogEntries } from "@carrotsearch/ui/LogEntries.js";
import { persistentLruStore } from "@carrotsearch/ui/store/persistent-store.js";
import { Loading } from "@carrotsearch/ui/Loading.js";
import { Setting } from "@carrotsearch/ui/settings/Setting.js";
import { addFactory } from "@carrotsearch/ui/settings/Group.js";

import { extractSchema } from "./schema-extractor.js";

import {
  CustomSchemaResult,
  CustomSchemaResultConfig
} from "./CustomSchemaResult.js";

export const createSchemaExtractorStores = sourceId => {
  // A non-reactive holder for the contents of the last loaded file. This is not reactive, so that
  // the results display component does not update right after a new file is selected, but when
  // documents are requested from the source for clustering.
  const resultHolder = {
    query: "",
    documents: []
  };

  // A reactive store backing the local file loading user interface.
  const schemaInfoStore = store({
    loading: false,
    log: [],
    fileLoaded: false,
    fieldStats: [],
    fieldsAvailable: [],
    fieldsAvailableForClustering: [],
    clusterNaturalTextOnly: true,
    getFieldsAvailableForClustering: naturalTextOnly => {
      const fields = schemaInfoStore.fieldStats;

      // Create a fresh non-reactive array, so that we can sort it.
      // Sorting a reactive array would cause infinite render loops.
      return Array.from(
        (naturalTextOnly
          ? fields.filter(f => f.naturalTextScore >= 8 || f.titleScore >= 8)
          : fields.filter(f => f.type === "String")
        ).map(f => f.field)
      ).sort();
    },
    fieldsToCluster: [],
    load: async loader => {
      schemaInfoStore.loading = true;
      const logger = new ArrayLogger();
      try {
        const result = await loader(logger);
        const parsed = extractSchema(result.documents, logger);

        // We remember the fields the user selected for clustering on a per-schema (set of all fields)
        // basis, so that the user doesn't have to re-select the right fields every time they upload
        // a similar data set.
        const cachedToCluster = fieldsToClusterConfigs.get(
          fieldsToClusterConfigsKey(parsed.fieldsAvailable)
        );
        let newToCluster;
        if (cachedToCluster && cachedToCluster.length > 0) {
          const cached = new Set(cachedToCluster);

          // Intersection of parsed and cached set of fields, in case this specific instance
          // had data that caused some field to be unsuitable for clustering.
          newToCluster = new Set(
            [...parsed.fieldsToCluster].filter(f => cached.has(f))
          );
        } else {
          newToCluster = new Set(parsed.fieldsToCluster);
        }

        schemaInfoStore.fieldsToCluster = newToCluster;
        schemaInfoStore.fieldStats = parsed.fieldStats;
        schemaInfoStore.fieldsAvailable = parsed.fieldsAvailable;
        schemaInfoStore.fileLoaded = true;

        resultHolder.documents = result.documents;
        resultHolder.query = result.query;
      } catch (e) {
        logger.error(e instanceof Error ? e.toString() : e);
      } finally {
        schemaInfoStore.log = logger.getEntries();
        schemaInfoStore.loading = false;
      }
    }
  });

  // Store the last selected set of fields to cluster for each schema
  const fieldsToClusterConfigsKey = item => item.join("--");
  const fieldsToClusterConfigs = persistentLruStore(
    `workbench:source:${sourceId}:lastConfigs`,
    () => {
      const fieldsAvailable = schemaInfoStore.fieldsAvailable;
      return fieldsAvailable.length > 0
        ? fieldsToClusterConfigsKey(fieldsAvailable)
        : null;
    },
    () => {
      return Array.from(schemaInfoStore.fieldsToCluster);
    }
  );

  return {
    schemaInfoStore,
    resultHolder
  };
};

const FieldList = view(({ schemaInfoStore }) => {
  const store = schemaInfoStore;
  const availableForClustering = store.getFieldsAvailableForClustering(
    store.clusterNaturalTextOnly
  );
  const toCluster = store.fieldsToCluster;
  const noContentMessage =
    availableForClustering.length === 0 ? (
      <small>No natural text content detected</small>
    ) : null;

  return (
    <div className="FieldList">
      {noContentMessage}
      {availableForClustering.map(f => {
        return (
          <Checkbox
            label={f}
            key={f}
            checked={toCluster.has(f)}
            onChange={e => {
              e.target.checked ? toCluster.add(f) : toCluster.delete(f);
            }}
          />
        );
      })}
    </div>
  );
});

const FieldChoiceFieldFilter = view(({ schemaInfoStore }) => {
  const store = schemaInfoStore;
  const clusterNaturalTextOnly = store.clusterNaturalTextOnly;

  return (
    <div className="FieldChoiceFieldFilter">
      {clusterNaturalTextOnly ? (
        <>
          Only natural text fields shown,{" "}
          <ButtonLink onClick={() => (store.clusterNaturalTextOnly = false)}>
            show all fields
          </ButtonLink>
          .
        </>
      ) : (
        <>
          All string-typed fields shown,{" "}
          <ButtonLink onClick={() => (store.clusterNaturalTextOnly = true)}>
            show text fields
          </ButtonLink>
          .
        </>
      )}
    </div>
  );
});

const FieldChoiceSetting = view(({ setting, get, set }) => {
  const { label, description, schemaInfoStore } = setting;

  return (
    <Setting
      className="FieldChoiceSetting"
      label={label}
      description={description}
    >
      <Loading isLoading={() => schemaInfoStore.loading} />
      <FieldList schemaInfoStore={schemaInfoStore} />
      <LogEntries entries={schemaInfoStore.log} />
      <FieldChoiceFieldFilter schemaInfoStore={schemaInfoStore} />
    </Setting>
  );
});
addFactory("field-choice", (s, get, set) => {
  return <FieldChoiceSetting setting={s} get={get} set={set} />;
});

export const createFieldChoiceSetting = (
  sourceId,
  schemaInfoStore,
  overrides
) => {
  return Object.assign(
    {
      id: `${sourceId}:fieldChoice`,
      type: "field-choice",
      label: "Fields to cluster",
      visible: () =>
        schemaInfoStore.loading ||
        schemaInfoStore.fileLoaded ||
        schemaInfoStore.log.length > 0,
      get: () => schemaInfoStore.fieldsToCluster,
      set: () => {},
      schemaInfoStore: schemaInfoStore
    },
    overrides
  );
};

export const createSource = (schemaInfoStore, resultConfigStore, base) => {
  // Create a local copy of fields to cluster. The cluster store calls the getFieldsToCluster() method
  // before clustering and if the method returned a value from the schemaInfoStore reactive store,
  // clustering would be triggered right after the selection of fields changed, which we want to avoid.
  let currentFieldsToCluster;
  autoEffect(() => {
    currentFieldsToCluster = Array.from(schemaInfoStore.fieldsToCluster);
  });

  return Object.assign(base, {
    createResult: props => {
      return <CustomSchemaResult {...props} configStore={resultConfigStore} />;
    },
    createConfig: () => (
      <CustomSchemaResultConfig configStore={resultConfigStore} />
    ),
    createSourceConfig: () => {
      throw new Error("Not available in search app.");
    },
    getFieldsToCluster: () => currentFieldsToCluster
  });
};
