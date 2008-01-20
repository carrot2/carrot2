/**
 * TODO: This may sound a little fetishistic, but this package should probably be called
 * something like "bindables" or "bindable".
 */
package org.carrot2.core.parameter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Parameter {
	BindingPolicy policy();
	
    String key() default "";
}

