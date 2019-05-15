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
    if (!props.hasChildren && !props.selected && !props.exposed) {
      vars.groupColor.a = 0.7;
    }
    if (props.group.rank) {
      vars.groupColor.l = 35 + 40 * props.group.rank
    }
    vars.labelColor = "black";
  }
};

export const Treemap = props => {
  const [ dataObject, setDataObject ] = useState({});
  useEffect(() => {
    const fn = () => {
      const clusters = props.clusterStore.clusters;
      const documents = props.clusterStore.documents;
      setDataObject({
        groups: clusters.map(function clusters(c) {
          return {
            cluster: c,
            label: `${c.labels.join(", ")} (${c.size})`,
            weight: c.size,
            groups: c.documents.map(d => {
              let document = documents[d];
              return {
                document: document,
                label: document.title,
                rank: document.rank
              }
            }).concat((c.clusters || []).map(clusters))
          }
        })
      });
    };

    // Run fn every time the values it observes change
    observe(fn);

    // When component unmounts, remove observer.
    return () => { unobserve(fn); };
  }, [ props.clusterStore ]);

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
      onGroupSelectionChanged: function (e) {
        props.clusterSelectionStore.replaceSelection(e.groups.filter(g => !!g.cluster).map(g => g.cluster));
        props.documentSelectionStore.replaceSelection(e.groups.filter(g => !!g.document).map(g => g.document));
      },
      ...themeOptions
    }} dataObject={dataObject} />
  );
};

Treemap.propTypes = {
  clusterStore: PropTypes.object.isRequired
};