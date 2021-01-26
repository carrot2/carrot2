import { compile, match } from "path-to-regexp";

class RouteSpec {
  path;
  pathCompiled;
  match;

  constructor(path) {
    this.path = path;
    this.pathCompiled = compile(path);
    this.match = match(path);
  }

  buildUrl(params) {
    return this.pathCompiled(params);
  }
}

export const routes = {
  about: new RouteSpec("/about"),
  search: new RouteSpec("/search"),
  searchStart: new RouteSpec("/search/:source?"),
  searchResults: new RouteSpec("/search/:source/:query/:view?"),
  workbench: new RouteSpec("/workbench")
};
