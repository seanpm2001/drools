package org.drools.jsr94.rules;

/*
 $Id: StatelessRuleSessionTestCase.java,v 1.4 2004-04-02 23:03:18 n_alex Exp $

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

import junit.framework.TestCase;

import javax.rules.ObjectFilter;
import javax.rules.StatelessRuleSession;
import java.util.ArrayList;
import java.util.List;

/**
 * Test the <code>StatelessRuleSession</code> implementation.
 *
 * @author N. Alex Rupp (n_alex <at> codehaus.org)
 * @author <a href="mailto:thomas.diesler@softcon-itec.de">thomas diesler</a>
 * @see StatelessRuleSession
 */
public class StatelessRuleSessionTestCase extends TestCase {

    private StatelessRuleSession statelessSession;
    private ExampleRuleEngineFacade sessionBuilder;
    private String bindUri = "sisters.drl";

    /**
     * Setup the test case.
     */
    protected void setUp() throws Exception {
        super.setUp();
        sessionBuilder = new ExampleRuleEngineFacade();
        sessionBuilder.addRuleExecutionSet(bindUri, StatelessRuleSessionTestCase.class.getResourceAsStream(bindUri));
        this.statelessSession = sessionBuilder.getStatelessRuleSession(bindUri);
    }

    /**
     * Test executeRules.
     */
    public void testExecuteRules() throws Exception {

        List inObjects = new ArrayList();

        Person bob = new Person("bob");
        inObjects.add(bob);

        Person rebecca = new Person("rebecca");
        rebecca.addSister("jeannie");
        inObjects.add(rebecca);

        Person jeannie = new Person("jeannie");
        jeannie.addSister("rebecca");
        inObjects.add(jeannie);

        // execute the rules
        List outList = statelessSession.executeRules(inObjects);

        assertEquals("incorrect size", 7, outList.size());

        assertTrue("where is bob", outList.contains(bob));
        assertTrue("where is rebecca", outList.contains(rebecca));
        assertTrue("where is jeannie", outList.contains(jeannie));

        assertTrue(outList.contains("bob says: rebecca and jeannie are sisters"));
        assertTrue(outList.contains("bob says: jeannie and rebecca are sisters"));

        assertTrue(outList.contains("rebecca: I like cheese"));
        assertTrue(outList.contains("jeannie: I like cheese"));

        statelessSession.release();
    }

    /**
     * Test executeRules with ObjectFilter.
     */
    public void testExecuteRulesWithFilter() throws Exception {
        List inObjects = new ArrayList();

        Person bob = new Person("bob");
        inObjects.add(bob);

        Person rebecca = new Person("rebecca");
        rebecca.addSister("jeannie");
        inObjects.add(rebecca);

        Person jeannie = new Person("jeannie");
        jeannie.addSister("rebecca");
        inObjects.add(jeannie);

        // execute the rules
        List outList = statelessSession.executeRules(inObjects, new PersonFilter());
        assertEquals("incorrect size", 3, outList.size());

        assertTrue("where is bob", outList.contains(bob));
        assertTrue("where is rebecca", outList.contains(rebecca));
        assertTrue("where is jeannie", outList.contains(jeannie));
    }

    /**
     * Filter accepts only objects of type Person.
     */
    static class PersonFilter implements ObjectFilter {

        public Object filter(Object object) {
            return (object instanceof Person ? object : null);
        }

        public void reset() {
        }
    }
}
