import React from 'react';
import { HashRouter as Router, Redirect, Route, Switch } from "react-router-dom";

import './SearchApp.css';
import { Backdrop } from "../../ui/Backdrop.js";

import { routes } from "../routes";
import { ResultsScreen } from "./ResultsScreen";
import { SearchAppInfoButton } from "./SearchAppInfoButton.js";
import { StartScreen } from "./StartScreen";
import { ThemeSwitch } from "./ThemeSwitch";

function SearchApp() {
  return (
    <React.Fragment>
      <Router>
        <Switch>
          <Route exact path={routes._root.path} component={Backdrop} />
        </Switch>
      </Router>
      <div className="ThemeSwitch"><ThemeSwitch /></div>
      <SearchAppInfoButton />
      <div className="SearchApp">
        <Router>
          <Switch>
            <Redirect from='/search/' to='/' exact />
            <Route exact path={routes._root.path} component={StartScreen} />
            <Route path={routes.search.path} component={ResultsScreen} />
          </Switch>
        </Router>
      </div>
    </React.Fragment>
  );
}

export default SearchApp;
