import xmlParser from "fast-xml-parser";

import { Stats } from "fast-stats";

import { forEachOwnProp, incrementInMap } from "../../../carrotsearch/lang/objects.js";

const EMPTY = {
  fieldsAvailable: [],
  fieldsAvailableForClustering: [],
  fieldsToCluster: [],
  query: "",
  documents: []
};

/**
 * Collects all field names appearing in the submitted JSON documents.
 */
const collectFieldNames = json => {
  return Array.from(json.reduce((set, entry) => {
    forEachOwnProp(entry, (val, prop) => {
      set.add(prop);
    });
    return set;
  }, new Set()));
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
      incrementInMap(typeCounts.get(prop), Object.prototype.toString.call(v), 1);
    });
  });

  return Array.from(typeCounts.keys()).reduce((map, f) => {
    let maxCount = 0, majorityType = null;
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

/**
 * Collects some statistics about the field values. The statistics may be useful in guessing
 * which fields to cluster and which fields to show in the document view.
 */
const collectStringFieldValueStats = (json, fields) => {
  const stats = fields.reduce((map, f) => {
    map.set(f, { length: new Stats(), count: new Stats(), empty: 0, distinct: 0 });
    return map;
  }, new Map());

  fields.forEach(f => {
    const values = new Set();
    json.forEach(entry => {
      const val = entry[f];
      const s = stats.get(f);
      if (!val) {
        s.empty++;
        return;
      }

      // Count each array value separately.
      if (Array.isArray(val)) {
        s.count.push(val.length);
        val.forEach(v => {
          s.length.push(v.length);
          values.add(v);
        });
      } else {
        s.count.push(1);
        s.length.push(val.length);
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
      }
    });
    return map;
  }, new Map());
};

/**
 * Computes various score helping to determine whether the field is clusterable, contains
 * a document title etc.
 */
const collectFileTypeScores = (fields, docCount) => {
  fields.forEach(f => {
    if (f.type !== "String") {
      f.titleScore = 0;
      f.naturalTextScore = 0;
      return;
    }

    let titleScore = 1.0;
    let naturalTextScore = 1.0;

    if (/title/i.test(f.field)) {
      titleScore *= 2.0;
      naturalTextScore *= 2.0;
    }

    if (f.distinct === docCount) {
      titleScore *= 2.0;
      naturalTextScore *= 2.0;
    }

    if (f.length.avg > 10 && f.length.avg < 140) {
      titleScore *= 2.0;
    }

    if (f.length.avg <= 16) {
      naturalTextScore = 0;
    }

    f.titleScore = titleScore;
    f.naturalTextScore = naturalTextScore;
  });
};

const collectFieldInformation = json => {
  const allFieldNames = collectFieldNames(json);
  const types = collectFieldMajorityTypes(json, allFieldNames);
  const stats = collectStringFieldValueStats(json,
      allFieldNames.filter(f => types.get(f) === "String"));

  // Combine all the information
  const fields = allFieldNames.map(f => {
    return Object.assign({
      field: f,
      type: types.get(f)
    }, stats.get(f));
  });

  collectFileTypeScores(fields, json.length);
  return fields;
};

const prepareResult = object => {
  const fields = collectFieldInformation(object);

  const allFields = fields.map(f => f.field);
  const naturalTextFields = fields.filter(f => f.naturalTextScore >= 1).map(f => f.field);
  return {
    fieldStats: fields,
    fieldsAvailable: allFields,
    fieldsAvailableForClustering: naturalTextFields,
    fieldsToCluster: naturalTextFields,
    query: "",
    documents: object
  };
};

const parseSheet = async (file, logger) =>{
  const XLSX = await import("xlsx");
  const buffer = await file.arrayBuffer();
  const workbook = XLSX.read(buffer, { type: "array" });
  const worksheet = workbook.Sheets[workbook.SheetNames[0]];

  const NO_DATA_MESSAGE = "The spreadsheet contains no data.";

  const rangeRaw = worksheet["!ref"];
  if (!rangeRaw) {
    logger.info(NO_DATA_MESSAGE);
    return EMPTY;
  }

  // Check if there is any data. The first row is a heading.
  const range = XLSX.utils.decode_range(rangeRaw);
  if (range.e.r <= 0) {
    logger.info(NO_DATA_MESSAGE);
    return EMPTY;
  }

  const get = (r, c) => worksheet[XLSX.utils.encode_cell({ c: c, r: r })];
  const getV = (r, c) => {
    const e = get(r, c);
    return e && e.v;
  };

  const fields = [];
  for (let c = 0; c <= range.e.c; c++) {
    fields.push(getV(0, c));
  }

  const documents = [];
  for (let r = 1; r < range.e.r; r++) {
    const doc = {};
    for (let c = 0; c <= range.e.c; c++) {
      doc[fields[c]] = getV(r, c);
    }
    documents.push(doc);
  }

  return prepareResult(documents);
};


const parsers = {
  "text/xml": async (file, logger) => {
    const xml = await file.text();
    const xmlJson = xmlParser.parse(xml);

    if (!xmlJson.searchresult) {
      logger.error("XML must be in Carrot2 format.");
      return EMPTY;
    } else {
      return {
        fieldsAvailable: [ "title", "snippet", "url" ],
        fieldsAvailableForClustering: [ "title", "snippet" ],
        fieldsToCluster: [ "title", "snippet" ],
        query: xmlJson.searchresult.query,
        documents: xmlJson.searchresult.document
      };
    }
  },

  "application/json": async (file, logger) => {
    const json = await file.text();
    return prepareResult(JSON.parse(json));
  },

  "application/vnd.oasis.opendocument.spreadsheet": parseSheet,
  "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet": parseSheet,
  "application/vnd.ms-excel": parseSheet
};

export const parseFile = async (file, logger) => {
  const parser = parsers[file.type];
  if (!parser) {
    logger.error(`Unknown file type ${file.type}.`);
    return EMPTY;
  }

  return parser(file, logger);
};