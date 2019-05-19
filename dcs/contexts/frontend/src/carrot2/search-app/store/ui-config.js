import { persistentStore } from "../../util/persistent-store.js";

export const resultListConfigStore = persistentStore("resultListConfig",
  {
    showSiteIcons: true,
    showRank: true,
    openInNewTab: true
  },
  {
  }
);