package org.drools.smf;

import org.drools.spi.Consequence;

public interface ConsequenceFactory
{
    Consequence newConsequence(Configuration config)
        throws FactoryException;
}