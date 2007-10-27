package org.krukar.converter.core.logic.loader;

import org.eclipse.core.commands.operations.OperationStatus;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IStatus;
import org.krukar.converter.core.logic.DefaultConverter;
import org.krukar.converter.core.logic.ITextConverter;
import org.ukrukar.converter.core.Activator;

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
	private String caption;

	private TextConverterWrapper() {
	};

	public TextConverterWrapper(IConfigurationElement element) {
		this.element = element;
		this.caption = element.getAttribute(ConverterLoader.ATT_CAPTION);
		if (caption == null || caption.length() == 0) {
			throw new IllegalArgumentException("Missing "+ConverterLoader.ATT_CAPTION+" attribute");
		}
		String classAtt = element.getAttribute(ConverterLoader.ATT_CLASS);
		if (classAtt == null || classAtt.length() == 0) {
			throw new IllegalArgumentException("Missing "+ConverterLoader.ATT_CLASS+" attribute");
		}
	}

	public String getCaption() {
		return caption;
	}

	public ITextConverter getExecutableConverter() {
		try {
			return (ITextConverter) element
					.createExecutableExtension(ConverterLoader.ATT_CLASS);
		} catch (CoreException e) {
			Activator.getDefault().getLog().log(
					new OperationStatus(IStatus.ERROR,
							Activator.PLUGIN_ID, -2,
							"Error while initializing converter "+
							element.getDeclaringExtension().getContributor().getName(),
							e));
		}
		return null;
	}
}
