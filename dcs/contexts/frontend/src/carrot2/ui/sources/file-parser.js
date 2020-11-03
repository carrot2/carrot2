import xmlParser from "fast-xml-parser";

const parsers = {
  "text/xml": async (file, logger) => {
    const xml = await file.text();
    const xmlJson = xmlParser.parse(xml);

    if (!xmlJson.searchresult) {
      logger.error("XML must be in Carrot2 format.");
    } else {
      return {
        fieldsAvailable: [ "title", "snippet", "url" ],
        fieldsAvailableForClustering: [ "title", "snippet" ],
        fieldsToCluster: [ "title", "snippet" ],
        query: xmlJson.searchresult.query,
        documents: xmlJson.searchresult.document
      };
    }
  }
};

export const parseFile = async (file, logger) => {
  const parser = parsers[file.type];
  if (!parser) {
    logger.error(`Unknown file type ${file.type}.`);
    return;
  }

  return parser(file, logger);
};