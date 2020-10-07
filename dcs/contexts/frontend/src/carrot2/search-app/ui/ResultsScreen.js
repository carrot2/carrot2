import React, { useCallback, useEffect, useRef } from 'react';

import './ResultsScreen.css';

import { view } from "@risingstack/react-easy-state";
import { clusterViews, resultListConfigStore, resultsViews } from "../../config-views.js";
import { clusterStore, searchResultStore } from "../store/services";
import {
  clusterSelectionStore,
  documentSelectionStore,
  documentVisibilityStore
} from "../store/selection";
import { ClusteringEngineErrorMessage, SearchEngineErrorMessage } from "./ErrorMessage.js";
import { ShowHide } from "./Optional.js";
import { themeStore } from "./ThemeSwitch.js";

import { routes } from "../routes";

import { ResultList } from "./ResultList";
import { Switcher } from "./Switcher.js";
import { SearchForm } from "./SearchForm";

import { ViewTabs } from "./Views.js";

import { branding } from "../../config-branding.js";

const Loading = view(props => (
    <ShowHide className="Loading" visible={props.store.loading}>
      Loading
    </ShowHide>
));

const usePrevious = value => {
  const ref = useRef();

  useEffect(() => {
    ref.current = value;
  }, [ value ]);

  return ref.current;
};

export const ResultsScreen = ({ match, history }) => {
  const source = decodeURIComponent(match.params.source);
  const query  = decodeURIComponent(match.params.query);

  const prevSource = usePrevious(source);

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

  // Set loading state when source changes, so that the to-be-replaced
  // document list is not re-rendered with incompatible (new) source renderer
  // before the new source returns the results.
  if (prevSource !== source) {
    searchResultStore.loading = true;
  }

  const panelProps = {
    clusterStore,
    clusterSelectionStore,
    documentSelectionStore,
    searchResultStore,
    themeStore
  };

  const contentPanels = Object.keys(clusterViews)
      .map(v => {
        return {
          id: v,
          isVisible: (visibleId, p) => {
            return p.id === visibleId;
          },
          createElement: (visible) => {
            return clusterViews[v].createContentElement({ visible: visible, ...panelProps });
          }
        };
      });

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
        <div className="clusters-tabs">
          <ViewTabs activeView={getView()} views={clusterViews}
                    onViewChange={onViewChange} />
        </div>
        <div className="docs-tabs">
          <ViewTabs views={resultsViews} activeView="list" onViewChange={() => {}} source={source} />
        </div>
        <div className="clusters">
          <Loading store={clusterStore} />
          <ClusteringEngineErrorMessage store={clusterStore} />
          <Switcher panels={contentPanels} visible={getView()} />
        </div>
        <div className="docs">
          <Loading store={searchResultStore} />
          <SearchEngineErrorMessage source={source} store={searchResultStore}
                                    runSearch={runSearch} />
          <div>
            <ResultList source={source} store={searchResultStore}
                        visibilityStore={documentVisibilityStore}
                        clusterSelectionStore={clusterSelectionStore}
                        commonConfigStore={resultListConfigStore} />
          </div>
        </div>
      </main>
  );
};
