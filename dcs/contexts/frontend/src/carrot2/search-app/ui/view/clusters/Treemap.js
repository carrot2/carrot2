import PropTypes from 'prop-types';
import React, { useEffect, useState } from 'react';

import { FoamTree } from "../../../../../carrotsearch/foamtree/FoamTree.js";
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

function buildOptions(theme, layout, stacking, clusterSelectionStore, documentSelectionStore) {
  const flattened = stacking === "flattened";

  return {
    rolloutDuration: 0,
    pullbackDuration: 0,

    groupLabelFontFamily: "Cabin Condensed, sans-serif",
    wireframeDrawMaxDuration: 1000,
    finalCompleteDrawMaxDuration: 20000,
    finalIncrementalDrawMaxDuration: 20000,
    wireframeLabelDrawing: "always",


    layout: layout,
    stacking: stacking,
    groupLabelVerticalPadding: flattened ? 0.05 : 1.0,
    groupLabelMaxTotalHeight: flattened ? 1.0 : 0.9,
    descriptionGroupType: "stab",
    descriptionGroupSize: 0.2,
    descriptionGroupMinHeight: 40,
    descriptionGroupMaxHeight: 0.3,
    descriptionGroupPosition: "top",
    rectangleAspectRatioPreference: 0,

    groupBorderWidth: 2.5,
    groupInsetWidth: 5,
    groupBorderRadius: 0.1,
    groupBorderRadiusCorrection: 1,
    groupBorderWidthScaling: 0.6,

    groupFillType: "plain",
    groupStrokeWidth: 1,
    groupStrokeType: "plain",
    groupSelectionOutlineWidth: 3.5,

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

  const [ dataObject ] = useDataObject(props.clusterStore, props.visible);
  const [ selection ] = useSelection(props.clusterSelectionStore,
    props.documentSelectionStore, dataObject);

  const [ options, setOptions ] = useState({});
  useEffect(() => {
    setOptions(buildOptions(props.themeStore.theme, props.configStore.layout, props.configStore.stacking,
      props.clusterSelectionStore, props.documentSelectionStore));
  }, [ props.themeStore.theme, props.configStore.layout, props.configStore.stacking,
    props.clusterSelectionStore, props.documentSelectionStore ]);

  return (
    <FoamTree options={options} dataObject={dataObject} selection={selection} />
  );
};

Treemap.propTypes = {
  clusterStore: PropTypes.object.isRequired
};