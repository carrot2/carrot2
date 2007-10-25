package org.krukar.converter.core.logic.loader;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.krukar.converter.core.logic.DefaultConverter;
import org.krukar.converter.core.logic.ITextConverter;

public class TextConverterWrapper {

	public static final TextConverterWrapper DEFAULT = new TextConverterWrapper() {

		@Override
		public String getCaption() {
			return "Default";
		}
		
		@Override
		public ITextConverter getExecutableConverter() {
			return new DefaultConverter();
		}
		
	};

	private IConfigurationElement element;
	private String className;
	private String caption;

	private TextConverterWrapper() {
	};

	public TextConverterWrapper(IConfigurationElement element) {
		this.element = element;
		this.caption = element.getAttribute(ConverterLoader.ATT_CAPTION);
		this.className = element.getAttribute(ConverterLoader.ATT_CLASS);
	}

	public String getCaption() {
		return caption;
	}

	public ITextConverter getExecutableConverter() {
		try {
			return (ITextConverter) element
					.createExecutableExtension(ConverterLoader.ATT_CLASS);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return null;
	}
}
