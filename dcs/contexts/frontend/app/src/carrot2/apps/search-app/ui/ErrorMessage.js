import "./ErrorMessage.css";

import React from "react";

import { branding } from "@carrot2/config/branding.js";

export const ErrorMessage = ({ children }) => {
  return (
    <div className="Error">
      {children}
      <p>That's all we know, sorry.</p>
    </div>
  );
};

export const GenericErrorMessage = ({ error, children }) => {
  let message;
  if (error instanceof Error) {
    message = `${error.name}: ${error.message}`;
  } else {
    message = error?.statusText?.replace(/([&/?])/g, "$1\u200b");
  }

  return (
    <ErrorMessage error={error}>
      {children}
      <pre>{message}</pre>
    </ErrorMessage>
  );
};

export const GenericSearchEngineErrorMessage = ({ error }) => {
  return (
    <GenericErrorMessage error={error}>
      <h2>Search engine error</h2>
      <p>Search could not be performed due to the following error:</p>
    </GenericErrorMessage>
  );
};

export const SearchEngineErrorMessage = ({ children }) => {
  return (
    <ErrorMessage>
      <h2>Search engine error</h2>
      <p>Search could not be performed due to the following error:</p>
      {children}
    </ErrorMessage>
  );
};

const ResponseInfo = ({ response }) => {
  if (!response) {
    return null;
  }
  return (
    <dl className="ResponseInfo">
      <dt>URL</dt>
      <dd>{response.url}</dd>
      <dt>Status</dt>
      <dd>
        {response.status} {response.statusText}
      </dd>
    </dl>
  );
};

export const HttpErrorMessage = ({ error, children }) => {
  return (
    <>
      {children}
      <ResponseInfo response={error.response} />
    </>
  );
};

export const ClusteringServerRateLimitExceededError = () => {
  return (
    <div className="Error">
      <h2>Too many clustering requests</h2>

      <p>
        You are making too many clustering requests for our little demo server
        to handle. Please check back in a minute.
      </p>

      <p>
        <small className="light">
          {branding.createUnlimitedDistributionInfo()}
        </small>
      </p>
    </div>
  );
};

export const ClusteringRequestSizeLimitExceededError = () => {
  return (
    <div className="Error">
      <h2>Too much data to cluster</h2>

      <p>
        You sent too much data for our little demo server to handle. Lower the
        number of search results and try again.
      </p>

      <p>
        <small className="light">
          {branding.createUnlimitedDistributionInfo()}
        </small>
      </p>
    </div>
  );
};

export const ClusteringExceptionMessage = ({ exception }) => {
  return (
    <div className="Error">
      <h2>Clustering engine error</h2>

      <p>Results could not be clustered due to the following error:</p>

      <pre>{exception.stacktrace}</pre>

      <p>That's all we know.</p>
    </div>
  );
};

export const ClusteringErrorMessage = ({ message }) => {
  return (
    <ErrorMessage>
      <h2>Clustering engine error</h2>

      <p>Results could not be clustered due to the following error:</p>

      <pre>{message}</pre>
    </ErrorMessage>
  );
};

export const createClusteringErrorElement = error => {
  if (error && error.status === 429) {
    return <ClusteringServerRateLimitExceededError />;
  }

  if (error && error.status === 413) {
    return <ClusteringRequestSizeLimitExceededError />;
  }
  if (error && error.bodyParsed) {
    if (error.bodyParsed.stacktrace) {
      return <ClusteringExceptionMessage exception={error.bodyParsed} />;
    }
    if (error.bodyParsed.message) {
      return <ClusteringErrorMessage message={error.bodyParsed.message} />;
    }
  }

  return (
    <GenericErrorMessage error={error}>
      <h2>Clustering engine error</h2>
      <p>Results could not be clustered due to the following error:</p>
    </GenericErrorMessage>
  );
};
