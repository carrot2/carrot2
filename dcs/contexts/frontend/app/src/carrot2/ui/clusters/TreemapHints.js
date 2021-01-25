import React from "react";

import "./VisualizationHints.css";

import { view } from "@risingstack/react-easy-state";

export const TreemapHints = view(() => {
  return (
    <div className="VisualizationHints">
      <h4>Treemap interaction help</h4>
      <table>
        <tbody>
          <tr>
            <td>Click</td>
            <td>Select or unselect group.</td>
          </tr>

          <tr className="separator">
            <td>
              <kbd>Ctrl</kbd> + click
            </td>
            <td>Select or unselect multiple groups.</td>
          </tr>

          <tr>
            <td>Double click</td>
            <td>Zoom and open group for browsing.</td>
          </tr>

          <tr className="separator">
            <td>
              <kbd>Shift</kbd> + double click
            </td>
            <td>Unzoom and close group.</td>
          </tr>

          <tr>
            <td>Click-and-hold</td>
            <td>Open group for browsing.</td>
          </tr>

          <tr className="separator">
            <td>
              <kbd>Shift</kbd> + click-and-hold
            </td>
            <td>Close group.</td>
          </tr>

          <tr>
            <td>
              Mouse wheel,
              <br />
              click and move
            </td>
            <td>Zoom in and out, pan around.</td>
          </tr>

          <tr>
            <td>
              <kbd>Esc</kbd>
            </td>
            <td>Reset view: unzoom, close all groups.</td>
          </tr>
        </tbody>
      </table>
    </div>
  );
});
