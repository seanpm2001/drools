package org.drools.semantics.annotation.model;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.drools.DroolsException;
import org.drools.rule.Declaration;
import org.drools.rule.InvalidRuleException;
import org.drools.rule.Rule;
import org.drools.semantics.annotation.*;
import org.drools.semantics.base.ClassObjectType;
import org.drools.spi.Consequence;

public class AnnonatedPojoRuleBuilder
{
    private static interface ParameterValidator
    {
        void assertParameter( ParameterValue newParameterValue, List<ParameterValue> parameterValues )
                throws DroolsException;
    }

    public Rule buildRule( Rule rule, Object pojo ) throws DroolsException
    {
        Class< ? > ruleClass = pojo.getClass( );
        buildConditions( rule, ruleClass, pojo );
        buildConsequence( rule, ruleClass, pojo );
        return rule;
    }

    private static void buildConditions( Rule rule, Class< ? > ruleClass, Object pojo )
            throws DroolsException
    {
        for (Method method : ruleClass.getMethods( ))
        {
            DroolsCondition conditionAnnotation = method.getAnnotation( DroolsCondition.class );
            if (conditionAnnotation != null)
            {
                PojoCondition condition = newMethodCondition( rule, pojo, method );
                rule.addCondition( condition );
            }
        }
    }

    private static void buildConsequence( Rule rule, Class< ? > ruleClass, Object pojo )
            throws DroolsException
    {
        Consequence consequence = null;
        for (Method method : ruleClass.getMethods( ))
        {
            DroolsConsequence consequenceAnnotation = method
                    .getAnnotation( DroolsConsequence.class );
            if (consequenceAnnotation != null)
            {
                if (consequence != null)
                {
                    throw new DroolsException( "Rule must only contain one consequence method"
                            + ": class = " + ruleClass + ", method = " + method );
                }
                consequence = newMethodConsequence( rule, pojo, method );
                rule.setConsequence( consequence );
            }
        }
        if (consequence == null)
        {
            throw new DroolsException( "Rule must define a consequence method" + ": class = "
                    + ruleClass );
        }
    }

    private static PojoCondition newMethodCondition( Rule rule, Object pojo, Method pojoMethod )
            throws DroolsException
    {
        assertReturnType( pojoMethod, boolean.class );
        ParameterValidator parameterValidator = new ParameterValidator( ) {
            public void assertParameter( ParameterValue newParameterValue,
                                        List<ParameterValue> parameterValues )
                    throws DroolsException
            {
                if (newParameterValue instanceof DroolsParameterValue)
                {
                    throw new DroolsException(
                            "Condition methods cannot declare a parameter of type Drools" );
                }
            }
        };
        return new PojoCondition( new RuleReflectMethod( rule, pojo, pojoMethod,
                getParameterValues( rule, pojoMethod, parameterValidator ) ) );
    }

    private static PojoConsequence newMethodConsequence( Rule rule, Object pojo, Method pojoMethod )
            throws DroolsException
    {
        assertReturnType( pojoMethod, void.class );
        ParameterValidator parameterValidator = new ParameterValidator( ) {
            private boolean hasDroolsParameterValue;

            public void assertParameter( ParameterValue newParameterValue,
                                        List<ParameterValue> parameterValues )
                    throws DroolsException
            {
                if (newParameterValue instanceof DroolsParameterValue)
                {
                    if (hasDroolsParameterValue)
                    {
                        throw new DroolsException(
                                "Consequence methods can only declare on parameter of type Drools" );
                    }
                    hasDroolsParameterValue = true;
                }
            }
        };
        return new PojoConsequence( new RuleReflectMethod( rule, pojo, pojoMethod,
                getParameterValues( rule, pojoMethod, parameterValidator ) ) );
    }

    private static void assertReturnType( Method method, Class returnClass ) throws DroolsException
    {
        if (method.getReturnType( ) != returnClass)
        {
            throw new DroolsException( "Rule method returns the wrong class" + ": method = "
                    + method + ", expected return class = " + returnClass
                    + ", actual return class = " + method.getReturnType( ) );
        }
    }

    private static ParameterValue[] getParameterValues( Rule rule, Method method,
                                                       ParameterValidator validator )
            throws DroolsException
    {
        Class< ? >[] parameterClasses = method.getParameterTypes( );
        Annotation[][] parameterAnnotations = method.getParameterAnnotations( );
        List<ParameterValue> parameterValues = new ArrayList<ParameterValue>( );

        for (int i = 0; i < parameterClasses.length; i++)
        {
            Class< ? > parameterClass = parameterClasses[i];
            ParameterValue parameterValue = null;
            try
            {
                parameterValue = getParameterValue( rule, parameterClass, parameterAnnotations[i] );
            }
            catch (DroolsException e)
            {
                throwContextDroolsException( method, i, parameterClass, e );
            }

            try
            {
                validator.assertParameter( parameterValue, parameterValues );
            }
            catch (DroolsException e)
            {
                throwContextDroolsException( method, i, parameterClass, e );
            }
            parameterValues.add( parameterValue );
        }
        return parameterValues.toArray( new ParameterValue[parameterValues.size( )] );
    }

    private static void throwContextDroolsException( Method method, int i,
                                                    Class< ? > parameterClass, Exception e )
            throws DroolsException
    {
        throw new DroolsException( e.getMessage( ) + ": method = " + method + ", parameter[" + i
                + "] = " + parameterClass, e );
    }

    private static ParameterValue getParameterValue( Rule rule, Class< ? > parameterClass,
                                                    Annotation[] parameterAnnotations )
            throws InvalidRuleException, DroolsException
    {

        ParameterValue parameterValue = null;
        if (parameterClass == Drools.class)
        {
            parameterValue = new DroolsParameterValue( rule );
        }
        else
        {
            for (Annotation annotation : parameterAnnotations)
            {
                if (annotation instanceof DroolsParameter)
                {
                    assertNonConflictingParameterAnnotation( parameterValue );
                    String parameterId = ((DroolsParameter) annotation).value( );
                    Declaration declaration = rule.getParameterDeclaration( parameterId );
                    if (declaration == null)
                    {
                        ClassObjectType classObjectType = new ClassObjectType( parameterClass );
                        declaration = rule.addParameterDeclaration( parameterId, classObjectType );
                    }
                    parameterValue = new TupleParameterValue( declaration );
                }
                else if (annotation instanceof DroolsApplicationData)
                {
                    assertNonConflictingParameterAnnotation( parameterValue );
                    String parameterId = ((DroolsApplicationData) annotation).value( );
                    parameterValue = new ApplicationDataParameterValue( parameterId, parameterClass );
                }
            }
        }
        if (parameterValue == null)
        {
            throw new DroolsException(
                    "Method parameter not annoated with either @Drools.Parameter or @Drools.ApplicationData" );
        }
        return parameterValue;
    }

    private static void assertNonConflictingParameterAnnotation( ParameterValue parameterValue )
            throws DroolsException
    {
        if (parameterValue != null)
        {
            throw new DroolsException( "Method parameter contains conflicting @Drools annotations" );
        }
    }
}