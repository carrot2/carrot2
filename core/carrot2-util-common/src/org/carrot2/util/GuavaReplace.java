package org.carrot2.util;

import java.util.*;

public class GuavaReplace {
	public static <E> ArrayList<E> newArrayList(E e1, E e2) {
	    return new ArrayList<>(Arrays.asList(e1, e2));
    }

	public static <E> ArrayList<E> newArrayList(E e1, E e2, E e3) {
	    return new ArrayList<>(Arrays.asList(e1, e2, e3));
    }
	
	@SafeVarargs
	public static <E> ArrayList<E> newArrayList(E... elements) {
	    return new ArrayList<>(Arrays.asList(elements));
    }
}
