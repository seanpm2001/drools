package org.drools.examples.fibonacci;

/*
$Id: FibonacciSerializedExample.java,v 1.1 2004-07-07 04:45:21 dbarnett Exp $

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

import org.drools.RuleBase;
import org.drools.WorkingMemory;
import org.drools.io.RuleBaseBuilder;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectOutput;
import java.io.ObjectInputStream;
import java.io.ObjectInput;


public class FibonacciSerializedExample
{
    private static RuleBase getSerializedRuleBase(String drl)
        throws Exception
    {
        RuleBase ruleBaseIn = RuleBaseBuilder.buildFromUrl( FibonacciExample.class.getResource( drl ) );

        // Serialize to a byte array
        ByteArrayOutputStream bos = new ByteArrayOutputStream() ;
        ObjectOutput out = new ObjectOutputStream(bos) ;
        out.writeObject(ruleBaseIn);
        out.close();

        // Get the bytes of the serialized object
        byte[] bytes = bos.toByteArray();

        // Deserialize from a byte array
        ObjectInput in = new ObjectInputStream(new ByteArrayInputStream(bytes));
        RuleBase ruleBaseOut = (RuleBase) in.readObject();
        in.close();
        return ruleBaseOut;
    }

    public static void main(String[] args)
        throws Exception
    {
        RuleBase ruleBase = getSerializedRuleBase("fibonacci.java.drl");

        WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        Fibonacci fibonacci = new Fibonacci( 50 );

        long start = System.currentTimeMillis();

        workingMemory.assertObject( fibonacci );

        workingMemory.fireAllRules();

        long stop = System.currentTimeMillis();

        System.out.println( "fibanacci(" + fibonacci.getSequence() + ") == " + fibonacci.getValue() + " took " + (stop-start) + "ms" );
    }
}