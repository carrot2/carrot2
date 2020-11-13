import React, { useEffect } from 'react';

import "./App.css";

import classnames from "classnames";
import { view } from "@risingstack/react-easy-state";

import {
  HashRouter as Router,
  Link,
  matchPath,
  NavLink,
  Redirect,
  Route,
  Switch
} from "react-router-dom";
import { Button, Popover, PopoverInteractionKind, PopoverPosition } from "@blueprintjs/core";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faFlask, faFrown, faInfo, faSearch } from "@fortawesome/pro-regular-svg-icons";
import { faGithub } from "@fortawesome/free-brands-svg-icons";

import { errors } from "./store/errors.js";

import { SearchApp } from "./apps/search-app/ui/SearchApp.js";
import { routes } from "./routes.js";
import { Backdrop } from "../carrotsearch/ui/backdrop/Backdrop.js";
import { ThemeSwitch } from "../carrotsearch/ui/ThemeSwitch.js";
import { WorkbenchApp } from "./apps/workbench/ui/WorkbenchApp.js";
import { CarrotLogo } from "../carrotsearch/logo/CarrotLogo.js";
import { AboutApp } from "./apps/about/AboutApp.js";

const AppLink = ({ to, title, children, icon }) => {
  return (
      <NavLink className="NavLink AppLink" to={to} activeClassName="active">
        <Popover popoverClassName="NavPopover" position={PopoverPosition.RIGHT}
                 interactionKind={PopoverInteractionKind.HOVER} hoverOpenDelay={650}>
          <FontAwesomeIcon icon={icon} size="2x" />
          <div className="NavPopoverContent">
            <h3>{title}</h3>
            {children}
          </div>
        </Popover>
      </NavLink>
  );
};

const NavExternalLink = ({ href, icon }) => {
  return <a className="NavLink" href={href} target="_blank"
            rel="noopener noreferrer"><FontAwesomeIcon icon={icon} size="2x" /></a>;
};

export const AppInternal = ({ location }) => {
  const m = matchPath(location.pathname, { path: routes.searchStart.path, exact: true });
  return (
      <div className={classnames("App", { "WithBackdrop": m !== null })}>
        <Switch>
          <Route exact path={routes.searchStart.path} component={Backdrop} />
          <Route exact path={routes.about.path} component={Backdrop} />
        </Switch>

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
            <AppLink to={routes.about.path} title="About this application" icon={faInfo} />
            <NavExternalLink icon={faGithub} href="https://github.com/carrot2/carrot2" />
            <ThemeSwitch />
          </div>
        </nav>

        <main>
          <Router>
            <Switch>
              <Redirect from='/' to={routes.searchStart.path} exact />
              <Route path={routes.about.path} component={AboutApp} />
              <Route path={routes.searchStart.path} component={SearchApp} />
              <Route path={routes.workbench.path} component={WorkbenchApp} />
            </Switch>
          </Router>
          <AppError />
        </main>
      </div>
  );
};

export const AppErrorContent = ({ children }) => {
  if (!children) {
    return null;
  }

  return (
      <div>
        <FontAwesomeIcon icon={faFrown} size="2x" />
        {children}

        <div className="AppErrorButtons">
          <Button outlined={false} onClick={() => errors.dismiss()}>Dismiss</Button>
        </div>
      </div>
  );
};

export const AppError = view(() => {
  const errorElement = errors.current;

  useEffect(() => {
    const listener = e => {
      if (errors.current && e.keyCode === 27) {
        errors.dismiss();
      }
    };
    window.addEventListener("keyup", listener);

    return () => { window.removeEventListener("keyup", listener)};
  }, []);

  return (
      <div className={classnames("AppError", { visible: errorElement !== null })}>
        <AppErrorContent>{errorElement}</AppErrorContent>
      </div>
  );
});

export const App = () => {
  return (
      <Router>
        <Switch>
          <Route path="/" component={AppInternal} />
        </Switch>
      </Router>
  );
};