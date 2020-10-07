import React from 'react';

import { store, view } from "@risingstack/react-easy-state";

import { Drawer } from "@blueprintjs/core";
import { ButtonLink } from "../../../carrotsearch/ui/ButtonLink.js";
import { SearchAppInfo } from "./SearchAppInfo.js";

const aboutStore = store({open: false});

export const ShowAppInfoButton = props => {
  return <ButtonLink {...props} onClick={() => aboutStore.open = !aboutStore.open}>About</ButtonLink>;
};

export const SearchAppInfoButton = view(() => {
  return (
    <>
      <ShowAppInfoButton className="About" text="About" />
      <Drawer isOpen={aboutStore.open} onClose={() => aboutStore.open = false} size="28em">
        <ButtonLink className="About link" onClick={() => aboutStore.open = false}>Close</ButtonLink>
        <SearchAppInfo/>
      </Drawer>
    </>
  );
});