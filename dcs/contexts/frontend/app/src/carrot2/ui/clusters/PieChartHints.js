import React from "react";

import "./VisualizationHints.css";

import { view } from "@risingstack/react-easy-state";

export const PieChartHints = view(() => {
  return (
    <div className="VisualizationHints">
      <h4>Pie-chart interaction help</h4>
      <table>
        <tbody>
          <tr>
            <td>Click</td>
            <td>Select or unselect group.</td>
          </tr>

          <tr>
            <td>
              <kbd>Ctrl</kbd> + click
            </td>
            <td>Select or unselect multiple groups.</td>
          </tr>

          <tr>
            <td>Double click</td>
            <td>
              Expand group for easier browsing.
              <br />
              Double click again to collapse.
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  );
});
