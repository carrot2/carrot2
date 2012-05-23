
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2012, Dawid Weiss, Stanisław Osiński.
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
import java.util.regex.Pattern;

import javax.annotation.processing.*;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.*;
import javax.tools.Diagnostic.Kind;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.runtime.RuntimeInstance;
import org.carrot2.util.attribute.*;

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
     * Ignored Eclipse compilation phases.
     */
    private final static Set<String> ignoredPhases = new HashSet<String>(
        Arrays.asList("RECONCILE", "OTHER"));

    /**
     * Mirror element utilities.
     */
    private Elements elements;

    /**
     * Apt filer utilities.
     */
    private Filer filer;
    
    /**
     * Apt type utilities.
     */
    private Types types;

    /**
     * Round number.
     */
    private int round;

    /**
     * Messager.
     */
    private Messager messager;
    
    /**
     * Initialize processing environment.
     */
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv)
    {
        super.init(processingEnv);

        elements = this.processingEnv.getElementUtils();
        filer = this.processingEnv.getFiler();
        types = this.processingEnv.getTypeUtils();
        messager = this.processingEnv.getMessager();

        round = 0;
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
        final long start = System.currentTimeMillis();

        // Scan for all types marked with @Bindable and processed in this round.
        round++;

        int count = 0;
        for (TypeElement e : ElementFilter.typesIn(env.getElementsAnnotatedWith(Bindable.class)))
        {
            // e can be null in Eclipse, so check for this case.
            if (e == null) continue;
            processBindable(e);
            count++;
        }

        if (count > 0)
        {
            System.out.println(
                String.format(Locale.ENGLISH,
                    "%d @Bindable metadata processed in round %d in %.2f secs.",
                    count,
                    round, 
                    (System.currentTimeMillis() - start) / 1000.0));
        }

        return false;
    }

    /**
     * All bindable types have been collected for this round, do the actual processing.
     */
    private void processBindable(TypeElement type)
    {
        // Extract the bindable's title, description and label.
        final Map<String, String> tags = new HashMap<String, String>();
        final String javaDoc = processJavaDoc(type, tags);
        final BindableMetadata metadata = new BindableMetadata();
        extractTitleDescription(metadata, javaDoc);
        
        if (tags.get("label") != null) {
            messager.printMessage(Kind.WARNING, 
                "Replace @label javadoc with a @Label annotation in: " +
                    type.getQualifiedName());
        }
        Label label = type.getAnnotation(Label.class);
        if (label != null) {
            metadata.setLabel(label.value());
        }

        // Collect this bindable's attribute metadata.
        final Map<String, AttributeMetadata> attributeMetadata = new LinkedHashMap<String, AttributeMetadata>();
        final List<AttributeFieldInfo> ownFields = new ArrayList<AttributeFieldInfo>();
        extractAttributeMetadata(type, ownFields, attributeMetadata);
        metadata.setAttributeMetadata(attributeMetadata);

        final List<AttributeFieldInfo> superFields = new ArrayList<AttributeFieldInfo>();
        for (TypeElement i = extractSuperBindable(type); i != null; i = extractSuperBindable(i))
        {
            extractAttributeMetadata(i, superFields, null);
        }

        final List<AttributeFieldInfo> allFields = new ArrayList<AttributeFieldInfo>();
        allFields.addAll(ownFields);
        allFields.addAll(superFields);

        emitMetadataClass(metadata, ownFields, allFields, type);
    }

    /**
     * Extract the descriptor class for the first superclass that is {@link Bindable}
     * or return null if none.
     */
    private TypeElement extractSuperBindable(TypeElement type)
    {
        TypeMirror superType = type.getSuperclass();
        while (superType.getKind() == TypeKind.DECLARED) 
        {
            TypeElement asElement = (TypeElement) types.asElement(superType);

            if (asElement != null && asElement.getAnnotation(Bindable.class) != null)
            {
                return asElement;
            }

            superType = asElement.getSuperclass();
        }

        return null;
    }

    /**
     * Extract attribute information and metadata (if available).
     */
    private void extractAttributeMetadata(TypeElement type,
        List<AttributeFieldInfo> attributeFields,
        Map<String, AttributeMetadata> attributeMetadata)
    {
        List<TypeElement> inheritedTypes = extractInheritedTypes(type);
        Map<String, AttributeFieldInfo> inheritedAttrs = extractInheritedAttrs(inheritedTypes);

        for (VariableElement field : ElementFilter.fieldsIn(type.getEnclosedElements()))
        {
            Attribute attr = field.getAnnotation(Attribute.class);
            if (attr != null)
            {
                AttributeMetadata metadata = null;
                String javaDoc = null;

                if (attributeMetadata != null)
                {
                    metadata = new AttributeMetadata();

                    final Map<String, String> tags = new HashMap<String, String>();
                    javaDoc = processJavaDoc(field, tags);

                    extractTitleDescription(metadata, javaDoc);

                    // Process @Level
                    if (tags.get("level") != null) {
                        messager.printMessage(Kind.WARNING, 
                            "Replace @level javadoc with a @Level annotation in: " +
                                type.getQualifiedName() + "#" + field.getSimpleName());
                    }

                    Level level = field.getAnnotation(Level.class);
                    if (level != null) {
                        metadata.setLevel(level.value());
                    }

                    // Process @Group
                    if (tags.get("group") != null) {
                        messager.printMessage(Kind.WARNING, 
                            "Replace @group javadoc with a @Group annotation in: " +
                                type.getQualifiedName() + "#" + field.getSimpleName());
                    }

                    Group group = field.getAnnotation(Group.class);
                    if (group != null) {
                        metadata.setGroup(group.value());
                    }                    

                    // Process @Label
                    if (tags.get("label") != null) {
                        messager.printMessage(Kind.WARNING, 
                            "Replace @label javadoc with a @Label annotation in: " +
                                type.getQualifiedName() + "#" + field.getSimpleName());
                    }

                    Label label = field.getAnnotation(Label.class);
                    if (label != null) {
                        metadata.setLabel(label.value());
                    }
                }

                AttributeFieldInfo inherited = null;
                final String attributeKey = getAttributeKey(field, attr);
                if (attr.inherit())
                {
                    inherited = inheritedAttrs.get(attributeKey);
                    if (inherited == null)
                    {
                        /*
                         * Eclipse compiler is broken: the reflected types are sometimes returned
                         * as inaccessible (unresolved). Try to hardcode the field.
                         */
                        if (inheritedTypes.size() == 1 && isEclipseCompiler())
                        {
                            TypeElement e = inheritedTypes.get(0);

                            DummyVariableElement _field = new DummyVariableElement(field.getSimpleName());

                            inherited = new AttributeFieldInfo(
                                attributeKey, null, null, _field, types, 
                                e.getQualifiedName().toString(), 
                                getDescriptorClassName(e), null,
                                false);
                        }
                    }

                    if (inherited == null)
                    {
                        String message = "No inheritable attribute for field " + field
                            + " in class " + type.getQualifiedName() + " (expected inherited key: " +
                            attributeKey + ").";

                        messager.printMessage(Kind.ERROR, message); 
                    }
                }

                attributeFields.add(
                    new AttributeFieldInfo(attributeKey, metadata, javaDoc, field, types, 
                        type.getQualifiedName().toString(),
                        getDescriptorClassName(type), inherited,
                        shouldGenerateClassSetter(field)));

                if (attributeMetadata != null)
                {
                    // See http://issues.carrot2.org/browse/CARROT-706
                    final String fieldName = field.getSimpleName().toString();
                    attributeMetadata.put(fieldName, metadata);
                }
            }
        }
    }

    /**
     * Try to detect eclipse compiler.
     */
    private boolean isEclipseCompiler()
    {
        return processingEnv.getClass().getName().startsWith("org.eclipse.");
    }

    /**
     * Extract doc. inheritance sources from {@link Bindable#inherit()} attribute.
     */
    private Map<String, AttributeFieldInfo> extractInheritedAttrs(List<TypeElement> inheritedTypes)
    {
        final Map<String, AttributeFieldInfo> inheritedAttrs = 
            new HashMap<String, AttributeFieldInfo>();

        for (TypeElement elem : inheritedTypes)
        {
            List<AttributeFieldInfo> attrs = new ArrayList<AttributeFieldInfo>();
            extractAttributeMetadata(elem, attrs, null);
    
            for (AttributeFieldInfo attr : attrs)
            {
                if (inheritedAttrs.containsKey(attr.getKey()))
                {
                    String message = "Duplicated attribute key "
                        + attr.getKey() + " in attribute documentation inheritance " 
                        + "classes: "
                        + attr.getDeclaringClass() + ", "
                        + inheritedAttrs.get(attr.getKey()).getDeclaringClass();
    
                    messager.printMessage(Kind.ERROR, message);
                    throw new RuntimeException(message); 
                }
                inheritedAttrs.put(attr.getKey(), attr);
            }
        }

        return inheritedAttrs;
    }
    
    /**
     * Extract a list of type mirrors from the {@link Bindable#inherit()}.
     */
    private List<TypeElement> extractInheritedTypes(TypeElement type)
    {
        final List<TypeElement> inheritedTypes = new ArrayList<TypeElement>();

        for (AnnotationMirror m : type.getAnnotationMirrors())
        {
            final Map<? extends ExecutableElement, ? extends AnnotationValue> values = 
                elements.getElementValuesWithDefaults(m);

            for (ExecutableElement e : values.keySet())
            {
                if (e.getSimpleName().toString().equals("inherit"))
                {
                    @SuppressWarnings("unchecked")
                    List<? extends AnnotationValue> clazzMirrors = 
                        (List<? extends AnnotationValue>) values.get(e).getValue();

                    for (AnnotationValue clazzMirror : clazzMirrors)
                    {
                        TypeMirror typeMirror = (TypeMirror) clazzMirror.getValue();
                        TypeElement elem = (TypeElement) types.asElement(typeMirror);

                        inheritedTypes.add(elem);
                    }
                }
            }
        }        

        return inheritedTypes;
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
            {
                return elements.getBinaryName((TypeElement) field.getEnclosingElement())
                    + "."
                    + field.getSimpleName().toString();
            }
            else
            {
                return bindablePrefix + "." + field.getSimpleName();
            }
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
     * Emit bindable matadata as a class with statically collected information.
     */
    private void emitMetadataClass(BindableMetadata metadata, 
        List<AttributeFieldInfo> ownFields, 
        List<AttributeFieldInfo> allFields, 
        TypeElement type)
    {
        String packageName = elements.getPackageOf(type).getQualifiedName().toString();
        String descriptorClassName = getDescriptorClassName(type);

        PrintWriter w = null;
        ClassLoader ccl = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
        try
        {
            w = new PrintWriter(filer.createSourceFile(descriptorClassName, type).openWriter());

            final RuntimeInstance velocity = VelocityInitializer.createInstance(
                super.processingEnv.getMessager());
            final VelocityContext context = VelocityInitializer.createContext();
            context.put("packageName", packageName);
            context.put("descriptorClassName", descriptorClassName);
            context.put("sourceType", type);
            context.put("bindable", type.getAnnotation(Bindable.class));
            context.put("metadata", metadata);
            context.put("ownFields", ownFields);
            context.put("allFields", allFields);
            context.put("nestedFields", extractStaticNestedBindables(type));
            
            TypeElement superBindable = extractSuperBindable(type);
            if (superBindable != null)
            {
                context.put("superBindableDescriptor", getDescriptorClassName(superBindable));
            }

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
            Thread.currentThread().setContextClassLoader(ccl);
            if (w != null) closeQuietly(w);
        }
    }

    /**
     * Return the descriptor class for a given type.
     */
    private String getDescriptorClassName(TypeElement type)
    {
        return BindableDescriptorUtils.getDescriptorClassName(
            elements.getBinaryName(type).toString());
    }

    /**
     * Extract static nested bindables: final fields that whose type is {@link Bindable}
     * and which can be integrated into the builder.
     */
    private List<BindableFieldInfo> extractStaticNestedBindables(TypeElement type)
    {
        final List<BindableFieldInfo> list = new ArrayList<BindableFieldInfo>();
        
        for (VariableElement field : ElementFilter.fieldsIn(type.getEnclosedElements()))
        {
            TypeElement fieldClass = (TypeElement) types.asElement(field.asType());
            if (fieldClass != null && fieldClass.getAnnotation(Bindable.class) != null)
            {
                if (field.getModifiers().contains(Modifier.FINAL))
                {
                    list.add(new BindableFieldInfo(
                        field, fieldClass, getDescriptorClassName(fieldClass)));
                }
                else
                {
                    /*
                    messager.printMessage(Kind.NOTE, "Non-final bindable field: " +
                        field + " in class " + type.getSimpleName());
                    */
                }
            }
        }

        return list;
    }

    /**
     * Check if a field's type a primitive or a boxed primitive.
     */
    private boolean shouldGenerateClassSetter(VariableElement f)
    {
        TypeMirror asType = f.asType();
        TypeKind kind = asType.getKind();

        if (kind.isPrimitive() || kind != TypeKind.DECLARED)
            return false;

        String rawType = types.erasure(f.asType()).toString();
        if (rawType.startsWith("java.lang.") || 
            rawType.startsWith("java.util."))
        {
            return false;
        }

        return true;
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
        catch (Exception e)
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

        final String javaDoc = elements.getDocComment(e);
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
        
        // Post-process the acquired JavaDoc.
        String processed = javaDocText.toString();
        
        // Process local type references and add full prefix.
        Pattern typeRefs = Pattern.compile("\\{\\@link\\s+\\#", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
        processed = typeRefs.matcher(processed).replaceAll(
            "{@link " + typeOf(e).getQualifiedName() + "#");

        return processed.toString();
    }

    /**
     * Get the class of an element (or return itself if it's a class already). 
     */
    private TypeElement typeOf(Element e)
    {
        switch (e.getKind())
        {
            case CLASS:
            case ENUM:
                return (TypeElement) e;

            case METHOD:
            case FIELD:
                return (TypeElement) e.getEnclosingElement();

            default:
                throw new RuntimeException("Unexpected type: " + e);
        }
    }
}
