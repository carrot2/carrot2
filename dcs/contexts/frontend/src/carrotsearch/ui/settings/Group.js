import React from 'react';

import PropTypes from 'prop-types';
import { view } from "@risingstack/react-easy-state";
import { RadioSetting } from "./RadioSetting.js";
import { BooleanSetting } from "./BooleanSetting.js";
import { StringSetting } from "./StringSetting.js";
import { NumericSetting } from "./NumericSetting.js";
import { Section } from "../Section.js";

export const Group = view(({ setting, get, set, filter = () => true, className }) => (
    <Section className={className} label={setting.label}>
      {
        setting.settings.filter(filter).map(s => {
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
    return <BooleanSetting label={s.label} checked={get(s)} onChange={v => set(s, v)} />;
  },

  "string": (s, get, set) => {
    return <StringSetting value={get(s)} onChange={v => set(s, v)} {...s} />
  },

  "enum": (s, get, set) => {
    if (s.ui === "radio") {
      return <RadioSetting label={s.label} selected={get(s)} onChange={v => set(s, v)}
                           options={s.options} />;
    }
  },
  "number": (s, get, set) => {
    return <NumericSetting value={get(s)} onChange={v => set(s, v)} {...s} />;
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