package org.drools.reteoo;

/*
 $Id: Dumper.java,v 1.4 2004-08-07 16:23:31 mproctor Exp $

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
import org.drools.Visitor;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

/** Implementation of <code>RuleBase</code>.
 *
 *  @author <a href="mailto:bob@werken.com">bob mcwhirter</a>
 *
 *  @version $Id: Dumper.java,v 1.4 2004-08-07 16:23:31 mproctor Exp $
 */
public class Dumper
{
    private RuleBaseImpl ruleBase;

    public Dumper(RuleBase ruleBase)
    {
        this.ruleBase = (RuleBaseImpl) ruleBase;
    }

    public void dumpRete(PrintStream out)
    {
        dumpRete(out, "  ");
    }

    public void dumpRete(PrintStream out, String indent)
    {
        Visitor visitor = new ReteooPrintDumpVisitor(out, indent);
        visitor.visit(ruleBase);
    }

    /**
     * Compatible with the GraphViz DOT format.
     */
    public void dumpReteToDot(PrintStream out)
    {
        JoinNodeInput.resetDump();
        out.println(ruleBase.dumpReteToDot());
    }

    /**
     * Converts line-breaks into \n for GraphViz DOT compatibility.
     */
    static String formatForDot(Object object)
    {
        if (null == object)
        {
            return "<NULL>";
        }

        BufferedReader br = new BufferedReader(
            new InputStreamReader(
                new ByteArrayInputStream(object.toString().getBytes())));

        StringBuffer buffer = new StringBuffer();
        try
        {
            boolean firstLine = true;
            for (String line = br.readLine(); null != line; line = br.readLine())
            {
                if (line.trim().length() == 0)
                {
                    continue;
                }
                if (!firstLine)
                {
                    buffer.append("\\n");
                }
                buffer.append(line);
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException("Error formatting '" + object + "'", e);
        }

        return buffer.toString();
    }
}
