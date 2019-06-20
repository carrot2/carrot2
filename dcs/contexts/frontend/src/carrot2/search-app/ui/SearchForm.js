import { Button, Classes, ControlGroup, InputGroup, Popover, Position } from "@blueprintjs/core";
import * as PropTypes from "prop-types";
import React, { Component } from "react";

import './SearchForm.css';
import { SourceTabs } from "./SourceTabs";

import { SearchEngineSettings } from "./SearchEngineSettings.js";
import { ClusteringEngineSettings } from "./ClusteringEngineSettings.js";

export class SearchForm extends Component {
  constructor(props) {
    super(props);
    this.state = { query: this.props.initialQuery || "" };
  }

  componentDidMount() {
    this.searchInput.focus();
  }

  submit(e) {
    e.preventDefault();
    this.triggerOnSubmit();
  }

  triggerOnSubmit() {
    const trimmed = this.state.query.trim();
    if (trimmed.length > 0) {
      this.props.onSubmit(trimmed);
    }
  }

  onSourceChange(newSource, oldSource, e) {
    e.preventDefault();
    this.searchInput.focus();
    this.props.onSourceChange && this.props.onSourceChange(newSource);
  }

  render() {
    const searchEngineSettings = (
      <Popover position={Position.BOTTOM} className={Classes.FIXED}
               popoverClassName="bp3-popover-content-sizing SearchAppSettingsContainer">
        <Button rightIcon="caret-down" text="options" minimal={true} title="Search engine options" />
        <SearchEngineSettings source={this.props.source} onApply={this.triggerOnSubmit.bind(this)} />
      </Popover>
    );

    const clusteringEngineSettings = (
      <Popover position={Position.RIGHT_TOP} className={Classes.FIXED}
               popoverClassName="bp3-popover-content-sizing SearchAppSettingsContainer">
        <Button icon="wrench" minimal={true} title="Clustering algorithm" />
        <ClusteringEngineSettings/>
      </Popover>
    );

    return (
      <div className="SearchForm">
        <SourceTabs active={this.props.source} onChange={this.onSourceChange.bind(this)} />

        <form onSubmit={this.submit.bind(this)}>
          <ControlGroup fill={true}>
            <InputGroup inputRef={(input) => this.searchInput = input} rightElement={searchEngineSettings}
                        value={this.state.query} onChange={(e) => this.setState({ query: e.target.value })} />
            <Button className={Classes.FIXED} icon="search" type="submit" text="Search" />
            { clusteringEngineSettings }
          </ControlGroup>
        </form>
      </div>
    );
  }
}

SearchForm.propTypes = {
  onSubmit: PropTypes.func.isRequired,
  onSourceChange: PropTypes.func.isRequired,
  initialQuery: PropTypes.string
};