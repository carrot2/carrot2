import React from "react";

import { saveAs } from "file-saver";

import { Button } from "@blueprintjs/core";

import { VscSave } from "react-icons/vsc";

import { buildFileName } from "../../store/services.js";

function saveJpeg(impl, fileNameSuffix) {
  const type = "image/jpeg";

  // Use the actual background color for the exported bitmap
  const style = window.getComputedStyle(
    impl.get("element").parentElement.parentElement
  );

  const base64 = impl.get("imageData", {
    format: type,
    pixelRatio: 2,
    backgroundColor: style.backgroundColor
  });

  // A neat trick to convert a base64 string to a binary array.
  fetch("data:" + type + ";" + base64)
    .then(result => result.blob())
    .then(blob => {
      saveAs(blob, buildFileName(fileNameSuffix, `jpg`));
    });
}

function saveJson(impl, fileNameSuffix) {
  const type = "application/json";

  const data = Object.assign({}, impl.get());
  delete data.element;

  saveAs(
    new Blob([JSON.stringify(data)], { type: type }),
    buildFileName(fileNameSuffix, `json`)
  );
}

const save = (impl, fileNameSuffix, type) => {
  if (impl) {
    switch (type) {
      case "jpeg":
      default:
        saveJpeg(impl, fileNameSuffix);
        break;

      case "json":
        saveJson(impl, fileNameSuffix);
        break;
    }
  }
};

export const VisualizationExport = props => {
  return (
    <Button
      icon={<VscSave />}
      minimal={true}
      onClick={e =>
        save(
          props.implRef.current,
          props.fileNameSuffix,
          e.shiftKey ? "json" : "jpeg"
        )
      }
    />
  );
};
