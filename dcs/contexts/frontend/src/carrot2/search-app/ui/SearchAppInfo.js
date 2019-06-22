import "./SearchAppInfo.css";
import { Icon } from "@blueprintjs/core";

import React from "react";

import introHtml from "../../about-intro.html";
import detailsHtml from "../../about-details.html";
import { applicationTitle } from "../../config.js";

import { sources } from "../../config-sources.js";
import { algorithms } from "../../config-algorithms.js";

export const SearchEnginesInfo = () => {
  return (
    <>
      <ul>
        {
          Object.keys(sources).map(s => (
            <li key={s}>
              <strong>{sources[s].label}</strong>:&nbsp;
              <span dangerouslySetInnerHTML={{__html: sources[s].descriptionHtml}} />
            </li>
          ))
        }
      </ul>
    </>
  );
};

export const ClusteringEnginesInfo = () => {
  return (
    <>
      <p>
        Click the <Icon icon="wrench" /> button to choose from the following clustering algorithms:
      </p>
      <ul>
        {
          Object.keys(algorithms).map(a => (
            <li key={a}>
              <strong>{algorithms[a].label}</strong>:&nbsp;
              <span dangerouslySetInnerHTML={{__html: algorithms[a].descriptionHtml}} />
            </li>
          ))
        }

      </ul>
    </>
  );
};

export const SearchAppInfo = () => {
  return (
    <div className="SearchAppInfo">
      <h3>{applicationTitle}</h3>
      <div dangerouslySetInnerHTML={{__html: introHtml}} />

      <h3>Search engines</h3>
      <SearchEnginesInfo />

      <h3>Clustering algorithms</h3>
      <ClusteringEnginesInfo />

      <div dangerouslySetInnerHTML={{__html: detailsHtml}} />
    </div>
  );
};