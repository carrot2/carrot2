import React, { useCallback } from "react";

import "./FileSetting.css";

import filesize from "filesize";
import classnames from "classnames";

import { view, store } from "@risingstack/react-easy-state";
import { useDropzone } from "react-dropzone";
import { Button } from "@blueprintjs/core";
import { Setting } from "./Setting.js";

export const FileSetting = view(({ setting, get, set }) => {
  const { label, description } = setting;
  const currentFile = store({ file: null });

  const onDrop = useCallback(
    acceptedFiles => {
      if (acceptedFiles.length > 0) {
        const file = acceptedFiles[0];
        currentFile.file = file;
        set(setting, file);
      }
    },
    [set, setting, currentFile]
  );
  const { getRootProps, getInputProps, isDragActive } = useDropzone({
    onDrop,
    multiple: false
  });

  const fileInfo = currentFile.file ? (
    <div className="FileSettingFileInfo">
      <span>{currentFile.file.name}</span>
      <span>{filesize(currentFile.file.size)}</span>
      <span>{currentFile.file.type}</span>
    </div>
  ) : null;

  return (
    <Setting className="FileSetting" label={label} description={description}>
      <div {...getRootProps()}>
        <input {...getInputProps()} />
        <div
          className={classnames("FileSettingDropZone", {
            FileSettingDropZoneActive: isDragActive
          })}
        >
          {fileInfo}
          <div>
            <Button small={true}>Browse</Button> or drag 'n' drop your file
            here.
          </div>
        </div>
      </div>
    </Setting>
  );
});
