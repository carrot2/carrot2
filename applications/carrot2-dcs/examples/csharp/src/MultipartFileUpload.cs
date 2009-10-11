using System;
using System.Collections.Generic;
using System.Text;
using System.Net;
using System.IO;

namespace Org.Carrot2
{
    /// <summary>
    /// Stream data for the form.
    /// </summary>
    internal sealed class StreamData
    {
        internal Stream stream;
        internal string streamName;

        internal StreamData(string name, Stream stream)
        {
            this.stream = stream;
            this.streamName = name;
        }
    }

    /// <summary>
    /// A simplified HTTP POST <code>multipart/form-data</code> uploader. 
    /// </summary>
    public class MultipartFileUpload
    {
        private static readonly uint BUFFER_SIZE = 1024 * 8;

        private Uri uri;
        private IList<KeyValuePair<string, object>> formData 
            = new List<KeyValuePair<string, object>>();

        public MultipartFileUpload(Uri target)
        {
            this.uri = target;
        }

        /// <summary>
        /// Reset the object for reuse.
        /// </summary>
        public void Reset()
        {
            formData.Clear();
        }

        /// <summary>
        /// Adds a form value to the request, if the form field already exists, overwrite it.
        /// </summary>
        /// <param name="name">the name of the form field</param>
        /// <param name="value">value of the form field</param>
        public void AddFormValue(String name, String value)
        {
            AddFormValueInternal(name, (object) value);
        }

        /// <summary>
        /// Adds stream data to the request's form data.
        /// </summary>
        /// <param name="name">the name of the form field</param>
        /// <param name="value">string or StreamData object.</param>
        private void AddFormValueInternal(String name, object value)
        {
            this.formData.Add(new KeyValuePair<string,object>(name, value));
        }

        /// <summary>
        /// Attach a stream (file) to the post method, the parameter name and file can not be null.
        /// </summary>
        /// <param name="parameterName">the parameter that your web server expects to be associated with a file</param>
        /// <param name="fileDisplayName">the name of the file as it should appear to the web server</param>
        /// <param name="stream">the actual content of the file you want to upload</param>
        /// <exception cref="ArgumentNullException">file can not be null, name of the parameter can’t be null</exception>
        public void AddFormStream(String parameterName, String fileDisplayName, Stream stream)
        {
            if (stream == null || !stream.CanRead)
            {
                throw new ArgumentNullException("stream", "You must pass a reference to a readable stream");
            }

            if (parameterName == null)
            {
                throw new ArgumentNullException("parameterName", "You must provide the name of the file parameter.");
            }

            AddFormValueInternal(parameterName, new StreamData(fileDisplayName, stream));
        }

        /// <summary>
        /// Performs the actual upload.
        /// </summary>
        /// <returns>the response as a byte array</returns>
        public byte[] Post()
        {
            // generate parameter boundary
            string boundaryRaw = "boundary" + DateTime.Now.Ticks.ToString("x");
            string boundary = "--" + boundaryRaw;

            HttpWebRequest webrequest = (HttpWebRequest) WebRequest.Create(uri);
            webrequest.ContentType = "multipart/form-data; boundary=" + boundaryRaw;
            webrequest.Method = "POST";
            webrequest.KeepAlive = false;

            // Encode form parameters and push them to the stream.
            using (Stream requestStream = webrequest.GetRequestStream())
            {
                byte[] buffer = new byte[BUFFER_SIZE];
                int bytesRead = 0;
                byte[] bytes;

                foreach (KeyValuePair<string, object> kv in formData)
                {
                    string key = kv.Key;
                    object value = kv.Value;

                    if (value is string)
                    {
                        string part =
                            boundary + "\r\n"
                            + "content-disposition: form-data; name=\""
                            + key + "\"\r\n\r\n"
                            + (value as string)
                            + "\r\n";

                        bytes = Encoding.UTF8.GetBytes(part);
                        requestStream.Write(bytes, 0, bytes.Length);
                    }
                    else if (value is StreamData)
                    {
                        StreamData sd = value as StreamData;
                        string part =
                            boundary + "\r\n"
                            + "content-disposition: form-data; name=\""
                            + key + "\"; filename=\"" + sd.streamName + "\"\r\n"
                            + "content-type: application/octet-stream\r\n\r\n";

                        bytes = Encoding.UTF8.GetBytes(part);
                        requestStream.Write(bytes, 0, bytes.Length);

                        // Copy stream contents.
                        using (Stream s = sd.stream)
                        {
                            while ((bytesRead = s.Read(buffer, 0, buffer.Length)) != 0)
                            {
                                requestStream.Write(buffer, 0, bytesRead);
                            }
                        }

                        requestStream.WriteByte((byte) '\r');
                        requestStream.WriteByte((byte) '\n');
                    }
                    else
                    {
                        throw new Exception("Panic: object of unknown type: " + value);
                    }
                }

                bytes = Encoding.UTF8.GetBytes(boundary + "--\r\n");
                requestStream.Write(bytes, 0, bytes.Length);
                requestStream.Close();

                // Copy the response to a byte array in memory (so that we don't need
                // to track web connection resources after returning).
                WebResponse response = webrequest.GetResponse();
                Stream responseStream = response.GetResponseStream();
                MemoryStream memStream = new MemoryStream();
                while ((bytesRead = responseStream.Read(buffer, 0, buffer.Length)) != 0)
                {
                    memStream.Write(buffer, 0, bytesRead);
                }

                responseStream.Close();
                response.Close();

                Reset();
                return memStream.ToArray();
            }
        }
    }
}