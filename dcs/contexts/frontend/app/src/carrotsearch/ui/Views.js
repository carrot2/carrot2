import React, { useEffect, useState } from "react";
import PropTypes from "prop-types";

import classNames from "classnames";

import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  Button,
  Classes,
  ControlGroup,
  Popover,
  Position,
  Tab
} from "@blueprintjs/core";
import { PointedTabs } from "./PointedTabs.js";

import "./Views.css";

const ShowHide = props => {
  return (
    <span
      className={props.className}
      title={props.title}
      style={props.visible ? {} : { display: "none" }}
    >
      {props.children}
    </span>
  );
};

export const Tool = ({ tool, visible, props }) => {
  if (tool.icon) {
    return (
      <Popover
        className={Classes.FIXED}
        position={Position.BOTTOM_RIGHT}
        autoFocus={true}
        popoverClassName="view-tool-content"
        disabled={!visible}
        boundary="viewport"
      >
        <ShowHide visible={visible} className="view-tool-trigger">
          <Button
            icon={<FontAwesomeIcon icon={tool.icon} />}
            minimal={true}
            title={tool.title}
          />
        </ShowHide>
        {tool.createContentElement(props)}
      </Popover>
    );
  } else {
    return (
      <ShowHide
        visible={visible}
        className={Classes.FIXED + " view-tool-trigger"}
        title={tool.title}
      >
        {tool.createContentElement()}
      </ShowHide>
    );
  }
};

const Switch = props => {
  const [initialized, setInitialized] = useState(false);
  useEffect(() => {
    if (props.visible) {
      setInitialized(true);
    }
  }, [props.visible]);

  if (!initialized) {
    return null;
  }

  return (
    <div
      style={{
        display: props.visible ? "block" : "none",
        position: "relative"
      }}
    >
      {props.createElement(props.visible)}
    </div>
  );
};

Switch.propTypes = {
  visible: PropTypes.bool.isRequired,
  createElement: PropTypes.func.isRequired
};

export const Views = ({
  views,
  className,
  activeView,
  onViewChange,
  children,
  ...props
}) => {
  // Flatten views from all groups into a single object.
  const allViews = views.reduce((map, group) => {
    Object.assign(map, group.views);
    return map;
  }, {});

  const viewKeys = Object.keys(allViews);
  return (
    <div className={classNames("Views", className)}>
      <ControlGroup className="ViewsTabs" fill={true} vertical={false}>
        <PointedTabs selectedTabId={activeView} onChange={onViewChange}>
          {views.map(group => {
            const components = [];
            if (group.label) {
              components.push(
                <div className="TabGroupLabel" key={group.label}>
                  {group.label}
                </div>
              );
            }

            return components.concat(
              Object.keys(group.views).map(v => {
                return <Tab key={v} id={v} title={allViews[v].label} />;
              })
            );
          })}
        </PointedTabs>
        {viewKeys
          .filter(v => allViews[v].tools && allViews[v].tools.length > 0)
          .reduce(function (tools, v) {
            allViews[v].tools.forEach(t => {
              const key = v + "." + t.id;
              tools.push(
                <Tool
                  key={key}
                  tool={t}
                  visible={activeView === v}
                  props={props}
                />
              );
            });
            return tools;
          }, [])}
      </ControlGroup>
      <div className="ViewsContent">
        {children}
        {viewKeys.map(v => {
          return (
            <Switch
              key={v}
              visible={v === activeView}
              createElement={allViews[v].createContentElement}
            />
          );
        })}
      </div>
    </div>
  );
};

Views.propTypes = {
  activeView: PropTypes.string.isRequired,
  views: PropTypes.array.isRequired,
  onViewChange: PropTypes.func.isRequired
};
