import React, { useEffect } from 'react';

import { view } from 'react-easy-state';
import { LightDarkSwitch } from "../../../carrotsearch/ui/LightDarkSwitch.js";
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
    <LightDarkSwitch dark={isDarkTheme} onChange={flipTheme} />
  );
}

export const ThemeSwitch = view(ThemeSwitchImpl);
