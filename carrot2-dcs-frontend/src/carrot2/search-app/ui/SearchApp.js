import React from 'react';
import { view } from 'react-easy-state';
import { HashRouter as Router, Redirect, Route, Switch } from "react-router-dom";
import { persistentStore } from "../../util/persistent-store";

import './SearchApp.css';

import { routes } from "../routes";
import { ResultsScreen } from "./ResultsScreen";
import { StartScreen } from "./StartScreen";
import { ThemeSwitch } from "./ThemeSwitch";

export const uiConfig = persistentStore("uiConfig",
  {
    theme: "dark"
  },
  {
    flipTheme: () => uiConfig.theme = uiConfig.isDarkTheme() ? "light" : "dark",
    isDarkTheme: () => uiConfig.theme === "dark"
  });

function ThemeContainer(props) {
  return (
    <div className={"SearchApp" + (uiConfig.isDarkTheme() ? " bp3-dark" : "")}>
      <header className="top">
        <ThemeSwitch theme={uiConfig.theme} onThemeFlip={uiConfig.flipTheme} />
      </header>
      {props.children}
    </div>
  )
}
const ThemeContainerView = view(ThemeContainer);

function SearchApp() {
  return (
    <ThemeContainerView>
      <Router>
        <Switch>
          <Redirect from='/search/' to='/' exact />
          <Route exact path={routes._root.path} component={StartScreen} />
          <Route path={routes.search.path} component={ResultsScreen} />
        </Switch>
      </Router>
    </ThemeContainerView>
  );
}

export default SearchApp;
