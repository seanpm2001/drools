package org.drools.examples.manners;

/*
 * $Id: MannersNative.java,v 1.5 2004-11-13 01:43:07 simon Exp $
 *
 * Copyright 2002 (C) The Werken Company. All Rights Reserved.
 *
 * Redistribution and use of this software and associated documentation
 * ("Software"), with or without modification, are permitted provided that the
 * following conditions are met:
 *
 * 1. Redistributions of source code must retain copyright statements and
 * notices. Redistributions must also contain a copy of this document.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. The name "drools" must not be used to endorse or promote products derived
 * from this Software without prior written permission of The Werken Company.
 * For written permission, please contact bob@werken.com.
 *
 * 4. Products derived from this Software may not be called "drools" nor may
 * "drools" appear in their names without prior written permission of The Werken
 * Company. "drools" is a registered trademark of The Werken Company.
 *
 * 5. Due credit should be given to The Werken Company.
 * (http://drools.werken.com/).
 *
 * THIS SOFTWARE IS PROVIDED BY THE WERKEN COMPANY AND CONTRIBUTORS ``AS IS''
 * AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE WERKEN COMPANY OR ITS CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

import org.drools.FactException;
import org.drools.RuleBase;
import org.drools.RuleBaseBuilder;
import org.drools.WorkingMemory;
import org.drools.examples.manners.model.Context;
import org.drools.examples.manners.model.Guest;
import org.drools.examples.manners.model.LastSeat;
import org.drools.examples.manners.model.Seat;
import org.drools.examples.manners.model.Seating;
import org.drools.rule.Declaration;
import org.drools.rule.Rule;
import org.drools.rule.RuleSet;
import org.drools.semantics.base.ClassObjectType;
import org.drools.spi.Condition;
import org.drools.spi.Consequence;
import org.drools.spi.ConsequenceException;
import org.drools.spi.Tuple;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Andy Barnett
 */
public class MannersNative extends MannersBase
{
    /** Drools working memory. */
    private WorkingMemory workingMemory;

    public static void main(String[] args) throws Exception
    {
        new MannersNative( args ).run( );
    }

    public MannersNative(String[] args)
    {
        super( args );
    }

    protected void setUp() throws Exception
    {
        // Reuse the Java semantics ObjectType
        // so Drools can identify the model classes
        ClassObjectType contextType = new ClassObjectType( Context.class );
        ClassObjectType guestType = new ClassObjectType( Guest.class );
        ClassObjectType seatingType = new ClassObjectType( Seating.class );
        ClassObjectType lastSeatType = new ClassObjectType( LastSeat.class );

        // <rule-set name="Miss Manners" ...>
        RuleSet ruleSet = new RuleSet( "Miss Manners" );

        // ===========================================
        // <rule name="find first seat" salience="40">
        // ===========================================
        final Rule ruleA = new Rule( "find first seat" );
        ruleA.setSalience( 40 );

        // Build the declaration and specify it as a parameter of the Rule
        // <parameter identifier="context">
        //   <class>org.drools.examples.manners.model.Context</class>
        // </parameter>
        final Declaration contextDeclA = ruleA.addParameterDeclaration( "context", contextType );

        // <parameter identifier="guest">
        //   <class>org.drools.examples.manners.model.Guest</class>
        // </parameter>
        final Declaration guestDeclA = ruleA.addParameterDeclaration( "guest", guestType );

        // Build and Add the Condition to the Rule
        // <java:condition>context.isState("start")</java:condition>
        final Condition conditionA1 = new Condition( )
        {
            public boolean isAllowed(Tuple tuple)
            {
                Context context = ( Context ) tuple.get( contextDeclA );
                return context.isState( "start" );
            }

            public Declaration[] getRequiredTupleMembers()
            {
                return new Declaration[]{contextDeclA};
            }

            public String toString()
            {
                return "context.isState(\"start\")";
            }
        };
        ruleA.addCondition( conditionA1 );

        // Build and Add the Consequence to the Rule
        // <java:consequence>
        //   System.out.println("FIRE: find first seat: " + guest);
        //   import org.drools.examples.manners.model.Seating;
        //   drools.assertObject(new Seating(1, guest, null));
        //   context.setState("find_seating");
        //   drools.modifyObject(context);
        // </java:consequence>
        final Consequence consequenceA = new Consequence( )
        {
            public void invoke(Tuple tuple, WorkingMemory workingMemory) throws ConsequenceException
            {
                Context context = ( Context ) tuple.get( contextDeclA );
                Guest guest = ( Guest ) tuple.get( guestDeclA );

                System.out.println( "FIRE: find first seat: " + guest );

                try
                {
                    workingMemory.assertObject( new Seating( 1, guest, null ) );
                }
                catch ( FactException e )
                {
                    throw new ConsequenceException( e );
                }

                context.setState( "find_seating" );

                try
                {
                    workingMemory.modifyObject( tuple.getFactHandleForObject( context ), context );
                }
                catch ( FactException e )
                {
                    throw new ConsequenceException( e );
                }
            }
        };
        ruleA.setConsequence( consequenceA );
        ruleSet.addRule( ruleA );

        // ========================================
        // <rule name="find seating" salience="30">
        // ========================================
        final Rule ruleB = new Rule( "find seating" );
        ruleB.setSalience( 30 );

        // Build the declaration and specify it as a parameter of the Rule
        // <parameter identifier="context">
        //   <class>org.drools.examples.manners.model.Context</class>
        // </parameter>
        //        final Declaration contextDecl = new Declaration(contextType,
        // "context");
        final Declaration contextDeclB = ruleB.addParameterDeclaration( "context", contextType );

        // <parameter identifier="guest">
        //   <class>org.drools.examples.manners.model.Guest</class>
        // </parameter>
        //        final Declaration guestDecl = new Declaration(guestType, "guest");
        final Declaration guestDeclB = ruleB.addParameterDeclaration( "guest", guestType );

        // <parameter identifier="seating">
        //     <class>org.drools.examples.manners.model.Seating</class>
        // </parameter>
        final Declaration seatingDeclB = ruleB.addParameterDeclaration( "seating", seatingType );

        // Build and Add the Condition to the Rule
        // <java:condition>context.isState("find_seating")</java:condition>
        final Condition conditionB1 = new Condition( )
        {
            public boolean isAllowed(Tuple tuple)
            {
                Context context = ( Context ) tuple.get( contextDeclB );
                return context.isState( "find_seating" );
            }

            public Declaration[] getRequiredTupleMembers()
            {
                return new Declaration[]{contextDeclB};
            }

            public String toString()
            {
                return "context.isState(\"find_seating\")";
            }
        };
        ruleB.addCondition( conditionB1 );

        // <java:condition>seating.getGuest2() == null</java:condition>
        final Condition conditionB2 = new Condition( )
        {
            public boolean isAllowed(Tuple tuple)
            {
                Seating seating = ( Seating ) tuple.get( seatingDeclB );
                return seating.getGuest2( ) == null;
            }

            public Declaration[] getRequiredTupleMembers()
            {
                return new Declaration[]{seatingDeclB};
            }

            public String toString()
            {
                return "seating.getGuest2() == null";
            }
        };
        ruleB.addCondition( conditionB2 );

        // <java:condition>!seating.getTabooList().contains(guest)</java:condition>
        final Condition conditionB3 = new Condition( )
        {
            public boolean isAllowed(Tuple tuple)
            {
                Seating seating = ( Seating ) tuple.get( seatingDeclB );
                Guest guest = ( Guest ) tuple.get( guestDeclB );
                return !seating.getTabooList( ).contains( guest );
            }

            public Declaration[] getRequiredTupleMembers()
            {
                return new Declaration[]{seatingDeclB, guestDeclB};
            }

            public String toString()
            {
                return "!seating.getTabooList().contains(guest)";
            }
        };
        ruleB.addCondition( conditionB3 );

        // <java:condition>seating.getGuest1().hasOppositeSex(guest)</java:condition>
        final Condition conditionB4 = new Condition( )
        {
            public boolean isAllowed(Tuple tuple)
            {
                Seating seating = ( Seating ) tuple.get( seatingDeclB );
                Guest guest = ( Guest ) tuple.get( guestDeclB );
                return seating.getGuest1( ).hasOppositeSex( guest );
            }

            public Declaration[] getRequiredTupleMembers()
            {
                return new Declaration[]{seatingDeclB, guestDeclB};
            }

            public String toString()
            {
                return "seating.getGuest1().hasOppositeSex(guest)";
            }
        };
        ruleB.addCondition( conditionB4 );

        // <java:condition>seating.getGuest1().hasSameHobby(guest)</java:condition>
        final Condition conditionB5 = new Condition( )
        {
            public boolean isAllowed(Tuple tuple)
            {
                Seating seating = ( Seating ) tuple.get( seatingDeclB );
                Guest guest = ( Guest ) tuple.get( guestDeclB );
                return seating.getGuest1( ).hasSameHobby( guest );
            }

            public Declaration[] getRequiredTupleMembers()
            {
                return new Declaration[]{seatingDeclB, guestDeclB};
            }

            public String toString()
            {
                return "seating.getGuest1().hasSameHobby(guest)";
            }
        };
        ruleB.addCondition( conditionB5 );

        // Build and Add the Consequence to the Rule
        // <java:consequence>
        //    System.out.println("FIRE: find seating: " + seating + " " + guest);
        //
        //    Seating nextSeat = new Seating(seating.getSeat2(), guest, seating);
        //    drools.assertObject(nextSeat);
        //
        //    seating.setGuest2(guest);
        //    seating.getTabooList().add(guest);
        //    drools.modifyObject(seating);
        // </java:consequence>
        final Consequence consequenceB = new Consequence( )
        {
            public void invoke(Tuple tuple, WorkingMemory workingMemory) throws ConsequenceException
            {
                Guest guest = ( Guest ) tuple.get( guestDeclB );
                Seating seating = ( Seating ) tuple.get( seatingDeclB );

                System.out.println( "FIRE: find seating: " + seating + " " + guest );

                Seating nextSeat = new Seating( seating.getSeat2( ), guest, seating );
                try
                {
                    workingMemory.assertObject( nextSeat );
                }
                catch ( FactException e )
                {
                    throw new ConsequenceException( e );
                }

                seating.setGuest2( guest );
                seating.getTabooList( ).add( guest );

                try
                {
                    workingMemory.modifyObject( tuple.getFactHandleForObject( seating ), seating );
                }
                catch ( FactException e )
                {
                    throw new ConsequenceException( e );
                }
            }
        };
        ruleB.setConsequence( consequenceB );
        ruleSet.addRule( ruleB );

        // ===========================================
        // <rule name="try another path" salience="20">
        // ===========================================
        final Rule ruleC = new Rule( "try another path" );
        ruleC.setSalience( 20 );

        // Build the declaration and specify it as a parameter of the Rule
        // <parameter identifier="context">
        //   <class>org.drools.examples.manners.model.Context</class>
        // </parameter>
        final Declaration contextDeclC = ruleC.addParameterDeclaration( "context", contextType );

        // <parameter identifier="lastSeat">
        //     <class>org.drools.examples.manners.model.LastSeat</class>
        // </parameter>
        final Declaration lastSeatDeclC = ruleC.addParameterDeclaration( "lastSeat", lastSeatType );

        // <parameter identifier="seating">
        //     <class>org.drools.examples.manners.model.Seating</class>
        // </parameter>
        final Declaration seatingDeclC = ruleC.addParameterDeclaration( "seating", seatingType );

        // <java:condition>context.isState("find_seating")</java:condition>
        final Condition conditionC1 = new Condition( )
        {
            public boolean isAllowed(Tuple tuple)
            {
                Context context = ( Context ) tuple.get( contextDeclC );
                return context.isState( "find_seating" );
            }

            public Declaration[] getRequiredTupleMembers()
            {
                return new Declaration[]{contextDeclC};
            }

            public String toString()
            {
                return "context.isState(\"find_seating\")";
            }
        };
        ruleC.addCondition( conditionC1 );

        // <java:condition>lastSeat.getSeat() >
        // seating.getSeat1()</java:condition>
        final Condition conditionC2 = new Condition( )
        {
            public boolean isAllowed(Tuple tuple)
            {
                LastSeat lastSeat = ( LastSeat ) tuple.get( lastSeatDeclC );
                Seating seating = ( Seating ) tuple.get( seatingDeclC );
                return lastSeat.getSeat( ) > seating.getSeat1( );
            }

            public Declaration[] getRequiredTupleMembers()
            {
                return new Declaration[]{lastSeatDeclC, seatingDeclC};
            }

            public String toString()
            {
                return "lastSeat.getSeat() > seating.getSeat1()";
            }
        };
        ruleC.addCondition( conditionC2 );

        // <java:condition>seating.getGuest2() == null</java:condition>
        final Condition conditionC3 = new Condition( )
        {
            public boolean isAllowed(Tuple tuple)
            {
                Seating seating = ( Seating ) tuple.get( seatingDeclC );
                return seating.getGuest2( ) == null;
            }

            public Declaration[] getRequiredTupleMembers()
            {
                return new Declaration[]{seatingDeclC};
            }

            public String toString()
            {
                return "seating.getGuest2() == null";
            }
        };
        ruleC.addCondition( conditionC3 );

        // <java:consequence>
        //    System.out.println("FIRE: try another path: " + seating);
        //
        //    Seating prevSeat = seating.getPrevSeat();
        //    prevSeat.setGuest2(null);
        //    drools.modifyObject(prevSeat);
        //
        //    drools.retractObject(seating);
        // </java:consequence>
        final Consequence consequenceC = new Consequence( )
        {
            public void invoke(Tuple tuple, WorkingMemory workingMemory) throws ConsequenceException
            {
                Seating seating = ( Seating ) tuple.get( seatingDeclC );

                System.out.println( "FIRE: try another path: " + seating );

                Seating prevSeat = seating.getPrevSeat( );
                prevSeat.setGuest2( null );

                try
                {
                    workingMemory.modifyObject( tuple.getFactHandleForObject( prevSeat ), prevSeat );
                }
                catch ( FactException e )
                {
                    throw new ConsequenceException( e );
                }

                try
                {
                    workingMemory.retractObject( tuple.getFactHandleForObject( seating ) );
                }
                catch ( FactException e )
                {
                    throw new ConsequenceException( e );
                }
            }
        };
        ruleC.setConsequence( consequenceC );
        ruleSet.addRule( ruleC );

        // =======================================
        // <rule name="we are done" salience="10">
        // =======================================
        final Rule ruleD = new Rule( "we are done" );
        ruleD.setSalience( 10 );

        // Build the declaration and specify it as a parameter of the Rule
        // <parameter identifier="context">
        //   <class>org.drools.examples.manners.model.Context</class>
        // </parameter>
        final Declaration contextDeclD = ruleD.addParameterDeclaration( "context", contextType );

        // <parameter identifier="lastSeat">
        //     <class>org.drools.examples.manners.model.LastSeat</class>
        // </parameter>
        final Declaration lastSeatDeclD = ruleD.addParameterDeclaration( "lastSeat", lastSeatType );

        // <parameter identifier="seating">
        //     <class>org.drools.examples.manners.model.Seating</class>
        // </parameter>
        final Declaration seatingDeclD = ruleD.addParameterDeclaration( "seating", seatingType );

        // <java:condition>context.isState("find_seating")</java:condition>
        final Condition condition = new Condition( )
        {
            public boolean isAllowed(Tuple tuple)
            {
                Context context = ( Context ) tuple.get( contextDeclD );
                return context.isState( "find_seating" );
            }

            public Declaration[] getRequiredTupleMembers()
            {
                return new Declaration[]{contextDeclD};
            }

            public String toString()
            {
                return "context.isState(\"find_seating\")";
            }
        };
        ruleD.addCondition( condition );

        // <java:condition>lastSeat.getSeat() ==
        // seating.getSeat1()</java:condition>
        final Condition conditionD1 = new Condition( )
        {
            public boolean isAllowed(Tuple tuple)
            {
                LastSeat lastSeat = ( LastSeat ) tuple.get( lastSeatDeclD );
                Seating seating = ( Seating ) tuple.get( seatingDeclD );
                return lastSeat.getSeat( ) == seating.getSeat1( );
            }

            public Declaration[] getRequiredTupleMembers()
            {
                return new Declaration[]{lastSeatDeclC, seatingDeclD};
            }

            public String toString()
            {
                return "lastSeat.getSeat() == seating.getSeat1()";
            }
        };
        ruleD.addCondition( conditionD1 );

        // <java:consequence>
        //    System.out.println("FIRE: we are done");
        //
        //    import org.drools.examples.manners.model.Seat;
        //    List list = new ArrayList();
        //    while(seating != null) {
        //        Seat seat = new Seat(seating.getSeat1(),
        // seating.getGuest1().getName());
        //        seating = seating.getPrevSeat();
        //        list.add(seat);
        //    }
        //
        //    for (int i = list.size(); i > 0; i--) {
        //        Seat seat = (Seat)list.get(i-1);
        //        System.out.println(seat);
        //        drools.assertObject(seat);
        //    }
        //
        //    context.setState("all_done");
        //    drools.modifyObject(context);
        // </java:consequence>
        final Consequence consequenceD = new Consequence( )
        {
            public void invoke(Tuple tuple, WorkingMemory workingMemory) throws ConsequenceException
            {
                Seating seating = ( Seating ) tuple.get( seatingDeclD );
                Context context = ( Context ) tuple.get( contextDeclD );

                System.out.println( "FIRE: we are done" );

                List list = new ArrayList( );
                while ( seating != null )
                {
                    Seat seat = new Seat( seating.getSeat1( ),
                                          seating.getGuest1( ).getName( ) );
                    seating = seating.getPrevSeat( );
                    list.add( seat );
                }

                for ( int i = list.size( ); i > 0; i-- )
                {
                    Seat seat = ( Seat ) list.get( i - 1 );
                    System.out.println( seat );
                    try
                    {
                        workingMemory.assertObject( seat );
                    }
                    catch ( FactException e )
                    {
                        throw new ConsequenceException( e );
                    }
                }

                context.setState( "all_done" );
                try
                {
                    workingMemory.modifyObject( tuple.getFactHandleForObject( context ), context );
                }
                catch ( FactException e )
                {
                    throw new ConsequenceException( e );
                }
            }
        };
        ruleD.setConsequence( consequenceD );
        ruleSet.addRule( ruleD );

        // ==================
        // Build the RuleSet.
        // ==================
        RuleBaseBuilder builder = new RuleBaseBuilder( );
        builder.addRuleSet( ruleSet );
        RuleBase ruleBase = builder.build( );
        workingMemory = ruleBase.newWorkingMemory( );
    }

    protected void tearDown() throws Exception
    {
        workingMemory = null;
    }

    protected List test(List inList) throws Exception
    {
        for ( Iterator i = inList.iterator( ); i.hasNext( ); )
        {
            workingMemory.assertObject( i.next( ) );
        }

        workingMemory.fireAllRules( );

        return workingMemory.getObjects( );
    }
}