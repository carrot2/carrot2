import React from "react";

export function Loading(props) {
  if (props.loading) {
    return <div className="Loading">Loading</div>;
  }

  if (props.error) {
    return <div>Error</div>;
  }

  return props.children;
}