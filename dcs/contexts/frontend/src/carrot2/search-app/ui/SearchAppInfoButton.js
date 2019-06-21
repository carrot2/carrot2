import React, { useState } from 'react';

import { Button, Drawer } from "@blueprintjs/core";
import { SearchAppInfo } from "./SearchAppInfo.js";
import { applicationTitle } from "../../config.js";

export const SearchAppInfoButton = () => {
  const [ isOpen, setOpen ] = useState(true);

  return (
    <>
      <Button className="About link" text="About" minimal={true} small={true}
              onClick={() => setOpen(!isOpen)} />
      <Drawer isOpen={isOpen} onClose={() => setOpen(false)} size="40em">
        <Button className="About link" text="Close" minimal={true} small={true}
                icon="cross" onClick={() => setOpen(false)} />
        <SearchAppInfo/>
      </Drawer>
    </>
  );
};