import React from "react";

import "./StartScreen.css";

import { NavLink, Redirect } from "react-router-dom";

import { faFlask } from "@fortawesome/pro-regular-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";

import { sources } from "../../../config-sources.js";
import { clusterViews } from "../../../config-views.js";
import { branding } from "../../../config-branding.js";
import { routes } from "../../../routes.js";

import { SearchForm } from "./SearchForm.js";

export const StartScreen = ({ match, history }) => {
  const runSearch = query => {
    history.push(
      routes.searchResults.buildUrl({
        query: query,
        source: match.params.source,
        view: Object.keys(clusterViews[0].views)[0] // start with the first view by default
      })
    );
  };

  const changeSource = newSource => {
    history.push(routes.searchStart.buildUrl({ source: newSource }));
  };

  if (!sources[match.params.source]) {
    return <Redirect to={routes.searchStart.buildUrl({ source: "web" })} />;
  }

  return (
    <main className="StartScreen">
      {branding.createStartPageLogo()}

      <SearchForm
        source={match.params.source}
        onSourceChange={changeSource}
        onSubmit={runSearch}
      />

      <div className="slogan">{branding.createSlogan()}</div>

      <div className="WorkbenchHint">
        New! Try{" "}
        <NavLink to={routes.workbench.path}>
          <FontAwesomeIcon icon={faFlask} /> Clustering Workbench
        </NavLink>{" "}
        to process data from local files, Solr, Elasticsearch.
      </div>
    </main>
  );
};
