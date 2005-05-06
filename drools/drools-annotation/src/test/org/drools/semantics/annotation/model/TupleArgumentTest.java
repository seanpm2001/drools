package org.drools.semantics.annotation.model;

import org.drools.rule.Declaration;
import org.drools.rule.Rule;
import org.drools.semantics.annotation.model.TupleArgument;
import org.drools.spi.Tuple;
import org.easymock.container.EasymockContainer;
import org.easymock.container.EasymockContainer.Mock;

import junit.framework.TestCase;

public class TupleArgumentTest extends TestCase {
    private EasymockContainer mocks = new EasymockContainer();

    private static class ValueClass extends Object {}

    Declaration declaration;
    ValueClass expectedValue = new ValueClass();
    Mock<Tuple> mockTuple = mocks.createMock(Tuple.class);

    private TupleArgument arg;

    protected void setUp() throws Exception {
        Rule rule = new Rule("for-test.declaration-factory");
        declaration = rule.addParameterDeclaration("parameter-name", null);

        arg = new TupleArgument(declaration);
    }

    public void testConstructionNullDeclaration() {
        try {
            TupleArgument value = new TupleArgument(null);
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    public void testGetValue() throws Exception {
        mockTuple.control.expectAndReturn(mockTuple.object.get(declaration), expectedValue);
        mocks.replay();

        arg.getValue(mockTuple.object);

        mocks.verify();
    }
}