import React from "react";

import "./settings/ExclusionsSetting.css";

import _set from "lodash.set";

import { firstField } from "@carrotsearch/ui/lang/objects.js";
import {
  createExclusionViews,
  ExclusionsSetting
} from "@carrot2/app/service/algorithms/settings/ExclusionsSetting.js";
import { persistentStore } from "@carrotsearch/ui/store/persistent-store.js";

const depthFirstAttributes = descriptor => {
  const collect = (descriptor, target) => {
    Object.keys(descriptor.attributes).forEach(k => {
      const attribute = descriptor.attributes[k];

      target.push(attribute);

      if (attribute.attributes) {
        collect(attribute, target);
      }
      if (attribute.implementations) {
        const keys = Object.keys(attribute.implementations);
        keys.forEach(k => collect(attribute.implementations[k], target));
      }
    });

    return target;
  };

  return collect(descriptor, []);
};

export const getDescriptorsById = descriptors => {
  return depthFirstAttributes(descriptors).reduce((map, a) => {
    map.set(a.id, a);
    return map;
  }, new Map());
};

const parseNumberConstraint = constraints => {
  return (constraints || []).reduce((result, constraint) => {
    const split = constraint.split(/\s+/);
    const bound = parseFloat(split[2]);
    switch (split[1].charAt(0)) {
      case ">":
        result.min = bound;
        break;
      case "<":
        result.max = bound;
        break;

      default:
        throw new Error("Unknown constraint: " + constraint);
    }

    return result;
  }, {});
};

const settingConfigFromNumberDescriptor = descriptor => {
  const constraints = parseNumberConstraint(descriptor.constraints);
  return {
    type: "number",
    ...constraints
  };
};

const settingConfigFromInterfaceDescriptor = descriptor => {
  const implementations = descriptor.implementations;
  return {
    type: "enum",
    ui: "select",
    options: Object.keys(implementations).map(impl => {
      return {
        value: impl,
        label: impl,
        description: implementations[impl].javadoc.text
      };
    })
  };
};

const settingConfigFromEnumDescriptor = descriptor => {
  const values = descriptor.constraints[0].split(/[[\]]/)[1].split(/,\s*/);
  return {
    type: "enum",
    ui: "select",
    options: values.map(v => {
      return {
        value: v,
        label: v
      };
    })
  };
};

const getDescriptor = (map, id) => {
  const descriptor = map.get(id);
  if (descriptor) {
    return descriptor;
  } else {
    throw new Error(`Unknown attribute ${id}.`);
  }
};

export const settingFromDescriptor = (map, id, override) => {
  const descriptor = getDescriptor(map, id);
  const setting = {
    id: id,
    label: descriptor.description,
    description: descriptor.javadoc.text,
    pathRest: descriptor.pathRest
  };

  if (descriptor.implementations) {
    Object.assign(setting, settingConfigFromInterfaceDescriptor(descriptor));
  } else if (descriptor?.constraints?.[0].startsWith("value in")) {
    Object.assign(setting, settingConfigFromEnumDescriptor(descriptor));
  } else {
    switch (descriptor.type) {
      case "Double":
      case "Float":
        Object.assign(
          setting,
          settingConfigFromNumberDescriptor(descriptor, override)
        );
        break;

      case "Integer":
        Object.assign(
          setting,
          settingConfigFromNumberDescriptor(descriptor, override)
        );
        setting.integer = true;
        break;

      case "Boolean":
        setting.type = "boolean";
        break;

      case "String[]":
        setting.type = "string-array";
        break;

      default:
        throw new Error(`Unsupported type ${descriptor.type} for id ${id}`);
    }
  }

  return Object.assign(setting, override);
};

export const settingFromDescriptorRecursive = (
  map,
  id,
  getterProvider,
  override = () => null
) => {
  const descriptor = getDescriptor(map, id);
  const rootSetting = settingFromDescriptor(map, id, override(descriptor));
  const implementations = descriptor.implementations;
  if (implementations) {
    rootSetting.pathRest += ".@type";
  }

  if (implementations) {
    return [
      rootSetting,
      ...Object.keys(implementations)
        .filter(k => {
          const implAttributes = implementations[k].attributes;
          return Object.keys(implAttributes).length > 0;
        })
        .map(k => {
          const implAttributes = implementations[k].attributes;
          return {
            type: "group",
            id: descriptor.id + ":" + k,
            visible: () => getterProvider()(rootSetting) === k,
            settings: Object.keys(implAttributes)
              .map(ak => {
                return settingFromDescriptorRecursive(
                  map,
                  implAttributes[ak].id,
                  override
                );
              })
              .flat()
          };
        })
    ];
  } else {
    return [rootSetting];
  }
};

export const settingFromFilterDescriptor = (map, id, getterProvider) => {
  const rootDescriptor = getDescriptor(map, id);
  const enabledSetting = settingFromDescriptor(map, id + ".enabled");

  const implementation = firstField(rootDescriptor.implementations);
  enabledSetting.description = implementation.javadoc.text;

  const attributes = implementation.attributes;
  const settings = Object.keys(attributes)
    .filter(att => att !== "enabled")
    .map(a => {
      return settingFromDescriptor(map, attributes[a].id);
    });
  if (settings.length > 0) {
    return [
      enabledSetting,
      {
        type: "group",
        id: rootDescriptor.id + ":children",
        visible: () => getterProvider()(enabledSetting),
        settings: settings
      }
    ];
  } else {
    return [enabledSetting];
  }
};

export const collectDefaults = (map, settings) =>
  settings.flat().reduce(function collect(defs, setting) {
    if (setting.type === "group") {
      setting.settings.reduce(collect, defs);
    } else {
      defs[setting.id] = map.get(setting.id).value;
    }
    return defs;
  }, {});

export const collectParameters = (settings, getter, filter) =>
  settings.reduce(function collect(params, setting) {
    if (setting.visible && !setting.visible()) {
      return params;
    }
    if (setting.type === "group") {
      setting.settings.reduce(collect, params);
    } else {
      if (setting.pathRest) {
        const value = getter(setting);
        if (!filter || filter(setting, value)) {
          _set(params, setting.pathRest, value);
        }
      }
    }

    return params;
  }, {});

export const advanced = setting => {
  setting.advanced = true;
  return setting;
};

const createActiveViewStore = (algorithmId, paramName) => {
  const activeViewStore = persistentStore(
    `workbench:settings:${algorithmId}:${paramName}:view`,
    {
      activeView: "glob"
    },
    {
      get: () => activeViewStore.activeView,
      set: v => (activeViewStore.activeView = v)
    }
  );
  return activeViewStore;
};

const patternTypesHelp = `
<p>
  Three pattern types are available:
</p>

<ul>
  <li>
    <strong>glob</strong>: allows simple word-based wildcard matching. 
    Use it for case-insensitive matching of literal phrases, as well as "begins with…", 
    "ends with…" or "contains…" types of expressions.
  </li>
  <li>
    <strong>exact</strong>: requires exact case-sensitive equality between the word or phrase
    and the dictionary entry.  
  </li>
  <li>
    <strong>regex</strong>: word and phrase filtering based on Java regular expressions.
  </li>
</ul>

<p>
  Combine some or all pattern types as required.
</p>`;

export const createExcludedLabelsSetting = (algorithmId, viewCustomizer) => {
  const activeViewStore = createActiveViewStore(algorithmId, "labelExclusions");
  const views = createExclusionViews(viewCustomizer);

  return {
    id: "dictionaries.labelFilters",
    label: "Excluded label patterns",
    pathRest: "dictionaries.labelFilters",
    factory: (s, get, set) => (
      <ExclusionsSetting
        setting={s}
        get={get}
        set={set}
        views={views}
        getActiveView={activeViewStore.get}
        setActiveView={activeViewStore.set}
      />
    ),
    description: `
<p>
  Label exclusion patterns. If a word or a phrase matches any of the patterns provided here,
  it will not be used as a cluster label.  
</p>

${patternTypesHelp}`
  };
};

export const createExcludedWordsSetting = algorithmId => {
  const activeViewStore = createActiveViewStore(algorithmId, "wordExclusions");
  const views = createExclusionViews();

  return {
    id: "dictionaries.wordFilters",
    label: "Stop words",
    pathRest: "dictionaries.wordFilters",
    description: `
<p>
  List of words to exclude from processing. If a word is excluded, it will not have
   influence on the clusters the algorithm creates, but may still appear in cluster 
   labels, such as <em>University of Washington</em>, to aid readability.
</p>

${patternTypesHelp}`,
    factory: (s, get, set) => (
      <ExclusionsSetting
        setting={s}
        get={get}
        set={set}
        views={views}
        getActiveView={activeViewStore.get}
        setActiveView={activeViewStore.set}
      />
    )
  };
};
