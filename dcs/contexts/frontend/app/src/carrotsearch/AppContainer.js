import React, { useEffect, useState } from "react";

import "./AppContainer.css";

import classnames from "classnames";

import { view } from "@risingstack/react-easy-state";

import {
  HashRouter as Router,
  Link,
  NavLink,
  Redirect,
  Route,
  Switch,
  useLocation,
  useRouteMatch
} from "react-router-dom";

import {
  Button,
  Popover,
  PopoverInteractionKind,
  PopoverPosition
} from "@blueprintjs/core";

import { faFrown } from "@fortawesome/pro-regular-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";

import { errors } from "./store/errors.js";
import { ThemeSwitch } from "./ThemeSwitch.js";

const AppLink = ({ to, title, children, icon }) => {
  const [open, setOpen] = useState(false);
  const match = useRouteMatch(to);
  return (
    <NavLink
      onClick={() => setOpen(false)}
      className="NavLink AppLink"
      to={to}
      activeClassName="active"
    >
      <NavLinkContent
        icon={icon}
        title={title}
        open={open}
        onTooltipInteraction={visible => setOpen(visible && !match)}
      >
        {children}
      </NavLinkContent>
    </NavLink>
  );
};

const NavExternalLink = ({ href, title, icon, children }) => {
  const [open, setOpen] = useState(false);

  return (
    <a
      className="NavLink"
      href={href}
      target="_blank"
      rel="noopener noreferrer"
    >
      <NavLinkContent
        icon={icon}
        title={title}
        open={open}
        onTooltipInteraction={visible => setOpen(visible)}
      >
        {children}
      </NavLinkContent>
    </a>
  );
};

const NavLinkContent = ({
  icon,
  title,
  children,
  open,
  onTooltipInteraction
}) => {
  const handlePopoverInteraction = nextOpenState => {
    onTooltipInteraction(nextOpenState);
  };

  return (
    <Popover
      popoverClassName="NavPopover"
      position={PopoverPosition.RIGHT}
      interactionKind={PopoverInteractionKind.HOVER}
      onInteraction={handlePopoverInteraction}
      hoverOpenDelay={450}
      isOpen={open}
    >
      <FontAwesomeIcon icon={icon} size="2x" />
      <div className="NavPopoverContent">
        <h3>{title}</h3>
        {children}
      </div>
    </Popover>
  );
};

export const AppContainerInternal = ({ logo, apps, extras, containerClassName, children }) => {
  const allApps = [...apps?.props?.children, ...extras?.props?.children].filter(
    e => !!e && !!e.props.component
  );
  const defaultApp = allApps.find(a => a.props.default) || allApps[0];

  const location = useLocation();
  const className = containerClassName?.(location);

  return (
    <div className={classnames("AppContainer", className)}>
      {children}

      <nav>
        <Link to="/">{logo}</Link>

        {apps}

        <div className="NavExtras">
          {extras}
          <ThemeSwitch />
        </div>
      </nav>

      <main>
        <Router>
          <Switch>
            <Redirect from="/" to={defaultApp.props.path} exact />
            {allApps.map(app => {
              return (
                <Route
                  key={app.props.path}
                  path={app.props.path}
                  component={app.props.component}
                />
              );
            })}
            <Redirect to={defaultApp.props.path} />
          </Switch>
        </Router>
        <AppError />
      </main>
    </div>
  );
};

export const App = ({ path, component, icon, title, children }) => {
  return component ? (
    <AppLink to={path} title={title} icon={icon}>
      {children}
    </AppLink>
  ) : (
    <NavExternalLink href={path} icon={icon} title={title}>
      {children}
    </NavExternalLink>
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
        <Button outlined={false} onClick={() => errors.dismiss()}>
          Dismiss
        </Button>
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

    return () => {
      window.removeEventListener("keyup", listener);
    };
  }, []);

  return (
    <div className={classnames("AppError", { visible: errorElement !== null })}>
      <AppErrorContent>{errorElement}</AppErrorContent>
    </div>
  );
});

export const AppContainer = ({ containerClassName, children }) => {
  const logo = children[0];
  const apps = children[1];
  const extras = children[2];
  const more = children[3];

  return (
    <Router>
      <Switch>
        <Route path="/">
          <AppContainerInternal
            containerClassName={containerClassName}
            logo={logo}
            apps={apps}
            extras={extras}
          >
            {more}
          </AppContainerInternal>
        </Route>
      </Switch>
    </Router>
  );
};
