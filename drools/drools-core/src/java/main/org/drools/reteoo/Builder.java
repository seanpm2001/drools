package org.drools.reteoo;

/*
 $Id: Builder.java,v 1.10 2002-07-27 05:55:59 bob Exp $

 Copyright 2002 (C) The Werken Company. All Rights Reserved.
 
 Redistribution and use of this software and associated documentation
 ("Software"), with or without modification, are permitted provided
 that the following conditions are met:

 1. Redistributions of source code must retain copyright
    statements and notices.  Redistributions must also contain a
    copy of this document.
 
 2. Redistributions in binary form must reproduce the
    above copyright notice, this list of conditions and the
    following disclaimer in the documentation and/or other
    materials provided with the distribution.
 
 3. The name "drools" must not be used to endorse or promote
    products derived from this Software without prior written
    permission of The Werken Company.  For written permission,
    please contact bob@werken.com.
 
 4. Products derived from this Software may not be called "drools"
    nor may "drools" appear in their names without prior written
    permission of The Werken Company. "drools" is a registered
    trademark of The Werken Company.
 
 5. Due credit should be given to The Werken Company.
    (http://drools.werken.com/).
 
 THIS SOFTWARE IS PROVIDED BY THE WERKEN COMPANY AND CONTRIBUTORS
 ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT
 NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL
 THE WERKEN COMPANY OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 OF THE POSSIBILITY OF SUCH DAMAGE.
 
 */
import org.drools.spi.Rule;
import org.drools.spi.Declaration;
import org.drools.spi.ObjectType;
import org.drools.spi.FilterCondition;
import org.drools.spi.AssignmentCondition;
import org.drools.spi.Condition;

import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

/** Builds the Rete-OO network for a <code>RuleSet</code>.
 *
 *  @see org.drools.spi.RuleSet
 *
 *  @author <a href="mailto:bob@eng.werken.com">bob mcwhirter</a>
 */
public class Builder
{
    // ------------------------------------------------------------
    //     Instance members
    // ------------------------------------------------------------

    /** Root node to build against. */
    private RootNode rootNode;

    /** Total-ordering priority counter. */
    private int priorityCounter;

    // ------------------------------------------------------------
    //     Constructors
    // ------------------------------------------------------------

    /** Construct a <code>Builder</code> against an existing
     *  <code>RootNode</code> in the network.
     *
     *  @param rootNode The network to add on to.
     */
    public Builder(RootNode rootNode)
    {
        this.rootNode = rootNode;
    }

    // ------------------------------------------------------------
    //     Instance methods
    // ------------------------------------------------------------

    /** Retrieve the <code>RootNode</code> this <code>Builder</code>
     *  appends to.
     *
     *  @return The <code>RootNode</code>.
     */
    public RootNode getRootNode()
    {
        return this.rootNode;
    }

    /** Add a <code>Rule</code> to the network.
     *
     *  @param rule The rule to add.
     *
     *  @throws ReteConstructionException if an error prevents complete
     *          construction of the network for the <code>Rule</code>.
     */
    public void addRule(Rule rule) throws ReteConstructionException
    {
        Set assignmentConds = new HashSet( rule.getAssignmentConditions() );
        Set filterConds     = new HashSet( rule.getFilterConditions() );

        Set attachableNodes = null;

        boolean performedJoin     = false;
        boolean attachAssign      = false;
        boolean cycleAttachAssign = false;
        
        attachableNodes = createParameterNodes( rule );

        // System.err.println( " 1->" + attachableNodes );

        do 
        {
            performedJoin = false;
            attachAssign  = false;

            if ( ! filterConds.isEmpty() )
            {
                attachFilterConditions( filterConds,
                                        attachableNodes );
            }

            // System.err.println( " 2->" + attachableNodes );

            attachAssign = attachAssignmentConditions( assignmentConds,
                                                       attachableNodes );

            // System.err.println( " 3->" + attachableNodes );

            performedJoin = createJoinNodes( attachableNodes );

            // System.err.println( " 4->" + attachableNodes );
        }
        while ( ! attachableNodes.isEmpty() 
                &&
                ( performedJoin
                  ||
                  attachAssign
                  ) );

        TupleSource lastNode = (TupleSource) attachableNodes.iterator().next();

        TerminalNode terminal = new TerminalNode( lastNode,
                                                  rule,
                                                  ++this.priorityCounter);
    }

    /** Create the <code>ParameterNode</code>s for the <code>Rule</code>,
     *  and link into the network.
     *
     *  @param rule The rule.
     *
     *  @return A <code>Set</code> of <code>ParameterNodes</code> created
     *          and linked into the network.
     */
    protected Set createParameterNodes(Rule rule)
    {
        Set attachableNodes = new HashSet();

        Set      parameterDecls  = rule.getParameterDeclarations();

        Iterator    declIter = parameterDecls.iterator();
        Declaration eachDecl = null;

        ObjectType     objectType     = null;
        ObjectTypeNode objectTypeNode = null;
        ParameterNode  paramNode      = null;

        while ( declIter.hasNext() )
        {
            eachDecl = (Declaration) declIter.next();

            objectType = eachDecl.getObjectType();

            objectTypeNode = getRootNode().getOrCreateObjectTypeNode( objectType );

            paramNode = new ParameterNode( objectTypeNode,
                                           eachDecl );

            attachableNodes.add( paramNode );
            
        }

        return attachableNodes;
    }
    

    /** Create and attach <code>FilterCondition</code>s to the network.
     *
     *  <p>
     *  It may not be possible to satisfy all filder conditions
     *  on the first pass.  This method removes satisfied conditions
     *  from the <code>filterCond</code> parameter, and leaves
     *  unsatisfied ones in the <code>Set</code>.
     *  </p>
     *
     *  @param filterConds Set of <code>FilterConditions</code>
     *         to attempt attaching.
     *  @param attachableNodes The current attachable leaf nodes
     *         of the network.
     */
    protected void attachFilterConditions(Set filterConds,
                                          Set attachableNodes)
    {
        Iterator        condIter    = filterConds.iterator();
        FilterCondition eachCond    = null;
        TupleSource     tupleSource = null;

        FilterNode filterNode = null;

        while ( condIter.hasNext() )
        {
            eachCond = (FilterCondition) condIter.next();

            tupleSource = findMatchingTupleSourceForFiltering( eachCond,
                                                               attachableNodes );

            if ( tupleSource == null )
            {
                continue;
            }

            condIter.remove();

            filterNode = new FilterNode( tupleSource,
                                         eachCond );

            attachableNodes.remove( tupleSource );

            attachableNodes.add( filterNode );
        }
    }

    /** Create and attach <code>JoinNode</code>s to the network.
     *
     *  <p>
     *  It may not be possible to join all <code>attachableNodes</code>.
     *  </p>
     *
     *  <p>
     *  Any <code>attachabeNodes</code> member that particiates
     *  in a <i>join</i> is removed from the <code>attachableNodes</code>
     *  collection, and replaced by the joining <code>JoinNode</code>.
     *  </p>
     *
     *  @param attachableNodes The current attachable leaf nodes of
     *         the network.
     *
     *  @return <code>true</code> if at least one <code>JoinNode</code>
     *          was created, else <code>false</code>.
     */
    protected boolean createJoinNodes(Set attachableNodes)
    {
        // System.err.println( "ENTER joinNodes" );
        boolean performedJoin = false;

        Object[] leftNodes  = attachableNodes.toArray();
        Object[] rightNodes = attachableNodes.toArray();

        TupleSource left  = null;
        TupleSource right = null;

        JoinNode joinNode = null;

      outter:
        for ( int i = 0 ; i < leftNodes.length ; ++i )
        {
            left = (TupleSource) leftNodes[i];

            if ( ! attachableNodes.contains( left ) )
            {
                continue outter;
            }

          inner:
            for ( int j = i + 1; j < rightNodes.length ; ++j )
            {
                right = (TupleSource) rightNodes[j];

                if ( ! attachableNodes.contains( right ) )
                {
                    continue inner;
                }

                if ( canBeJoined( left,
                                  right ) )
                
                {
                    joinNode = new JoinNode( left,
                                             right );

                    attachableNodes.remove( left );
                    attachableNodes.remove( right );

                    attachableNodes.add( joinNode );

                    performedJoin = true;

                    // System.err.println( joinNode + " from " + left + " and " + right );
                    // System.err.println( attachableNodes );

                    continue outter;
                }
            }
        }

        // System.err.println( "EXIT joinNodes" );
        return performedJoin;
    }

    /** Determine if two <code>TupleSource</code>s can be joined.
     *
     *  @param left The left tuple source
     *  @param right The right tuple source
     *
     *  @return <code>true</code> if they can be joined (they share
     *          at least one common member declaration), else
     *          <code>false</code>.
     */
    protected boolean canBeJoined(TupleSource left,
                                  TupleSource right)
    {
        Set      leftDecls     = left.getTupleDeclarations();
        Iterator rightDeclIter = right.getTupleDeclarations().iterator();

        while ( rightDeclIter.hasNext() )
        {
            if ( leftDecls.contains( rightDeclIter.next() ) )
            {
                return true;
            }
        }

        return false;
    }

    /** Create and attach <code>AssignmentCondition</code>s to the network.
     *
     *  <p>
     *  It may not be possible to satisfy all <code>assignmentConds</code>,
     *  in which case, unsatisfied conditions will remain in the <code>Set</code>
     *  passed in as <code>assignmentConds</code>.
     *  </p>
     *
     *  @param assignmentConds Set of <code>AssignmentConditions</code> to
     *         attach to the network.
     *  @param attachableNodes The current attachable leaf nodes of
     *         the network.
     *
     *  @return <code>true</code> if assignment conditions have been
     *          attached, otherwise <code>false</code>.
     */
    protected boolean attachAssignmentConditions(Set assignmentConds,
                                                 Set attachableNodes)
    {
        boolean attached      = false;
        boolean cycleAttached = false;

        do
        {
            cycleAttached = false;

            Iterator            condIter  = assignmentConds.iterator();
            AssignmentCondition eachCond  = null;
            TupleSource         tupleSource = null;
            
            AssignmentNode assignNode = null;
            
            while ( condIter.hasNext() )
            {
                eachCond = (AssignmentCondition) condIter.next();
                
                tupleSource = findMatchingTupleSourceForAssignment( eachCond,
                                                                    attachableNodes );
                
                if ( tupleSource == null )
                {
                    continue;
                }
                
                condIter.remove();
                
                assignNode = new AssignmentNode( tupleSource,
                                                 eachCond.getTargetDeclaration(),
                                                 eachCond.getFactExtractor() );
                
                attachableNodes.remove( tupleSource );
                attachableNodes.add( assignNode );
                
                cycleAttached = true;
            }

            if ( cycleAttached )
            {
                attached = true;
            }
        }
        while ( cycleAttached );

        return attached;
    }

    /** Locate a <code>TupleSource</code> suitable for attaching
     *  the <code>FilterCondition</code>.
     *
     *  @param condition The <code>Condition</code> to attach.
     *  @param sources Candidate <code>TupleSources</code>.
     *
     *  @return Matching <code>TupleSource</code> if a suitable one
     *          can be found, else <code>null</code>.
     */
    protected TupleSource findMatchingTupleSourceForFiltering(FilterCondition condition,
                                                              Set sources)
    {
        Iterator    sourceIter = sources.iterator();
        TupleSource eachSource = null;

        Set decls = null;

        while ( sourceIter.hasNext() )
        {
            eachSource = (TupleSource) sourceIter.next();

            decls = eachSource.getTupleDeclarations();

            if ( matches( condition,
                          decls ) )
            {
                return eachSource;
            }
        }

        return null;
    }

    /** Locate a <code>TupleSource</code> suitable for attaching
     *  the <code>AssignmentCondition</code>.
     *
     *  @param condition The <code>Condition</code> to attach.
     *  @param sources Candidate <code>TupleSources</code>.
     *
     *  @return Matching <code>TupleSource</code> if a suitable one
     *          can be found, else <code>null</code>.
     */
    protected TupleSource findMatchingTupleSourceForAssignment(AssignmentCondition condition,
                                                               Set sources)
    {
        Declaration targetDecl = condition.getTargetDeclaration();

        Iterator    sourceIter = sources.iterator();
        TupleSource eachSource = null;

        Set decls = null;

        while ( sourceIter.hasNext() )
        {
            eachSource = (TupleSource) sourceIter.next();

            decls = eachSource.getTupleDeclarations();
            // System.err.println( "decls -> " + decls );

            if ( decls.contains( targetDecl ) )
            {
                continue;
            }

            if ( matches( condition,
                          decls ) )
            {
                return eachSource;
            }
        }

        return null;
    }

    /** Determine if a set of <code>Declarations</code> match those
     *  required by a <code>Condition</code>.
     *
     *  @param condition The <code>Condition</code>.
     *  @param declarations The set of <code>Declarations</code> to compare against.
     *
     *  @return <code>true</code> if the set of <code>Declarations</code> is a
     *          super-set of the <code>Declarations</code> required by the
     *          <code>Condition</code>.
     */
    protected boolean matches(Condition condition,
                              Set declarations)
    {
        Declaration[] requiredDecls = condition.getRequiredTupleMembers();

        for ( int i = 0 ; i < requiredDecls.length ; ++i )
        {
            if ( ! declarations.contains( requiredDecls[i] ) )
            {
                return false;
            }
        }

        return true;
    }
                                                  
}
