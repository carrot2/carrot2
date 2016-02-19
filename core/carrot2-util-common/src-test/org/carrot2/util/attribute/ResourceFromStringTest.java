
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

package org.carrot2.util.attribute;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;

import org.carrot2.util.attribute.AttributeBinder.AttributeTransformerFromString;
import org.carrot2.util.attribute.constraint.ImplementingClasses;
import org.carrot2.util.resource.FileResource;
import org.carrot2.util.resource.IResource;
import org.carrot2.util.resource.URLResource;
import org.carrot2.util.resource.URLResourceWithParams;
import org.carrot2.util.tests.CarrotTestCase;
import org.junit.Test;

/**
 * Test cases for {@link AttributeTransformerFromString}.
 */
public class ResourceFromStringTest extends CarrotTestCase
{
    @ImplementingClasses(classes =
    {
        FileResource.class, URLResourceWithParams.class, URLResource.class
    })
    private IResource resource;

    @Test
    public void testFileResourceFile() throws Exception
    {
        final File file = File.createTempFile(
            ResourceFromStringTest.class.getSimpleName(), "");
        file.deleteOnExit();
        check("resource", file.getAbsolutePath(), new FileResource(file));
    }

    @Test
    public void testFileResourceUrlWithParameters() throws Exception
    {
        String url = "http://search.carrot2.org?q=test";
        check("resource", url, new URLResourceWithParams(new URL(url)));
    }

    private void check(String fieldName, String stringValue,
        Object expectedTransformedValue) throws Exception
    {
        final Field field = ResourceFromStringTest.class.getDeclaredField(fieldName);
        assertThat(AttributeTransformerFromString.INSTANCE.transform(
            stringValue, null, field)).isEqualTo(expectedTransformedValue);
    }
}
