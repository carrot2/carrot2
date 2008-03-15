/**
 * 
 */
package org.carrot2.workbench.core.ui.clusters;

import org.carrot2.core.Cluster;
import org.carrot2.core.ClusterWithParent;
import org.carrot2.workbench.core.CorePlugin;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

final class ClusterLabelProvider extends LabelProvider
{
    Image folderImage = CorePlugin.getImageDescriptor("icons/folder.gif").createImage();

    @Override
    public String getText(Object element)
    {
        Cluster cluster = ((ClusterWithParent) element).cluster;
        return String.format("%s (%d)", cluster.getLabel(), cluster.size());
    }

    @Override
    public Image getImage(Object element)
    {
        return folderImage;
    }

    @Override
    public void dispose()
    {
        folderImage.dispose();
        super.dispose();
    }
}