import React from 'react';
import { HashRouter as Router, Redirect, Route, Switch } from "react-router-dom";

import './SearchApp.css';

import { routes } from "../routes";
import { ResultsScreen } from "./ResultsScreen";
import { StartScreen } from "./StartScreen";
import { ThemeSwitch } from "./ThemeSwitch";

function SearchApp() {
  return (
    <React.Fragment>
      <div style={{position: "absolute", top: 0, right: 0}}><ThemeSwitch /></div>
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
