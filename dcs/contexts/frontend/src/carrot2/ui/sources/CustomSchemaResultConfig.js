import React from 'react';

import "./CustomSchemaResultConfig.css";

import { FormGroup, HTMLSelect } from "@blueprintjs/core";
import { view } from "@risingstack/react-easy-state";

import { persistentStore } from "../../util/persistent-store.js";
import { TitleAndRank } from "../../apps/search-app/ui/view/results/result-components.js";

import { resultListConfigStore } from "../../apps/search-app/ui/ResultListConfig.js";

import { ResultWrapper } from "../../apps/search-app/ui/ResultList.js";
import { mapUpToMaxLength, wrapIfNotArray } from "../../../carrotsearch/lang/arrays.js";

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

          // TODO: don't display if all docs have the same value (distinct = 1)

          const map = fieldStats.reduce((map, field) => {
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
  "not shown", "title", "body", "tag", "property", "id"
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

export const ResultPreview = view(({ configStore, previewResultProvider }) => {
  const result = previewResultProvider();
  const preview = result ?
      <ResultWrapper document={result}>
        <CustomSchemaResult result={result} rank={1} configStore={configStore} />
      </ResultWrapper>
      : <span>Not available</span>;

  return (
      <div>
        <p>Preview:</p>
        <div className="ResultPreview">
          {preview}
        </div>
      </div>
  )
});

export const CustomSchemaResultConfig = view(({ configStore, previewResultProvider }) => {
  return (
      <div className="CustomSchemeResultConfig">
        <div>
          <p>Choose the fields to show:</p>
          <FieldRoles configStore={configStore} />
        </div>
        <ResultPreview configStore={configStore} previewResultProvider={previewResultProvider} />
      </div>
  );
});

export const CustomSchemaResult = view(({ document, rank, configStore }) => {
  const rolesMap = configStore.fieldRoles;
  const roles = Object.keys(rolesMap).reduce((groups, field) => {
    const role = rolesMap[field];
    if (role !== "not shown") {
      groups[role].push(field);
    }
    return groups;
  }, { "title": [], "body": [], "tag": [], "property": [], "id": [] });

  const bodyParagraphs = roles["body"].reduce((arr, field) => {
    const value = document[field];
    if (value) {
      const val = wrapIfNotArray(value);
      val.forEach((v, index) => {
        if (index === 0) {
          arr.push({ text: v, field: field });
        } else {
          arr.push({ text: v });
        }
      });
    }
    return arr;
  }, []);

  const allProperties = roles["property"].reduce((arr, field) => {
    const val = wrapIfNotArray(document[field]);
    arr.push({ field: field, text: val.join(", ") })
    return arr;
  }, []);

  return (
      <>
        {
          roles["title"].map((field, index) => {
            return <TitleAndRank key={field} title={document[field]} rank={rank}
                                 showRank={index === 0 && resultListConfigStore.showRank} />
          })
        }

        <div>
          {
            mapUpToMaxLength(
                bodyParagraphs,
                resultListConfigStore.maxCharsPerResult,
                (text, obj, index) => {
                  const label = obj.field ? <span>{obj.field}</span> : null;
                  return <p key={index}>{label}{text}</p>;
                },
                obj => obj.text)
          }
        </div>

        {
          roles["tag"].map(field => {
            const tags = wrapIfNotArray(document[field]);
            return (
                <div className="tags" key={field}>
                  <span>{field}</span>
                  {
                    tags.join(", ")
                  }
                </div>
            );
          })
        }

        <dl className="properties">
          {
            allProperties.map(p => {
              return (
                  <>
                    <dt>{p.field}</dt><dd>{p.text}</dd>
                    {" "}
                  </>
              )
            })
          }
        </dl>
      </>
  );
});