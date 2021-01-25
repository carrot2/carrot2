import React, { useEffect, useState } from "react";

import { themeStore } from "@carrotsearch/ui/ThemeSwitch.js";
import { FoamTree } from "@carrotsearch/ui/visualizations/FoamTree.js";

import { clusterStore } from "../../store/services.js";
import {
  clusterSelectionStore,
  documentSelectionStore
} from "../../store/selection.js";
import { useDataObject, useSelection } from "./visualization-hooks.js";

const darkThemeOptions = {
  groupStrokePlainLightnessShift: 15,
  groupHoverStrokeLightnessShift: 20,
  groupSelectionOutlineColor: "rgba(255, 255, 255, 0.6)",
  attributionTheme: "dark",
  groupColorDecorator: function (opts, props, vars) {
    if (props.level === 0) {
      vars.groupColor.s =
        props.group.cluster && props.group.cluster.unclustered ? 0 : 60;
    }
    vars.groupColor.l = 25;
    if (!props.hasChildren && !props.selected && !props.hovered) {
      vars.groupColor.a = 0.95;
    }
    if (props.group.rank) {
      vars.groupColor.l = 10 + 40 * props.group.rank;
    }
    vars.labelColor = "white";
  }
};

const lightThemeOptions = {
  groupStrokePlainLightnessShift: -15,
  groupHoverStrokeLightnessShift: -20,
  attributionTheme: "light",
  groupSelectionOutlineColor: "rgba(0, 0, 0, 0.6)",
  groupColorDecorator: function (opts, props, vars) {
    if (props.level === 0) {
      vars.groupColor.s =
        props.group.cluster && props.group.cluster.unclustered ? 0 : 60;
    }
    vars.groupColor.l = 55;
    if (!props.hasChildren && !props.selected && !props.hovered) {
      vars.groupColor.a = 0.95;
    }
    if (props.group.rank) {
      vars.groupColor.l = 35 + 40 * props.group.rank;
    }
    vars.labelColor = "black";
  }
};

function buildOptions(
  theme,
  layout,
  stacking,
  clusterSelectionStore,
  documentSelectionStore
) {
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
      clusterSelectionStore.replaceSelection(
        e.groups.filter(g => !!g.cluster).map(g => g.cluster)
      );
      documentSelectionStore.replaceSelection(
        e.groups.filter(g => !!g.document).map(g => g.document)
      );
    },

    groupLabelDecorator: (opts, props, vars) => {
      if (vars.labelText && vars.labelText.length > 80) {
        vars.labelText = vars.labelText.substring(0, 100) + "...";
      }
    },

    titleBarDecorator: (opts, props, vars) => {
      if (vars.titleBarText && vars.titleBarText.length > 100) {
        vars.titleBarText = props.group.label;
        vars.titleBarShown = true;
      }
    },
    maxLabelSizeForTitleBar: 12,

    ...getThemeOptions()
  };

  function getThemeOptions() {
    return theme === "dark" ? darkThemeOptions : lightThemeOptions;
  }
}

export const Treemap = ({ visible, configStore, implRef }) => {
  // Without this dummy read, clusters don't get proxy wrappers, weird...
  clusterStore.clusters.forEach(function touch(c) {
    c.clusters && c.clusters.forEach(touch);
  });
  clusterStore.documents.forEach(ignored => {});

  const [dataObject] = useDataObject(
    clusterStore.clusters,
    clusterStore.documents,
    visible,
    configStore.includeResults
  );
  const [selection] = useSelection(
    clusterSelectionStore,
    documentSelectionStore,
    dataObject
  );

  const [options, setOptions] = useState({});
  const theme = themeStore.theme;
  useEffect(() => {
    setOptions(
      buildOptions(
        theme,
        configStore.layout,
        configStore.stacking,
        clusterSelectionStore,
        documentSelectionStore
      )
    );
  }, [theme, configStore.layout, configStore.stacking]);

  const noClustersMessage =
    clusterStore.clusters.length === 0 ? <div>No clusters to show</div> : null;
  return (
    <>
      {noClustersMessage}
      <FoamTree
        implRef={implRef}
        options={options}
        dataObject={dataObject}
        selection={selection}
        fontFamily="Cabin Condensed"
      />
    </>
  );
};

Treemap.propTypes = {};
