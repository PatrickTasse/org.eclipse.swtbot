/*******************************************************************************
 * Copyright (c) 2008 Ketan Padegaonkar and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     Ketan Padegaonkar - initial API and implementation
 *******************************************************************************/
package org.eclipse.swtbot.swt.finder.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TableCollectionTest {

	@Test
	public void hashCodeIsSameForSameTableCollections() throws Exception {
		assertEquals(emptyTableCollection().hashCode(), emptyTableCollection().hashCode());
		assertEquals(collection().hashCode(), collection().hashCode());
	}

	@Test
	public void equalsForSameTableCollections() throws Exception {
		assertTrue(emptyTableCollection().equals(emptyTableCollection()));
		assertTrue(collection().equals(collection()));

		final TableCollection collection = collection();
		assertTrue(collection.equals(collection));

		assertFalse(emptyTableCollection().equals(collection()));
		assertFalse(collection().equals(null));
		Object other = this;
		assertFalse(collection().equals(other));
	}

	@Test
	public void getsToString() throws Exception {
		assertEquals("[Hello, world]\n" + "[goodbye, world]\n", collection().toString());
	}

	private TableCollection emptyTableCollection() {
		return new TableCollection();
	}

	private TableCollection collection() {
		return new TableCollection(new TableRow[] { new TableRow(new String[] { "Hello", "world" }),
				new TableRow(new String[] { "goodbye", "world" }) });
	}
}
