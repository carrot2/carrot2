import React, { Component } from 'react';
import PropTypes from 'prop-types';

import { AnchorButton, Hotkey, Hotkeys, HotkeysTarget } from "@blueprintjs/core";

class ThemeSwitchImpl extends Component {
  render() {
    const isDarkTheme = this.props.theme === "dark";
    return (
      <AnchorButton text={(isDarkTheme ? "Light" : "Dark") + " theme"}
                    icon={(isDarkTheme ? "flash" : "moon")}
                    onClick={this.props.onThemeFlip}
                    minimal={true} small={true} />
    );
  }

  renderHotkeys() {
    return <Hotkeys>
      <Hotkey
        global={true}
        combo="alt + t"
        label="Switch color theme"
        onKeyDown={() => this.props.onThemeFlip()}
      />
    </Hotkeys>;
  }
}

ThemeSwitchImpl.propTypes = {
  theme: PropTypes.string.isRequired,
  onThemeFlip: PropTypes.func.isRequired
};

export const ThemeSwitch = HotkeysTarget(ThemeSwitchImpl);
