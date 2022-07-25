import React from "react";

import { view } from "@risingstack/react-easy-state";
import {
  exactExclusionsHelpHtml,
  globExclusionsHelpHtml,
  regexpExclusionsHelpHtml
} from "@carrot2/app/service/algorithms/settings/ExclusionsHelp.js";
import { Setting } from "@carrotsearch/ui/settings/Setting.js";
import { Views } from "@carrotsearch/ui/Views.js";
import { TextArea } from "@blueprintjs/core";
import { ButtonLink } from "@carrotsearch/ui/ButtonLink.js";
import { DescriptionPopover } from "@carrotsearch/ui/DescriptionPopover.js";
import { CopyToClipboard } from "@carrotsearch/ui/CopyToClipboard.js";

import { VscJson } from "react-icons/vsc";

const PlainTextExclusionEditor = view(({ setting, get, set, type }) => {
  const findEntry = () => {
    const array = get(setting);
    return array.find(e => Array.isArray(e[type]));
  };

  // Look for the first entry containing the "glob" list or create one.
  const getExclusions = () => {
    const entry = findEntry();
    return entry ? entry[type].join("\r") : "";
  };

  const setExclusions = val => {
    const entry = findEntry();

    // We can't remove empty entries at this point because this would prevent
    // the user from creating new lines in the text area. Instead, we allow
    // empty lines here and remove invalid entries when submitting the request.
    // See removeEmptyEntries() below.
    const split = val.trim().length > 0 ? val.split("\n") : [];
    if (entry) {
      entry[type] = split;
    } else {
      const array = get(setting);
      array.push({ [type]: split });
    }

    // Set a new shallow copy so that the upstream code sees the change we made inside the array.
    set(setting, get(setting).slice(0));
  };

  return (
    <TextArea
      value={getExclusions()}
      onChange={e => setExclusions(e.target.value)}
    />
  );
});

const createPlainTextExclusionEditor = (type, setting, get, set) => (
  <PlainTextExclusionEditor setting={setting} get={get} set={set} type={type} />
);

const createExclusionView = (
  setting,
  get,
  set,
  label,
  editorFactory,
  helpLine,
  helpText
) => {
  return {
    label: label,
    createContentElement: visible => (
      <>
        {editorFactory()}
        <div className="ExclusionsSettingInlineHelp">
          {helpLine},{" "}
          <DescriptionPopover description={helpText}>
            <ButtonLink>syntax help</ButtonLink>
          </DescriptionPopover>
        </div>
      </>
    ),
    tools: [
      {
        createContentElement: () => {
          return (
            <CopyToClipboard
              contentProvider={() =>
                JSON.stringify(removeEmptyEntries(get(setting)), null, 2)
              }
              buttonText="Copy JSON"
              buttonProps={{
                small: true,
                minimal: true,
                title: "Copy dictionaries JSON",
                icon: <VscJson />
              }}
            />
          );
        }
      }
    ]
  };
};

export const createExclusionViews = (setting, get, set, customizer) => {
  const views = {
    glob: createExclusionView(
      setting,
      get,
      set,
      "glob",
      () => createPlainTextExclusionEditor("glob", setting, get, set),
      <span>
        One pattern per line, separate words with spaces, <code>*</code> is zero
        or more words
      </span>,
      globExclusionsHelpHtml
    ),
    exact: createExclusionView(
      setting,
      get,
      set,
      "exact",
      () => createPlainTextExclusionEditor("exact", setting, get, set),
      <span>One label per line, exact matching</span>,
      exactExclusionsHelpHtml
    ),
    regex: createExclusionView(
      setting,
      get,
      set,
      "regexp",
      () => createPlainTextExclusionEditor("regexp", setting, get, set),
      <span>One Java regex per line</span>,
      regexpExclusionsHelpHtml
    )
  };
  if (customizer) {
    customizer(views, createExclusionView);
  }

  return [
    {
      views: views
    }
  ];
};

export const removeEmptyEntries = dictionaries => {
  return dictionaries.map(dictionary => {
    return Object.keys(dictionary).reduce((map, type) => {
      map[type] = dictionary[type].filter(e => e.trim().length > 0);
      return map;
    }, {});
  });
};

export const ExclusionsSetting = view(
  ({ setting, get, set, views, getActiveView, setActiveView, search }) => {
    const { label, description } = setting;

    return (
      <Setting
        className="ExclusionsSetting"
        label={label}
        description={description}
        search={search}
        labelSearchTarget={setting.labelSearchTarget}
      >
        <Views
          views={views}
          activeView={getActiveView()}
          onViewChange={setActiveView}
          setting={setting}
          get={get}
          set={set}
        />
      </Setting>
    );
  }
);
