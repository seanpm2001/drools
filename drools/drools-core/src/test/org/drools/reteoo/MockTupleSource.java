package org.drools.reteoo;

import org.drools.reteoo.impl.TupleSourceImpl;
import org.drools.spi.Declaration;

import java.util.Set;
import java.util.HashSet;

public class MockTupleSource extends TupleSourceImpl
{
    private Set declarations;

    public MockTupleSource()
    {
        this.declarations = new HashSet();
    }

    public void addTupleDeclaration(Declaration decl)
    {
        this.declarations.add( decl );
    }

    public Set getTupleDeclarations()
    {
        return this.declarations;
    }

    public String toString()
    {
        return "[MockTupleSource: tuple-decls=" + getTupleDeclarations() + "]";
    }
}
