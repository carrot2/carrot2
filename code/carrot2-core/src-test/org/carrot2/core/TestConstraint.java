package org.carrot2.core;

import org.carrot2.core.parameters.Constraint;

public class TestConstraint implements Constraint<String> {

	public <V extends String> boolean isMet(V value) {
		return true;
	}

	

}
