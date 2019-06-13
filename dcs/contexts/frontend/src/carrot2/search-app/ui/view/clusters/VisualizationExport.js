import { Button } from "@blueprintjs/core";

import { saveAs } from "file-saver";
import React from "react";

import { searchResultStore } from "../../../store/services.js";

const save = (impl, fileNameSuffix) => {
  if (impl) {
    const type = "image/jpeg";

    // Use the actual background color for the exported bitmap
    const style = window.getComputedStyle(impl.get("element").parentElement.parentElement);

    const base64 = impl.get("imageData", {
      format: type,
      pixelRatio: 2,
      backgroundColor: style.backgroundColor
    });

    // A neat trick to convert a base64 string to a binary array.
    fetch("data:" + type + ";" + base64)
      .then(result => result.blob())
      .then(blob => {
        const queryCleaned = searchResultStore.searchResult.query
          .replace(/[\s:]+/g, "_")
          .replace(/[+-\\"'/\\?]+/g, "");
        const source = searchResultStore.source;

        saveAs(blob, `${source}-${queryCleaned}-${fileNameSuffix}.jpg`);
      });
  }
};

export const VisualizationExport = props => {
  return (
    <Button icon="floppy-disk" minimal={true} 
            onClick={() => save(props.implRef.current, props.fileNameSuffix)} />
  );
};