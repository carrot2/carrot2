import PropTypes from 'prop-types';
import React, { useState, useEffect } from 'react';

import { Circles } from "../../../../../carrotsearch/circles/Circles.js";
import { useDataObject, useSelection } from "./visualization-hooks.js";

const darkThemeOptions = {
  groupOutlineColor: "rgba(255, 255, 255, 0.1)",
  groupSelectionOutlineColor: "rgba(255, 255, 255, 0.8)",
  expanderColor: "rgba(255, 255, 255, 0.15)",
  expanderOutlineColor: "rgba(255, 255, 255, 0.2)",
  groupColorDecorator: function(opts, props, vars) {
    if (props.level === 0) {
      vars.groupColor.s = props.group.cluster && props.group.cluster.unclustered ? 0 : 60;
    }
    vars.groupColor.l = 20;
    if (props.group.rank) {
      vars.groupColor.l = 10 + 40 * props.group.rank
    }
    vars.labelColor = "rgba(255, 255, 255, 0.8)";
  }
};

const lightThemeOptions = {
  groupOutlineColor: "rgba(0, 0, 0, 0.1)",
  groupSelectionOutlineColor: "rgba(0, 0, 0, 0.8)",
  expanderColor: "rgba(0, 0, 0, 0.15)",
  expanderOutlineColor: "rgba(0, 0, 0, 0.2)",
  groupColorDecorator: function(opts, props, vars) {
    if (props.level === 0) {
      vars.groupColor.s = props.group.cluster && props.group.cluster.unclustered ? 0 : 60;
    }
    vars.groupColor.l = 70;
    if (props.group.rank) {
      vars.groupColor.l = 35 + 40 * props.group.rank
    }
  }
};

function buildOptions(theme, clusterSelectionStore, documentSelectionStore) {
  return {
    groupFontFamily: "Cabin Condensed, sans-serif",
    rainbowStartColor: "hsla(0, 100%, 50%, 1.0)",
    rainbowEndColor: "hsla(300, 100%, 50%, 1.0)",
    angleStart: 180,
    onGroupSelectionChanged: function (e) {
      clusterSelectionStore.replaceSelection(e.groups.filter(g => !!g.cluster).map(g => g.cluster));
      documentSelectionStore.replaceSelection(e.groups.filter(g => !!g.document).map(g => g.document));
    },
    ...getThemeOptions()
  };

  function getThemeOptions() {
    return theme === "dark" ? darkThemeOptions : lightThemeOptions;
  }
}


export const PieChart = props => {
  // Without this dummy read, clusters don't get proxy wrappers, weird...
  props.clusterStore.clusters.forEach(function touch(c) {
    c.clusters && c.clusters.forEach(touch);
  });
  props.clusterStore.documents.forEach(ignored => {});

  const [ dataObject ] = useDataObject(props.clusterStore, props.visible, props.configStore.includeResults);
  const [ selection ] = useSelection(props.clusterSelectionStore,
    props.documentSelectionStore, dataObject);

  const [ options, setOptions ] = useState({});
  useEffect(() => {
    setOptions(buildOptions(props.themeStore.theme, props.clusterSelectionStore, props.documentSelectionStore));
  }, [ props.themeStore.theme, props.clusterSelectionStore, props.documentSelectionStore ]);

  return (
    <Circles options={options} dataObject={dataObject} selection={selection} />
  );
};

PieChart.propTypes = {
  clusterStore: PropTypes.object.isRequired,
  configStore: PropTypes.object.isRequired
};