import './ResultsScreen.css';

import React, { Component } from 'react';

import { view } from 'react-easy-state';
import { clusterViews, resultListConfigStore, resultsViews } from "../../config-views.js";
import { clusterStore, searchResultStore } from "../store/services";
import { clusterSelectionStore, documentSelectionStore, documentVisibilityStore } from "../store/selection";
import { themeStore } from "./ThemeSwitch.js";

import { routes } from "../routes";

import logo from './assets/carrot-search-logo.svg';

import { ResultList } from "./ResultList";
import { Switcher } from "./Switcher.js";
import { SearchForm } from "./SearchForm";

import { ViewTabs } from "./Views.js";

import { applicationTitle } from "../../config.js";

const ResultListView = view(ResultList);

export class ResultsScreen extends Component {
  runSearch() {
    searchResultStore.load(this.getSource(), this.getQuery());
    document.title = this.getQuery() + (this.getQuery().length > 0 ? " - " : "") + applicationTitle;
  }

  componentDidMount() {
    this.runSearch();
    this.prevSource = this.getSource();
  }

  componentDidUpdate(prevProps, prevState, snapshot) {
    if (this.getQuery(prevProps) !== this.getQuery() || this.getSource(prevProps) !== this.getSource()) {
      this.runSearch();
    }
    this.prevSource = this.getSource();
  }

  onQueryChange(newQuery) {
    this.pushNewUrl({
      query: newQuery,
      source: this.getSource(),
      view: this.getView()
    });
  }

  getSource(props) { return decodeURIComponent((props || this.props).match.params.source); }
  getQuery(props) { return decodeURIComponent((props || this.props).match.params.query); }
  getView(props) { return decodeURIComponent((props || this.props).match.params.view); }

  onSourceChange(newSource) {
    this.pushNewUrl({
      query: this.getQuery(),
      source: newSource,
      view: this.getView()
    });
  }

  onViewChange(newView) {
    this.pushNewUrl({
      query: this.getQuery(),
      source: this.getSource(),
      view: newView
    });
  }

  pushNewUrl(params) {
    const newPath = routes.search.buildUrl(params);
    if (newPath === this.props.history.location.pathname) {
      this.runSearch();
    } else {
      this.props.history.push(newPath);
    }
  }

  render() {
    // Set loading state when source changes, so that the to-be-replaced
    // document list is not re-rendered with incompatible (new) source renderer
    // before the new source returns the results.
    if (this.prevSource !== this.getSource()) {
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
            return clusterViews[v].createContentElement({ visible: visible, ...panelProps});
          }
        };
      });

    return (
      <main className="ResultsScreen">
        <img src={logo} className="logo" alt="Carrot Search logo" />
        {/**
           * The key prop is used to re-create the component on query changes,
           * so that the internal state holding value is thrown away and
           * replaced with the provided initialQuery prop.
           */ }
        <SearchForm initialQuery={this.getQuery()} key={this.getQuery()}
                    source={this.getSource()}
                    onSourceChange={this.onSourceChange.bind(this)}
                    onSubmit={this.onQueryChange.bind(this)} />
        <div className="clusters-tabs">
          <ViewTabs activeView={this.getView()} views={clusterViews} onViewChange={this.onViewChange.bind(this)} />
        </div>
        <div className="docs-tabs">
          <ViewTabs views={resultsViews} activeView="list" onViewChange={() => {}} source={this.getSource()} />
        </div>
        <div className="clusters">
          <Switcher panels={contentPanels} visible={this.getView()} />
        </div>
        <div className="docs">
          <div>
            <ResultListView source={this.getSource()} store={searchResultStore}
                            visibilityStore={documentVisibilityStore}
                            clusterSelectionStore={clusterSelectionStore}
                            commonConfigStore={resultListConfigStore} />
          </div>
        </div>
      </main>
    );
  }
}
