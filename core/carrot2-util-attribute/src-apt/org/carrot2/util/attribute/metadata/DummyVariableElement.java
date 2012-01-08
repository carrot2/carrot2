
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

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;

/**
 * A dummy class patching Eclipse bugs.
 */
public class DummyVariableElement implements VariableElement
{
    private Name simpleName;

    public DummyVariableElement(Name simpleName)
    {
        this.simpleName = simpleName;
    }

    @Override
    public TypeMirror asType()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public ElementKind getKind()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<? extends AnnotationMirror> getAnnotationMirrors()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public <A extends Annotation> A getAnnotation(Class<A> annotationType)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<Modifier> getModifiers()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Name getSimpleName()
    {
        return simpleName;
    }

    @Override
    public Element getEnclosingElement()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<? extends Element> getEnclosedElements()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public <R, P> R accept(ElementVisitor<R, P> v, P p)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getConstantValue()
    {
        throw new UnsupportedOperationException();
    }
}
