import React from "react";

import "./AppWithSidePanel.css";

import classnames from "classnames";

import { view } from "@risingstack/react-easy-state";
import { Button } from "@blueprintjs/core";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";

const AppSide = ({ logo, button, sideFixed, sideMain }) => {
  return (
    <div className="AppSide">
      <div className="AppSideFixed">
        <div className="AppSideHeader">
          {logo}
          {button}
        </div>

        {sideFixed}
      </div>

      {sideMain}
    </div>
  );
};

const AppMain = view(({ welcome, main, isInitial, stats, globalActions }) => {
  if (isInitial) {
    return <div className="AppMain AppWelcome">{welcome}</div>;
  }

  return (
    <div className="AppMain">
      <div className="stats">
        {stats}
        {globalActions}
      </div>

      {main}
    </div>
  );
});

export const AppMainButton = ({ icon, children, ...props }) => {
  return (
    <Button
      className="AppMainButton"
      large={true}
      icon={<FontAwesomeIcon icon={icon} />}
      {...props}
    >
      {children}
    </Button>
  );
};

export const AppWithSidePanel = ({
  className,
  welcome,
  logo,
  button,
  sideFixed,
  sideMain,
  stats,
  globalActions,
  main,
  isInitial
}) => {
  return (
    <div className={classnames("AppWithSidePanel", className)}>
      <AppSide
        logo={logo}
        button={button}
        sideFixed={sideFixed}
        sideMain={sideMain}
      />
      <AppMain
        welcome={welcome}
        main={main}
        isInitial={isInitial}
        stats={stats}
        globalActions={globalActions}
      />
    </div>
  );
};
