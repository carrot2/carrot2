
using System;
using System.Reflection;
using System.IO;
using System.Xml.Serialization;
using System.Xml;
using System.Xml.XPath;
using System.Collections;

/// 
/// <summary>
/// Contains several examples of clustering using the Document Clustering Server.
/// </summary>
///
namespace Org.Carrot2.Examples
{
    public sealed class Examples
    {
        public static void Main()
        {
            MultipartFileUpload service = new MultipartFileUpload(new Uri("http://localhost:8080/dcs/rest"));

            string examplePath = "..\\..\\..\\shared\\data-mining.xml";
            if (!File.Exists(examplePath))
            {
                Console.WriteLine("Input path does not exist: " + examplePath);
                return;
            }

            // Cluster directly from file (no buffering).
            Console.WriteLine("## Clustering documents from a file...");
            ClusterFromFile(service, "..\\..\\..\\shared\\data-mining.xml", "data mining");

            // Cluster from an XML in memory.
            string xml = File.ReadAllText(examplePath, System.Text.Encoding.UTF8);
            Console.WriteLine("## Clustering documents from an XML string...");
            ClusterFromStream(service, 
                new MemoryStream(System.Text.Encoding.UTF8.GetBytes(xml)), "data mining");

            // Cluster form an external document source (on the DCS).
            Console.WriteLine("## Clustering search results from a search engine...");
            ClusterFromSearchEngine(service, "etools", "data mining");
        }

        /// <summary>
        /// An example of clustering data from an arbitrary byte stream holding input XML for
        /// the DCS (can be an in-memory stream if clustering from a string).
        /// </summary>
        private static void ClusterFromStream(MultipartFileUpload service, Stream xmlStream, string queryHint)
        {
            // The output format is XML.
            service.AddFormValue("dcs.output.format", "XML");

            // We don't need documents in the output, only clusters.
            service.AddFormValue("dcs.clusters.only", "true");

            // Pass query hint.
            service.AddFormValue("query", queryHint);

            // The algorithm to use for clustering. Omit to select the default. An example of
            // using Lingo with custom parameters follows.

            service.AddFormValue("dcs.algorithm", "lingo");
            service.AddFormValue("LingoClusteringAlgorithm.desiredClusterCountBase", "10");
            service.AddFormValue("LingoClusteringAlgorithm.factorizationQuality", "LOW");
            service.AddFormValue("LingoClusteringAlgorithm.factorizationFactory",
                "org.carrot2.matrix.factorization.PartialSingularValueDecompositionFactory");

            // Add the XML stream here.
            service.AddFormStream("dcs.c2stream", "anything.xml", xmlStream);

            // Perform the actual query.
            byte[] response = service.Post();

            // Parse the output and dump group headers.
            MemoryStream input = new MemoryStream(response);

            XmlDocument document = new XmlDocument();
            document.PreserveWhitespace = true;
            document.Load(input);

            PrintResults(document);
        }

        /// <summary>
        /// An example of clustering data stored in a local file and passed
        /// as part of the HTTP request. A query hint is provided for the
        /// clustering algorithm (to avoid trivial clusters).
        /// </summary>
        private static void ClusterFromFile(MultipartFileUpload service, string filePath, string queryHint)
        {
            using (FileStream fs = File.Open(filePath, FileMode.Open))
            {
                ClusterFromStream(service, fs, queryHint);
            }
        }

        /// <summary>
        /// Cluster data retrieved from a search engine or some other source registered in the
        /// DCS as a document source.
        /// </summary>
        private static void ClusterFromSearchEngine(MultipartFileUpload service, string sourceId, string query)
        {
            // The output format is XML.
            service.AddFormValue("dcs.output.format", "XML");

            // This time we will be interested in both clusters and documents.
            service.AddFormValue("dcs.clusters.only", "false");

            // Add query.
            service.AddFormValue("query", query);

            // Add the number of results.
            service.AddFormValue("results", "20");

            // Specify the source.
            service.AddFormValue("dcs.source", sourceId);

            // Perform the actual query.
            byte[] response = service.Post();

            // Parse the output and dump group headers.
            MemoryStream input = new MemoryStream(response);

            XmlDocument document = new XmlDocument();
            document.PreserveWhitespace = true;
            document.Load(input);

            PrintResults(document);
        }

        /// <summary>
        /// Dump the result (group labels).
        /// </summary>
        private static void PrintResults(XmlDocument document)
        {
            foreach (XmlNode group in
                document.SelectNodes("/searchresult/group"))
            {
                PrintGroup(group, 1);
            }
            Console.WriteLine();
        }

        /// <summary>
        /// Dump a single cluster and its subclusters.
        /// </summary>
        private static void PrintGroup(XmlNode group, int level)
        {
            for (int i = 0; i < level; i++) Console.Write("  ");
            Console.Write(group.SelectSingleNode("title/phrase").InnerText);

            Console.WriteLine(" [" + group.Attributes["size"].Value + " document(s)]");

            foreach (XmlNode subgroup in group.SelectNodes("group"))
            {
                PrintGroup(subgroup, level + 1);
            }
        }
    }
}
