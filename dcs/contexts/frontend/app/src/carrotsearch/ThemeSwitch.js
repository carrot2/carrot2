import React, { useEffect } from "react";

import { view } from "@risingstack/react-easy-state";
import { LightDarkSwitch } from "./LightDarkSwitch.js";
import { persistentStore } from "./store/persistent-store.js";

const isDarkSchemePreferred = () =>
  window.matchMedia("(prefers-color-scheme: dark)").matches;

export const themeStore = persistentStore(
  "uiConfig",
  {
    theme: isDarkSchemePreferred() ? "dark" : "light"
  },
  {
    flipTheme: () =>
      (themeStore.theme = themeStore.isDarkTheme() ? "light" : "dark"),
    isDarkTheme: () => themeStore.theme === "dark"
  }
);

export const ThemeSwitch = view(() => {
  const updateTheme = () => {
    const classList = document.body.classList;
    if (themeStore.isDarkTheme()) {
      classList.remove("light");
      classList.add("bp3-dark", "dark");
    } else {
      classList.remove("bp3-dark", "dark");
      classList.add("light");
    }
  };

  const flipTheme = () => {
    themeStore.flipTheme();
    updateTheme();
  };

  // Set theme on initial render.
  useEffect(function () {
    updateTheme();
  }, []);

  const isDarkTheme = themeStore.isDarkTheme();
  return (
    <LightDarkSwitch
      className="ThemeSwitch"
      dark={isDarkTheme}
      onChange={flipTheme}
    />
  );
});
