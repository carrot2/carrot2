package com.dawidweiss.carrot.ant;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Copy;
import org.apache.tools.ant.types.FileList;
import org.apache.tools.ant.util.FileUtils;



/**
 * An ANT task that copies a file list to certain directory.
 */
public class CopyFileList
    extends org.apache.tools.ant.Task
{
    private File toDir;
    private FileList fileList;

    /** Public empty constructor */
    public CopyFileList()
    {
    }
    
    public void setToDir(File todir) {
        this.toDir = todir;
    }
    
    /**
     * Creates a new file list element.
     */
    public FileList createFileList() {
        if (fileList != null)
            throw new RuntimeException("Only one file list accepted.");
        fileList = new FileList();
        return fileList;
    }


    /**
     * @see org.apache.tools.ant.Task#execute()
     */
    public void execute() throws BuildException
    {
        if (getProject() == null)
            throw new BuildException("Project reference is required.");
        if (toDir.isDirectory()==false) {
            throw new BuildException("Not a directory: " + toDir);
        }
        if (this.fileList == null) {
            throw new BuildException("File list expected.");
        }
        
        Project prj = getProject();
        FileUtils futils = FileUtils.newFileUtils();
        String [] files = fileList.getFiles(prj);
        for (int i=0;i<files.length;i++) {
            File file = futils.resolveFile(fileList.getDir(prj), files[i]);
            Copy copyTask = new Copy();
            copyTask.setProject(prj);
            copyTask.setFile(file);
            copyTask.setTodir(toDir);
            copyTask.execute();
        }
    }

}
