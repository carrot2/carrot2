package org.ukrukar.converter.core.editors;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

public class TextInput implements IEditorInput {

	private String text;
	public TextInput(String text) {
		this.text = text;
	}
	
	public boolean exists() {
	    return false;
	  }

	  public String getToolTipText() {
	    return "tooltip";
	  }

	  public ImageDescriptor getImageDescriptor() {
	    return null;
	  }

	  public String getName() {
	    return text;
	  }

	  public IPersistableElement getPersistable() {
	    return null;
	  }

	  public boolean equals(Object obj) {
	    return false;
	  }

	  public int hashCode() {
	    return text.hashCode();
	  }

	public Object getAdapter(Class arg0) {
		return null;
	}


}
