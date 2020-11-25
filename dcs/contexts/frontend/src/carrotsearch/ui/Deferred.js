import React, { useEffect, useState } from "react";

import "./Deferred.css";

export const Deferred = ({ timeout, children }) => {
  const [show, setShow] = useState(false);
  useEffect(() => {
    const to = setTimeout(() => {
      setShow(true);
    }, timeout);
    return () => {
      clearTimeout(to);
    };
  }, [setShow, timeout]);
  return show ? children : <div className="Deferred">Initializing...</div>;
};
