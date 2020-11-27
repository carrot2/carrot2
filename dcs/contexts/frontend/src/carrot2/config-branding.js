import React from "react";
import carrot2IntroHtml from "./apps/about/about-intro-carrot2.html";
import carrot2DetailsHtml from "./apps/about/about-details-carrot2.html";
import { isCarrot2Distribution } from "./config.js";

import { CarrotLogo } from "../carrotsearch/logo/CarrotLogo.js";
import { Carrot2Text } from "../carrotsearch/logo/Carrot2Text.js";
import { CarrotSearchText } from "../carrotsearch/logo/CarrotSearchText.js";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faFlask, faSearch } from "@fortawesome/pro-regular-svg-icons";
import { routes } from "./routes.js";
import { NavLink } from "react-router-dom";

const carrot2 = isCarrot2Distribution();

const carrot2Branding = {
  product: "Carrot2",
  pageTitle: process.env.REACT_APP_META_TITLE,
  createProductName: () => (
    <>
      Carrot<sup>2</sup>
    </>
  ),
  createSlogan: () => (
    <>
      <a href="http://project.carrot2.org">
        Carrot<sup>2</sup>
      </a>{" "}
      organizes your search results into topics. With an instant overview of
      what's available, you will quickly find what you're looking for.
    </>
  ),
  createStartPageLogo: () => (
    <>
      <CarrotLogo />
      <Carrot2Text title="Carrot2" />
    </>
  ),
  createAboutIntro: () => (
    <>
      <p>
        This is the demo application of the{" "}
        <a href="http://project.carrot2.org" target="_blank" rel="noreferrer">
          Carrot<sup>2</sup> clustering engine
        </a>
        . It uses Carrot<sup>2</sup>'s algorithms to organize search results
        into thematic folders.
      </p>

      <h3>User interfaces</h3>

      <ul style={{ listStyle: "none", paddingLeft: "0" }}>
        <li>
          <NavLink to={routes.searchStart.path}>
            <FontAwesomeIcon icon={faSearch} />
            Web Search Clustering
          </NavLink>
          : organizes search results from public search engines into clusters;
          offers treemap- and pie-chart visualizations of the clusters.
        </li>
        <li>
          <NavLink to={routes.workbench.path}>
            <FontAwesomeIcon icon={faFlask} />
            Clustering Workbench
          </NavLink>
          : clustering of content from local files in JSON or Excel format, Solr
          or Elasticsearch; tuning of clustering parameters, export of results
          into Excel or JSON.
        </li>
      </ul>
    </>
  ),
  createAboutDetails: () => (
    <div dangerouslySetInnerHTML={{ __html: carrot2DetailsHtml }} />
  ),
  createUnlimitedDistributionInfo: () => {
    return (
      <>
        <strong>Note:</strong> If you have IT and programming skills, you can
        install this application on your own hardware. This will remove limits
        on the rate and size of clustering requests. Please see{" "}
        <a
          href="http://project.carrot2.org"
          target="_blank"
          rel="noopener noreferrer"
        >
          http://project.carrot2.org
        </a>{" "}
        for more details.
      </>
    );
  }
};

const lingo3gBranding = {
  product: "Lingo3G",
  createProductName: () => <>Lingo3G</>,
  pageTitle: process.env.REACT_APP_META_TITLE,
  createSlogan: () => (
    <>
      This app uses Carrot Search{" "}
      <a href="https://carrotsearch.com/lingo3g">Lingo3G</a> to organize search
      results into clearly-labeled topics for instant overview and efficient
      research.
    </>
  ),
  createStartPageLogo: () => (
    <>
      <CarrotLogo />
      <CarrotSearchText title="Carrot Search" />
    </>
  ),
  createAboutIntro: () => (
    <div dangerouslySetInnerHTML={{ __html: carrot2IntroHtml }} />
  ),
  createAboutDetails: () => (
    <div dangerouslySetInnerHTML={{ __html: carrot2DetailsHtml }} />
  ),
  createUnlimitedDistributionInfo: () => {
    return (
      <>
        <strong>Tip:</strong> If you have IT and programming skills, you can
        install this application on your own hardware. This will remove limits
        on the rate and size of clustering requests. Get in touch at
        <a
          href="mailto:info@carrotsearch.com"
          target="_blank"
          rel="noopener noreferrer"
        >
          info@carrotsearch.com
        </a>{" "}
        for an evaluation package.
      </>
    );
  }
};

export const branding = carrot2 ? carrot2Branding : lingo3gBranding;
