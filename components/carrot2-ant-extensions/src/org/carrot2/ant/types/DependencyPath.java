
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2007, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.ant.types;

import java.io.*;
import java.util.HashMap;

import org.apache.tools.ant.*;
import org.apache.tools.ant.types.*;
import org.apache.tools.ant.util.FileUtils;
import org.carrot2.ant.deps.ComponentDependency;
import org.carrot2.ant.tasks.Timer;
import org.carrot2.ant.tasks.Utils;


/**
 * A subclass of {@link Path}.
 */
public final class DependencyPath extends Path {
    private final static int LOG_LEVEL = Project.MSG_VERBOSE;

    /** This object identifier */
    private final String id;
    
    /** 
     * A project property where we will serialize all component
     * dependencies found in path. 
     */
    private String cachePropertyName;
    
    /** 
     * Components found in path.
     */
    private HashMap components;

    /**
     * A {@link org.apache.tools.ant.BuildListener} which deletes a given
     * file at buidl
     * 
     * @author Dawid Weiss
     */
    private final class RemoveAtBuildEnd implements BuildListener, SubBuildListener {
        /** File to be deleted */
        private final File file;

        public RemoveAtBuildEnd(File f) {
            this.file = f;
        }

        private void removeFile(Project project) {
            if (project == getProject()) {
                getProject().log("Removing cache file: " + file.getAbsolutePath(), Project.MSG_VERBOSE);
                if (file.exists()) {
                    this.file.delete();
                }
            }
        }

        public void buildFinished(BuildEvent event) {
            this.removeFile(event.getProject());
        }

        public void subBuildFinished(BuildEvent event) {
            this.removeFile(event.getProject());
        }

        public void buildStarted(BuildEvent event) {}
        public void targetStarted(BuildEvent event) {}
        public void targetFinished(BuildEvent event) {}
        public void taskStarted(BuildEvent event) {}
        public void taskFinished(BuildEvent event) {}
        public void messageLogged(BuildEvent event) {}
        public void subBuildStarted(BuildEvent arg0) {}
    }
    
    public DependencyPath(Project project) {
        super(project);
        this.id = createId(this);
        log("Creating dependency path: " + id, LOG_LEVEL);
    }

    public void setCacheProperty(String cachePropertyName) {
        this.cachePropertyName = cachePropertyName;
    }

    public void setRefid(Reference r) throws BuildException {
        if (!(r.getReferencedObject() instanceof DependencyPath)) {
            throw new BuildException("Referenced object is not" +
                    " an instance of " + DependencyPath.class.getName());
        }
        log("Creating reference to a dependency path named "
                + r.getRefId() + "(" + id + " -> " + createId(r.getReferencedObject()) + ")", LOG_LEVEL);
        super.setRefid(r);
    }

    public HashMap getAllComponents() throws IOException {
        if (this.isReference()) {
            if (!(this.getRefid().getReferencedObject() instanceof DependencyPath)) {
                throw new BuildException("Not a dependency path?: " + this.getRefid().getReferencedObject().getClass());
            }
            return ((DependencyPath) this.getRefid().getReferencedObject()).getAllComponents();
        }

        final String id;
        if (this.getRefid() != null) {
            id = "[" + this.getRefid().getRefId() + "]";
        } else {
            id = "[unnamed]";
        }

        final Project p = getProject();
        synchronized (Object.class) {
            if (this.components != null) {
                log("Reusing descriptors map (field): " + id, LOG_LEVEL);
                return components;
            }

            if (this.cachePropertyName != null) {
                final Timer timer = new Timer();
                if (p.getProperty(cachePropertyName) != null) {
                    this.components = deserialize(p.getProperty(cachePropertyName));
                    log("Reusing descriptors map (cache [" 
                            + timer.elapsed() + "]): " + cachePropertyName + " " + id);
                    return components;
                } else {
                    this.components = loadComponentDependencies(getProject(), this);
                    timer.start();
                    p.setNewProperty(cachePropertyName, serialize(components));
                    log("Caching descriptors map (" 
                            + timer.elapsed() + "): " + cachePropertyName + " " + id);
                    return components;
                }
            } else {
                this.components = loadComponentDependencies(getProject(), this);
                return components;
            }
        }
    }

    private HashMap deserialize(String property) {
        try {
            final File f = new File(property);
            final ObjectInputStream ois = new ObjectInputStream(
                    new BufferedInputStream(new FileInputStream(f)));
            try {
                return (HashMap) ois.readObject();
            } finally {
                ois.close();
            }
        } catch (ClassNotFoundException e) {
            throw new BuildException("Could not deserialize *.dep.xml.", e);
        } catch (IOException e) {
            throw new BuildException("Could not deserialize *.dep.xml.", e);
        }
    }

    private String serialize(HashMap components) {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        final ObjectOutputStream dos;
        try {
            dos = new ObjectOutputStream(bos);
            dos.writeObject(components);
            dos.close();
            final byte [] bytes = bos.toByteArray();

            final File f = File.createTempFile(".c2", ".dep.cache");
            getProject().addBuildListener(new RemoveAtBuildEnd(f));

            final FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes);
            fo.close();
            return f.getAbsolutePath();
        } catch (IOException e) {
            throw new BuildException("Could not serialize *.dep.xml.", e);
        }
    }

    /**
     * Collects all <code>*.dep.xml</code> files from filesets given 
     * in the argument.
     */
    private static HashMap loadComponentDependencies(
            final Project project, final Path path) throws IOException
    {
        final Timer timer = new Timer();

        final FileUtils futils = FileUtils.newFileUtils();
        final HashMap components = new HashMap();

        // Scan all locations pointed to by the path.
        final String [] pathLocations = path.list();
        for (int i = 0; i < pathLocations.length; i++) {
            final File location = new File(pathLocations[i]);
            if (!location.exists()) { 
                project.log("File does not exist: "
                        + location.getAbsolutePath(), Project.MSG_VERBOSE);
                continue;
            }

            if (location.isFile()) {
                final ComponentDependency dep = 
                    new ComponentDependency(project, location);
                Utils.addComponentToMap(dep, components);
            } else if (location.isDirectory()) {
                // scan a fileset and include all dependencies.
                final FileSet fs = new FileSet();
                fs.setProject(project);
                fs.setDir(location);
                fs.setIncludes("**/*.dep.xml");
                fs.setIncludes("*.dep.xml");
                final DirectoryScanner ds = fs.getDirectoryScanner(project);
                final File fromDir = fs.getDir(project);
                final String[] srcFiles = ds.getIncludedFiles();
                for (int j = 0; j < srcFiles.length; j++) {
                    final ComponentDependency dep = 
                        new ComponentDependency(
                            project, futils.resolveFile(fromDir, srcFiles[j]).getCanonicalFile());
                    Utils.addComponentToMap(dep, components);
                }
            } else {
                project.log("Unknown file type: " + location.getAbsolutePath(), Project.MSG_WARN);
            }
        }

        project.log("Descriptors map collection time: " + timer.elapsed(), LOG_LEVEL);
        return components;
    }

    private String createId(Object ob) {
        return "O:" + Integer.toHexString(ob.hashCode()) + "/C:" + Integer.toHexString(ob.getClass().hashCode());
    }
}
