
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2008, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.text.suffixtrees2;

/**
 * Default {@link INodeFactory} returning {@link Node}s.
 */
public final class DefaultNodeFactory implements INodeFactory<Node>
{
    public Node createNode(SuffixTree<? super Node> owner)
    {
        return new Node(owner);
    }
}
