import React from "react";

import "./SearchApp.css";

import { HashRouter as Router, Route, Switch } from "react-router-dom";

import { routes } from "../../../routes.js";
import { ResultsScreen } from "./ResultsScreen.js";
import { StartScreen } from "./StartScreen.js";

export const SearchApp = () => (
  <React.Fragment>
    <div className="SearchApp">
      <Router>
        <Switch>
          <Route exact path={routes.searchStart.path} component={StartScreen} />
          <Route path={routes.searchResults.path} component={ResultsScreen} />
        </Switch>
      </Router>
    </div>
  </React.Fragment>
);
