package me.nekocloud.core.api.module;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({})
public @interface Dependency {
  String name();

  boolean optional() default false;
}
