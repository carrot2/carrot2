import React from "react";

import { view } from "@risingstack/react-easy-state";

import { firstField } from "../../../carrotsearch/lang/objects.js";
import _set from "lodash.set";
import { Setting } from "../../../carrotsearch/settings/Setting.js";
import { TextArea } from "@blueprintjs/core";

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

const ExclusionsSetting = view(({ setting, get, set, type }) => {
  const { label, description } = setting;

  const findEntry = () => {
    const array = get(setting);
    return array.find(e => Array.isArray(e[type]));
  };

  // Look for the first entry containing the "glob" list or create one.
  const getExclusions = () => {
    const entry = findEntry();
    return entry ? entry[type].join("\r") : "";
  };

  const setExclusions = val => {
    const entry = findEntry();
    const split = val.trim().length > 0 ? val.split("\n") : [];
    if (entry) {
      entry[type] = split;
    } else {
      const array = get(setting);
      array.push({ [type]: split });
    }

    // Set a new shallow copy so that the upstream code sees the change we made inside the array.
    set(setting, get(setting).slice(0));
  };

  return (
    <Setting
      className="StringListSetting"
      label={label}
      description={description}
    >
      <TextArea
        style={{ width: "100%", minHeight: "8rem" }}
        value={getExclusions()}
        onChange={e => setExclusions(e.target.value)}
      />
    </Setting>
  );
});

export const createExcludedLabelsSetting = () => {
  return [
    {
      id: "dictionaries.labelFilters",
      label: "Excluded label patterns",
      pathRest: "dictionaries.labelFilters",
      factory: (s, get, set) => (
        <ExclusionsSetting setting={s} get={get} set={set} type="glob" />
      ),
      description: `
<p>
 If a phrase matches any pattern listed, the phrase will not be used as a cluster label.
 Put one pattern per line. Separate words with spaces, <code>*</code> matches zero or more words.
</p>

<h4>Pattern syntax and matching rules</h4>

<ul>
  <li>Each entry must consist of one or more space-separated tokens.</li>
  <li>A token can be a sequence of arbitrary characters, such as words, numbers, identifiers.</li>
  <li>Matching is case-insensitive by default.</li>
  <li>The <code>*</code> token matches zero or more words.</li>
  <li>
    Using the <code>*</code> wildcard character in combination with other characters, for
    example 1<code>programm*</code>, is not supported.
  </li>
  <li>
    Token put in double quotes, for example <code>"Rating***"</code> is taken literally: matching
    is case-sensitive, <code>*</code> characters are allowed and taken literally.
  </li>
  <li>
    To include double quotes as part of the token, escape them with the <code>\\</code> character,
    for example: <code>\\"information\\"</code>.
  </li>
</ul>

<h4>Example patterns</h4>

<dl>
  <dt><code>more information</code> (exact match)</dt>
  <dd>
    Matches:
    <ul>
      <li><code>more information</code></li>
      <li><code>More information</code></li>
      <li><code>MORE INFORMATION</code></li>
    </ul>

    Does not match:

    <ul>
      <li><code title="'informations' does not match pattern token 'information'.">more informations</code><br/></li>
      <li><code title="Pattern does not contain wildards, only 2-word strings can match.">more information about</code><br/></li>
      <li><code title="Pattern does not contain wildards, only 2-word strings can match.">some more information</code></li>
    </ul>
  </dd>

  <dt><code>more information *</code> (leading match)</dt>
  <dd>
    Matches:
    <ul>
      <li><code>more information</code></li>
      <li><code>More information about</code></li>
      <li><code>More information about a</code></li>
    </ul>

    Does not match:

    <ul>
      <li title="'informations' does not match pattern token 'information'."><code>informations</code></li>
      <li title="'informations' does not match pattern token 'information'."><code>more informations about</code></li>
      <li title="'informations' does not match pattern token 'information'."><code>some more informations</code></li>
    </ul>
  </dd>


  <dt><code>* more information *</code> (containing match)</dt>
  <dd>
    Matches:
    <ul>
      <li><code>information</code></li>
      <li><code>more information</code></li>
      <li><code>information about</code></li>
      <li><code>a lot more information on</code></li>
    </ul>

    Does not match:
    <ul>
      <li title="'informations' does not match pattern token 'information'."><code>informations</code></li>
      <li title="'informations' does not match pattern token 'information'."><code>more informations about</code></li>
      <li title="'informations' does not match pattern token 'information'."><code>some more informations</code></li>
    </ul>
  </dd>

  <dt><code>"Information" *</code> (literal match)</dt>
  <dd>
    Matches:
    <ul>
      <li><code>Information</code></li>
      <li><code>Information about</code></li>
      <li><code>Information ABOUT</code></li>
    </ul>

    Does not match:
    <ul>
      <li title="&quot;Information&quot; token is case-sensitive, it does not match 'information'."><code>information</code></li>
      <li title="&quot;Information&quot; token is case-sensitive, it does not match 'information'."><code>information about</code></li>
      <li title="'Informations' does not match pattern token &quot;Information&quot;."><code>Informations about</code></li>
    </ul>
  </dd>

  <dt><code>"Programm*"</code> (literal match)</dt>
  <dd>
    Matches:
    <ul>
      <li><code>Programm*</code></li>
    </ul>

    Does not match:
    <ul>
      <li title="&quot;Programm*&quot; token is taken literally, it matches only 'Programm*'."><code>Programmer</code></li>
      <li title="&quot;Programm*&quot; token is taken literally, it matches only 'Programm*'."><code>Programming</code></li>
    </ul>
  </dd>

  <dt><code>\\"information\\"</code> (escaping quote characters)</dt>
  <dd>
    Matches:
    <ul>
      <li><code>"information"</code></li>
    </ul>

    Does not match:
    <ul>
      <li title="Escaped quotes are taken literally, so match is case-insensitive"><code>"INFOrmation"</code></li>
      <li title="Escaped quotes not found in the string being matched."><code>information</code></li>
      <li title="Escaped quotes not found in the string being matched."><code>"information</code></li>
    </ul>
  </dd>

  <dt><code>programm*</code></dt>
  <dd>
    Illegal pattern, combinations of the <code>*</code> wildcard and other characters are not supported.
  </dd>

  <dt><code>"information</code></dt>
  <dd>
    Illegal pattern, unbalanced double quotes.
  </dd>

  <dt><code>*</code></dt>
  <dd>
    Illegal pattern, there must be at least one non-wildcard token.
  </dd>
</dl>`
    },

    {
      id: "dictionaries.wordFilters",
      label: "Stop words",
      pathRest: "dictionaries.wordFilters",
      description: `
<p>
  List of words to exclude from processing. Put one word per line, matching is
  case-insensitive.
</p>`,
      factory: (s, get, set) => (
        <ExclusionsSetting setting={s} get={get} set={set} type="exact" />
      )
    }
  ];
};
