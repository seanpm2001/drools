package org.drools.conflict;

/*
 $Id: SalienceConflictResolutionStrategy.java,v 1.5 2003-11-27 04:32:22 bob Exp $

 Copyright 2001-2003 (C) The Werken Company. All Rights Reserved.
 
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
    permission of The Werken Company. "drools" is a trademark of 
    The Werken Company.
 
 5. Due credit should be given to The Werken Company.
    (http://werken.com/)
 
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

import org.drools.spi.ConflictResolutionStrategy;
import org.drools.spi.Activation;

/** <code>ConflictResolutionStrategy</code> that uses only the salience
 *  specified on rules.
 *
 *  <p>
 *  If multiple rules are on the agenda, they are sorted according to
 *  their salience from highest to lowest.  By default, each rule has
 *  a salience of <b>0</b> if not otherwise specified.  Both positive
 *  and negative salience values are allowed.
 *  </p>
 *
 *  @see #getInstance
 *  @see Rule#setSalience
 *  @see Rule#getSalience
 *
 *  @author <a href="mailto:bob@werken.com">bob mcwhirter</a>
 *
 *  @version $Id: SalienceConflictResolutionStrategy.java,v 1.5 2003-11-27 04:32:22 bob Exp $
 */
public class SalienceConflictResolutionStrategy
    implements ConflictResolutionStrategy
{
    // ----------------------------------------------------------------------
    //     Class members
    // ----------------------------------------------------------------------

    /** Singleton instance. */
    private static final ConflictResolutionStrategy INSTANCE = new SalienceConflictResolutionStrategy();

    // ----------------------------------------------------------------------
    //     Class methods
    // ----------------------------------------------------------------------

    /** Retrieve the singleton instance.
     *
     *  @return The singleton instance.
     */
    public static ConflictResolutionStrategy getInstance()
    {
        return INSTANCE;
    }

    // ----------------------------------------------------------------------
    //     Constructors
    // ----------------------------------------------------------------------

    /** Construct.
     */
    public SalienceConflictResolutionStrategy()
    {
        // intentionally left blank
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

    /** @see ConflictResolutionStrategy
     */
    public int compare(Activation activationOne,
                       Activation activationTwo)
    {
        int salienceOne = activationOne.getRule().getSalience();
        int salienceTwo = activationTwo.getRule().getSalience();

        if ( salienceOne > salienceTwo )
        {
            return -1;
        }

        if ( salienceOne < salienceTwo )
        {
            return 1;
        }

        return 0;
    }
}

