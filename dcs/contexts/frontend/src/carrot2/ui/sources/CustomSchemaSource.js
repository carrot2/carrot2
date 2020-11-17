import React from "react";

import "./CustomSchemaSource.css";

import { view } from "@risingstack/react-easy-state";
import { Checkbox } from "@blueprintjs/core";

export const FieldList = view(({ schemaInfoStore }) => {
  const store = schemaInfoStore;
  const availableForClustering = store.fieldsAvailableForClustering;
  const toCluster = store.fieldsToCluster;
  const noContentMessage = availableForClustering.length === 0 ?
      <small>No natural text content detected</small> : null;

  return (
      <div className="FieldList">
        {noContentMessage}
        {
          availableForClustering.map(f => {
            return <Checkbox label={f} key={f} checked={toCluster.has(f)}
                             onChange={e => {
                               e.target.checked ? toCluster.add(f) : toCluster.delete(f);
                             }} />;
          })
        }
      </div>
  );
});