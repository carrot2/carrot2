import descriptor from "./descriptors/org.carrot2.clustering.lingo.LingoClusteringAlgorithm.json";

const isContainer = descriptor => {
  const implementations = descriptor.implementations;
  if (implementations) {
    const implementationKeys = Object.keys(implementations);
    return implementationKeys.length === 1 && descriptor.type
        === implementations[implementationKeys[0]].type;
  }
  return false;
};

const depthFirstAttributes = descriptor => {
  const collect = (descriptor, target) => {
    Object.keys(descriptor.attributes).forEach(k => {
      const attribute = descriptor.attributes[k];

      if (!isContainer(attribute)) {
        target.push(attribute);
      }

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
  return depthFirstAttributes(descriptor).reduce((map, a) => {
    map.set(a.id, a);
    return map;
  }, new Map());
}

const parseNumberConstraintValue = constraint => {
  const split = constraint.split(/\s+/);
  return parseFloat(split[2]);
};

const settingConfigFromNumberDescriptor = descriptor => {
  const c1 = parseNumberConstraintValue(descriptor.constraints[0]);
  const c2 = parseNumberConstraintValue(descriptor.constraints[1]);
  const min = Math.min(c1, c2);
  const max = Math.max(c1, c2);

  return {
    type: "number",
    min: min,
    max: max,
    step: (max - min) / 10
  }
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
        description: implementations[impl].javadoc.summary
      };
    })
  }
};

const settingConfigFromEnumDescriptor = descriptor => {
  const values = descriptor.constraints[0].split(/[\[\]]/)[1].split(/,\s*/);
  return {
    type: "enum",
    ui: "select",
    options: values.map(v => {
      return {
        value: v,
        label: v
      };
    })
  }
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
    description: descriptor.javadoc.text
  };

  if (descriptor.implementations) {
    Object.assign(setting, settingConfigFromInterfaceDescriptor(descriptor));
  } else if (descriptor?.constraints?.[0].startsWith("value in")) {
    Object.assign(setting, settingConfigFromEnumDescriptor(descriptor));
  } else {
    switch (descriptor.type) {
      case "Double":
      case "Float":
        Object.assign(setting, settingConfigFromNumberDescriptor(descriptor));
        break;

      case "Integer":
        Object.assign(setting, settingConfigFromNumberDescriptor(descriptor));
        setting.integer = true;
        break;

      case "Boolean":
        setting.type = "boolean";
        break;

      default:
        throw new Error(`Unsupported type ${descriptor.type} for id ${id}`);
    }
  }

  return Object.assign(setting, override);
};

export const settingFromDescriptorRecursive = (map, id, getterProvider, override = () => null) => {
  const descriptor = getDescriptor(map, id);
  const rootSetting = settingFromDescriptor(map, id, override(descriptor));

  const implementations = descriptor.implementations;
  if (implementations) {
    return {
      type: "group",
      id: descriptor.id + ":children",
      settings: [
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
                visible: () => { return getterProvider()(rootSetting) === k },
                settings: Object.keys(implAttributes).map(ak => {
                  return settingFromDescriptorRecursive(map, implAttributes[ak].id, override);
                })
              };
            })
      ]
    };
  } else {
    return rootSetting;
  }
};

export const collectDefaults = (map, settings) => settings.flat().reduce(function collect(defs, setting) {
  if (setting.type === "group") {
    setting.settings.reduce(collect, defs);
  } else {
    defs[setting.id] = map.get(setting.id).value;
  }
  return defs;
}, {});


