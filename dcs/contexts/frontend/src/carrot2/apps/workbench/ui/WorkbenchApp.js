import React from 'react';

import "./WorkbenchApp.css";

import { routes } from "../../../routes.js";

import { HashRouter as Router, Route, Switch } from "react-router-dom";
import { WorkbenchResults } from "./WorkbenchResults.js";

export const WorkbenchApp = () => {
  return (
      <React.Fragment>
        <div className="WorkbenchApp">
          <Router>
            <Switch>
              <Route path={routes.workbench.path} component={WorkbenchResults} />
            </Switch>
          </Router>
        </div>
      </React.Fragment>
  );
};