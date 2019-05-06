import React from 'react';
import PropTypes from 'prop-types';
import { FoamTree } from "../../../../carrotsearch/foamtree/FoamTree.js";

const darkThemeOptions = {
  groupFillType: "plain",
  groupColorDecorator: function(opts, props, vars) {
    vars.groupColor.s = 50;
    vars.groupColor.l = 25;
    vars.labelColor = "white";
  },
  groupLabelFontFamily: "Lato, sans-serif"
};

export const Treemap = props => {
  const groups = props.store.clusters.map(c => {
    return {
      label: `${c.labels.join(", ")} (${c.size})`,
      weight: c.size,
      groups: c.documents.map(d => {
        return {
          label: props.searchResultStore.searchResult.documents[d].title
        }
      })
    }
  });

  return (
    <FoamTree options={{
      rolloutDuration: 0,
      pullbackDuration: 0,
      ...darkThemeOptions,
      dataObject: {
        groups: groups
      }
    }} />
  );
};

Treemap.propTypes = {

};