import React from 'react';

import { FormGroup, HTMLSelect } from "@blueprintjs/core";
import { view } from "@risingstack/react-easy-state";

import { persistentStore } from "../../util/persistent-store.js";

export const customSchemaResultConfig = persistentStore(
    "workbench:customSchema:resultConfig",
    {
      fieldRoles: {}
    },
    {
      load: fieldStats => {
        customSchemaResultConfig.fieldRoles = fieldStats.reduce((map, field) => {
          map[field.field] = "title";
          return map;
        }, {});
      }
    });

const FIELD_ROLES = [
  "not shown", "title", "subtitle", "body", "tag", "property", "id"
];
const FieldRole = ({ field }) => {
  return (
      <FormGroup label={field} inline={true}>
        <HTMLSelect>
          {
            FIELD_ROLES.map(role => <option value={role}>{role}</option>)
          }
        </HTMLSelect>
      </FormGroup>
  );
};

export const CustomSchemaResultConfig = view(() => {
  return (
      <div className="CustomSchemeResultConfig">
        {
/*
          fileContentsStore.fieldsAvailable.map(f => {
            return <FieldRole key={f} field={f} />;
          })
*/
        }
      </div>
  );
});
