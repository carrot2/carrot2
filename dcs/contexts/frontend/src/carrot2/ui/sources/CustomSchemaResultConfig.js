import React from 'react';

import "./CustomSchemaResultConfig.css";

import { FormGroup, HTMLSelect, Popover, PopoverPosition } from "@blueprintjs/core";
import { view } from "@risingstack/react-easy-state";

import { persistentStore } from "../../util/persistent-store.js";

export const createResultConfigStore = (key) => {
  const store = persistentStore(
      `workbench:source:${key}:resultConfig`,
      {
        fieldRoles: {}
      },
      {
        load: fieldStats => {
          if (fieldStats.length === 0) {
            return;
          }

          const map = fieldStats.reduce((map, field) => {
            console.log(field.field, field.propScore, field.tagScore);
            if (field.tagScore > 1 && field.tagScore > field.propScore) {
              map[field.field] = "tag";
            } else if (field.propScore > 1 && field.propScore > field.tagScore) {
              map[field.field] = "property";
            } else if (field.naturalTextScore > 2) {
              map[field.field] = "body";
            } else {
              map[field.field] = "not shown";
            }
            return map;
          }, {});

          const byIdScore = fieldStats.sort((a, b) => b.idScore - a.idScore);
          if (byIdScore[0].idScore >= 2) {
            map[byIdScore[0].field] = "id";
          }
          const byTitle = fieldStats.sort((a, b) => b.titleScore - a.titleScore);
          if (byTitle[0].titleScore >= 2) {
            map[byTitle[0].field] = "title";
          }

          store.fieldRoles = map;
        }
      });
  return store;
};

const FIELD_ROLES = [
  "not shown", "title", "subtitle", "body", "tag", "property", "id"
];
const FieldRole = view(({ field, configStore }) => {
  return (
      <FormGroup label={field} inline={true}>
        <HTMLSelect value={configStore.fieldRoles[field]}
                    onChange={e => configStore.fieldRoles[field] = e.currentTarget.value}>
          {
            FIELD_ROLES.map(role => <option key={role} value={role}>{role}</option>)
          }
        </HTMLSelect>
      </FormGroup>
  );
});

const FieldRoles = ({ configStore }) => {
  return (
      <div className="FieldRoles">
        {
          Object.keys(configStore.fieldRoles).map(f => {
            return <FieldRole key={f} field={f} configStore={configStore} />;
          })
        }
      </div>
  );
};

export const ResultPreview = () => {
  return (
      <div className="ResultPreview">
        <p>Preview:</p>
      </div>
  )
};

export const CustomSchemaResultConfig = view(({ configStore }) => {
  return (
      <div className="CustomSchemeResultConfig">
        <div>
          <p>Choose the fields to show:</p>
          <FieldRoles configStore={configStore} />
        </div>
        <ResultPreview />
      </div>
  );
});

export const CustomSchemaResult = ({ document, configStore }) => {

  return (
      <>

      </>
  );
};