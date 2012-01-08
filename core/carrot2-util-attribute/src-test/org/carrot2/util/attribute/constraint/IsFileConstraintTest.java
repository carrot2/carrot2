
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

package org.carrot2.util.attribute.constraint;

import org.junit.Test;

/**
 * Test cases for {@link IsFile}.
 */
public class IsFileConstraintTest extends FileConstraintTestBase<IsFile>
{
    static class AnnotationContainer
    {
        @IsFile(mustExist = true)
        String mustExist;

        @IsFile(mustExist = false)
        String doesNotHaveToExist;
    }

    private static final String MUST_EXIST_FIELD_NAME = "mustExist";
    private static final String DOES_NOT_HAVE_TO_EXIST_FIELD_NAME = "doesNotHaveToExist";

    @Override
    Class<?> getAnnotationContainerClass()
    {
        return AnnotationContainer.class;
    }

    @Override
    Class<IsFile> getAnnotationType()
    {
        return IsFile.class;
    }

    String getInvalidTypeCheckFieldName()
    {
        return DOES_NOT_HAVE_TO_EXIST_FIELD_NAME;
    }

    @Test
    public void testNull() throws Exception
    {
        assertNotMet(null, MUST_EXIST_FIELD_NAME);
    }

    @Test
    public void testMustExistMet() throws Exception
    {
        assertMet(existingFile, MUST_EXIST_FIELD_NAME);
    }

    @Test
    public void testMustExistNotMetExistingDirectory() throws Exception
    {
        assertNotMet(existingDirectory, MUST_EXIST_FIELD_NAME);
    }

    @Test
    public void testMustExistNotMetNonExisting() throws Exception
    {
        assertNotMet(nonExisting, MUST_EXIST_FIELD_NAME);
    }

    @Test
    public void testDoesNotHaveToExistMet() throws Exception
    {
        assertMet(existingFile, DOES_NOT_HAVE_TO_EXIST_FIELD_NAME);
    }

    @Test
    public void testDoesNotHaveToExistMetNonExisting() throws Exception
    {
        assertMet(nonExisting, DOES_NOT_HAVE_TO_EXIST_FIELD_NAME);
    }

    @Test
    public void testDoesNotHaveToExistNotMetExistingDirectory() throws Exception
    {
        assertNotMet(existingDirectory, DOES_NOT_HAVE_TO_EXIST_FIELD_NAME);
    }
}
