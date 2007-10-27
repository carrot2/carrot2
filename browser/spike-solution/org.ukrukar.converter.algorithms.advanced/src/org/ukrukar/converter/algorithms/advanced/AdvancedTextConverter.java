package org.ukrukar.converter.algorithms.advanced;

import org.converter.utils.ConverterUtils;
import org.krukar.converter.core.logic.ITextConverter;


public class AdvancedTextConverter implements ITextConverter {

	public String convert(String text) {
		return ConverterUtils.doubleString(text);
	}

}
