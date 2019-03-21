import { Button, Classes, ControlGroup, InputGroup } from "@blueprintjs/core";
import * as PropTypes from "prop-types";
import React, { Component } from "react";

import './SearchForm.css';
import { SourceTabs } from "./SourceTabs";

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
    return (
      <div className="SearchForm">
        <SourceTabs active={this.props.source} onChange={this.onSourceChange.bind(this)} />

        <form onSubmit={this.submit.bind(this)}>
          <ControlGroup fill={true}>
            <InputGroup inputRef={(input) => this.searchInput = input}
                        value={this.state.query} onChange={(e) => this.setState({ query: e.target.value })} />
            <Button className={Classes.FIXED} icon="search" type="submit" />
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