package org.drools.jsr94.rules;

/*
 $Id: StatefulRuleSessionTestCase.java,v 1.1 2003-03-22 00:59:49 tdiesler Exp $

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


import sisters.Person;

import javax.rules.Handle;
import javax.rules.ObjectFilter;
import javax.rules.RuleRuntime;
import javax.rules.StatefulRuleSession;
import javax.rules.admin.LocalRuleExecutionSetProvider;
import javax.rules.admin.RuleAdministrator;
import javax.rules.admin.RuleExecutionSet;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * Test the <code>StatefulRuleSession</code> implementation.
 *
 * @see StatefulRuleSession
 *
 * @author <a href="mailto:thomas.diesler@softcon-itec.de">thomas diesler</a>
 */
public class StatefulRuleSessionTestCase extends JSR94TestBase {

   private StatefulRuleSession statefulSession;

   /**
    * Setup the test case.
    */
   protected void setUp() throws Exception {
      super.setUp();
      RuleAdministrator ruleAdministrator = ruleServiceProvider.getRuleAdministrator();
      LocalRuleExecutionSetProvider ruleSetProvider = ruleAdministrator.getLocalRuleExecutionSetProvider(null);
      RuleRuntime ruleRuntime = ruleServiceProvider.getRuleRuntime();

      // read rules and register with administrator
      Reader ruleReader = new InputStreamReader(getResourceAsStream(RULES_RESOURCE));
      RuleExecutionSet ruleSet = ruleSetProvider.createRuleExecutionSet(ruleReader, null);
      ruleAdministrator.registerRuleExecutionSet(RULES_RESOURCE, ruleSet, null);

      // obtain the stateless rule session
      statefulSession = (StatefulRuleSession)ruleRuntime.createRuleSession(RULES_RESOURCE, null, RuleRuntime.STATEFUL_SESSION_TYPE);
   }

   /**
    * Test containsObject.
    */
   public void testContainsObject() throws Exception {
      Person bob = new Person("bob");
      Handle handle = statefulSession.addObject(bob);
      assertTrue("where is bob", statefulSession.containsObject(handle));
   }

   /**
    * Test addObject.
    */
   public void testAddObject() throws Exception {
      // tested in testContainsObject
   }

   /**
    * Test addObjects.
    */
   public void testAddObjects() throws Exception {

      List inObjects = new ArrayList();

      Person bob = new Person("bob");
      inObjects.add(bob);

      Person rebecca = new Person("rebecca");
      rebecca.addSister("jeannie");
      inObjects.add(rebecca);

      Person jeannie = new Person("jeannie");
      jeannie.addSister("rebecca");
      inObjects.add(jeannie);

      List handleList = statefulSession.addObjects(inObjects);
      assertEquals("incorrect size", 3, handleList.size());
      assertEquals("where is bob", bob, statefulSession.getObject((Handle)handleList.get(0)));
      assertEquals("where is rebecca", rebecca, statefulSession.getObject((Handle)handleList.get(1)));
      assertEquals("where is jeannie", jeannie, statefulSession.getObject((Handle)handleList.get(2)));
   }

   /**
    * Test getObject.
    */
   public void testGetObject() throws Exception {
      // tested in testAddObjects
   }

   /**
    * Test updateObject.
    */
   public void testUpdateObject() throws Exception {
      Person bob = new Person("bob");
      Handle handle = statefulSession.addObject(bob);
      statefulSession.updateObject(handle, bob = new Person("boby"));
      assertEquals("where is boby", bob, statefulSession.getObject(handle));
   }

   /**
    * Test removeObject.
    */
   public void testRemoveObject() throws Exception {
      Person bob = new Person("bob");
      Handle handle = statefulSession.addObject(bob);
      assertTrue("where is bob", statefulSession.containsObject(handle));

      statefulSession.removeObject(handle);
      assertTrue("bob still there", !statefulSession.containsObject(handle));
   }

   /**
    * Test getObjects.
    */
   public void testGetObjects() throws Exception {

      Person bob = new Person("bob");
      statefulSession.addObject(bob);

      Person rebecca = new Person("rebecca");
      rebecca.addSister("jeannie");
      statefulSession.addObject(rebecca);

      Person jeannie = new Person("jeannie");
      jeannie.addSister("rebecca");
      statefulSession.addObject(jeannie);

      // execute the rules
      statefulSession.executeRules();
      List outList = statefulSession.getObjects();
      assertEquals("incorrect size", 7, outList.size());

      assertTrue("where is bob", outList.contains(bob));
      assertTrue("where is rebecca", outList.contains(rebecca));
      assertTrue("where is jeannie", outList.contains(jeannie));

      assertTrue(outList.contains("bob says: rebecca and jeannie are sisters"));
      assertTrue(outList.contains("bob says: jeannie and rebecca are sisters"));

      assertTrue(outList.contains("rebecca: I like cheese"));
      assertTrue(outList.contains("jeannie: I like cheese"));

      statefulSession.release();
   }

   /**
    * Test getObjects with ObjectFilter.
    */
   public void testGetObjectsWithFilter() throws Exception {

      Person bob = new Person("bob");
      statefulSession.addObject(bob);

      Person rebecca = new Person("rebecca");
      rebecca.addSister("jeannie");
      statefulSession.addObject(rebecca);

      Person jeannie = new Person("jeannie");
      jeannie.addSister("rebecca");
      statefulSession.addObject(jeannie);

      // execute the rules
      statefulSession.executeRules();
      List outList = statefulSession.getObjects(new PersonFilter());
      assertEquals("incorrect size", 3, outList.size());

      assertTrue("where is bob", outList.contains(bob));
      assertTrue("where is rebecca", outList.contains(rebecca));
      assertTrue("where is jeannie", outList.contains(jeannie));

      statefulSession.release();
   }

   /**
    * Test executeRules.
    */
   public void testExecuteRules() throws Exception {
      // tested in testGetObjects, testGetObjectsWithFilter
   }

   /**
    * Test reset.
    */
   public void testReset() throws Exception {
      Person bob = new Person("bob");
      Handle handle = statefulSession.addObject(bob);
      assertTrue("where is bob", statefulSession.containsObject(handle));

      statefulSession.reset();
      assertTrue("bob still there", !statefulSession.containsObject(handle));
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
