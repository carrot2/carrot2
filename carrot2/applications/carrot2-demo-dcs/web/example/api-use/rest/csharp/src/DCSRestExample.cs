
using System;
using System.Reflection;
using System.IO;
using System.Xml.Serialization;
using System.Xml;
using System.Xml.XPath;
using System.Collections;

//
// This is a very simple example of using DCS REST interface
// from C#
//
namespace Org.Carrot2.Examples
{
    //
    // Clustering example. Read XML data from disk, cluster 
    // some slices and print the results to console.
    //
    public sealed class DCSRestExample
    {
        public static void Main()
        {
            MultipartFileUpload upload = new MultipartFileUpload();
            upload.Uri = new Uri("http://localhost:8080/rest/processor");

            // 'xml' or 'json'
            upload.AddFormValue("dcs.default.output", "xml");

            // If not present, documents are also returned. Usually
            // not required.
            upload.AddFormValue("dcs.clusters.only", "true");

            // The algorithm to use for clustering. Leave empty
            // for default.
            upload.AddFormValue("dcs.default.algorithm", "");

            // Add the XML stream here. It can be read directly
            // from a file or just placed as a string.

            // upload.AddFormValue("c2stream", "xml-string-here");
            upload.AttachFile("input.xml", "c2stream", new FileInfo("input\\data-mining.xml"));

            // Perform the actual query.
            byte [] response = upload.UploadFileEx();

            // Parse the output and dump group headers.
            MemoryStream input = new MemoryStream(response);

            XmlDocument document = new XmlDocument();
		    document.PreserveWhitespace = true;
            document.Load(input);

            foreach (XmlNode group in
                document.SelectNodes("/searchresult/group"))
            {
                Console.WriteLine(group.SelectSingleNode("title/phrase").InnerText);
                Console.WriteLine("Documents: " + group.SelectNodes("document").Count);
                Console.WriteLine();
            }

            Console.ReadLine();
        }
    }
}
