
using System;
using System.Reflection;
using System.IO;
using System.Xml.Serialization;
using System.Xml;
using System.Xml.XPath;
using System.Collections;

/// 
/// <summary>
/// Clustering examples. Read and cluster XML data from disk, get
/// XML data from an external source, print the results to console.
/// </summary>
/// 
/// See http://localhost:8080 for documentation concerning service parameters.
///
namespace Org.Carrot2.Examples
{
    public sealed class DCSRestExample
    {
        public static void Main()
        {
            MultipartFileUpload service = new MultipartFileUpload();
            service.Uri = new Uri("http://localhost:8080/dcs/rest");

            XmlDocument result;

            /*
             * An example of clustering data stored in a local file and passed
             * as part of the HTTP request. A query hint is provided for the
             * clustering algorithm (to avoid trivial clusters).
             */
            Console.WriteLine("## Clustering data from file...");
            result = clusterFromFile(service, "input\\data-mining.xml", "data mining");
            printResults(result);

            /*
             * An example of clustering data fetched from an external document source.
             */
            Console.WriteLine("## Clustering data from external source...");
            result = clusterFromSource(service, "boss-web", "data mining");
            printResults(result);

            Console.ReadLine();
        }

        /*
         * 
         */
        private static XmlDocument clusterFromFile(MultipartFileUpload service, string filePath, string queryHint)
        {
            // The output format is XML.
            service.AddFormValue("dcs.output.format", "XML");

            // We don't need documents in the output, only clusters.
            service.AddFormValue("dcs.clusters.only", "true");

            // Pass query hint.
            service.AddFormValue("query", queryHint);

            // The algorithm to use for clustering. Omit to select the default. An example of
            // using Lingo with custom parameters follows.

            // service.AddFormValue("dcs.algorithm", "lingo");
            // service.AddFormValue("LingoClusteringAlgorithm.desiredClusterCountBase", "10");
            // service.AddFormValue("LingoClusteringAlgorithm.factorizationQuality", "LOW");

            // [TODO] this currently fails to bind on the server side, will check it later.
            // service.AddFormValue("LingoClusteringAlgorithm.factorizationFactory",
            //     "org.carrot2.matrix.factorization.PartialSingularValueDecompositionFactory");

            // Add the XML stream here.
            service.AttachFile("input.xml", "dcs.c2stream", new FileInfo(filePath));

            // Perform the actual query.
            byte[] response = service.UploadFileEx();

            // Parse the output and dump group headers.
            MemoryStream input = new MemoryStream(response);

            XmlDocument document = new XmlDocument();
            document.PreserveWhitespace = true;
            document.Load(input);

            return document;
        }

        /*
         * 
         */
        private static XmlDocument clusterFromSource(MultipartFileUpload service, string sourceId, string query)
        {
            // The output format is XML.
            service.AddFormValue("dcs.output.format", "XML");

            // This time we will be interested in both clusters and documents.
            service.AddFormValue("dcs.clusters.only", "false");

            // Add query.
            service.AddFormValue("query", query);

            // Specify the source.
            service.AddFormValue("dcs.source", sourceId);

            // Perform the actual query.
            byte[] response = service.UploadFileEx();

            // Parse the output and dump group headers.
            MemoryStream input = new MemoryStream(response);

            XmlDocument document = new XmlDocument();
            document.PreserveWhitespace = true;
            document.Load(input);

            return document;
        }

        /*
         * 
         */
        private static void printResults(XmlDocument document)
        {
            foreach (XmlNode group in
                document.SelectNodes("/searchresult/group"))
            {
                Console.WriteLine(group.SelectSingleNode("title/phrase").InnerText);
                Console.WriteLine("Documents: " + group.SelectNodes("document").Count);
                Console.WriteLine();
            }
        }
    }
}
