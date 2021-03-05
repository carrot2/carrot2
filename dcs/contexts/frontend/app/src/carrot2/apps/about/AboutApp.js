import React from "react";
import "./AboutApp.css";

import { branding } from "@carrot2/config/branding.js";
import { algorithms } from "@carrot2/config/algorithms.js";
import { sources } from "@carrot2/app/sources.js";

export const SearchEnginesInfo = () => {
  return (
    <>
      <ul>
        {Object.keys(sources).map(s => (
          <li key={s}>
            <strong>{sources[s].label}</strong>:&nbsp;
            <span
              dangerouslySetInnerHTML={{ __html: sources[s].descriptionHtml }}
            />
          </li>
        ))}
      </ul>
    </>
  );
};

export const ClusteringEnginesInfo = () => {
  return (
    <>
      <ul>
        {Object.keys(algorithms).map(a => (
          <li key={a} style={{ breakInside: "avoid" }}>
            <strong>{algorithms[a].label}</strong>:&nbsp;
            <span
              dangerouslySetInnerHTML={{
                __html: algorithms[a].descriptionHtml
              }}
            />
          </li>
        ))}
      </ul>
    </>
  );
};

export const VersionInfo = () => {
  return (
    <div className="VersionInfo">
      <ul>
        <li>
          <span>version:</span> {process.env.REACT_APP_VERSION}
        </li>
        <li>
          <span>build date:</span> {process.env.REACT_APP_BUILD_DATE}
        </li>
        <li>
          <span>git rev:</span> {process.env.REACT_APP_GIT_REV}
        </li>
      </ul>
    </div>
  );
};

export const AboutApp = () => {
  return (
    <div className="SearchAppInfo">
      <h2>{branding.createProductName()} clustering engine</h2>
      <main>
        {branding.createAboutIntro()}

        <h3>Search engines</h3>
        <SearchEnginesInfo />

        <h3>Clustering algorithms</h3>
        <ClusteringEnginesInfo />

        {branding.createAboutDetails()}

        <VersionInfo />
      </main>
    </div>
  );
};
