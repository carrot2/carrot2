import React, { useEffect, useState } from 'react';
import PropTypes from 'prop-types';
import { observe, unobserve } from '@nx-js/observer-util';

import { FoamTree } from "../../../../carrotsearch/foamtree/FoamTree.js";

const darkThemeOptions = {
  groupStrokePlainLightnessShift: 15,
  groupHoverStrokeLightnessShift: 20,
  groupSelectionOutlineColor: "rgba(255, 255, 255, 0.6)",
  attributionTheme: "dark",
  groupColorDecorator: function(opts, props, vars) {
    vars.groupColor.s = 60;
    vars.groupColor.l = 25;
    if (!props.hasChildren && !props.selected && !props.exposed) {
      vars.groupColor.a = 0.7;
    }

    if (props.group.rank) {
      vars.l = props.group.rank * 100
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
    if (!props.hasChildren && !props.selected && !props.exposed) {
      vars.groupColor.a = 0.7;
    }
    vars.labelColor = "black";
  }
};

export const Treemap = props => {
  const [ dataObject, setDataObject ] = useState({});
  useEffect(() => {
    const fn = () => {
      const clusters = props.clusterStore.clusters;
      const searchResult = props.searchResultStore.searchResult;
      setDataObject({
        groups: clusters.map(function clusters(c) {
          return {
            label: `${c.labels.join(", ")} (${c.size})`,
            weight: c.size,
            groups: c.documents.map(d => {
              return {
                label: searchResult.documents[d].title,
                rank: d.rank
              }
            }).concat((c.subclusters || []).map(clusters))
          }
        })
      })
    };

    // Run fn every time the values it observes change
    observe(fn);

    // When component unmounts, remove observer.
    return () => { unobserve(fn); };
  }, [ props.clusterStore, props.searchResultStore.searchResult ]);

  const themeOptions = props.themeStore.theme === "dark" ? darkThemeOptions : lightThemeOptions;

  return (
    <FoamTree options={{
      rolloutDuration: 0,
      pullbackDuration: 0,
      groupLabelFontFamily: "Lato, sans-serif",
      wireframeDrawMaxDuration: 1000,
      finalCompleteDrawMaxDuration: 20000,
      finalIncrementalDrawMaxDuration: 20000,
      wireframeLabelDrawing: "always",
      groupFillType: "plain",
      groupStrokeWidth: 1,
      ...themeOptions
    }} dataObject={dataObject} />
  );
};

Treemap.propTypes = {
  clusterStore: PropTypes.object.isRequired
};