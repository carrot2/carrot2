package org.ukrukar.converter.core.algotithms;

import org.krukar.converter.core.logic.ITextConverter;

public class ToAConverter implements ITextConverter {

	public String convert(String text) {
		return text.replaceAll("\\S", "A");
	}

}
