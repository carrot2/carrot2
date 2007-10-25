package org.krukar.converter.core.logic.loader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.TypeConstraintException;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Platform;
import org.krukar.converter.core.logic.ITextConverter;
import org.ukrukar.converter.core.Activator;

public class ConverterLoader {

	static final String ELEMENT_NAME = "convertion";
	static final String ATT_CAPTION = "caption";
	static final String ATT_CLASS = "class";
	private static Map<String, TextConverterWrapper> converterCache;

	public static List<String> getCaptions() {
		loadExtensions();
		return new ArrayList<String>(converterCache.keySet());
	}

	public static ITextConverter getConverter(String caption) {
		return converterCache.get(caption).getExecutableConverter();
	}

	private static void loadExtensions() {
		if (converterCache == null) {
			converterCache = new HashMap<String, TextConverterWrapper>();
			converterCache.put(TextConverterWrapper.DEFAULT.getCaption(),
					TextConverterWrapper.DEFAULT);
			IExtension[] extensions = Platform.getExtensionRegistry()
					.getExtensionPoint(Activator.PLUGIN_ID, "convertion")
					.getExtensions();
			for (int i = 0; i < extensions.length; i++) {
				IExtension extension = extensions[i];
				parseExtension(extension.getConfigurationElements());
			}
		}
	}

	private static void parseExtension(
			IConfigurationElement[] configurationElements) {
		for (int i = 0; i < configurationElements.length; i++) {
			IConfigurationElement configurationElement = configurationElements[i];
			if (!configurationElement.getName().equals(ELEMENT_NAME)) {
				return;
			}
			TextConverterWrapper wrapper = new TextConverterWrapper(
					configurationElement);
			converterCache.put(wrapper.getCaption(), wrapper);
		}

	}

}
