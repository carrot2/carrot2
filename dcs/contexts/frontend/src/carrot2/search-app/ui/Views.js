import React from "react";
import PropTypes from "prop-types";

import { Button, Classes, ControlGroup, Popover, Position, Tab, Tabs } from "@blueprintjs/core";

const createToolElement = (view, tool, visible) => {
  if (tool.icon) {
    return (
      <Popover className={Classes.FIXED} position={Position.BOTTOM_RIGHT}
               autoFocus={true} popoverClassName="bp3-popover-content-sizing"
               disabled={!visible} key={view + "." + tool.id}>
        <span style={visible ? {} : {display: "none"}}><Button icon={tool.icon} minimal={true} /></span>
        {tool.createContentElement()}
      </Popover>
    );
  } else {
    return tool.createContentElement();
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
              tools.push(createToolElement(v, t, props.activeView === v));
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
