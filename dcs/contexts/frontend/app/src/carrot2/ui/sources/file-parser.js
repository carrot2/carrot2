import { pluralize } from "@carrotsearch/ui/lang/humanize.js";

const parseSheet = async (file, logger) => {
  const XLSX = await import("xlsx");
  const buffer = await file.arrayBuffer();
  const workbook = XLSX.read(buffer, { type: "array" });
  const worksheet = workbook.Sheets[workbook.SheetNames[0]];

  const NO_DATA_MESSAGE = "The spreadsheet contains no data.";

  const rangeRaw = worksheet["!ref"];
  if (!rangeRaw) {
    logger.info(NO_DATA_MESSAGE);
    return EMPTY_PARSER_RESULT;
  }

  // Check if there is any data. The first row is a heading.
  const range = XLSX.utils.decode_range(rangeRaw);
  if (range.e.r <= 0) {
    logger.info(NO_DATA_MESSAGE);
    return EMPTY_PARSER_RESULT;
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

  return parserResultFrom(documents);
};

const EMPTY_PARSER_RESULT = {
  documents: [],
  query: ""
};

const parserResultFrom = (documents, query = "") => {
  return {
    documents: documents,
    query: query
  };
};

const parsers = {
  "text/xml": async (file, logger) => {
    const xml = await file.text();
    const xmlParser = await import(
      /* webpackChunkName: "xml-parser" */ "fast-xml-parser"
    );
    const xmlJson = xmlParser.parse(xml);

    if (!xmlJson.searchresult) {
      logger.error("XML must be in Carrot2 format.");
      return EMPTY_PARSER_RESULT;
    } else {
      return parserResultFrom(
        xmlJson.searchresult.document,
        xmlJson.searchresult.query
      );
    }
  },

  "application/json": async file => {
    return parserResultFrom(JSON.parse(await file.text()));
  },

  "application/vnd.oasis.opendocument.spreadsheet": parseSheet,
  "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet": parseSheet,
  "application/vnd.ms-excel": parseSheet
};

export const parseFile = async (file, logger) => {
  const parser = parsers[file.type];
  if (!parser) {
    logger.error(`Unknown file type ${file.type}.`);
    return EMPTY_PARSER_RESULT;
  }
  const result = await parser(file, logger);

  logger.log(`${pluralize(result.documents.length, "document", true)} loaded.`);

  return result;
};
