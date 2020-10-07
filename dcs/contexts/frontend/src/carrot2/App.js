import React from 'react';

import "./App.css";

import { HashRouter as Router, Link, NavLink, Redirect, Route, Switch } from "react-router-dom";
import { Popover, PopoverInteractionKind, PopoverPosition } from "@blueprintjs/core";

import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faSearch, faFlask } from "@fortawesome/pro-regular-svg-icons";
import { faGithub } from "@fortawesome/free-brands-svg-icons";
import { SearchApp } from "./search-app/ui/SearchApp.js";
import { routes } from "./search-app/routes.js";
import { Backdrop } from "./ui/Backdrop.js";
import { ThemeSwitch } from "./search-app/ui/ThemeSwitch.js";
import { WorkbenchApp } from "./workbench/WorkbenchApp.js";
import { CarrotLogo } from "../carrotsearch/logo/CarrotLogo.js";

const AppLink = ({ to, title, children, icon }) => {
  return (
      <NavLink className="NavLink AppLink" to={to} activeClassName="active">
        <Popover popoverClassName="NavPopover" position={PopoverPosition.RIGHT} interactionKind={PopoverInteractionKind.HOVER}>
          <FontAwesomeIcon icon={icon} size="2x" />
          <div className="NavPopoverContent">
            <p><strong>{title}</strong></p>
            {children}
          </div>
        </Popover>
      </NavLink>
  );
};

const NavExternalLink = ({ href, icon }) => {
  return <a className="NavLink" href={href} target="_blank" rel="noopener noreferrer"><FontAwesomeIcon icon={icon} size="2x" /></a>;
};

export const App = () => {
  return (
      <div className="App">
        <Router>
          <nav>
            <Link to="/"><CarrotLogo className="dark" /></Link>

            <AppLink to={routes.search.path} title="Web search clustering" icon={faSearch}>
              <p>
                Clustering of search results from different search engines.
              </p>
            </AppLink>
            <AppLink to={routes.workbench.path} title="Clustering workbench" icon={faFlask}>
              <p>
                Parameter tuning, clustering of data from:
              </p>

              <ul>
                <li>JSON, XML, CSV and Excel files,</li>
                <li>web search results,</li>
                <li>search results from Solr and Elasticsearch.</li>
              </ul>
            </AppLink>

            <div className="NavExtras">
              <NavExternalLink icon={faGithub} href="https://github.com/carrot2/carrot2" />
              <ThemeSwitch />
            </div>
          </nav>

          <main>
            <Router>
              <Switch>
                <Redirect from='/' to={routes.searchStart.path} exact />
                <Route path={routes.searchStart.path} component={SearchApp} />
                <Route path={routes.workbench.path} component={WorkbenchApp} />
              </Switch>
            </Router>
          </main>

          <Switch>
            <Route exact path={routes.searchStart.path} component={Backdrop} />
          </Switch>
        </Router>
      </div>
  );
};