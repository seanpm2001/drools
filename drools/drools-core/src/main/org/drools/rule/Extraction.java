package org.drools.rule;

/*
 * $Id: Extraction.java,v 1.15 2004-11-19 02:14:17 mproctor Exp $
 *
 * Copyright 2001-2003 (C) The Werken Company. All Rights Reserved.
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
 * Company. "drools" is a trademark of The Werken Company.
 *
 * 5. Due credit should be given to The Werken Company. (http://werken.com/)
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
 *
 */

import org.drools.spi.Extractor;

import java.io.Serializable;

/**
 * A <code>Condition</code> representing a <i>consistent assignment </i> as
 * defined by the Rete-OO algorithm.
 * 
 * The assignment occurs through the process of extracting a new fact from
 * existing facts.
 * 
 * @see Extractor
 * 
 * @author <a href="mailto:bob@eng.werken.com">bob mcwhirter </a>
 */
public class Extraction
    implements
    Serializable
{
    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------

    /** The target of the assignment. */
    private final Declaration targetDeclaration;

    /** Extractor to acquire value for assignment. */
    // TODO: Make this final. This will require rejigging the RuleBuilder stuff.
    private Extractor extractor;

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------

    /**
     * Construct.
     * 
     * @param targetDeclaration
     *            The target of this assignment.
     * @param extractor
     *            Value generator for the assignment.
     */
    Extraction(Declaration targetDeclaration,
               Extractor extractor)
    {
        this.targetDeclaration = targetDeclaration;
        this.extractor = extractor;
    }

    // ------------------------------------------------------------
    // Instance methods
    // ------------------------------------------------------------

    /**
     * Retrieve the <code>Declaration</code> for the target of the assignment.
     * 
     * @return The target's <code>Declaration</code>
     */
    public Declaration getTargetDeclaration()
    {
        return this.targetDeclaration;
    }

    /**
     * Retrieve the <code>Extractor</code> responsible for generating the
     * assignment value.
     * 
     * @return The <code>Extractor</code>.
     */
    public Extractor getExtractor()
    {
        return this.extractor;
    }

    // TODO: Get rid of this
    public void setExtractor(Extractor extractor)
    {
        this.extractor = extractor;
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // org.drools.spi.Condition
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    /**
     * Retrieve the array of <code>Declaration</code> s required by this
     * condition to perform its duties.
     * 
     * @return The array of <code>Declarations</code> expected on incoming
     *         <code>Tuple</code>s.
     */
    public Declaration[] getRequiredTupleMembers()
    {
        return getExtractor( ).getRequiredTupleMembers( );
    }

    public String toString()
    {
        return "[Extraction: " + extractor + "]";
    }

    /*
     * public String dump(String indent) { StringBuffer buffer = new
     * StringBuffer(); buffer.append(indent + "Extraction\n");
     * buffer.append(indent + "----------\n"); buffer.append(indent +
     * "extractor: " + extractor + "\n");
     * buffer.append(targetDeclaration.dump(indent + " ")); Declaration[]
     * declarations = extractor.getRequiredTupleMembers(); for (int i = 0; i <
     * declarations.length; i++) { buffer.append(declarations[i].dump(indent + "
     * ")); }
     * 
     * return buffer.toString(); }
     */

}