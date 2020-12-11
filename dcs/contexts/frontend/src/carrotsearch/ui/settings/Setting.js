import React, { useEffect, useState } from "react";

import "./Setting.css";

import { FormGroup, Popover, PopoverPosition } from "@blueprintjs/core";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faQuestionCircle } from "@fortawesome/pro-regular-svg-icons";

export const isSettingVisible = s => !s.visible || s.visible();

export const LabelWithHelp = ({ label, description }) => {
  if (description) {
    return (
      <div className="LabelWithHelp">
        {label}
        <DescriptionPopover description={description} />
      </div>
    );
  } else {
    return label;
  }
};

export const DescriptionPopover = ({ description }) => {
  const [open, setOpen] = useState(false);
  useEffect(() => {
    const listener = e => {
      if (e.keyCode === 27) {
        setOpen(false);
      }
    };
    document.body.addEventListener("keydown", listener);
    return () => {
      document.body.removeEventListener("keydown", listener);
    };
  }, []);
  return (
    <Popover
      content={<SettingDescription description={description} />}
      position={PopoverPosition.RIGHT_BOTTOM}
      canEscapeKeyClose={true}
      isOpen={open}
      onInteraction={setOpen}
      boundary="viewport"
    >
      <FontAwesomeIcon className="HelpIcon" icon={faQuestionCircle} />
    </Popover>
  );
};

const SettingDescription = ({ description }) => {
  return typeof description === "string" ? (
    <div
      className="SettingDescription"
      dangerouslySetInnerHTML={{ __html: description }}
    />
  ) : (
    <div className="SettingDescription">{description}</div>
  );
};

export const Setting = ({
  className,
  inline = false,
  label,
  description,
  message,
  children
}) => {
  let messageElement;
  if (message) {
    messageElement = <div className="SettingMessage">{message}</div>;
  }
  return (
    <FormGroup
      className={`${className} Setting`}
      inline={inline}
      label={<LabelWithHelp label={label} description={description} />}
    >
      {children}
      {messageElement}
    </FormGroup>
  );
};

export const storeAccessors = (store, property) => {
  return {
    get: () => store[property],
    set: (s, val) => (store[property] = val)
  };
};
