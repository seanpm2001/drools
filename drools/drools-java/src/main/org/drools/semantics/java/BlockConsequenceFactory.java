package org.drools.semantics.java;

import org.drools.rule.Declaration;
import org.drools.smf.Configuration;
import org.drools.smf.ConsequenceFactory;
import org.drools.smf.FactoryException;
import org.drools.spi.Consequence;

public class BlockConsequenceFactory implements ConsequenceFactory
{
    private static final BlockConsequenceFactory INSTANCE = new BlockConsequenceFactory( );

    public static BlockConsequenceFactory getInstance()
    {
        return INSTANCE;
    }

    public Consequence newConsequence(Configuration config,
                                      Declaration[] availDecls) throws FactoryException
    {
        try
        {
            return new BlockConsequence( config.getText( ), availDecls );
        }
        catch ( Exception e )
        {
            throw new FactoryException( e );
        }
    }
}