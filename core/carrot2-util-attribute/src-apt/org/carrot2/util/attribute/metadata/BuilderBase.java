
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

import com.thoughtworks.qdox.model.Annotation;
import com.thoughtworks.qdox.model.Type;
import com.thoughtworks.qdox.parser.Builder;
import com.thoughtworks.qdox.parser.structs.ClassDef;
import com.thoughtworks.qdox.parser.structs.FieldDef;
import com.thoughtworks.qdox.parser.structs.MethodDef;
import com.thoughtworks.qdox.parser.structs.PackageDef;
import com.thoughtworks.qdox.parser.structs.TagDef;
import com.thoughtworks.qdox.parser.structs.TypeDef;

/**
 * Empty implementation of qdox's builder.
 */
class BuilderBase implements Builder
{
    @Override
    public void addAnnotation(Annotation arg0)
    {
    }

    @Override
    public void addField(FieldDef arg0)
    {
    }

    @Override
    public void addImport(String arg0)
    {
    }

    @Override
    public void addJavaDoc(String arg0)
    {
    }

    @Override
    public void addJavaDocTag(TagDef arg0)
    {
    }

    @Override
    public void addPackage(PackageDef arg0)
    {
    }

    @Override
    public void beginClass(ClassDef arg0)
    {
    }

    @Override
    public Type createType(TypeDef arg0)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Type createType(String arg0, int arg1)
    {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void addParameter(FieldDef arg0)
    {
    }

    @Override
    public void endClass()
    {
    }

    @Override
    public void beginMethod()
    {
    }

    @Override
    public void endMethod(MethodDef arg0)
    {
    }
}
