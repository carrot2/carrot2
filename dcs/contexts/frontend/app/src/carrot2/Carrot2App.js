import React from "react";

import "./App.css";

import { faFlask, faInfo, faSearch } from "@fortawesome/pro-regular-svg-icons";
import { faGithub } from "@fortawesome/free-brands-svg-icons";

import { SearchApp } from "./apps/search-app/ui/SearchApp.js";
import { routes } from "./routes.js";
import { WorkbenchApp } from "./apps/workbench/ui/WorkbenchApp.js";
import { CarrotLogo } from "../carrotsearch/logo/CarrotLogo.js";
import { AppContainer, App } from "../carrotsearch/AppContainer.js";
import { AboutApp } from "./apps/about/AboutApp.js";
import { matchPath, Route, Switch } from "react-router-dom";
import { Backdrop } from "../carrotsearch/backdrop/Backdrop.js";

export const Carrot2App = () => {
  const className = location => {
    return matchPath(location.pathname, {
      path: routes.searchStart.path,
      exact: true
    }) ? "WithBackdrop" : null;
  };

  return (
    <AppContainer containerClassName={className}>
      <CarrotLogo className="dark" />

      <>
        <App
          path={routes.search.path}
          title="Web search clustering"
          icon={faSearch}
          component={SearchApp}
          default={true}
        >
          <p>Clustering of search results public search engines.</p>
        </App>

        <App
          path={routes.workbench.path}
          title="Clustering workbench"
          icon={faFlask}
          component={WorkbenchApp}
        >
          <ul className="WorkbenchLinkPopover">
            <li>clustering data from files, Solr, Elasticsearch</li>
            <li>experimenting with clustering parameters</li>
            <li>exporting results to Excel/OpenOffice</li>
          </ul>
        </App>
      </>

      <>
        <App
          path={routes.about.path}
          title="About this application"
          icon={faInfo}
          component={AboutApp}
        />
        <App
          icon={faGithub}
          title="Source code on GitHub"
          path="https://github.com/carrot2/carrot2"
        />
      </>

      <Switch>
        <Route exact path={routes.searchStart.path} component={Backdrop} />
        <Route exact path={routes.about.path} component={Backdrop} />
      </Switch>
    </AppContainer>
  );
};
