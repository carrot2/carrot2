import React from 'react';
import PropTypes from 'prop-types';

import { Group } from "./Group.js";

export { addFactory } from "./Group.js";

export const Settings = ({ settings, get, set }) => (
    <Group className="Settings" setting={settings} set={set} get={get} />
);

Settings.propTypes = {
  settings: PropTypes.object.isRequired
};

