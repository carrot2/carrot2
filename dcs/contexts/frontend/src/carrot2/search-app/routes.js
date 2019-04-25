import pathToRegexp from 'path-to-regexp';

class RouteSpec {
  path;
  pathCompiled;

  constructor(path) {
    this.path = path;
    this.pathCompiled = pathToRegexp.compile(path);
  }

  buildUrl(params) {
    return this.pathCompiled(params);
  }
}

export const routes = {
  "_root": new RouteSpec("/:source?"),
  "search": new RouteSpec("/search/:source/:query"),
};