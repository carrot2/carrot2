import React from 'react';

import { store, view } from "@risingstack/react-easy-state";

import { Button, ControlGroup, InputGroup } from "@blueprintjs/core";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faPlug, faCheck } from "@fortawesome/pro-regular-svg-icons";

import { Setting } from "./Setting.js";
import { LogEntry } from "../LogEntries.js";

export const CheckButton = view(({ store, onClick }) => {
  const ok = store.status === "ok";
  return (
      <Button icon={<FontAwesomeIcon icon={ok ? faCheck : faPlug} />}
              intent={ok ? "success" : "none"}
              title="Check connection"
              loading={store.status === "loading"}
              onClick={onClick} outlined={ok} />
  );
});

export const createStateStore = (overrides) => {
  const urlStore = store(Object.assign({
    message: null,
    status: "pending",
    urlDirty: () => {
      urlStore.status = "pending";
    }
  }, overrides));
  return urlStore;
};

export const ServiceUrlSetting = view(({ setting, get, set }) => {
  const { label, description, stateStore, checkUrl } = setting;

  const urlStore = store({
    url: get(setting),
    setUrl: url => {
      urlStore.url = url;
      stateStore.urlDirty();
    }
  });

  const message = stateStore.message ?
      <LogEntry entry={{ level: "error", message: stateStore.message }} /> : null;

  return (
      <Setting className="ServiceUrlSetting" label={label} description={description}
               message={message}>
        <ControlGroup fill={true}>
          <InputGroup value={urlStore.url} fill={true}
                      onChange={e => urlStore.setUrl(e.target.value)} />
          <CheckButton store={stateStore} onClick={() => checkUrl(urlStore.url)} />
        </ControlGroup>
      </Setting>
  );
});
