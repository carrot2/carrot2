import React, { useCallback, useEffect } from 'react';

import './ResultsScreen.css';
import "three-dots/dist/three-dots.css";

import classnames from "classnames";
import { view } from "@risingstack/react-easy-state";

import { clusterViews, resultsViews } from "../../../config-views.js";
import { clusterStore, searchResultStore } from "../store/services";
import {
  clusterSelectionStore,
  documentSelectionStore,
} from "../store/selection";
import { themeStore } from "../../../ui/ThemeSwitch.js";

import { routes } from "../../../routes.js";

import { SearchForm } from "./SearchForm";

import { Views } from "../../../../carrotsearch/ui/Views.js";

import { branding } from "../../../config-branding.js";

export const Loading = view(({ store }) => {
  return (
      <div className={classnames("Loading", { "visible" : store.loading })}>
        <div className="dot-bricks" />
      </div>
  );
});

export const ResultsScreen = ({ match, history }) => {
  const source = decodeURIComponent(match.params.source);
  const query  = decodeURIComponent(match.params.query);

  const runSearch = useCallback(() => {
    searchResultStore.load(source, query);
    document.title = query + (query.length > 0 ? " - " : "") + branding.pageTitle;
  }, [source, query ]);

  useEffect(() => {
    runSearch();
  }, [ source, query, runSearch ]);

  const onQueryChange = (newQuery) => {
    pushNewUrl({
      query: newQuery,
      source: source,
      view: getView()
    });
  }

  const getView = () => {
    let view = match.params.view;
    if (view) {
      view = decodeURIComponent(view);
    } else {
      view = Object.keys(clusterViews)[0];
    }
    return view;
  };

  const onSourceChange = (newSource) => {
    pushNewUrl({
      query: query,
      source: newSource,
      view: getView()
    });
  };

  const onViewChange = (newView) => {
    pushNewUrl({
      query: query,
      source: source,
      view: newView
    });
  };

  const pushNewUrl = (params) => {
    const newPath = routes.searchResults.buildUrl(params);
    if (newPath === encodeURI(history.location.pathname)) {
      runSearch();
    } else {
      history.push(newPath);
    }
  };

  const panelProps = {
    clusterStore,
    clusterSelectionStore,
    documentSelectionStore,
    searchResultStore,
    themeStore
  };

  return (
      <main className="ResultsScreen">
        {/**
         * The key prop is used to re-create the component on query changes,
         * so that the internal state holding value is thrown away and
         * replaced with the provided initialQuery prop.
         */}
        <SearchForm initialQuery={query} key={query}
                    source={source}
                    onSourceChange={onSourceChange}
                    onSubmit={onQueryChange} />
        <div className="clusters">
          <Views activeView={getView()} views={clusterViews} onViewChange={onViewChange} {...panelProps}>
            <Loading store={clusterStore} />
          </Views>
        </div>
        <div className="docs">
          <Views views={resultsViews} activeView="list" onViewChange={() => {}} source={source}>
            <Loading store={searchResultStore} />
          </Views>
        </div>
      </main>
  );
};
