import React from 'react';
import { TitleAndRank, Url } from "./result-components.js";

/**
 * Renders a single search result from PubMed.
 */
export const PubMedResult = (props) => {
  const document = props.document;
  const config = props.commonConfigStore;

  let rank = null;
  if (config.showRank) {
    rank = <span>{props.rank}</span>;
  }

  return (
    <>
      <TitleAndRank title={document.title} rank={rank} showRank={config.showRank} />
      <div>
      {
        (document.paragraphs || []).map((p, index) => {
          return <p key={index}>{p}</p>;
        })
      }
      </div>
      <Url url={document.url} />
    </>
  )
};