
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2016, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.dcs;

import java.io.*;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;

/**
 * A file item factory storing contents of uploaded files in-memory.
 */
final class MemoryFileItemFactory implements FileItemFactory
{
    /**
     * 
     */
    @SuppressWarnings("serial")
    private static class MemoryFileItem implements FileItem
    {
        private final String fieldName;
        private final String contentType;
        private final boolean isFormField;
        private final String fileName;
        private ByteArrayOutputStream baos;

        public MemoryFileItem(String fieldName, String contentType, boolean isFormField, String fileName)
        {
            this.fieldName = fieldName;
            this.contentType = contentType;
            this.isFormField = isFormField;
            this.fileName = fileName;
        }

        public InputStream getInputStream() throws IOException
        {
            return new ByteArrayInputStream(baos.toByteArray());
        }

        public String getContentType()
        {
            return contentType;
        }

        public String getName()
        {
            return fileName;
        }

        public boolean isInMemory()
        {
            return true;
        }

        public long getSize()
        {
            return baos.toByteArray().length;
        }

        public byte [] get()
        {
            return baos.toByteArray();
        }

        public String getString(String encoding) throws UnsupportedEncodingException
        {
            throw new UnsupportedOperationException();
        }

        public String getFieldName()
        {
            return fieldName;
        }

        public boolean isFormField()
        {
            return isFormField;
        }

        public OutputStream getOutputStream() throws IOException
        {
            this.baos = new ByteArrayOutputStream();
            return baos;
        }

        public String getString()
        {
            try
            {
                return new String(this.baos.toByteArray(), "UTF-8");
            }
            catch (UnsupportedEncodingException e)
            {
                throw new RuntimeException("UTF-8 must be supported.");
            }
        }

        public void delete()
        {
            throw new UnsupportedOperationException();
        }

        public void write(File file) throws Exception
        {
            throw new UnsupportedOperationException();
        }

        public void setFieldName(String fieldName)
        {
            throw new UnsupportedOperationException();
        }

        public void setFormField(boolean arg0)
        {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * 
     */
    public FileItem createItem(String fieldName, String contentType, boolean isFormField, String fileName)
    {
        return new MemoryFileItem(fieldName, contentType, isFormField, fileName);
    }
}
