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

const PlainTextExclusionSetting = view(({ setting, get, set, type }) => {
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
      style={{ width: "100%", minHeight: "8rem" }}
      value={getExclusions()}
      onChange={e => setExclusions(e.target.value)}
    />
  );
});

const createExclusionView = (type, helpLine, helpText) => {
  return {
    label: type,
    createContentElement: (visible, { setting, get, set }) => {
      return (
        <>
          <PlainTextExclusionSetting
            setting={setting}
            get={get}
            set={set}
            type={type}
          />
          <div className="ExclusionsSettingInlineHelp">
            {helpLine},{" "}
            <DescriptionPopover description={helpText}>
              <ButtonLink>syntax help</ButtonLink>
            </DescriptionPopover>
          </div>
        </>
      );
    }
  };
};

const exclusionViews = [
  {
    views: {
      glob: createExclusionView(
        "glob",
        <span>
          One pattern per line, separate words with spaces, <code>*</code> is
          zero or more words
        </span>,
        globExclusionsHelpHtml
      ),
      exact: createExclusionView(
        "exact",
        <span>One label per line, exact matching</span>,
        exactExclusionsHelpHtml
      ),
      regex: createExclusionView(
        "regexp",
        <span>One Java regex per line</span>,
        regexpExclusionsHelpHtml
      )
    }
  }
];

export const ExclusionsSetting = view(
  ({ setting, get, set, getActiveView, setActiveView }) => {
    const { label, description } = setting;

    return (
      <Setting
        className="ExclusionsSetting"
        label={label}
        description={description}
      >
        <Views
          views={exclusionViews}
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
