
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */

package com.dawidweiss.carrot.util.common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Some file-related utilities.
 * 
 * @author Dawid Weiss
 */
public final class FileUtils {

	private FileUtils() {
        // No instantiation.
	}

    /**
     * A {@link FileFilter} that skips Subversion and CVS folders.
     */
    public final static FileFilter SKIP_CVS_SVN_FILE_FILTER = new FileFilter() {
        public boolean accept(File f) {
            final String name = f.getName();
            if (name.equals(".svn") || name.equals("CVS")) {
                return false;
            }
            return true;
        }
    };

    /**
     * Deletes a directory and all subdirectories and files within it.
     */
    public static void deleteDirectoryRecursively(File directory) throws IOException {
        if (!directory.exists() || !directory.isDirectory())
            return;

        final List subdirs = new ArrayList(); 
        File [] files = directory.listFiles( new FilenameFilter() {
            public boolean accept(File dir, String name) {
                if (".".equals(name) || "..".equals(name))
                    return false;
                File f = new File(dir, name);
                if (f.isDirectory()) {
                    subdirs.add(f);
                    return false;
                }
                return true;
            }
        }); 
        
        for (int i=0;i<files.length;i++) {
            if (files[i].delete()==false)
                throw new IOException("Could not delete: "
                    + files[i].getAbsolutePath());
        }
        
        for (int i=0;i<subdirs.size();i++) {
            deleteDirectoryRecursively((File) subdirs.get(i));
        }
        
        if (directory.delete()==false) {
            throw new IOException("Could not delete: "
                + directory.getAbsolutePath());
        }
    }

    /**
     * Returns a list of all matching files from this and subdirectories.
     */
    public static File[] getAllFiles(File directory, final FileFilter filter) {
        final List subdirs = new LinkedList();
        
        File [] acceptedFiles = directory.listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                if (pathname.isDirectory()) {
                    if (!pathname.getName().startsWith("."))
                        subdirs.add(pathname);
                    return false;
                } else {
                    // try
                    return filter.accept(pathname); 
                }
            }
        });
        
        // descend into subdirs.
        for (Iterator i = subdirs.iterator(); i.hasNext(); ) {
            File subdir = (File) i.next();
            File [] subaccepted = getAllFiles(subdir, filter);
            
            File [] total = new File [ acceptedFiles.length + subaccepted.length ];
            System.arraycopy(acceptedFiles, 0, total, 0, acceptedFiles.length);
            System.arraycopy(subaccepted, 0, total, acceptedFiles.length, subaccepted.length);
            acceptedFiles = total;
        }

        return acceptedFiles;
    }
    
    /**
     * Returns a path relative to a given directory. The relative
     * path does not start with file separator.
     * 
     * @throws RuntimeException if <code>child</code> is not
     * a file under <code>parent</code>.
     */
    public static String relativePath(File parent, File child) {
        try {
            String parentPath = parent.getCanonicalPath();
            String childPath = child.getCanonicalPath();
            
            if (childPath.startsWith(parentPath)) {
                String relative = childPath.substring(parentPath.length());
                if (relative.startsWith(File.separator)) {
                    return relative.substring(File.separator.length());
                } else return relative; 
            } else {
                throw new RuntimeException("Child not under the parent: "
                    + parent.getPath() + ", child: " + child.getPath());
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not resolve relative path: "
                + parent.getPath() + ", child: " + child.getPath());
        }
    }

	/**
	 * Copies data from the input stream to the output stream and closes both
	 * streams afterwards (even if exceptions are thrown).
	 * 
	 * @param is
	 * @param os
	 */
	public static void copyAndClose(InputStream is, OutputStream os) throws IOException {
		try {
            copy(is,os);
		} finally {
			if (is != null) try { is.close(); } catch (IOException e) {/* ignore */}
			if (os != null) try { os.close(); } catch (IOException e) {/* ignore */}
		}
	}

    /**
     * Same as {@link #copyAndClose(InputStream, OutputStream)}, but does
     * not close the streams (even in case of an exception). 
     * 
     * @param is
     * @param os
     * @throws IOException
     */
    public static void copy(InputStream is, OutputStream os) throws IOException {
        byte [] buffer = new byte [2000];
        int j;
        while ( (j = is.read(buffer)) > 0 ) {
            os.write(buffer, 0, j);
        }
    }

	public static void touch(File deletedMarker, String content) throws IOException {
		FileOutputStream fos = new FileOutputStream( deletedMarker );
		fos.write(content.getBytes("UTF-8"));
		fos.close();
	}

	public static void touch(File deletedMarker) throws IOException {
		touch(deletedMarker, "");
	}

	/**
     * Reads an entire file into memory. all file handles are closed.
     * 
	 * @param versionFile
	 */
	public static byte [] readFile(File versionFile) throws IOException {
        FileInputStream fis = new FileInputStream( versionFile );
        DataInputStream is = null;
        try {
            is = new DataInputStream( fis );
            byte [] bytes = new byte [ (int) versionFile.length() ];
            is.readFully(bytes);
            return bytes;
        } finally {
            if (is != null) try { is.close(); } catch (IOException e) {/* ignore */}
            try { fis.close(); } catch (IOException e) {/* ignore */}
        }
	}

    /**
     * Ensures the file is ready to be created (parent folder exists).
     */
    public static void assertIsWritableFile(File file) {
        File parent = file.getAbsoluteFile().getParentFile();
        if (parent == null || parent.exists() == false || !parent.isDirectory()) {
            throw new RuntimeException("Parent folder does not exist or is not a directory: "
                    + parent.getAbsolutePath());
        }
        if (file.exists() && !file.canWrite()) {
            throw new RuntimeException("File already exists and is not writable: "
                    + file.getAbsolutePath());
        }
    }

    /**
     * Ensures the file exists, is a file and is readable.
     */
    public static void assertIsFile(File file) {
        if (!file.exists()) {
            throw new RuntimeException("File does not exist: "
                    + file.getAbsolutePath());
        }
        if (!file.canRead()) {
            throw new RuntimeException("Cannot read file: " 
                    + file.getAbsolutePath());
        }
    }

    public static Writer openUtf8File(File file, boolean append) throws FileNotFoundException {
        try {
            return new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(file, append)), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UTF-8 must be supported.");
        }
    }

    public static Serializable readObject(String file) throws IOException {
        final ObjectInputStream oos = new ObjectInputStream(
                new BufferedInputStream(new FileInputStream(file)));
        try {
            return (Serializable) oos.readObject();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Class not found when deserializing object: ", e);
        } finally {
            oos.close();
        }
    }

    public static void saveObject(String file, Serializable object) throws IOException {
        final ObjectOutputStream oos = new ObjectOutputStream(
                new BufferedOutputStream(new FileOutputStream(file)));
        try {
            oos.writeObject(object);
        } finally {
            oos.close();
        }
    }
}
