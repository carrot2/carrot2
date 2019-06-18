import React from "react";
import PropTypes from "prop-types";

import { Button, Classes, ControlGroup, Popover, Position, Tab, Tabs } from "@blueprintjs/core";

import "./Views.css";

const ShowHide = props => {
  return <span className={props.className} title={props.title}
               style={props.visible ? {} : {display: "none"}}>{props.children}</span>;
};

const createToolElement = (view, tool, visible, props) => {
  const key = view + "." + tool.id;
  if (tool.icon) {
    return (
      <Popover className={Classes.FIXED} position={Position.BOTTOM_RIGHT}
               autoFocus={true} popoverClassName="bp3-popover-content-sizing view-tool-content"
               disabled={!visible} key={key}>
        <ShowHide visible={visible} className="view-tool-trigger">
          <Button icon={tool.icon} minimal={true} title={tool.title} />
        </ShowHide>
        {tool.createContentElement(props)}
      </Popover>
    );
  } else {
    return <ShowHide key={key} visible={visible} className={Classes.FIXED + " view-tool-trigger"}
                     title={tool.title}>{tool.createContentElement()}</ShowHide>
  }
};

export const ViewTabs = (props) => {
  const views = props.views;
  return (
    <ControlGroup fill={true} vertical={false}>
      <Tabs id="views" className="views"
            selectedTabId={props.activeView} onChange={props.onViewChange}>
        {
          Object.keys(props.views).map(v => (
            <Tab key={v} id={v} title={props.views[v].label} />
          ))
        }
      </Tabs>
      {
        Object.keys(views)
          .filter(v => views[v].tools && views[v].tools.length > 0)
          .reduce(function (tools, v) {
            views[v].tools.forEach(t => {
              tools.push(createToolElement(v, t, props.activeView === v, props));
            });
            return tools;
          }, [])
      }
    </ControlGroup>
  );
};

ViewTabs.propTypes = {
  activeView: PropTypes.string.isRequired,
  views: PropTypes.object.isRequired,
  onViewChange: PropTypes.func.isRequired
};
