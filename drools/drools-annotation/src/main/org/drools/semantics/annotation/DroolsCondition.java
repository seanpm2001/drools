package org.drools.semantics.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Inherited;
import java.lang.annotation.Target;
import java.lang.annotation.Retention;

@Inherited
@Target( {ElementType.METHOD} )
@Retention( RetentionPolicy.RUNTIME )
public @interface DroolsCondition 
{
    // marker
}