import { Button, Classes, ControlGroup, InputGroup, Popover, Position } from "@blueprintjs/core";
import * as PropTypes from "prop-types";
import React, { useRef, useState, useEffect } from "react";

import { algorithms } from "../../config-algorithms.js";
import { ClusteringEngineSettings } from "./ClusteringEngineSettings.js";

import { SearchEngineSettings } from "./SearchEngineSettings.js";

import './SearchForm.css';
import { SourceTabs } from "./SourceTabs";

export const SearchForm = props => {
  const inputRef = useRef(null);
  const [ query, setQuery ] = useState(props.initialQuery || "");

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
      props.onSubmit(trimmed);
    }
  };

  const onSourceChange = (newSource, oldSource, e) => {
    e.preventDefault();
    focus();
    props.onSourceChange && props.onSourceChange(newSource);
  };


  const searchEngineSettings = (
    <Popover position={Position.BOTTOM} className={Classes.FIXED}
             popoverClassName="bp3-popover-content-sizing SearchAppSettingsContainer">
      <Button rightIcon="caret-down" text="options" minimal={true} title="Search engine options" />
      <SearchEngineSettings source={props.source} onApply={triggerOnSubmit} />
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
      <SourceTabs active={props.source} onChange={onSourceChange} />

      <form onSubmit={submit}>
        <ControlGroup fill={true}>
          <InputGroup inputRef={inputRef} rightElement={searchEngineSettings}
                      defaultValue={props.initialQuery} onChange={e => setQuery(e.target.value)} />
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