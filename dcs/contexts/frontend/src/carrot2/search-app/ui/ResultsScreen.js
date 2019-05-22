import './ResultsScreen.css';

import { ControlGroup, Button, Classes, Popover, Position } from "@blueprintjs/core";
import { IconNames } from "@blueprintjs/icons";
import React, { Component } from 'react';

import { view } from 'react-easy-state';
import { clusterStore, searchResultStore } from "../store/services";
import { clusterSelectionStore, documentSelectionStore, documentVisibilityStore } from "../store/selection";
import { resultListConfigStore } from "../store/ui-config.js";
import { themeStore } from "./ThemeSwitch.js";

import { routes } from "../routes";
import { views } from "../../config.js";

import logo from './assets/carrot-search-logo.svg';

import { ClusterSelectionSummary } from "./ClusterSelectionSummary";
import { ResultList } from "./ResultList";
import { Switcher } from "./Switcher.js";
import { SearchForm } from "./SearchForm";

import { ResultListConfig } from "./ResultListConfig.js";
import { ViewTabs } from "./Views.js";

const ResultListView = view(ResultList);
const ClusterSelectionSummaryView = view(ClusterSelectionSummary);
const ResultListConfigView = view(ResultListConfig);

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

    const contentPanels = Object.keys(views)
      .map(v => {
        return {
          id: v,
          isVisible: (visibleId, p) => {
            return p.id === visibleId;
          },
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
          <ViewTabs activeView={this.getView()} views={views} onViewChange={this.onViewChange.bind(this)} />
        </div>
        <div className="docs-tabs">
          <ControlGroup fill={true} vertical={false}>
            <ClusterSelectionSummaryView clusterSelectionStore={clusterSelectionStore}
              documentVisibilityStore={documentVisibilityStore}
              searchResultStore={searchResultStore} />
            <Popover className={Classes.FIXED}
                     position={Position.BOTTOM_RIGHT} autoFocus={true}
                     popoverClassName="bp3-popover-content-sizing">
              <Button icon={IconNames.COG} minimal={true} />
              <ResultListConfigView store={resultListConfigStore} />
            </Popover>
          </ControlGroup>
        </div>
        <div className="clusters">
          <Switcher panels={contentPanels} visible={this.getView()} />
        </div>
        <div className="docs">
          <div>
            <ResultListView store={searchResultStore} visibilityStore={documentVisibilityStore}
                            configStore={resultListConfigStore} />
          </div>
        </div>
      </main>
    );
  }
}
