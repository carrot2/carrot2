import PropTypes from "prop-types";
import React, { useEffect, useState } from "react";

import { themeStore } from "@carrotsearch/ui/ThemeSwitch.js";
import { Circles } from "@carrotsearch/ui/visualizations/Circles.js";

import { clusterStore } from "../../store/services.js";
import {
  clusterSelectionStore,
  documentSelectionStore
} from "../../store/selection.js";
import { useDataObject, useSelection } from "./visualization-hooks.js";

const darkThemeOptions = {
  groupOutlineColor: "rgba(255, 255, 255, 0.1)",
  groupSelectionOutlineColor: "rgba(255, 255, 255, 0.8)",
  expanderColor: "rgba(255, 255, 255, 0.15)",
  expanderOutlineColor: "rgba(255, 255, 255, 0.2)",
  groupColorDecorator: function (opts, props, vars) {
    if (props.level === 0) {
      vars.groupColor.s =
        props.group.cluster && props.group.cluster.unclustered ? 0 : 60;
    }
    vars.groupColor.l = 20;
    if (props.group.rank) {
      vars.groupColor.l = 10 + 40 * props.group.rank;
    }
    vars.labelColor = "rgba(255, 255, 255, 0.8)";
  }
};

const lightThemeOptions = {
  groupOutlineColor: "rgba(0, 0, 0, 0.1)",
  groupSelectionOutlineColor: "rgba(0, 0, 0, 0.8)",
  expanderColor: "rgba(0, 0, 0, 0.15)",
  expanderOutlineColor: "rgba(0, 0, 0, 0.2)",
  groupColorDecorator: function (opts, props, vars) {
    if (props.level === 0) {
      vars.groupColor.s =
        props.group.cluster && props.group.cluster.unclustered ? 0 : 60;
    }
    vars.groupColor.l = 70;
    if (props.group.rank) {
      vars.groupColor.l = 35 + 40 * props.group.rank;
    }
  }
};

function buildOptions(theme, clusterSelectionStore, documentSelectionStore) {
  return {
    pullbackTime: 0,
    groupFontFamily: "Cabin Condensed, sans-serif",
    rainbowStartColor: "hsla(0, 100%, 50%, 1.0)",
    rainbowEndColor: "hsla(300, 100%, 50%, 1.0)",
    angleStart: 180,
    onGroupSelectionChanged: function (e) {
      clusterSelectionStore.replaceSelection(
        e.groups.filter(g => !!g.cluster).map(g => g.cluster)
      );
      documentSelectionStore.replaceSelection(
        e.groups.filter(g => !!g.document).map(g => g.document)
      );
    },
    ...getThemeOptions()
  };

  function getThemeOptions() {
    return theme === "dark" ? darkThemeOptions : lightThemeOptions;
  }
}

export const PieChart = ({ visible, configStore, implRef }) => {
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
      buildOptions(theme, clusterSelectionStore, documentSelectionStore)
    );
  }, [theme]);

  const noClustersMessage =
    clusterStore.clusters.length === 0 ? <div>No clusters to show</div> : null;
  return (
    <>
      {noClustersMessage}
      <Circles
        implRef={implRef}
        options={options}
        dataObject={dataObject}
        selection={selection}
        fontFamily="Cabin Condensed"
      />
    </>
  );
};

PieChart.propTypes = {
  configStore: PropTypes.object.isRequired
};
