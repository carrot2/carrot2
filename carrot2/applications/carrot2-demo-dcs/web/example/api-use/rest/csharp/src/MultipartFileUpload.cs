using System;
using System.Collections.Generic;
using System.Text;
using System.Collections.Specialized;
using System.Net;
using System.IO;

namespace Org.Carrot2
{
    //
    // This class based on an example from:
    // http://blog.mindbridge.com/?p=59
    //
    // With modifications.
    //
    public class MultipartFileUpload
    {
        private Uri uri = null;

        private NameValueCollection parameters = null;
        private NameValueCollection formData = null;

        private String fileDisplayName = null;
        private String fileParameterName = null;

        private String boundary;

        private FileInfo file = null;
        private NetworkCredential networkCredential = null;
        private readonly int BUFFER_SIZE = 500;

        public MultipartFileUpload()
        {
            parameters = new NameValueCollection();
            formData = new NameValueCollection();
        }

        public MultipartFileUpload(Uri uri)
            : this()
        {
            this.uri = uri;
        }

        #region getters and setters
        /// <summary>
        /// the destination of the multipart file upload
        /// </summary>
        public Uri Uri
        {
            get { return this.uri; }
            set { this.uri = value; }
        }

        /// <summary>
        /// the parameters to be passed along with the post method — optional
        /// </summary>
        public NameValueCollection Parameters
        {
            get { return this.parameters; }
            set { this.parameters = value; }
        }

        /// <summary>
        /// any form data associated with the post method
        /// </summary>
        public NameValueCollection FormData
        {
            get { return this.formData; }
            set { this.formData = value; }
        }

        /// <summary>
        /// if your web server requires credentials to upload a file, set them here — optional
        /// </summary>
        public NetworkCredential NetworkCredential
        {
            get { return this.networkCredential; }
            set { this.networkCredential = value; }
        }

        #endregion

        #region public methods

        /// <summary>
        /// adds a parameter to the request, if the parameter already has a value,
        /// it will be overwritten
        /// </summary>
        /// <param name="name">name of the parameter</param>
        /// <param name="value">value of the parameter</param>
        public void AddParameter(String name, String value)
        {
            if (this.parameters == null)
            {
                this.parameters = new NameValueCollection();
            }
            this.parameters.Set(name, value);
        }

        /// <summary>
        /// adds a form value to the request, if the form field already exists, overwrite it
        /// </summary>
        /// <param name="name">the name of the form field</param>
        /// <param name="value">value of the form field</param>
        public void AddFormValue(String name, String value)
        {
            if (this.formData == null)
            {
                this.formData = new NameValueCollection();
            }
            this.formData.Set(name, value);
        }

        /// <summary>
        /// attach a file to the post method, the parameter name and file can not be null
        /// </summary>
        /// <param name="fileDisplayName">the name of the file as it should appear to the web server</param>
        /// <param name="parameterName">the parameter that your web server expects to be associated with a file</param>
        /// <param name="file">the actual file you want to upload</param>
        /// <exception cref="ArgumentNullException">file can not be null, name of the parameter can’t be null</exception>
        public void AttachFile(String fileDisplayName, String parameterName, FileInfo file)
        {
            if (file == null)
            {
                throw new ArgumentNullException("file", "You must pass a reference to a file");
            }
            if (parameterName == null)
            {
                throw new ArgumentNullException("parameterName", "You must provide the name of the file parameter.");
            }

            this.file = file;
            this.fileParameterName = parameterName;
            this.fileDisplayName = fileDisplayName;
        }

        /// <summary>
        /// performs the actual upload
        /// </summary>
        /// <returns>the response as a byte array</returns>
        public byte[] UploadFileEx()
        {
            // generate boundary
            boundary = "-----------boundary" + DateTime.Now.Ticks.ToString("x");

            // Tack on any parameters or just give us back the uri if there are no parameters
            Uri targetUri = CreateUriWithParameters();
            
            HttpWebRequest webrequest = (HttpWebRequest)WebRequest.Create(targetUri);
            webrequest.Credentials = networkCredential; //fine if it’s null
            webrequest.ContentType = "multipart/form-data; boundary=" + boundary;
            webrequest.Method = "POST";

            // Encode form parameters
            String postHeader = CreatePostDataString();
            byte[] postHeaderBytes = Encoding.UTF8.GetBytes(postHeader);
            byte[] startBoundaryBytes = Encoding.ASCII.GetBytes("\r\n--" + boundary + "\r\n");
            byte[] endBoundaryBytes = Encoding.ASCII.GetBytes("\r\n--" + boundary + "--");

            // Read in the file as a stream
            FileStream fileStream = file.Open(FileMode.Open, FileAccess.Read);

            // Estimate the length of the request.
            long length = postHeaderBytes.Length + fileStream.Length + endBoundaryBytes.Length;
            webrequest.ContentLength = length;

            Stream requestStream = webrequest.GetRequestStream();

            // Write out our post header
            requestStream.Write(postHeaderBytes, 0, postHeaderBytes.Length);

            // Write out the file contents
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead = 0;
            while ((bytesRead = fileStream.Read(buffer, 0, buffer.Length)) != 0)
            {
                requestStream.Write(buffer, 0, bytesRead);
            }

            // Write out the trailing boundary
            requestStream.Write(endBoundaryBytes, 0, endBoundaryBytes.Length);

            WebResponse response = webrequest.GetResponse();
            Stream responseStream = response.GetResponseStream();
            MemoryStream memStream = new MemoryStream();
            while ((bytesRead = responseStream.Read(buffer, 0, buffer.Length)) != 0)
            {
                memStream.Write(buffer, 0, bytesRead);
            }

            responseStream.Close();
            response.Close();

            return memStream.ToArray();
        }

        #endregion

        #region private methods

        /// <summary>
        /// helper method to tack on parameters to the request
        /// </summary>
        /// <returns>Uri with parameters, or the original uri if it’s null</returns>
        private Uri CreateUriWithParameters()
        {
            if (uri == null) return null;
            if (parameters == null || parameters.Count <= 0)
            {
                return this.uri;
            }
            String paramString = "?";
            foreach (String key in parameters.Keys)
            {
                paramString += key + "=" + parameters.Get(key) + "&";
            }
            paramString = paramString.Substring(0, paramString.Length - 1); //strip off last &
            return new Uri(uri.ToString() + paramString);
        }

        /// <summary>
        /// post data as a string with the boundaries
        /// </summary>
        /// <returns>a string representing the form data</returns>
        private String CreatePostDataString()
        {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < formData.Count; i++)
            {
                sb.Append("--" + boundary + "\r\n");
                sb.Append("Content-Disposition: form-data; name=\"");
                sb.Append(formData.GetKey(i) + "\"\r\n\r\n" + formData.Get(i) + "\r\n");
            }
            sb.Append("--" + boundary + "\r\n");
            sb.Append("Content-Disposition: form-data; name=\"" + fileParameterName + "\"; ");
            sb.Append("filename=\"" + fileDisplayName + "\"\r\n");
            sb.Append("Content-Type: application/octet-stream\r\n\r\n");
            return sb.ToString();
        }

        #endregion
    }
}