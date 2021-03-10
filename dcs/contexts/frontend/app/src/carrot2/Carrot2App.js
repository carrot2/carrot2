import React from "react";

import "./Carrot2App.css";

import { matchPath, Route, Switch } from "react-router-dom";

import { faFlask, faInfo, faSearch } from "@fortawesome/pro-regular-svg-icons";

import { routes } from "./routes.js";

import { AppContainer, App } from "@carrotsearch/ui/AppContainer.js";
import { Backdrop } from "@carrotsearch/ui/backdrop/Backdrop.js";

import { SearchApp } from "./apps/search-app/ui/SearchApp.js";
import { WorkbenchApp } from "./apps/workbench/ui/WorkbenchApp.js";
import { AboutApp } from "./apps/about/AboutApp.js";
import { branding } from "@carrot2/config/branding.js";

export const Carrot2App = () => {
  const className = location => {
    return matchPath(location.pathname, {
      path: routes.searchStart.path,
      exact: true
    })
      ? "WithBackdrop"
      : null;
  };

  return (
    <AppContainer containerClassName={className}>
      {branding.createAppLogo()}

      <>
        <App
          path={routes.search.path}
          title="Web search clustering"
          icon={faSearch}
          component={SearchApp}
          default={true}
        >
          <p>Clustering of search results from public search engines.</p>
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
        {branding.createAppInfoNavLink()}
      </>

      <Switch>
        <Route exact path={routes.searchStart.path} component={Backdrop} />
        <Route exact path={routes.about.path} component={Backdrop} />
      </Switch>
    </AppContainer>
  );
};
