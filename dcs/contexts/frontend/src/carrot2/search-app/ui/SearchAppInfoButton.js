import React from 'react';

import { store, view } from "@risingstack/react-easy-state";

import { Button, Drawer } from "@blueprintjs/core";
import { ButtonLink } from "../../../carrotsearch/ui/ButtonLink.js";
import { SearchAppInfo } from "./SearchAppInfo.js";

const aboutStore = store({open: false});

export const ShowAppInfoButton = props => {
  return <ButtonLink {...props} onClick={() => aboutStore.open = !aboutStore.open} />;
};

export const SearchAppInfoButton = view(() => {
  return (
    <>
      <ShowAppInfoButton className="About" text="About" />
      <Drawer isOpen={aboutStore.open} onClose={() => aboutStore.open = false} size="28em">
        <Button className="About link" text="Close" minimal={true} small={true}
                icon="cross" onClick={() => aboutStore.open = false} />
        <SearchAppInfo/>
      </Drawer>
    </>
  );
});