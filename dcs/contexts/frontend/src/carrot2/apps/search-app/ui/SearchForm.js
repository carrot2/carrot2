import React, { useRef, useState, useEffect } from "react";
import * as PropTypes from "prop-types";

import './SearchForm.css';

import { Button, Classes, ControlGroup, InputGroup, Popover, Position } from "@blueprintjs/core";
import { algorithms } from "../../../config-algorithms.js";
import { ClusteringEngineSettings } from "./ClusteringEngineSettings.js";

import { SearchEngineSettings } from "./SearchEngineSettings.js";

import { SourceTabs } from "./SourceTabs";

export const SearchForm = ({ initialQuery, source, onSourceChange, onSubmit }) => {
  const inputRef = useRef(null);
  const [ query, setQuery ] = useState(initialQuery || "");

  const focus = () => {
    if (inputRef.current) {
      inputRef.current.focus();
    }
  };

  useEffect(focus, []);

  const submit = e => {
    e.preventDefault();
    triggerOnSubmit();
  };

  const triggerOnSubmit = () => {
    const trimmed = query.trim();
    if (trimmed.length > 0) {
      onSubmit(trimmed);
    }
  };

  const changeSource = (newSource, oldSource, e) => {
    e.preventDefault();
    focus();
    onSourceChange && onSourceChange(newSource);
  };


  const searchEngineSettings = (
    <Popover position={Position.BOTTOM} className={Classes.FIXED}
             popoverClassName="bp3-popover-content-sizing SearchAppSettingsContainer">
      <Button rightIcon="caret-down" text="options" minimal={true} title="Search engine options" />
      <SearchEngineSettings source={source} onApply={triggerOnSubmit} />
    </Popover>
  );

  const clusteringEngineSettings = Object.keys(algorithms).length < 2 ? null : (
    <Popover position={Position.RIGHT_TOP} className={Classes.FIXED}
             popoverClassName="bp3-popover-content-sizing SearchAppSettingsContainer">
      <Button icon="wrench" minimal={true} title="Clustering algorithm" />
      <ClusteringEngineSettings/>
    </Popover>
  );

  return (
    <div className="SearchForm">
      <SourceTabs active={source} onChange={changeSource} />

      <form onSubmit={submit}>
        <ControlGroup fill={true}>
          <InputGroup inputRef={inputRef} rightElement={searchEngineSettings}
                      defaultValue={initialQuery} onChange={e => setQuery(e.target.value)} />
          <Button className={Classes.FIXED} icon="search" type="submit" text="Search" />
          { clusteringEngineSettings }
        </ControlGroup>
      </form>
    </div>
  );
};

SearchForm.propTypes = {
  onSubmit: PropTypes.func.isRequired,
  onSourceChange: PropTypes.func.isRequired,
  initialQuery: PropTypes.string
};