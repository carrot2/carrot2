import "./SearchAppInfo.css";
import { Drawer } from "@blueprintjs/core";

import React from "react";

import aboutHtml from "../../about.html";
import { applicationTitle } from "../../config.js";

export const SearchAppInfo = () => {
  return (
    <div className="SearchAppInfo">
      <h2>About {applicationTitle}</h2>
      <div dangerouslySetInnerHTML={{__html: aboutHtml}} />
    </div>
  );
};