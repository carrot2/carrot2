import React from "react";

import "./WorkbenchWelcome.css";

import { view } from "@risingstack/react-easy-state";

import { sources } from "../../../sources.js";
import { workbenchSourceStore } from "../store/source-store.js";
import { ButtonLink } from "@carrotsearch/ui/ButtonLink.js";
import { branding } from "@carrot2/config/branding.js";

const SourceConfigurationStep = view(() => {
  const source = sources[workbenchSourceStore.source];
  const help = source.createIntroHelp?.();
  return (
    <li className="SourceConfiguration">
      <h3>
        Configure <strong>{source.label}</strong> data source
      </h3>
      {help}
    </li>
  );
});

const SourceButtonLink = ({ source, children }) => {
  return (
    <ButtonLink onClick={() => (workbenchSourceStore.source = source)}>
      {children}
    </ButtonLink>
  );
};

const SourceSummaries = () => {
  return (
    <ul>
      {Object.keys(sources).map(s => {
        return (
          <li key={s}>
            <SourceButtonLink source={s}>
              {sources[s].contentSummary}
            </SourceButtonLink>
          </li>
        );
      })}
    </ul>
  );
};

const WorkbenchIntroSteps = () => {
  return (
    <div className="WorkbenchIntroSteps">
      <ol>
        <li className="SourceAlgorithmChoice">
          <h3>Choose data source:</h3>
          <SourceSummaries />
        </li>
        <SourceConfigurationStep />
        <li className="ButtonPress">
          <h3>
            Press the <strong>Cluster</strong> button
          </h3>
        </li>
      </ol>
    </div>
  );
};

export const WorkbenchIntro = () => {
  return (
    <>
      <div className="WorkbenchIntroWelcome">
        <h2>Welcome to Clustering Workbench</h2>
        <h3>the expert-level {branding.createProductName()} application</h3>
      </div>
      <WorkbenchIntroSteps />
    </>
  );
};
