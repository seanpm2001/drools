package org.drools.io;

/*
 * $Id: FunctionsFactory.java,v 1.3 2005/05/07 04:39:30 dbarnett Exp $
 *
 * Copyright 2004-2005 (C) The Werken Company. All Rights Reserved.
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
 *
 */


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.List;

import org.drools.IntegrationException;
import org.drools.RuleBase;
import org.drools.WorkingMemory;
import org.drools.spi.ConflictResolver;
import org.drools.spi.RuleBaseContext;
import org.xml.sax.SAXException;

/**
 * This wraps a rulebase, and makes it serializable by storing the binary
 * ruleset data.
 * 
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 * 
 */
public class SerializableRuleBaseProxy
    implements
    RuleBase
{

    private static final long  serialVersionUID = -5251454333223656432L;

    // this is transient, as when we serialize, we will really just be saving
    // and loading the binaries.
    private transient RuleBase ruleBase;

    private List               ruleSetBinaries;

    public SerializableRuleBaseProxy() {
        
    }
    
    public SerializableRuleBaseProxy(RuleBase ruleBase,
                                     List ruleSetBinaries)
    {
        this.ruleBase = ruleBase;
        this.ruleSetBinaries = ruleSetBinaries;
    }

    public WorkingMemory newWorkingMemory()
    {
        return ruleBase.newWorkingMemory( );
    }

    public ConflictResolver getConflictResolver()
    {

        return ruleBase.getConflictResolver( );
    }

    public List getRuleSets()
    {
        return ruleBase.getRuleSets( );
    }

    public RuleBaseContext getRuleBaseContext()
    {
        return ruleBase.getRuleBaseContext( );
    }

    public WorkingMemory getCurrentThreadWorkingMemory()
    {
        return ruleBase.getCurrentThreadWorkingMemory( );
    }

     private void writeObject(ObjectOutputStream out) throws IOException
    {
       
        out.writeObject( this.ruleSetBinaries );
      

       
    }

    private void readObject(ObjectInputStream in) throws IOException,
                                                 ClassNotFoundException,
                                                 IntegrationException,
                                                 SAXException
    {
        
        this.ruleSetBinaries = (List) in.readObject( );
        RuleBaseLoader loader = new RuleBaseLoader( );
        for ( Iterator iter = this.ruleSetBinaries.iterator( ); iter.hasNext( ); )
        {
            byte[] ruleSetBin = (byte[]) iter.next( );
            loader.addFromByteArray( ruleSetBin );
        }

        this.ruleBase = loader.buildRuleBase( );
        
    }

    public RuleBase getWrappedRuleBase() {
        return this.ruleBase;
    }

    List getBinaries() {
        return this.ruleSetBinaries;
    }


}