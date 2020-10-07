import { compile } from 'path-to-regexp';

class RouteSpec {
  path;
  pathCompiled;

  constructor(path) {
    this.path = path;
    this.pathCompiled = compile(path);
  }

  buildUrl(params) {
    return this.pathCompiled(params);
  }
}

export const routes = {
  search: new RouteSpec("/search"),
  searchStart: new RouteSpec("/search/:source?"),
  searchResults: new RouteSpec("/search/:source/:query/:view?"),
  workbench: new RouteSpec("/workbench"),
};