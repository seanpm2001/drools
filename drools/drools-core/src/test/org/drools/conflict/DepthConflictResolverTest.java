package org.drools.conflict;

/*
$Id: DepthConflictResolverTest.java,v 1.1 2004-06-27 12:31:17 mproctor Exp $

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

import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;

import org.drools.rule.InstrumentedRule;
import org.drools.spi.ConflictResolver;
import org.drools.spi.MockTuple;

public class DepthConflictResolverTest extends TestCase
{
	private ConflictResolver conflictResolver;

	private InstrumentedRule brieRule;
	private InstrumentedRule camembertRule;
	private InstrumentedRule stiltonRule;
	private InstrumentedRule cheddarRule;
	private InstrumentedRule fetaRule;
	private InstrumentedRule mozzarellaRule;

	private MockAgendaItem brie;
	private MockAgendaItem camembert;
	private MockAgendaItem stilton;
	private MockAgendaItem cheddar;
	private MockAgendaItem feta;
	private MockAgendaItem mozzarella;

	private LinkedList items;
	private List conflictItems;

	public DepthConflictResolverTest( String name )
	{
		super( name );
	}

	public void setUp()
	{
		this.conflictResolver = DepthConflictResolver.getInstance( );
		items = new LinkedList( );

		brieRule = new InstrumentedRule( "brie" );
		camembertRule = new InstrumentedRule( "camembert" );
		stiltonRule = new InstrumentedRule( "stilton" );
		cheddarRule = new InstrumentedRule( "cheddar" );
		fetaRule = new InstrumentedRule( "feta" );
		mozzarellaRule = new InstrumentedRule( "mozzarella" );

		brie = new MockAgendaItem( new MockTuple( ), brieRule );
		camembert = new MockAgendaItem( new MockTuple( ), camembertRule );
		stilton = new MockAgendaItem( new MockTuple( ), stiltonRule );
		cheddar = new MockAgendaItem( new MockTuple( ), cheddarRule );
		feta = new MockAgendaItem( new MockTuple( ), fetaRule );
		mozzarella = new MockAgendaItem( new MockTuple( ), mozzarellaRule );
	}

	public void tearDown()
	{
		this.conflictResolver = null;
		items = null;

		brieRule = null;
		camembertRule = null;
		stiltonRule = null;
		cheddarRule = null;
		fetaRule = null;
		mozzarellaRule = null;

		brie = null;
		camembert = null;
		stilton = null;
		cheddar = null;
		feta = null;
		mozzarella = null;
	}

	public void testSingleInsert() throws Exception
	{
		items.clear( );
		conflictItems = this.conflictResolver.insert( brie, items );
		assertNull( conflictItems );
		MockAgendaItem item = (MockAgendaItem) items.get( 0 );
		assertEquals( "brie", item.getRule( ).getName( ) );
	}

	public void testInsertsNoConflicts()
	{
		MockAgendaItem item;
		items.clear( );

		//try ascending
		conflictItems = this.conflictResolver.insert( brie, items );
		assertNull( conflictItems );
		conflictItems = this.conflictResolver.insert( camembert, items );
		assertNull( conflictItems );
		conflictItems = this.conflictResolver.insert( stilton, items );
		assertNull( conflictItems );

		item = (MockAgendaItem) items.get( 0 );
		assertEquals( "brie", item.getRule( ).getName( ) );
		item = (MockAgendaItem) items.get( 2 );
		assertEquals( "stilton", item.getRule( ).getName( ) );
		item = (MockAgendaItem) items.get( 1 );
		assertEquals( "camembert", item.getRule( ).getName( ) );

		//try descending
		items.clear( );
		conflictItems = this.conflictResolver.insert( stilton, items );
		assertNull( conflictItems );
		conflictItems = this.conflictResolver.insert( camembert, items );
		assertNull( conflictItems );
		conflictItems = this.conflictResolver.insert( brie, items );
		assertNull( conflictItems );

		item = (MockAgendaItem) items.get( 0 );
		assertEquals( "brie", item.getRule( ).getName( ) );
		item = (MockAgendaItem) items.get( 2 );
		assertEquals( "stilton", item.getRule( ).getName( ) );
		item = (MockAgendaItem) items.get( 1 );
		assertEquals( "camembert", item.getRule( ).getName( ) );

		//try mixed order
		items.clear( );
		conflictItems = this.conflictResolver.insert( camembert, items );
		assertNull( conflictItems );
		conflictItems = this.conflictResolver.insert( stilton, items );
		assertNull( conflictItems );
		conflictItems = this.conflictResolver.insert( brie, items );
		assertNull( conflictItems );

		item = (MockAgendaItem) items.get( 0 );
		assertEquals( "brie", item.getRule( ).getName( ) );
		item = (MockAgendaItem) items.get( 2 );
		assertEquals( "stilton", item.getRule( ).getName( ) );
		item = (MockAgendaItem) items.get( 1 );
		assertEquals( "camembert", item.getRule( ).getName( ) );
	}
}