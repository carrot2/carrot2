import React from "react";

import "./WorkbenchWelcome.css";

import { view } from "@risingstack/react-easy-state";

import { sources } from "../../../config-sources.js";
import { workbenchSourceStore } from "../store/source-store.js";

import { DottedAngledArrow } from "./arrows/DottedAngledArrow.js";
import { DottedStraightArrow } from "./arrows/DottedStraightArrow.js";
import { DottedArrowCurly } from "./arrows/DottedArrowCurly.js";

const SourceConfigurationStep = view(() => {
  const source = sources[workbenchSourceStore.source];
  const help = source.createIntroHelp?.();
  return (
    <li className="SourceConfiguration">
      <DottedAngledArrow />
      <h3>
        Configure <strong>{source.label}</strong> data source
      </h3>
      {help}
    </li>
  );
});

const WorkbenchIntroSteps = () => {
  return (
    <div className="WorkbenchIntroSteps">
      <ol>
        <li className="SourceAlgorithmChoice">
          <DottedStraightArrow />
          <h3>Choose data source and clustering algorithm</h3>
        </li>
        <SourceConfigurationStep />
        <li className="ButtonPress">
          <DottedArrowCurly />
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
        <h3>
          the expert-level Carrot<sup>2</sup> application
        </h3>
      </div>
      <WorkbenchIntroSteps />
    </>
  );
};
