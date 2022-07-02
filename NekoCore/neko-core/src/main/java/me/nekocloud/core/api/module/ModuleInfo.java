package me.nekocloud.core.api.module;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(TYPE)
@Retention(RUNTIME)
public @interface ModuleInfo {
	String name();
    String[] authors() default "";
	String version() default "";
    Dependency[] depends() default {};
}
