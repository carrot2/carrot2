import React from 'react';

import { store, view } from "@risingstack/react-easy-state";

import { Button, ControlGroup, InputGroup } from "@blueprintjs/core";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faPlug, faCheck, faExclamationTriangle } from "@fortawesome/pro-regular-svg-icons";

import { Setting } from "./Setting.js";
import { isEmpty } from "../../lang/objects.js";

const STATUS_ICONS = {
  "loading": faPlug,
  "pending": faPlug,
  "error": faExclamationTriangle,
  "ok": faCheck
};

export const CheckButton = view(({ store }) => {
  return (
      <Button icon={<FontAwesomeIcon icon={STATUS_ICONS[store.status]} />}
              title="Check connection" loading={store.status === "loading"}
              onClick={() => store.check() } />
  );
});

export const ServiceUrlSetting = view(({ setting, get, set }) => {
  const { label, description } = setting;

  const urlStore = store({
    url: get(setting),
    message: "",
    status: "pending",
    setUrl: url => {
      urlStore.url = url;
      urlStore.status = "pending";
    },
    check: async () => {
      if (isEmpty(urlStore.url)) {
        return;
      }
      const url = setting.pingUrl(urlStore.url);
      if (isEmpty(url)) {
        return;
      }

      urlStore.status = "loading";
      fetch(url)
          .catch(e => {
            return { ok: false, message: e };
          })
          .then(response => {
            if (!response.ok) {
              throw response;
            } else {
              urlStore.status = "ok";
            }
          })
          .catch(response => {
            urlStore.status = "error";
            if (response.text) {
              response.text().then(text => {
                urlStore.message = `Failed to connect: ${text}.`;
              });
            } else {
              urlStore.message = `Failed to connect: ${response.message}.`;
            }
          });
    }
  });

  return (
      <Setting className="ServiceUrlSetting" label={label} description={description}>
        <ControlGroup fill={true}>
          <InputGroup value={urlStore.url} fill={true}
                      onChange={e => urlStore.setUrl(e.target.value)} />
          <CheckButton store={urlStore} />
        </ControlGroup>
        <div>
          {urlStore.message}
        </div>
      </Setting>
  );
});
