/*
 * Copyright (c) 2000-2006 JGoodies Karsten Lentzsch. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 * 
 *  o Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer. 
 *     
 *  o Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution. 
 *     
 *  o Neither the name of JGoodies Karsten Lentzsch nor the names of 
 *    its contributors may be used to endorse or promote products derived 
 *    from this software without specific prior written permission. 
 *     
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR 
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; 
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE 
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. 
 */

package com.jgoodies.uif_lite.component;

import java.awt.Component;
import java.awt.Insets;

import javax.swing.*;


/**
 * A very light version of the JGoodies <code>UIFactory</code> class.
 * It consists only of static methods to create frequently used components.
 *
 * @author Karsten Lentzsch
 * @version $Revision$
 */

public final class Factory {

    /** Defines the margin used in toolbar buttons. */
    private static final Insets TOOLBAR_BUTTON_MARGIN = new Insets(1, 1, 1, 1);

    /**
     * Creates and answers a <code>JScrollPane</code> that has an empty
     * border.
     */
    public static JScrollPane createStrippedScrollPane(Component component) {
        JScrollPane scrollPane = new JScrollPane(component);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        return scrollPane;
    }

    /**
     * Creates and returns a <code>JSplitPane</code> that has empty borders.
     * Useful to avoid duplicate decorations, for example if the split pane
     * is contained by other components that already provide a border.
     * 
     * @param orientation    the split pane's orientation: horizontal or vertical
     * @param comp1          the top/left component
     * @param comp2          the bottom/right component
     * @param resizeWeight   indicates how to distribute extra space
     * @return a split panes that has an empty border
     */
    public static JSplitPane createStrippedSplitPane(int orientation,
            Component comp1, Component comp2, double resizeWeight) {
        JSplitPane split = UIFSplitPane.createStrippedSplitPane(orientation, comp1, comp2);
        split.setResizeWeight(resizeWeight);
        return split;
    }
    
    /**
     * Creates and answers an <code>AbstractButton</code> 
     * configured for use in a JToolBar.<p>
     * 
     * Superceded by ToolBarButton from the JGoodies UI framework.
     */
    public static AbstractButton createToolBarButton(Action action) {
        JButton button = new JButton(action);
        button.setFocusPainted(false);
        button.setMargin(TOOLBAR_BUTTON_MARGIN);
        //button.setHorizontalTextPosition(SwingConstants.CENTER);
        //button.setVerticalTextPosition(SwingConstants.BOTTOM);
        button.setText("");
        return button;
    }

}