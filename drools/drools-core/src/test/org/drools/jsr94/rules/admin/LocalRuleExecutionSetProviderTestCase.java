package org.drools.jsr94.rules.admin;

/*
 $Id: LocalRuleExecutionSetProviderTestCase.java,v 1.1 2003-03-22 00:59:49 tdiesler Exp $

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

import org.drools.jsr94.rules.admin.JSR94RuleSetLoader;
import org.drools.rule.RuleSet;
import org.drools.jsr94.rules.JSR94TestBase;

import javax.rules.admin.LocalRuleExecutionSetProvider;
import javax.rules.admin.RuleAdministrator;
import javax.rules.admin.RuleExecutionSet;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Test the LocalRuleExecutionSetProvider implementation.
 *
 * @author <a href="mailto:thomas.diesler@softcon-itec.de">thomas diesler</a>
 */
public class LocalRuleExecutionSetProviderTestCase extends JSR94TestBase {

   private RuleAdministrator ruleAdministrator;
   private LocalRuleExecutionSetProvider ruleSetProvider;

   /**
    * Setup the test case.
    */
   protected void setUp() throws Exception {
      super.setUp();
      ruleAdministrator = ruleServiceProvider.getRuleAdministrator();
      ruleSetProvider = ruleAdministrator.getLocalRuleExecutionSetProvider(null);
   }

   /**
    * Test createRuleExecutionSet from InputStream.
    */
   public void testCreateFromInputStream() throws Exception {
      InputStream rulesStream = getResourceAsStream(RULES_RESOURCE);
      RuleExecutionSet ruleSet = ruleSetProvider.createRuleExecutionSet(rulesStream, null);
      assertEquals("rule set name", "Sisters Rules", ruleSet.getName());
      assertEquals("number of rules", 2, ruleSet.getRules().size());
   }


   /**
    * Test createRuleExecutionSet from Object.
    */
   public void testCreateFromObject() throws Exception {

      JSR94RuleSetLoader ruleSetLoader = new JSR94RuleSetLoader();
      Reader ruleReader = new InputStreamReader(getResourceAsStream(RULES_RESOURCE));
      RuleSet droolRuleSet = (RuleSet)ruleSetLoader.load(ruleReader).get(0);

      RuleExecutionSet ruleSet = ruleSetProvider.createRuleExecutionSet(droolRuleSet, null);
      assertEquals("rule set name", "Sisters Rules", ruleSet.getName());
      assertEquals("number of rules", 2, ruleSet.getRules().size());
   }

   /**
    * Test createRuleExecutionSet from Reader.
    */
   public void testCreateFromReader() throws Exception {
      Reader ruleReader = new InputStreamReader(getResourceAsStream(RULES_RESOURCE));
      RuleExecutionSet ruleSet = ruleSetProvider.createRuleExecutionSet(ruleReader, null);
      assertEquals("rule set name", "Sisters Rules", ruleSet.getName());
      assertEquals("number of rules", 2, ruleSet.getRules().size());
   }
}
