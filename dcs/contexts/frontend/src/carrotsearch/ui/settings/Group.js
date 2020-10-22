import React from 'react';
import PropTypes from 'prop-types';

import { view } from "@risingstack/react-easy-state";

import { RadioSetting } from "./RadioSetting.js";
import { BooleanSetting } from "./BooleanSetting.js";
import { StringSetting } from "./StringSetting.js";
import { NumericSetting } from "./NumericSetting.js";
import { Section } from "../Section.js";

export const Group = view(({ setting, get, set, className }) => (
    <Section className={className} label={setting.label}>
      {
        setting.settings.filter(s => !s.visible || s.visible()).map(s => {
          return <section key={s.id} id={s.id}>
            {getFactory(s)(s, s.get || setting.get || get, s.set || setting.set || set)}
          </section>
        })
      }
    </Section>
));

const factories = {
  "group": (s, get, set) => {
    return <Group setting={s} get={get} set={set} />;
  },

  "boolean": (s, get, set) => {
    return <BooleanSetting setting={s} get={get} set={set} />;
  },

  "string": (s, get, set) => {
    return <StringSetting setting={s} get={get} set={set} />
  },

  "enum": (s, get, set) => {
    if (s.ui === "radio") {
      return <RadioSetting setting={s} get={get} set={set} />;
    }
  },
  "number": (s, get, set) => {
    return <NumericSetting setting={s} get={get} set={set} />;
  }
};
const getFactory = s => {
  const factory = factories[s.type];
  if (!factory) {
    throw new Error(`Unknown factory for setting type: ${s.type}`);
  }
  return factory;
};

export const addFactory = (type, factory) => factories[type] = factory

Group.propTypes = {
  setting: PropTypes.object.isRequired,
  get: PropTypes.func.isRequired,
  set: PropTypes.func.isRequired
};