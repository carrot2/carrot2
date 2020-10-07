import React from 'react';

import './StartScreen.css';

import { Redirect } from "react-router-dom";
import { sources } from "../../config-sources.js";
import { clusterViews } from "../../config-views.js";
import { SearchForm } from "./SearchForm";
import { branding } from "../../config-branding.js";

import { routes } from "../routes";

export const StartScreen = ({ match, history }) => {
  const runSearch = (query) => {
    history.push(routes.searchResults.buildUrl({
      query: query,
      source: match.params.source,
      view: Object.keys(clusterViews)[0] // start with the first view by default
    }));
  }

  const changeSource = (newSource) => {
    history.push(routes.search.buildUrl({ source: newSource }));
  };

  if (!sources[match.params.source]) {
    return <Redirect to={routes.search.buildUrl({ source: "web" })} />
  }

  return (
      <main className="StartScreen">
        {branding.createStartPageLogo()}

        <SearchForm source={match.params.source}
                    onSourceChange={changeSource}
                    onSubmit={runSearch} />

        <div className="slogan">
          {branding.createSlogan()}
        </div>
      </main>
  );
};
