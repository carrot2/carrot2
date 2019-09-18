import React from 'react';
import carrot2IntroHtml from "./about-intro-carrot2.html";
import carrot2DetailsHtml from "./about-details-carrot2.html";
import carrotSearchLogo from "./search-app/ui/assets/carrot-search-logo.svg";
import carrot2Text from "./search-app/ui/assets/carrot2-text.svg";
import carrotSearchText from './search-app/ui/assets/carrot-search-text.svg';
import { isCarrot2Distribution } from "./config.js";

const carrot2 = isCarrot2Distribution();

const carrot2Branding = {
  pageTitle: "Carrot2 clustering engine demo",
  createSlogan: () => (
    <>
      <a href="http://project.carrot2.org">Carrot<sup>2</sup></a> organizes
      your search results into topics. With an instant overview of what's
      available, you will quickly find what you're looking for.
    </>
  ),
  createStartPageLogo: () => (
    <>
      <img src={carrotSearchLogo} className="logo" alt="Carrot2 logo" />
      <img src={carrot2Text} className="carrot2 text" alt="Carrot2" />
    </>
  ),
  createAboutIntro: () => <div dangerouslySetInnerHTML={{ __html: carrot2IntroHtml }} />,
  createAboutDetails: () => <div dangerouslySetInnerHTML={{ __html: carrot2DetailsHtml }} />
};

const lingo3gBranding = {
  pageTitle: "Lingo3G document clustering engine demo",
  createSlogan: () => (
    <>
      This app uses Carrot Search <a href="https://carrotsearch.com/lingo3g">Lingo3G</a> to
      organize search results into clearly-labeled topics for instant overview
      and efficient research.
    </>
  ),
  createStartPageLogo: () => (
    <>
      <img src={carrotSearchLogo} className="logo" alt="Carrot Search logo" />
      <img src={carrotSearchText} className="text" alt="Carrot Search" />
    </>
  ),
  createAboutIntro: () => <div dangerouslySetInnerHTML={{ __html: carrot2IntroHtml }} />,
  createAboutDetails: () => <div dangerouslySetInnerHTML={{ __html: carrot2DetailsHtml }} />
};

export const branding = carrot2 ? carrot2Branding : lingo3gBranding;