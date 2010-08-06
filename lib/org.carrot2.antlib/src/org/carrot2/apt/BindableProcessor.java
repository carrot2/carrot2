package org.carrot2.apt;

import static javax.lang.model.SourceVersion.RELEASE_6;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementVisitor;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

/**
 * Java6+ compatible annotation processor for parsing <code>Bindable</code>-annotated
 * types and generating their metadata.
 */
@SupportedAnnotationTypes("org.carrot2.util.attribute.Bindable")
@SupportedSourceVersion(RELEASE_6)
public final class BindableProcessor extends AbstractProcessor
{
    /**
     * Bindable annotation class name, fully qualified.
     */
    private final static String BINDABLE_CLASS = "org.carrot2.util.attribute.Bindable";

    /**
     * Mirror element utilities.
     */
    private Elements elementUtils;

    /**
     * Apt filer utilities.
     */
    private Filer filer;

    /**
     * Visitor scanning for {@link #BINDABLE_CLASS}.
     */
    private final ElementVisitor<List<TypeElement>, List<TypeElement>> visitor = new ElementVisitorBase<List<TypeElement>, List<TypeElement>>()
    {
        @Override
        public List<TypeElement> visitType(TypeElement e, List<TypeElement> bindables)
        {
            for (AnnotationMirror annotation : e.getAnnotationMirrors())
            {
                String name = elementUtils.getBinaryName(
                    (TypeElement) annotation.getAnnotationType().asElement()).toString();

                if (BINDABLE_CLASS.equals(name)) bindables.add(e);
            }

            return bindables;
        }
    };

    /**
     * Initialize processing environment.
     */
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv)
    {
        super.init(processingEnv);
        elementUtils = this.processingEnv.getElementUtils();
        filer = this.processingEnv.getFiler();
    }

    /**
     * Process a set of roots for the current round.
     */
    @Override
    public boolean process(Set<? extends TypeElement> ann, RoundEnvironment env)
    {
        if (env.errorRaised() || env.processingOver())
        {
            return false;
        }

        // Scan for all types marked with @Bindable
        final ArrayList<TypeElement> bindableTypes = new ArrayList<TypeElement>(); 
        for (Element e : env.getRootElements())
        {
            e.accept(visitor, bindableTypes);
        }

        // For every bindable type, create and emit attribute metadata.
        for (TypeElement type : bindableTypes)
        {
            final String clazzName = elementUtils.getBinaryName(type).toString();
            final String metadataFileName = clazzName + ".apt.xml";

            touch(metadataFileName, type);
        }

        return false;
    }

    /**
     * Create an empty file corresponding to bindable metadata.  
     */
    private void touch(String metadataFileName, TypeElement type)
    {
        try
        {
            FileObject res = filer.createResource(
                StandardLocation.CLASS_OUTPUT, "", metadataFileName, type);

            res.openOutputStream().close();
        }
        catch (IOException e)
        {
            throw new RuntimeException("Could not process metadata file: " +
                metadataFileName, e);
        }
    }
}
