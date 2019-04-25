import './ResultsScreen.css';

import { Tab, Tabs } from "@blueprintjs/core";
import React, { Component } from 'react';

import { view } from 'react-easy-state';
import { clusterStore, searchResultStore } from "../store/services";

import logo from './assets/carrot-search-logo.svg';

import { ClusterList } from "./ClusterList";
import { ClusterSelectionSummary } from "./ClusterSelectionSummary";
import { DocumentList } from "./DocumentList";
import { routes } from "../routes";
import { SearchForm } from "./SearchForm";
import { clusterSelectionStore, documentVisibilityStore } from "../store/selection";

const ClusterListView = view(ClusterList);
const DocumentListView = view(DocumentList);
const ClusterSelectionSummaryView = view(ClusterSelectionSummary);

export class ResultsScreen extends Component {
  runSearch() {
    searchResultStore.load(this.getSource(), this.getQuery());
  }

  componentDidMount() {
    this.runSearch();
  }

  componentDidUpdate(prevProps, prevState, snapshot) {
    if (this.getQuery(prevProps) !== this.getQuery() || this.getSource(prevProps) !== this.getSource()) {
      this.runSearch();
    }
  }

  onQueryChange(newQuery) {
    this.pushNewUrl({
      query: newQuery,
      source: this.getSource()
    });
  }

  getSource(props) { return (props || this.props).match.params.source; }
  getQuery(props) { return (props || this.props).match.params.query; }

  onSourceChange(newSource) {
    this.pushNewUrl({
      query: this.getQuery(),
      source: newSource
    });
  }

  pushNewUrl(params) {
    this.props.history.push(routes.search.buildUrl(params));
  }

  render() {
    return (
      <main className="ResultsScreen">
        <img src={logo} className="logo" alt="Carrot Search logo" />
        {
          /**
           * The key prop is used to re-create the component on query changes,
           * so that the internal state holding value is thrown away and
           * replaced with the provided initialQuery prop.
           */ }
        <SearchForm initialQuery={this.getQuery()} key={this.getQuery()}
                    source={this.getSource()}
                    onSourceChange={this.onSourceChange.bind(this)}
                    onSubmit={this.onQueryChange.bind(this)} />
        <div className="clusters-tabs">
          <Tabs id="views" selectedTabId="folders" className="views">
            <Tab id="folders" title="Folders" />
            <Tab id="treemap" title="Treemap" />
            <Tab id="piechart" title="Pie-chart" />
          </Tabs>
        </div>
        <div className="docs-tabs">
          <ClusterSelectionSummaryView clusterSelectionStore={clusterSelectionStore}
                                       documentVisibilityStore={documentVisibilityStore}
                                       searchResultStore={searchResultStore} />
        </div>
        <div className="clusters">
          <div><ClusterListView store={clusterStore} selectionStore={clusterSelectionStore} /></div>
        </div>
        <div className="docs">
          <div><DocumentListView store={searchResultStore} visibilityStore={documentVisibilityStore} /></div>
        </div>
      </main>
    );
  }
}
