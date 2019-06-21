import React, { useState } from 'react';

import { Button, Drawer } from "@blueprintjs/core";
import { SearchAppInfo } from "./SearchAppInfo.js";

export const SearchAppInfoButton = () => {
  const [ isOpen, setOpen ] = useState(false);

  return (
    <>
      <Button className="About link" text="About" minimal={true} small={true}
              onClick={() => setOpen(!isOpen)} />
      <Drawer isOpen={isOpen} onClose={() => setOpen(false)} size={Drawer.SIZE_SMALL}>
        <SearchAppInfo/>
      </Drawer>
    </>
  );
};