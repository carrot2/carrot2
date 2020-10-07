import React from 'react';
import carrot2IntroHtml from "./about-intro-carrot2.html";
import carrot2DetailsHtml from "./about-details-carrot2.html";
import { isCarrot2Distribution } from "./config.js";

import { ReactComponent as CarrotLogo } from "./apps/search-app/ui/assets/carrot-search-logo.svg";
import { ReactComponent as Carrot2Text } from "./apps/search-app/ui/assets/carrot2-text.svg";
import { ReactComponent as CarrotSearchText } from "./apps/search-app/ui/assets/carrot-search-text.svg";

const carrot2 = isCarrot2Distribution();

const carrot2Branding = {
  pageTitle: process.env.REACT_APP_META_TITLE,
  createSlogan: () => (
    <>
      <a href="http://project.carrot2.org">Carrot<sup>2</sup></a> organizes
      your search results into topics. With an instant overview of what's
      available, you will quickly find what you're looking for.
    </>
  ),
  createStartPageLogo: () => (
    <>
      <CarrotLogo className="logo" />
      <Carrot2Text className="carrot2 text" title="Carrot2" />
    </>
  ),
  createAboutIntro: () => <div dangerouslySetInnerHTML={{ __html: carrot2IntroHtml }} />,
  createAboutDetails: () => <div dangerouslySetInnerHTML={{ __html: carrot2DetailsHtml }} />,
  createUnlimitedDistributionInfo: () => {
    return (
      <>
        <strong>Note:</strong> If you have IT and programming skills, you can install
        this application on your own hardware. This will remove limits on
        the rate and size of clustering requests. Please see <a href="http://project.carrot2.org"
        target="_blank" rel="noopener noreferrer">http://project.carrot2.org</a> for
        more details.
      </>
    );
  }
};

const lingo3gBranding = {
  pageTitle: process.env.REACT_APP_META_TITLE,
  createSlogan: () => (
    <>
      This app uses Carrot Search <a href="https://carrotsearch.com/lingo3g">Lingo3G</a> to
      organize search results into clearly-labeled topics for instant overview
      and efficient research.
    </>
  ),
  createStartPageLogo: () => (
    <>
      <CarrotLogo className="logo" />
      <CarrotSearchText className="text" title="Carrot Search" />
    </>
  ),
  createAboutIntro: () => <div dangerouslySetInnerHTML={{ __html: carrot2IntroHtml }} />,
  createAboutDetails: () => <div dangerouslySetInnerHTML={{ __html: carrot2DetailsHtml }} />,
  createUnlimitedDistributionInfo: () => {
    return (
      <>
        <strong>Tip:</strong> If you have IT and programming skills, you can install
        this application on your own hardware. This will remove limits on
        the rate and size of clustering requests. Get in touch at
        <a href="mailto:info@carrotsearch.com" target="_blank" rel="noopener noreferrer">info@carrotsearch.com</a> for
        an evaluation package.
      </>
    );
  }
};

export const branding = carrot2 ? carrot2Branding : lingo3gBranding;