import React from 'react';

import "./App.css";

import { HashRouter as Router, Link, Redirect, Route, Switch } from "react-router-dom";
import { Popover, PopoverInteractionKind, PopoverPosition, Tooltip } from "@blueprintjs/core";

import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faSearch, faFlask } from "@fortawesome/pro-regular-svg-icons";
import { SearchApp } from "./search-app/ui/SearchApp.js";
import { routes } from "./search-app/routes.js";
import { Backdrop } from "./ui/Backdrop.js";
import { ThemeSwitch } from "./search-app/ui/ThemeSwitch.js";
import { WorkbenchApp } from "./workbench/WorkbenchApp.js";
import { CarrotLogo } from "../carrotsearch/logo/CarrotLogo.js";

const NavLink = ({ to, title, children, icon }) => {
  return (
      <Link to={to}>
        <Popover popoverClassName="NavPopover" position={PopoverPosition.RIGHT} interactionKind={PopoverInteractionKind.HOVER}>
          <FontAwesomeIcon icon={icon} size="2x" />
          <div className="NavPopoverContent">
            <p><strong>{title}</strong></p>
            {children}
          </div>
        </Popover>
      </Link>
  );
};

export const App = () => {
  return (
      <div className="App">
        <Router>
          <nav style={{zIndex: 1}}>
            <CarrotLogo className="dark" />

            <NavLink to={routes.search.path} title="Web search clustering" icon={faSearch}>
              <p>
                Clustering of search results from different search engines.
              </p>
            </NavLink>
            <NavLink to={routes.workbench.path} title="Clustering workbench" icon={faFlask}>
              <p>
                Parameter tuning, clustering of data from:
              </p>

              <ul>
                <li>JSON, XML, CSV and Excel files,</li>
                <li>web search results,</li>
                <li>search results from Solr and Elasticsearch.</li>
              </ul>
            </NavLink>
            <ThemeSwitch />
          </nav>
          <main>
            <Router>
              <Switch>
                <Route path={routes.search.path} component={SearchApp} />
                <Route path={routes.workbench.path} component={WorkbenchApp} />
              </Switch>
            </Router>
          </main>

          <Switch>
            <Route exact path={routes.search.path} component={Backdrop} />
          </Switch>
        </Router>
      </div>
  );
};