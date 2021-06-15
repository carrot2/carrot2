import React, { useCallback, useEffect, useState } from "react";

import "./ResultList.css";

import {
  autoEffect,
  clearEffect,
  store,
  view
} from "@risingstack/react-easy-state";

import { ButtonLink } from "@carrotsearch/ui/ButtonLink.js";

import { Optional } from "@carrotsearch/ui/Optional.js";

import {
  ClusterInSummary,
  ClusterSelectionSummary
} from "./ClusterSelectionSummary.js";

import { clusterSelectionStore } from "../../store/selection.js";
import { resultListConfigStore } from "./ResultListConfig.js";
import { useScrollReset } from "@carrotsearch/ui/hooks/scroll-reset.js";

const ResultClusters = view(props => {
  const selectionStore = clusterSelectionStore;
  return (
    <div className="ResultClusters">
      <span>
        {(props.result.clusters || []).map(c => (
          <React.Fragment key={c.id}>
            <ClusterInSummary
              cluster={c}
              onClick={() => selectionStore.toggleSelection(c)}
            />{" "}
          </React.Fragment>
        ))}
      </span>
    </div>
  );
});

export const ResultWrapper = view(props => {
  const { document, children, visible = true } = props;

  const config = resultListConfigStore;

  return (
    <a
      className="Result"
      href={document.url}
      target={config.openInNewTab ? "_blank" : "_self"}
      rel="noopener noreferrer"
      style={{ display: visible ? "block" : "none" }}
    >
      {children}
      <Optional
        visible={config.showClusters}
        content={() => <ResultClusters result={document} />}
      />
    </a>
  );
});

/**
 * A simple functional component for displaying a single document.
 */
const SimpleResult = view(props => {
  const { document, source, visible = true } = props;

  return (
    <ResultWrapper document={document} visible={visible}>
      {source.createResult(props)}
    </ResultWrapper>
  );
});

/**
 * A reactive component for displaying a single document. The document visibility is determined
 * by reading the provided visibilityStore. See the ResultList component comment for the reason
 * why we need the reactive wrapper around the result component.
 */
const ReactiveResult = view(props => {
  const document = props.document;
  const visibilityStore = props.visibilityStore;
  const visible =
    visibilityStore.allDocumentsVisible || visibilityStore.isVisible(document);

  return <SimpleResult {...props} visible={visible} />;
});

const ResultListPaging = ({
  enabled,
  start,
  end,
  total,
  next,
  prev,
  nextEnabled,
  prevEnabled
}) => {
  if (!enabled) {
    return null;
  }
  return (
    <div className="ResultListPaging">
      <ButtonLink enabled={prevEnabled} onClick={prev}>
        &lt; Previous
      </ButtonLink>
      <span>
        {start + 1} &mdash; {end} of {total}
      </span>
      <ButtonLink enabled={nextEnabled} onClick={next}>
        Next &gt;
      </ButtonLink>
    </div>
  );
};

/**
 * Listens to cluster selection changes and calls the supplied callback on every change.
 */
const useSelectionChange = onSelectionChange => {
  useEffect(() => {
    const listener = () => {
      const selected = clusterSelectionStore.selected;

      // A dummy always-true condition to prevent removal of this code.
      // We need to read some property of the selected cluster set to
      // get notifications of changes.
      if (selected.size >= 0) {
        onSelectionChange();
      }
    };
    autoEffect(listener);
    return () => clearEffect(listener);
  }, [onSelectionChange]);
};

/**
 * Manages a simple list paging user interface.
 */
const usePaging = ({ enabled, maxPerPage, results, onChange }) => {
  const pagingStore = store({ start: 0 });

  const reset = useCallback(() => {
    pagingStore.start = 0;
  }, [pagingStore]);

  const start = pagingStore.start;
  const end = start + maxPerPage;
  return {
    end: end,
    start: start,
    total: results.length,
    results: enabled ? results.slice(start, end) : results,
    next: () => {
      pagingStore.start = end;
      onChange();
    },
    prev: () => {
      pagingStore.start = start - maxPerPage;
      onChange();
    },
    nextEnabled: end < results.length,
    prevEnabled: start > 0,
    enabled: enabled && results.length > maxPerPage,
    reset: reset
  };
};

const MAX_RESULTS_FOR_REACTIVE_DISPLAY = 200;

const ResultListImpl = view(props => {
  const resultsStore = props.store;
  const allResults = resultsStore.searchResult.documents;

  // For performance reasons we have two variants of the document list display:
  // one for small lists (<=200 results) and another one for larger lists.
  // For small lists, we render all results right away and then use CSS to show only
  // the results contained in the selected cluster. This allows for fast switching between
  // clusters because we don't create/destroy DOM elements on every selection change.
  // The above approach is not feasible for thousands of documents because creating so many
  // DOM elements would kill the browser. In this case, we apply result list paging and
  // have to update the DOM on every cluster selection change. With a reasonable page size this
  // shouldn't be prohibitive performance-wise, but is of course much slower than the small-list
  // approach.
  let visibleResults, Result;
  const pagingEnabled = allResults.length > MAX_RESULTS_FOR_REACTIVE_DISPLAY;
  if (pagingEnabled) {
    // Set up components for paged display of results. We'll re-render the list
    // on every cluster selection change.

    // Simple non-reactive document component is fine here.
    Result = SimpleResult;

    // The filtered list of documents to display.
    if (props.visibilityStore.allDocumentsVisible) {
      visibleResults = allResults;
    } else {
      visibleResults = allResults.filter(r =>
        props.visibilityStore.isVisible(r)
      );
    }
  } else {
    // In the small-list case where we hide documents through CSS, we need a reactive
    // document component that will check its visibility flag and update the its visibility
    // accordingly. With this approach we'll render the minimum required number of documents
    // and will not render documents whose visibility state doesn't change.
    Result = ReactiveResult;

    // In this case we pass all results for rendering, rendered documents will be hidden as required.
    visibleResults = allResults;
  }

  const { container, scrollReset } = useScrollReset();

  const { results, reset, ...paging } = usePaging({
    enabled: pagingEnabled,
    maxPerPage: resultListConfigStore.maxResultsPerPage,
    results: visibleResults,
    onChange: scrollReset
  });

  // Reset scroll and paging on cluster selection changes.
  const r = useCallback(() => {
    scrollReset();
    reset();
  }, [reset, scrollReset]);
  useSelectionChange(r);

  // Reset scroll on new search result.
  useEffect(() => {
    scrollReset();
  }, [allResults, scrollReset]);

  const limitedResults = props.limit ? results.slice(0, 5) : results;

  return (
    <div className="ResultList" ref={container}>
      <div>
        <ClusterSelectionSummary />
        {limitedResults.map((document, index) => (
          <Result
            key={index}
            document={document}
            source={resultsStore.searchResult.source}
            rank={index + 1}
            visibilityStore={props.visibilityStore}
          />
        ))}
        <ResultListPaging {...paging} />
      </div>
    </div>
  );
});

// A wrapper that renders a limited number of documents during the initial render.
// This is to speed up switching between workbench and other apps when a long list
// of documents is displaying. Every switch re-creates the DOM elements and creating
// hundreds of documents would stop the browser for a while.
export const ResultList = props => {
  const [limit, setLimit] = useState(true);
  useEffect(() => {
    setTimeout(() => {
      setLimit(false);
    }, 100);
  }, [setLimit]);

  return <ResultListImpl {...props} limit={limit} />;
};
