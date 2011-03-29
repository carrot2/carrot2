package org.carrot2.workbench.vis.aduna;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Arrays;

import javax.swing.*;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.widgets.Composite;

import biz.aduna.map.cluster.*;

public class AdunaTest
{
    private static Composite scrollable;

    public static void main(String [] args)
    {
        Display display = Display.getDefault();
        
        System.out.println(display.getThread());

        Shell shell = new Shell(display);
        GridLayout layout = new GridLayout();
        shell.setLayout(layout);

        shell.setSize(600, 400);
        shell.setLocation(100, 100);

        createAdunaControl(shell);
        shell.open();

        while (!shell.isDisposed())
        {
            if (!display.readAndDispatch()) display.sleep();
        }
        display.dispose();
    }

    /*
     * 
     */
    private static Composite createAdunaControl(Composite parent)
    {
        /*
         * If <code>true</code>, try some dirty hacks to avoid flicker on Windows.
         */
        final boolean windowsFlickerHack = true;
        if (windowsFlickerHack)
        {
            System.setProperty("sun.awt.noerasebackground", "true");
        }

        scrollable = new Composite(parent, 
            SWT.H_SCROLL | SWT.V_SCROLL);
        scrollable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        final GridLayout layout = new GridLayout();
        layout.marginBottom = 0;
        layout.marginLeft = 0;
        layout.marginRight= 0;
        layout.marginTop = 0;
        layout.horizontalSpacing = 0;
        layout.verticalSpacing = 0;
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        scrollable.setLayout(layout);

        final Composite embedded = new Composite(scrollable, SWT.NO_BACKGROUND | SWT.EMBEDDED);
        embedded.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        final Frame frame = SWT_AWT.new_Frame(embedded);
        frame.setLayout(new BorderLayout());

        final JScrollPane scrollPanel = new JScrollPane(
            JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPanel.setDoubleBuffered(true);

        scrollPanel.setBorder(BorderFactory.createEmptyBorder());
        frame.add(scrollPanel, BorderLayout.CENTER);

        final ClusterMapFactory factory = ClusterMapFactory.createFactory();
        final ClusterMap clusterMap = factory.createClusterMap();
        final ClusterMapMediator mapMediator = factory.createMediator(clusterMap);

        final ClusterGraphPanel graphPanel = mapMediator.getGraphPanel();
        scrollPanel.setViewportView(graphPanel);

        scrollable.addControlListener(new ControlAdapter()
        {
            @Override
            public void controlResized(ControlEvent e)
            {
                updateScrollBars();
            }
        });

        final SelectionAdapter adapter = new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                ScrollBar hbar = scrollable.getHorizontalBar();
                ScrollBar vbar = scrollable.getVerticalBar();
                final java.awt.Rectangle viewport = new java.awt.Rectangle(
                    hbar.getSelection(),
                    vbar.getSelection(),
                    hbar.getThumb(), 
                    vbar.getThumb());
                SwingUtilities.invokeLater(new Runnable() {
                    public void run()
                    {
                        graphPanel.scrollRectToVisible(viewport);
                    }
                });
            }
        };
        scrollable.getVerticalBar().addSelectionListener(adapter);
        scrollable.getHorizontalBar().addSelectionListener(adapter);

        final Runnable updateScrollBarsAsync = new Runnable() {
            public void run() {
                updateScrollBars();
            }
        };
        
        graphPanel.addComponentListener(new ComponentAdapter()
        {
            @Override
            public void componentShown(ComponentEvent e)
            {
                graphPanelSize = graphPanel.getPreferredSize();
                Display.getDefault().asyncExec(updateScrollBarsAsync);
            }

            @Override
            public void componentResized(ComponentEvent e)
            {
                graphPanelSize = graphPanel.getPreferredSize();
                Display.getDefault().asyncExec(updateScrollBarsAsync);
            }
        });

        String o1 = "o1", o2 = "o2", o3 = "o3";
        DefaultClassification c0 = new DefaultClassification("c0"), c1, c2;
        c0.addChild(c1 = new DefaultClassification("c1", Arrays.asList(o1, o2, o3)));
        c0.addChild(c2 = new DefaultClassification("c2", Arrays.asList(o1, o2)));
        mapMediator.setClassificationTree(c0);
        mapMediator.visualize(Arrays.asList(c0, c1, c2));
        
        return scrollable;
    }

    private static volatile Dimension graphPanelSize;

    /*
     * 
     */
    protected static void updateScrollBars()
    {
        if (Display.findDisplay(Thread.currentThread()) == null)
            throw new IllegalStateException("Not an SWT thread: " + Thread.currentThread());

        if (graphPanelSize == null) 
            return;

        Rectangle swtScrollableArea = scrollable.getClientArea();

        int width = Math.max(graphPanelSize.width, 0);
        int viewportWidth = Math.max(swtScrollableArea.width, 0);
        updateScrollBar(scrollable.getHorizontalBar(), width, viewportWidth);

        int height = Math.max(graphPanelSize.height, 0);
        int viewportHeight = Math.max(swtScrollableArea.height, 0);
        updateScrollBar(scrollable.getVerticalBar(), height, viewportHeight);
    }

    private static void updateScrollBar(ScrollBar sbar, int value, int viewportValue)
    {
        int selection = sbar.getSelection();
        int minimum = 0;
        int maximum = value;
        int thumb = Math.min(viewportValue, value);
        int increment = /* SharedScrolledComposite.V_SCROLL_INCREMENT */ 64;
        int pageIncrement = Math.max(thumb - 5 * thumb / 100, 5);

        sbar.setValues(selection, minimum, maximum, thumb, increment, pageIncrement);
    }
}
