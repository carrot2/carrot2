import React, { useRef, useState } from "react";

import { Button, Classes } from "@blueprintjs/core";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faClipboard,
  faClipboardCheck,
  faCross
} from "@fortawesome/pro-regular-svg-icons";

import copyToClipboard from "clipboard-copy";

export const CopyToClipboard = ({
  contentProvider,
  buttonText = "Copy to clipboard",
  buttonProps = { small: true, minimal: true }
}) => {
  const [copyStatus, setCopyStatus] = useState("none");
  const timeout = useRef();

  const copy = async () => {
    const content = contentProvider();

    let success;
    try {
      await copyToClipboard(content);
      success = true;
    } catch (ignored) {
      success = false;
    }

    setCopyStatus(success ? "success" : "error");
    if (timeout.current) {
      clearTimeout(timeout.current);
    }
    timeout.current = setTimeout(() => {
      setCopyStatus("none");
      timeout.current = undefined;
    }, 1000);
  };

  let iconProps;
  switch (copyStatus) {
    case "success":
      iconProps = {
        icon: <FontAwesomeIcon icon={faClipboardCheck} />,
        intent: "success",
        text: "Copied"
      };
      break;
    case "error":
      iconProps = {
        icon: <FontAwesomeIcon icon={faCross} />,
        intent: "error",
        text: "Couldn't copy"
      };
      break;
    default:
      iconProps = {
        icon: <FontAwesomeIcon icon={faClipboard} />,
        text: buttonText,
        title: "Copy to clipboard"
      };
      break;
  }

  return (
    <Button
      {...iconProps}
      {...buttonProps}
      className={Classes.FIXED + " CopyToClipboard"}
      onClick={copy}
    />
  );
};
