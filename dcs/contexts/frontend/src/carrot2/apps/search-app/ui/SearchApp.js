import React from 'react';

import './SearchApp.css';

import { HashRouter as Router, Route, Switch } from "react-router-dom";


import { routes } from "../routes";
import { ResultsScreen } from "./ResultsScreen";
import { SearchAppInfoButton } from "./SearchAppInfoButton.js";
import { StartScreen } from "./StartScreen";

export const SearchApp = () => (
    <React.Fragment>
      <SearchAppInfoButton />
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
