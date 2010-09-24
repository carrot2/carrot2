
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2010, Dawid Weiss, Stanisław Osiński.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.util.attribute.metadata;

import static javax.lang.model.SourceVersion.RELEASE_6;

import java.io.*;
import java.util.*;

import javax.annotation.processing.*;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic.Kind;
import javax.tools.*;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.runtime.RuntimeInstance;
import org.carrot2.util.attribute.*;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.stream.Format;

import com.thoughtworks.qdox.parser.Builder;
import com.thoughtworks.qdox.parser.ParseException;
import com.thoughtworks.qdox.parser.impl.JFlexLexer;
import com.thoughtworks.qdox.parser.impl.Parser;
import com.thoughtworks.qdox.parser.structs.TagDef;

/**
 * Java6+ compatible annotation processor for parsing <code>Bindable</code>-annotated
 * types and generating their metadata.
 */
@SupportedAnnotationTypes("org.carrot2.util.attribute.Bindable")
@SupportedSourceVersion(RELEASE_6)
public final class BindableProcessor extends AbstractProcessor
{
    /**
     * Empty package name.
     */
    private final static CharSequence EMPTY_PACKAGE_NAME = "";
    
    /**
     * We must use type mirrors, so this is a hardcoded name for:
     * {@link Bindable#inherit()}.
     */
    private final static String INHERIT_ATTRIBUTE_NAME = "inherit";

    /**
     * Ignored Eclipse compilation phases.
     */
    private final static Set<String> ignoredPhases = new HashSet<String>(
        Arrays.asList("RECONCILE", "OTHER"));

    /**
     * Mirror element utilities.
     */
    private Elements elementUtils;

    /**
     * Apt filer utilities.
     */
    private Filer filer;

    /**
     * Collected types annotated with {@link Bindable}.
     */
    private final ArrayList<TypeElement> bindableTypes = new ArrayList<TypeElement>();

    /**
     * Inheritance dependencies.
     */
    private final HashMap<String, List<String>> dependencies = new HashMap<String, List<String>>();

    /**
     * Already emitted bindable metadata for referencing current round.
     */
    private final HashMap<String, BindableMetadata> thisRoundMetadata = 
        new HashMap<String, BindableMetadata>();
    
    /**
     * Type name to reflected type mapping.
     */
    private final HashMap<String, TypeElement> nameToType =
        new HashMap<String, TypeElement>();

    /**
     * Classpath resource lookup routines (and compiler-specific hacks). 
     */
    private final ArrayList<IClassPathLookup> resourceLookup = new ArrayList<IClassPathLookup>();

    /**
     * Round number.
     */
    private int round;
    
    /**
     * Initialize processing environment.
     */
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv)
    {
        super.init(processingEnv);

        elementUtils = this.processingEnv.getElementUtils();
        filer = this.processingEnv.getFiler();

        initializeLookups();
    }

    /**
     * Process a set of roots for the current round (apt callback).
     */
    @Override
    public boolean process(Set<? extends TypeElement> ann, RoundEnvironment env)
    {
        // Check for Eclipse reconciliation phase and skip it.
        final String phase = super.processingEnv.getOptions().get("phase");
        if (phase != null && ignoredPhases.contains(phase))
        {
            return false;
        }

        // Check for any previous errors and skip.  
        if (env.errorRaised())
        {
            return false;
        }

        // Clear any previous junk.
        bindableTypes.clear();
        dependencies.clear();
        thisRoundMetadata.clear();
        nameToType.clear();

        // Scan for all types marked with @Bindable and processed in this round.
        round++;
        for (TypeElement e : ElementFilter.typesIn(env.getElementsAnnotatedWith(Bindable.class)))
        {
            // e can be null in Eclipse, so check for this case.
            if (e != null) bindableTypes.add(e);
        }

        // Do the round processing.
        final long start = System.currentTimeMillis();
        processBindables();
        if (bindableTypes.size() > 0)
        {
            System.out.println(
                String.format(Locale.ENGLISH,
                    "%d @Bindable metadata processed in round %d in %.2f secs.",
                    bindableTypes.size(), 
                    round, 
                    (System.currentTimeMillis() - start) / 1000.0));
        }

        return false;
    }

    /**
     * All bindable types have been collected for this round, do the actual processing.
     */
    private void processBindables()
    {
        /*
         * We need to sort bindables and process them in topological order because of
         * attribute inheritance. 
         */
        final List<TypeElement> sorted = topoSortForInheritance();

        final HashSet<String> rootSet = new HashSet<String>();
        for (TypeElement type : bindableTypes)
            rootSet.add(getName(type));

        // For every (new) bindable type, create and emit attribute metadata.
        for (TypeElement type : sorted)
        {
            // We skip those types in the dependency list, that are not in the root set.
            if (!rootSet.contains(getName(type)))
                continue;

            // Extract the bindable's title, description and label.
            final Map<String, String> tags = new HashMap<String, String>();
            final String javaDoc = processJavaDoc(type, tags);
            final BindableMetadata metadata = new BindableMetadata();
            extractTitleDescription(metadata, javaDoc);
            metadata.setLabel(tags.get("label"));

            // Collect inherited metadata, if any.
            final Map<String, AttributeMetadata> inheritedMetadata = new HashMap<String, AttributeMetadata>();
            extractInheritedAttributeMetadata(type, inheritedMetadata);

            // Collect this bindable's attribute metadata.
            final Map<String, AttributeMetadata> attributeMetadata = new LinkedHashMap<String, AttributeMetadata>();
            final List<AttributeFieldInfo> fieldInfos = new ArrayList<AttributeFieldInfo>();
            extractAttributeMetadata(type, attributeMetadata, inheritedMetadata, fieldInfos);
            metadata.setAttributeMetadata(attributeMetadata);

            // Emit auxiliary metadata files (XMLs, classes, etc.).
            thisRoundMetadata.put(getName(type), metadata);

            emitXML(metadata, type);
            emitMetadataClass(metadata, fieldInfos, type);
        }
    }

    /**
     * Sort topologically to account for attribute inheritance. Types not inheriting from anything come first.
     */
    private List<TypeElement> topoSortForInheritance()
    {
        dependencies.clear();
        nameToType.clear();

        // Calculate initial type names.
        for (TypeElement type : bindableTypes)
        {
            final String name = getName(type);

            if (nameToType.containsKey(name)) 
                throw new RuntimeException("Sanity check: two classes with the same on input: "
                    + name);

            nameToType.put(name, type);
        }

        // Calculate inheritance dependencies.
        for (TypeElement type : bindableTypes)
        {
            final String name = getName(type);
            dependencies.put(name, extractInheritClassNames(type));
        }

        // Reorder input.
        final ArrayList<String> reordered = new ArrayList<String>();
        final HashSet<String> processed = new HashSet<String>();
        for (String e : dependencies.keySet())
        {
            mark(e, reordered, processed, dependencies);
        }

        ArrayList<TypeElement> reorderedTypes = new ArrayList<TypeElement>();
        for (String name : reordered)
        {
            reorderedTypes.add(nameToType.get(name));
        }

        return reorderedTypes;
    }

    /**
     * Depth-first descent, no cycle checks.
     */
    private static void mark(String e, ArrayList<String> reordered, HashSet<String> processed,
        HashMap<String, List<String>> dependencies)
    {
        if (processed.contains(e))
            return;
        processed.add(e);
        
        if (dependencies.get(e) != null)
        {
            for (String dep : dependencies.get(e))
            {
                mark(dep, reordered, processed, dependencies);
            }
        }
        reordered.add(e);
    }

    /**
     * Extract class names from {@link Bindable#inherit()} attribute.
     */
    private List<String> extractInheritClassNames(TypeElement type)
    {
        final ArrayList<String> clazzNames = new ArrayList<String>();
        for (AnnotationMirror m : type.getAnnotationMirrors())
        {
            final Map<? extends ExecutableElement, ? extends AnnotationValue> values = 
                elementUtils.getElementValuesWithDefaults(m);

            for (ExecutableElement e : values.keySet())
            {
                if (e.getSimpleName().toString().equals(INHERIT_ATTRIBUTE_NAME))
                {
                    @SuppressWarnings("unchecked")
                    List<? extends AnnotationValue> clazzMirrors = 
                        (List<? extends AnnotationValue>) values.get(e).getValue();

                    for (AnnotationValue clazzMirror : clazzMirrors)
                    {
                        TypeMirror typeMirror = (TypeMirror) clazzMirror.getValue();
                        TypeElement elem = (TypeElement) processingEnv.getTypeUtils().asElement(typeMirror);
                        String typeName = elementUtils.getBinaryName(elem).toString();
                        if (!nameToType.containsKey(typeName))
                            nameToType.put(typeName, elem);
                        clazzNames.add(typeName);
                    }
                }
            }
        }
        return clazzNames;
    }

    /**
     * Simple names for a given type.
     */
    private String getName(TypeElement type)
    {
        return elementUtils.getBinaryName(type).toString();
    }

    /**
     * Extract attribute metadata from an existing class. 
     */
    private void extractInheritedAttributeMetadata(TypeElement type,
        Map<String, AttributeMetadata> inheritedMetadata)
    {
        // For each class, attempt to load its metadata.
        for (String clazzName : dependencies.get(getName(type)))
        {
            final BindableMetadata metadata;

            /*
             * Check if the metadata for this class is not part of the current
             * round. If so, we can't generate and read a resource in the same
             * round, so we use a local cache.  
             */
            if (thisRoundMetadata.containsKey(clazzName))
            {
                metadata = thisRoundMetadata.get(clazzName);
            }
            else
            {
                /*
                 * The class is already compiled so no source code level may be available for it.
                 * But since it's already compiled, we may simply load its own bindable metadata,
                 * it should (must?) be available.
                 */
                metadata = readMetadataFromResource(clazzName);
                if (metadata == null)
                {
                    // We failed to load metadata of this class (most likely due to class path
                    // isses - apt preprocessors don't seem to be able to get to class path
                    // resources normally). Skip.
                    return;
                }
            }
            
            final TypeElement depType = nameToType.get(clazzName);
            final Map<String, String> fieldNameToAttributeKey = new HashMap<String, String>();
            for (VariableElement field : ElementFilter.fieldsIn(depType.getEnclosedElements()))
            {
                Attribute a = field.getAnnotation(Attribute.class);
                if (a != null)
                {
                    fieldNameToAttributeKey.put(field.getSimpleName().toString(), 
                        getAttributeKey(field, a));
                }
            }

            for (Map.Entry<String, AttributeMetadata> e : metadata.getAttributeMetadata().entrySet())
            {
                if (inheritedMetadata.containsKey(e.getKey()))
                {
                    throw new RuntimeException("Duplicate key from inherited bindable classes: "
                        + e.getKey());
                }
                
                // http://issues.carrot2.org/browse/CARROT-706
                // Remap names of inherited attributes from actual field names to their associated keys.
                final String key = fieldNameToAttributeKey.get(e.getKey());
                if (key == null)
                {
                    throw new RuntimeException("Type: " + clazzName + " does not have a field named: " + e.getKey());
                }

                inheritedMetadata.put(key, e.getValue());
            }            
        }
    }

    /**
     * Read metadata from a resource location somewhere.
     */
    private BindableMetadata readMetadataFromResource(String clazzName)
    {
        final String metadataName = metadataFileName(clazzName);
        InputStream is = null;
        try
        {
            is = getResourceOrNull(StandardLocation.CLASS_OUTPUT, 
                EMPTY_PACKAGE_NAME, metadataName);
            
            if (is == null)
            {
                is = getResourceOrNull(StandardLocation.CLASS_PATH, 
                    EMPTY_PACKAGE_NAME, metadataName);
            }
            
            /*
             * javac has no support for reading CLASS_PATH resources (filer attempts
             * to open a resource for writing, hardcoded under CLASS_OUTPUT)... try 
             * compiler-specific hacks.
             */
            for (IClassPathLookup cpl : resourceLookup)
            {
                is = cpl.getResourceOrNull(metadataName);
                if (is != null) break;
            }

            if (is == null)
            {
                throw new IOException("Resource not found: " + metadataName);
            }

            return new Persister().read(BindableMetadata.class, is);
        }
        catch (Exception e)
        {
            if (is != null) closeQuietly(is);
            
            super.processingEnv.getMessager().printMessage(Kind.WARNING, 
                "Could not load resource metadata for class: " + clazzName);

            return null;
        }
    }

    /**
     * Initialize lookup hacks.
     */
    private void initializeLookups()
    {
        this.resourceLookup.clear();

        String [] hacks = {
            "org.carrot2.util.attribute.metadata.Javac16CpLookup"
        };

        final ClassLoader classLoader = this.getClass().getClassLoader();
        for (String clazzName : hacks)
        {
            try
            {
                Class<?> c = Class.forName(clazzName, true, classLoader);
                IClassPathLookup cpl = (IClassPathLookup) c.newInstance();
                cpl.init(super.processingEnv);
                resourceLookup.add(cpl);
            }
            catch (Throwable t)
            {
                // Skipping this hack.
            }
        }
    }

    /**
     * Returns an input stream from a valid FileObject 
     * or <code>null</code> if it isn't accessible.
     */
    private InputStream getResourceOrNull(StandardLocation location,
        CharSequence pkg, String relativeName)
    {
        try
        {
            final FileObject fo = filer.getResource(location, pkg, relativeName);
            if (fo == null) return null;
            return fo.openInputStream();
        }
        catch (Exception e)
        {
            return null;
        }
    }

    /**
     * Extract attribute metadata to a string.
     */
    private void extractAttributeMetadata(TypeElement type,
        Map<String, AttributeMetadata> attributeMetadata,
        Map<String, AttributeMetadata> inheritedMetadata,
        List<AttributeFieldInfo> attributeFields)
    {
        for (VariableElement field : ElementFilter.fieldsIn(type.getEnclosedElements()))
        {
            Attribute attr = field.getAnnotation(Attribute.class);
            if (attr != null)
            {
                final Map<String, String> tags = new HashMap<String, String>();
                final String javaDoc = processJavaDoc(field, tags);

                final AttributeMetadata metadata = new AttributeMetadata();
                extractTitleDescription(metadata, javaDoc);
                metadata.setLabel(tags.get("label"));
                metadata.setGroup(tags.get("group"));
                metadata.setLevel(AttributeLevel.robustValueOf(tags.get("level")));

                final String attributeKey = getAttributeKey(field, attr);
                if (attr.inherit())
                {
                    AttributeMetadata inherited = inheritedMetadata.get(attributeKey);
                    if (inherited == null)
                    {
                        super.processingEnv.getMessager().printMessage(
                            Kind.WARNING,
                            "Class " + type.getSimpleName() 
                            + " has no inherited attribute with key: "
                            + attributeKey + ", inherited attributes: " + formatInherited(inheritedMetadata));
                    }
                    else
                    {
                        metadata.setTitle(notNull(metadata.getTitle(), inherited.getTitle()));
                        metadata.setDescription(notNull(metadata.getDescription(), inherited.getDescription()));
                        metadata.setGroup(notNull(metadata.getGroup(), inherited.getGroup()));
                        metadata.setLabel(notNull(metadata.getLabel(), inherited.getLabel()));
                        metadata.setLevel(notNull(metadata.getLevel(), inherited.getLevel()));
                    }
                }
                
                // Fill in additional information.
                attributeFields.add(
                    new AttributeFieldInfo(attributeKey, metadata, javaDoc, field));

                // See http://issues.carrot2.org/browse/CARROT-706
                final String fieldName = field.getSimpleName().toString();
                attributeMetadata.put(fieldName, metadata);
            }
        }
    }

    /**
     * Format inherited metadata.
     */
    private String formatInherited(Map<String, AttributeMetadata> inheritedMetadata)
    {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, AttributeMetadata> e : inheritedMetadata.entrySet())
        {
            builder.append("\n\t");
            builder.append("Key: ").append(e.getKey());
            builder.append(" Description: ").append(e.getValue().getLabelOrTitle());
        }
        return builder.toString();
    }

    /**
     * Return the attribute's key.
     */
    private String getAttributeKey(VariableElement field, Attribute attr)
    {
        if (attr.key().length() > 0)
        {
            return attr.key();
        }
        else
        {
            String bindablePrefix = getBindablePrefix((TypeElement) field.getEnclosingElement());
            if (bindablePrefix == null)
                return field.getSimpleName().toString();
            else
                return bindablePrefix + "." + field.getSimpleName();
        }
    }

    /**
     * Return the bindable's prefix or <code>null</code> if empty/ not present.
     */
    private String getBindablePrefix(TypeElement bindable)
    {
        String prefix = bindable.getAnnotation(Bindable.class).prefix().trim();
        if (prefix.length() > 0)
            return prefix;
        else
            return null;
    }

    /**
     * Pick the first non-null value.
     */
    private <T> T notNull(T... objects)
    {
        for (T t : objects)
            if (t != null) return t;

        return null;
    }

    /**
     * Emit bindable matadata as a class with statically collected information.
     * @param fieldInfos 
     */
    private void emitMetadataClass(BindableMetadata metadata, 
        List<AttributeFieldInfo> fieldInfos, TypeElement type)
    {
        String packageName = elementUtils.getPackageOf(type).getQualifiedName().toString();
        String className = type.getSimpleName().toString() + "Descriptor";
        String fullyQualifiedName = packageName.isEmpty() ? className : packageName + "." + className;

        PrintWriter w = null;
        ClassLoader ccl = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
        try
        {
            w = new PrintWriter(filer.createSourceFile(fullyQualifiedName, type).openWriter());

            final RuntimeInstance velocity = VelocityInitializer.createInstance(
                super.processingEnv.getMessager());
            final VelocityContext context = VelocityInitializer.createContext();
            context.put("packageName", packageName);
            context.put("className", className);
            context.put("sourceType", type);
            context.put("bindable", type.getAnnotation(Bindable.class));
            context.put("metadata", metadata);
            context.put("fieldInfos", fieldInfos);

            final Template template = velocity.getTemplate("BindableDescriptor.template", "UTF-8");
            template.merge(context, w);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Could not serialize metadata for: "
                + type.toString(), e);
        }
        finally
        {
            if (w != null) closeQuietly(w);
            Thread.currentThread().setContextClassLoader(ccl);
        }
    }

    /**
     * Emit bindable matadata as an XML file.
     */
    private void emitXML(BindableMetadata metadata, TypeElement type)
    {
        OutputStream os = null;
        try
        {
            final FileObject out = filer.createResource(StandardLocation.CLASS_OUTPUT,
                EMPTY_PACKAGE_NAME, metadataFileName(type), type);
            os = out.openOutputStream();

            final Persister p = new Persister(new Format(2, "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"));
            p.write(metadata, os);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Could not serialize metadata for: "
                + type.toString(), e);
        }
        finally
        {
            if (os != null) closeQuietly(os);
        }
    }

    /**
     * Return the resource name for a metadata XML file associated with the given type. 
     */
    private String metadataFileName(TypeElement type)
    {
        return elementUtils.getBinaryName(type).toString() + ".xml";
    }

    /**
     * Return the resource name for a metadata XML file associated with the given class
     * name. 
     */
    private String metadataFileName(String clazzName)
    {
        return clazzName + ".xml";
    }

    /*
     * 
     */
    private void closeQuietly(Closeable os)
    {
        try
        {
            os.close();
        }
        catch (IOException e)
        {
            // Ignore.
        }
    }

    /**
     * Extract title and description in one step.
     */
    private void extractTitleDescription(CommonMetadata metadata, String javaDoc)
    {
        if (javaDoc == null || javaDoc.trim().isEmpty()) return;

        final String comment = MetadataExtractorUtils.toPlainText(javaDoc);
        if (comment == null)
        {
            return;
        }

        final int next = MetadataExtractorUtils.getEndOfFirstSentenceCharIndex(comment);
        if (next > 0 && next < comment.length())
        {
            final String description = comment.substring(next + 1).trim();
            if (description.length() > 0)
            {
                metadata.setDescription(description);
            }
        }

        if (next >= 0)
        {
            final String firstSentence = comment.substring(0, next).trim();
            if (firstSentence.length() > 0)
            {
                metadata.setTitle(firstSentence);
            }
        }
        else
        {
            metadata.setTitle(comment);
        }
    }

    /**
     * Process JavaDoc of an element and extract taglets.
     */
    private String processJavaDoc(Element e, final Map<String, String> tags)
    {
        final StringBuilder javaDocText = new StringBuilder();

        final String javaDoc = elementUtils.getDocComment(e);
        if (javaDoc == null || javaDoc.trim().length() == 0)
        {
            return null;
        }

        /*
         * Use qdox to parse the JavaDoc part. JavaDoc is partially parsed 
         * by apt infrastructure, but we can fake it easily. 
         */
        final JFlexLexer lexer = new JFlexLexer(
            new StringReader("/** " + javaDoc + " */"));

        final Builder builder = new BuilderBase()
        {
            @Override
            public void addJavaDoc(String arg0)
            {
                javaDocText.append(arg0);
            }

            @Override
            public void addJavaDocTag(TagDef tag)
            {
                tags.put(tag.name, tag.text);
            }
        };

        final Parser parser = new Parser(lexer, builder);
        try
        {
            parser.parse();
        }
        catch (ParseException x)
        {
            throw new RuntimeException("Could not parse JavaDoc of: " + e.toString(), x);
        }

        return javaDocText.toString();
    }
}
