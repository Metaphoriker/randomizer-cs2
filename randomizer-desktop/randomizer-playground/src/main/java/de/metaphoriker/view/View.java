package de.metaphoriker.view;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Marks a class as a view. */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface View {}
