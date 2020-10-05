import React, { useEffect } from 'react';

import { view } from "@risingstack/react-easy-state";
import { LightDarkSwitch } from "../../../carrotsearch/ui/LightDarkSwitch.js";
import { persistentStore } from "../../util/persistent-store.js";

const isDarkSchemePreferred = () => window.matchMedia("(prefers-color-scheme: dark)").matches;

export const themeStore = persistentStore("uiConfig",
  {
    theme: isDarkSchemePreferred() ? "dark" : "light"
  },
  {
    flipTheme: () => themeStore.theme = themeStore.isDarkTheme() ? "light" : "dark",
    isDarkTheme: () => themeStore.theme === "dark"
  });


function updateTheme() {
  const classList = document.body.classList;
  if (themeStore.isDarkTheme()) {
    classList.remove("light");
    classList.add("bp3-dark", "dark");
  } else {
    classList.remove("bp3-dark", "dark");
    classList.add("light");
  }
}
updateTheme();

function ThemeSwitchImpl () {
  function flipTheme() {
    themeStore.flipTheme();
    updateTheme();
  }

  // Set theme on initial render.
  useEffect(function () {
    updateTheme();
  }, []);

  const isDarkTheme = themeStore.isDarkTheme();
  return (
    <LightDarkSwitch dark={isDarkTheme} onChange={flipTheme} />
  );
}

export const ThemeSwitch = view(ThemeSwitchImpl);
