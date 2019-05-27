import React, { Component } from 'react';
import { Redirect } from "react-router-dom";

import './StartScreen.css';
import { sources } from "../../config-sources.js";
import { clusterViews } from "../../config-views.js";
import logo from './assets/carrot-search-logo.svg';
import text from './assets/carrot-search-text.svg';
import { SearchForm } from "./SearchForm";

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
        <img src={logo} className="logo" alt="Carrot Search logo" />
        <img src={text} className="text" alt="Carrot Search" />

        <SearchForm source={this.props.match.params.source}
                    onSourceChange={this.changeSource.bind(this)}
                    onSubmit={this.runSearch.bind(this)} />

        <div className="slogan">
          Carrot Search <a href="https://carrotsearch.com/lingo3g">Lingo3G</a> organizes search results into thematical
          folders.
        </div>
      </main>
    );
  }
}
