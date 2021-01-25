import { Stats } from "fast-stats";

import {
  forEachOwnProp,
  incrementInMap
} from "@carrotsearch/ui/lang/objects.js";

/**
 * Collects all field names appearing in the submitted JSON documents.
 */
const collectFieldNames = json => {
  return Array.from(
    json.reduce((set, entry) => {
      forEachOwnProp(entry, (val, prop) => {
        set.add(prop);
      });
      return set;
    }, new Set())
  );
};

/**
 * Collects the majority types of the submitted JSON documents. If the field is an array,
 * the type of the first element is taken.
 */
const collectFieldMajorityTypes = (json, fields) => {
  const typeCounts = fields.reduce((map, f) => {
    map.set(f, new Map());
    return map;
  }, new Map());

  json.forEach(entry => {
    forEachOwnProp(entry, (val, prop) => {
      const v = Array.isArray(val) ? val?.[0] : val;
      incrementInMap(
        typeCounts.get(prop),
        Object.prototype.toString.call(v),
        1
      );
    });
  });

  return Array.from(typeCounts.keys()).reduce((map, f) => {
    let maxCount = 0,
      majorityType = null;
    typeCounts.get(f).forEach((count, type) => {
      if (maxCount < count) {
        maxCount = count;
        majorityType = type;
      }
    });

    map.set(f, majorityType.substring(0, majorityType.length - 1).substring(8));
    return map;
  }, new Map());
};

const countSpaces = str => {
  if (!str.charAt) {
    return 0;
  }
  let spaces = 0;
  for (let i = 0; i < str.length; i++) {
    if (str.charAt(i) === " ") {
      spaces++;
    }
  }
  return spaces;
};

/**
 * Collects some statistics about the field values. The statistics may be useful in guessing
 * which fields to cluster and which fields to show in the document view.
 */
const collectFieldValueStats = (json, fields) => {
  const stats = fields.reduce((map, f) => {
    map.set(f, {
      length: new Stats(),
      count: new Stats(),
      empty: 0,
      distinct: 0,
      spaces: new Stats()
    });
    return map;
  }, new Map());

  fields.forEach(f => {
    const values = new Set();
    json.forEach(entry => {
      const val = entry[f];
      const s = stats.get(f);
      if (val === null || val === undefined) {
        s.empty++;
        return;
      }

      // Count each array value separately.
      if (Array.isArray(val)) {
        s.count.push(val.length);
        val.forEach(v => {
          s.length.push(v.length || (v + "").length);
          s.spaces.push(countSpaces(v));
          values.add(v);
        });
      } else {
        s.count.push(1);
        s.length.push(val.length || (val + "").length);
        s.spaces.push(countSpaces(val));
        values.add(val);
      }
    });
    stats.get(f).distinct = values.size;
  });

  return fields.reduce((map, f) => {
    const s = stats.get(f);
    map.set(f, {
      empty: s.empty,
      distinct: s.distinct,
      length: {
        avg: s.length.amean(),
        dev: s.length.σ()
      },
      count: {
        avg: s.count.amean(),
        dev: s.count.σ()
      },
      spaces: {
        avg: s.spaces.amean(),
        dev: s.spaces.σ()
      }
    });
    return map;
  }, new Map());
};

/**
 * Computes various score helping to determine whether the field is clusterable, contains
 * a document title etc.
 */
const collectFieldTypeScores = (fields, docCount) => {
  fields.forEach(f => {
    let idScore = Math.pow(2, 16 / f.length.avg);
    let tagScore =
      ((f.count.avg - 1) * f.distinct) / (docCount * (f.spaces.avg + 1));
    let propScore = Math.pow(2, 16 / f.distinct) / (f.spaces.avg + 1);
    let titleScore = f.length.avg >= 4 ? 1.0 : 0;
    let naturalTextScore = f.spaces.avg;

    if (f.type !== "String") {
      titleScore = 0;
      naturalTextScore = 0;
    }

    if (/title/i.test(f.field)) {
      titleScore *= 4.0;
      naturalTextScore *= 2.0;
    }

    if (
      /content|body|abstract|comment|question|answer|post|message/i.test(
        f.field
      )
    ) {
      titleScore *= 2.0;
      naturalTextScore *= 2.0;
    }

    if (f.distinct === docCount && f.count.avg === 1) {
      titleScore *= 2.0;
      idScore *= 2;
    }

    const distinctRatio = f.distinct / (f.count.avg * docCount);
    if (distinctRatio === 1) {
      naturalTextScore *= 2.0;
    } else {
      // Penalize fields that contain repeated values.
      naturalTextScore *= 1 / Math.exp(Math.abs(2 * (distinctRatio - 1)));
    }

    if (f.distinct !== docCount) {
      idScore = 0;
    }

    if (f.length.avg > 10 && f.length.avg < 200) {
      titleScore *= 2.0;
    }

    if (f.length.avg <= 16) {
      naturalTextScore = 0;
    }

    f.titleScore = titleScore;
    f.naturalTextScore = naturalTextScore;
    f.idScore = idScore;
    f.tagScore = tagScore;
    f.propScore = propScore;
  });
};

const collectFieldInformation = json => {
  const allFieldNames = collectFieldNames(json);
  const types = collectFieldMajorityTypes(json, allFieldNames);
  const stats = collectFieldValueStats(json, allFieldNames);

  // Combine all the information
  const fields = allFieldNames.map(f => {
    return Object.assign(
      {
        field: f,
        type: types.get(f)
      },
      stats.get(f)
    );
  });

  collectFieldTypeScores(fields, json.length);
  return fields;
};

export const extractSchema = (documents, logger) => {
  const fields = collectFieldInformation(documents);

  const allFields = fields.map(f => f.field);
  const naturalTextFields = fields
    .filter(f => f.naturalTextScore >= 8 || f.titleScore >= 8)
    .map(f => f.field);

  // If some field looks like a URL, put it in the "url" property so that the URL becomes active.
  const re = /^https?:\/\//i;
  const stringFields = fields.filter(f => f.type === "String");
  documents.forEach(d => {
    if (d.url !== undefined) {
      return;
    }
    for (const f of stringFields) {
      const val = d[f.field];
      if (f.naturalTextScore < 2 && re.test(val)) {
        d.url = val;
        break;
      }
    }
  });

  return {
    fieldStats: fields,
    fieldsAvailable: allFields,
    fieldsAvailableForClustering: naturalTextFields,
    fieldsToCluster: naturalTextFields
  };
};
