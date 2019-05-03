import React, { useEffect } from 'react';

import { AnchorButton } from "@blueprintjs/core";
import { view } from 'react-easy-state';
import { persistentStore } from "../../util/persistent-store.js";

export const uiConfig = persistentStore("uiConfig",
  {
    theme: "dark"
  },
  {
    flipTheme: () => uiConfig.theme = uiConfig.isDarkTheme() ? "light" : "dark",
    isDarkTheme: () => uiConfig.theme === "dark"
  });


function ThemeSwitchImpl () {
  function updateTheme() {
    const classList = document.body.classList;
    if (uiConfig.isDarkTheme()) {
      classList.remove("light");
      classList.add("bp3-dark", "dark");
    } else {
      classList.remove("bp3-dark", "dark");
      classList.add("light");
    }
  }

  function flipTheme() {
    uiConfig.flipTheme();
    updateTheme();
  }

  // Set theme on initial render.
  useEffect(function () {
    updateTheme();
  }, []);

  const isDarkTheme = uiConfig.isDarkTheme();
  return (
    <AnchorButton text={(isDarkTheme ? "Light" : "Dark") + " theme"}
                  icon={(isDarkTheme ? "flash" : "moon")}
                  onClick={flipTheme}
                  minimal={true} small={true} />
  );
}

export const ThemeSwitch = view(ThemeSwitchImpl);
