package org.drools.reteoo;

import org.drools.MockFactHandle;
import org.drools.AssertionException;
import org.drools.rule.Rule;
import org.drools.spi.InstrumentedConsequence;
import org.drools.DroolsTestCase;

import java.util.List;

public class TerminalNodeTest
    extends DroolsTestCase
{
    public void testAssertTuple()
        throws Exception
    {
        final InstrumentedAgenda agenda = new InstrumentedAgenda( null,
                                                                  null );
        
        WorkingMemoryImpl memory = new WorkingMemoryImpl( null )
            {
                public Agenda getAgenda()
                {
                    return agenda;
                }
            };

        

        Rule rule = new Rule( "test-rule" );

        InstrumentedConsequence consequence = new InstrumentedConsequence();

        rule.setConsequence( consequence );

        TerminalNode node = new TerminalNode( new MockTupleSource(),
                                              rule );

        ReteTuple tuple = new ReteTuple();

        node.assertTuple( tuple,
                          memory );
        
        assertLength( 1,
                      agenda.getAdded() );
        
        assertContains( tuple,
                        agenda.getAdded() );
    }

    public void testRetractTuples()
        throws Exception
    {
        final InstrumentedAgenda agenda = new InstrumentedAgenda( null,
                                                                  null );
        
        WorkingMemoryImpl memory = new WorkingMemoryImpl( null )
            {
                public Agenda getAgenda()
                {
                    return agenda;
                }
            };

        

        Rule rule = new Rule( "test-rule" );

        InstrumentedConsequence consequence = new InstrumentedConsequence();

        rule.setConsequence( consequence );

        TerminalNode node = new TerminalNode( new MockTupleSource(),
                                              rule );

        TupleKey key = new TupleKey();

        node.retractTuples( key,
                            memory );
        
        assertLength( 1,
                      agenda.getRemoved() );
        
        assertContains( key,
                        agenda.getRemoved() );
    }

    public void testModifyTuples()
        throws Exception
    {
        final InstrumentedAgenda agenda = new InstrumentedAgenda( null,
                                                                  null );
        
        WorkingMemoryImpl memory = new WorkingMemoryImpl( null )
            {
                public Agenda getAgenda()
                {
                    return agenda;
                }
            };

        

        Rule rule = new Rule( "test-rule" );

        InstrumentedConsequence consequence = new InstrumentedConsequence();

        rule.setConsequence( consequence );

        TerminalNode node = new TerminalNode( new MockTupleSource(),
                                              rule );

        MockFactHandle handle = new MockFactHandle( 42 );

        node.modifyTuples( handle,
                           new TupleSet(),
                           memory );
        
        assertLength( 1,
                      agenda.getModified() );
        
        assertContains( handle,
                        agenda.getModified() );
    }
}
