package org.drools.reteoo;

import junit.framework.TestCase;

public class TupleKeyTest extends TestCase
{
    public void testNothing()
    {
    }
    /*
     * public void testPutAll() { this.key.put( this.decl1, this.handle1,
     * this.obj1 );
     *
     * this.key.put( this.decl2, this.handle2, this.obj2 );
     *
     * TupleKey otherKey = new TupleKey();
     *
     * otherKey.putAll( this.key );
     *
     * assertEquals( 2, otherKey.size() );
     *
     * assertTrue( otherKey.containsDeclaration( this.decl1 ) ); assertTrue(
     * otherKey.containsDeclaration( this.decl2 ) );
     *
     * assertTrue( otherKey.containsFactHandle( this.handle1 ) );
     * assertTrue( otherKey.containsFactHandle( this.handle2 ) );
     *
     * assertSame( this.obj1, otherKey.get( this.decl1 ) ); assertSame(
     * this.obj2, otherKey.get( this.decl2 ) );
     *
     * assertSame( this.handle1, otherKey.getRootFactHandle( this.obj1 ) );
     *
     * assertSame( this.handle2, otherKey.getRootFactHandle( this.obj2 ) ); }
     *
     * public void testContainsAll_Exact() { TupleKey otherKey = new TupleKey();
     *
     * this.key.put( this.decl1, this.handle1, this.obj1 );
     *
     * this.key.put( this.decl2, this.handle2, this.obj2 );
     *
     * otherKey.put( this.decl1, this.handle1, this.obj1 );
     *
     * otherKey.put( this.decl2, this.handle2, this.obj2 );
     *
     * assertTrue( this.key.containsAll( otherKey ) ); assertTrue(
     * otherKey.containsAll( this.key ) ); }
     *
     * public void testContainsAll_Superset() { TupleKey otherKey = new
     * TupleKey();
     *
     * this.key.put( this.decl1, this.handle1, this.obj1 );
     *
     * this.key.put( this.decl2, this.handle2, this.obj2 );
     *
     * otherKey.put( this.decl1, this.handle1, this.obj1 );
     *
     * assertTrue( this.key.containsAll( otherKey ) ); assertTrue( !
     * otherKey.containsAll( this.key ) ); }
     *
     * public void testContainsAll_Subset() { TupleKey otherKey = new
     * TupleKey();
     *
     * this.key.put( this.decl1, this.handle1, this.obj1 );
     *
     * otherKey.put( this.decl1, this.handle1, this.obj1 );
     *
     * otherKey.put( this.decl2, this.handle2, this.obj2 );
     *
     *
     * assertTrue( ! this.key.containsAll( otherKey ) ); assertTrue(
     * otherKey.containsAll( this.key ) ); }
     *
     * public void testContainsAll_Null_Null() { TupleKey otherKey = new
     * TupleKey();
     *
     * this.key.put( this.decl1, this.handle1, this.obj1 );
     *
     * this.key.put( this.decl2, this.handle2, null );
     *
     * otherKey.put( this.decl1, this.handle1, this.obj1 );
     *
     * otherKey.put( this.decl2, this.handle2, null );
     *
     * assertTrue( this.key.containsAll( otherKey ) ); assertTrue(
     * otherKey.containsAll( this.key ) ); }
     *
     * public void testContainsAll_MismatchValues() { TupleKey otherKey = new
     * TupleKey();
     *
     * this.key.put( this.decl1, this.handle1, this.obj1 );
     *
     * this.key.put( this.decl2, this.handle2, this.obj2 );
     *
     * otherKey.put( this.decl1, this.handle1, this.obj1 );
     *
     * otherKey.put( this.decl2, this.handle2, new Object() );
     *
     * assertTrue( this.key.containsAll( otherKey ) ); assertTrue(
     * otherKey.containsAll( this.key ) ); }
     *
     * public void testContainsAll_MismatchHandle() { TupleKey otherKey = new
     * TupleKey();
     *
     * this.key.put( this.decl1, this.handle1, this.obj1 );
     *
     * this.key.put( this.decl2, this.handle2, this.obj2 );
     *
     * otherKey.put( this.decl1, this.handle1, this.obj1 );
     *
     * otherKey.put( this.decl2, new FactHandleImpl( 42 ), this.obj2 );
     *
     * assertTrue( ! this.key.containsAll( otherKey ) ); assertTrue( !
     * otherKey.containsAll( this.key ) ); }
     *
     * public void testContainsAll_MismatchDecls() { TupleKey otherKey = new
     * TupleKey();
     *
     * Declaration decl = new Declaration( new MockObjectType( Object.class ),
     * "yetAnother" );
     *
     * this.key.put( this.decl1, this.handle1, this.obj1 );
     *
     * this.key.put( this.decl2, this.handle2, this.obj2 );
     *
     * otherKey.put( this.decl1, this.handle1, this.obj1 );
     *
     * otherKey.put( decl, new FactHandleImpl( 42 ), this.obj2 );
     *
     * assertTrue( ! this.key.containsAll( otherKey ) ); assertTrue( !
     * otherKey.containsAll( this.key ) ); }
     *
     * public void testEquals_WrongClass() { this.key.put( this.decl1,
     * this.handle1, this.obj1 );
     *
     * assertFalse( new Object().equals( this.key ) ); assertFalse(
     * this.key.equals( new Object() ) ); }
     *
     * public void testEquals_SameObject() { this.key.put( this.decl1,
     * this.handle1, this.obj1 );
     *
     * assertEquals( this.key, this.key ); }
     *
     * public void testEquals_EqualButDifferent() { TupleKey otherKey = new
     * TupleKey();
     *
     * this.key.put( this.decl1, this.handle1, this.obj1 );
     *
     * this.key.put( this.decl2, this.handle2, this.obj2 );
     *
     * otherKey.put( this.decl1, this.handle1, new Object() );
     *
     * otherKey.put( this.decl2, this.handle2, new Object() );
     *
     * assertTrue( this.key.equals( otherKey ) ); assertTrue( otherKey.equals(
     * this.key ) ); }
     *
     * public void testHashCode_EqualButDifferent() { TupleKey otherKey = new
     * TupleKey();
     *
     * this.key.put( this.decl1, this.handle1, this.obj1 );
     *
     * this.key.put( this.decl2, this.handle2, this.obj2 );
     *
     * otherKey.put( this.decl1, this.handle1, "object-1" );
     *
     * otherKey.put( this.decl2, this.handle2, "object-2" );
     *
     * assertEquals( this.key.hashCode(), otherKey.hashCode() ); }
     *
     * public void testEquals_NoObjects() { TupleKey key1 = new TupleKey(
     * this.handle1 ); TupleKey key2 = new TupleKey( this.handle1 );
     *
     * assertEquals( key1, key2 );
     *
     * assertEquals( key1.hashCode(), key2.hashCode() ); }
     */
}