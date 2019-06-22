import { Button } from "@blueprintjs/core";
import React from 'react';

export const ButtonLink = (props) => {
  return <Button {...props} minimal={true} small={true}
                 className={(props.className || "") + " link"} />;
};