import './ResultsScreen.css';

import { Tab, Tabs } from "@blueprintjs/core";
import React, { Component } from 'react';

import { view } from 'react-easy-state';
import { clusterStore, searchResultStore } from "../store/services";
import { themeStore } from "./ThemeSwitch.js";

import { views } from "../../config.js";

import logo from './assets/carrot-search-logo.svg';

import { ClusterSelectionSummary } from "./ClusterSelectionSummary";
import { DocumentList } from "./DocumentList";
import { routes } from "../routes";
import { PanelSwitcher } from "./PanelSwitcher.js";
import { SearchForm } from "./SearchForm";
import { clusterSelectionStore, documentSelectionStore,
         documentVisibilityStore } from "../store/selection";

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
      source: this.getSource(),
      view: this.getView()
    });
  }

  getSource(props) { return (props || this.props).match.params.source; }
  getQuery(props) { return (props || this.props).match.params.query; }
  getView(props) { return (props || this.props).match.params.view; }

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
    this.props.history.push(routes.search.buildUrl(params));
  }

  render() {
    const panelProps = {
      clusterStore,
      clusterSelectionStore,
      documentSelectionStore,
      searchResultStore,
      themeStore
    };
    const panels = Object.keys(views)
      .map(v => {
        return {
          id: v,
          createElement: () => {
            return views[v].createContentElement(panelProps);
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
          <Tabs id="views" selectedTabId={this.getView()} onChange={this.onViewChange.bind(this)} className="views">
            {
              Object.keys(views).map(v => (
                <Tab key={v} id={v} title={views[v].label} />
              ))
            }
          </Tabs>
        </div>
        <div className="docs-tabs">
          <ClusterSelectionSummaryView clusterSelectionStore={clusterSelectionStore}
                                       documentVisibilityStore={documentVisibilityStore}
                                       searchResultStore={searchResultStore} />
        </div>
        <div className="clusters">
          <PanelSwitcher panels={panels} visible={this.getView()} />
        </div>
        <div className="docs">
          <div><DocumentListView store={searchResultStore} visibilityStore={documentVisibilityStore} /></div>
        </div>
      </main>
    );
  }
}
