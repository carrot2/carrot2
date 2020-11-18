import React from 'react';

import { store, view } from "@risingstack/react-easy-state";

import { Button, ControlGroup, InputGroup } from "@blueprintjs/core";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faPlug, faCheck } from "@fortawesome/pro-regular-svg-icons";

import { Setting } from "./Setting.js";
import { isEmpty } from "../../lang/objects.js";
import { LogEntry } from "../LogEntries.js";

export const CheckButton = view(({ store }) => {
  const ok = store.status === "ok";
  return (
      <Button icon={<FontAwesomeIcon icon={ok ? faCheck : faPlug} />}
              intent={ok ? "success" : "none"}
              title="Check connection"
              loading={store.status === "loading"}
              onClick={() => store.check()} />
  );
});

export const ServiceUrlSetting = view(({ setting, get, set }) => {
  const { label, description } = setting;

  const urlStore = store({
    url: get(setting),
    message: null,
    status: "pending",
    setUrl: url => {
      urlStore.url = url;
      urlStore.status = "pending";
    },
    check: async () => {
      urlStore.url = urlStore.url.replace(/\/+$/g, ""); // trim trailing slash

      if (isEmpty(urlStore.url)) {
        return;
      }
      const url = setting.pingUrl(urlStore.url);
      if (isEmpty(url)) {
        return;
      }

      urlStore.status = "loading";
      urlStore.message = null;
      fetch(url)
          .catch(e => {
            return { ok: false, message: e };
          })
          .then(response => {
            if (!response.ok) {
              throw response;
            } else {
              urlStore.status = "ok";

              // Commit new URL value
              set(setting, urlStore.url);
            }
          })
          .catch(response => {
            urlStore.status = "error";
            if (response.status) {
              urlStore.message = `Error ${response.status}: ${response.statusText}.`;
            } else if (response.text) {
              response.text().then(text => {
                urlStore.message = `${text}.`;
              });
            } else {
              urlStore.message = `${response.message}.`;
            }
          });
    }
  });

  const message = urlStore.message ?
      <LogEntry entry={{ level: "error", message: urlStore.message }} /> : null;

  return (
      <Setting className="ServiceUrlSetting" label={label} description={description}
               message={message}>
        <ControlGroup fill={true}>
          <InputGroup value={urlStore.url} fill={true}
                      onChange={e => urlStore.setUrl(e.target.value)} />
          <CheckButton store={urlStore} />
        </ControlGroup>
      </Setting>
  );
});
