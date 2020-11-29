import React from "react";

import { dcsServiceUrl, isCarrot2Distribution } from "./config.js";

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
          <strong>
            <NavLink to={routes.searchStart.path}>
              <FontAwesomeIcon icon={faSearch} />
              Web Search Clustering
            </NavLink>
          </strong>{" "}
          organizes search results from public search engines into clusters;
          offers treemap- and pie-chart visualizations of the clusters.
        </li>
        <li>
          <strong>
            <NavLink to={routes.workbench.path}>
              <FontAwesomeIcon icon={faFlask} />
              Clustering Workbench
            </NavLink>
          </strong>{" "}
          clusters content from local files in JSON or Excel format, Solr or
          Elasticsearch; allows tuning of clustering parameters and exporting
          results as Excel or JSON.
        </li>
      </ul>
    </>
  ),
  createAboutDetails: () => (
    <>
      <h3>FAQ</h3>

      <dl>
        <dt>Is this application Open Source?</dt>
        <dd>
          <p>
            Yes, the source code of this demo application is available as part
            of the{" "}
            <a href="http://project.carrot2.org">
              Carrot<sup>2</sup>
            </a>{" "}
            framework under the Apache Software License 2.0. This means you can
            freely reuse this application, along with Carrot<sup>2</sup>{" "}
            clustering algorithms, for your open source or commercial projects.
          </p>
        </dd>

        <dt>What happens to the data I submit for clustering?</dt>
        <dd>
          <p>
            The data is sent to Carrot<sup>2</sup> Document Clustering Server,
            located at{" "}
            <a
              href={dcsServiceUrl().toString()}
              target="_blank"
              rel="noreferrer"
            >
              {dcsServiceUrl().toString()}
            </a>
            , for clustering. The server will keep the data in memory for the
            duration of the clustering process. None of the data you submit will
            be permanently stored or logged.
          </p>
        </dd>
      </dl>

      <h3>Further reading</h3>

      <ul>
        <li>
          Source code of this application and several clustering algorithms is
          available in the{" "}
          <a href="http://project.carrot2.org" target="_blank">
            Carrot<sup>2</sup> framework
          </a>
          .
        </li>

        <li>
          The treemap view uses the{" "}
          <a href="https://carrotsearch.com/foamtree/">
            Carrot Search FoamTree
          </a>{" "}
          visualization component.
        </li>

        <li>
          The pie-chart view uses the{" "}
          <a href="https://carrotsearch.com/circles/">Carrot Search Circles</a>{" "}
          visualization component.
        </li>
      </ul>
    </>
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
  createAboutIntro: () => <div>TBD</div>,
  createAboutDetails: () => <div>TBD</div>,
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
