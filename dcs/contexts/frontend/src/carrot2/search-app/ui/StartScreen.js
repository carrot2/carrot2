import React, { Component } from 'react';
import { Redirect } from "react-router-dom";

import './StartScreen.css';
import { sources } from "../../config-sources.js";
import { clusterViews } from "../../config-views.js";
import { SearchForm } from "./SearchForm";
import { branding } from "../../config-branding.js";

import { routes } from "../routes";

export class StartScreen extends Component {
  runSearch(query) {
    this.props.history.push(routes.search.buildUrl({
      query: query,
      source: this.props.match.params.source,
      view: Object.keys(clusterViews)[0] // start with the first view by default
    }));
  }

  changeSource(newSource) {
    this.props.history.push(routes._root.buildUrl({source: newSource}));
  }

  render() {
    if (!sources[this.props.match.params.source]) {
      return <Redirect to={routes._root.buildUrl({source: "web"})} />
    }

    return (
      <main className="StartScreen">
        {branding.createStartPageLogo()}

        <SearchForm source={this.props.match.params.source}
                    onSourceChange={this.changeSource.bind(this)}
                    onSubmit={this.runSearch.bind(this)} />

        <div className="slogan">
          {branding.createSlogan()}
        </div>
      </main>
    );
  }
}
