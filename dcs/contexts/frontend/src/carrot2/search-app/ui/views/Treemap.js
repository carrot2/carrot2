import PropTypes from 'prop-types';
import React, { useEffect, useState } from 'react';

import { FoamTree } from "../../../../carrotsearch/foamtree/FoamTree.js";
import { useDataObject, useSelection } from "./visualization-hooks.js";

const darkThemeOptions = {
  groupStrokePlainLightnessShift: 15,
  groupHoverStrokeLightnessShift: 20,
  groupSelectionOutlineColor: "rgba(255, 255, 255, 0.6)",
  attributionTheme: "dark",
  groupColorDecorator: function(opts, props, vars) {
    vars.groupColor.s = 60;
    vars.groupColor.l = 25;
    if (!props.hasChildren && !props.selected && !props.hovered) {
      vars.groupColor.a = 0.95;
    }
    if (props.group.rank) {
      vars.groupColor.l = 10 + 40 * props.group.rank
    }
    vars.labelColor = "white";
  }
};

const lightThemeOptions = {
  groupStrokePlainLightnessShift: -15,
  groupHoverStrokeLightnessShift: -20,
  attributionTheme: "light",
  groupSelectionOutlineColor: "rgba(0, 0, 0, 0.6)",
  groupColorDecorator: function(opts, props, vars) {
    vars.groupColor.s = 50;
    vars.groupColor.l = 55;
    if (!props.hasChildren && !props.selected && !props.hovered) {
      vars.groupColor.a = 0.95;
    }
    if (props.group.rank) {
      vars.groupColor.l = 35 + 40 * props.group.rank
    }
    vars.labelColor = "black";
  }
};

function buildOptions(theme, clusterSelectionStore, documentSelectionStore) {
  return {
    rolloutDuration: 0,
    pullbackDuration: 0,
    groupLabelFontFamily: "Cabin Condensed, sans-serif",
    wireframeDrawMaxDuration: 1000,
    finalCompleteDrawMaxDuration: 20000,
    finalIncrementalDrawMaxDuration: 20000,
    wireframeLabelDrawing: "always",
    groupFillType: "plain",
    groupStrokeWidth: 1,
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

export const Treemap = props => {
  // Without this dummy read, clusters don't get proxy wrappers, weird...
  props.clusterStore.clusters.forEach(function touch(c) {
    c.clusters && c.clusters.forEach(touch);
  });
  props.clusterStore.documents.forEach(ignored => {});

  const [ dataObject ] = useDataObject(props.clusterStore);
  const [ selection ] = useSelection(props.clusterSelectionStore,
    props.documentSelectionStore, dataObject);

  const [ options, setOptions ] = useState({});
  useEffect(() => {
    setOptions(buildOptions(props.themeStore.theme, props.clusterSelectionStore, props.documentSelectionStore));
  }, [ props.themeStore.theme, props.clusterSelectionStore, props.documentSelectionStore ]);

  return (
    <FoamTree options={options} dataObject={dataObject} selection={selection} />
  );
};

Treemap.propTypes = {
  clusterStore: PropTypes.object.isRequired
};