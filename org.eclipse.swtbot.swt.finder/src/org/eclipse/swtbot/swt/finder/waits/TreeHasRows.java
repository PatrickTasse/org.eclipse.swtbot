/*******************************************************************************
 * Copyright (c) 2010 Ketan Padegaonkar and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Jesper S Møller - initial API and implementation 
 *******************************************************************************/
package org.eclipse.swtbot.swt.finder.waits;

import org.eclipse.swtbot.swt.finder.utils.internal.Assert;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;

/**
 * A condition that returns <code>false</code> until the tree has the specified number of rows.
 * 
 * @see Conditions
 * @author Jesper Steen Moeller &lt;jesper [at] selskabet [dot] org&gt;
 */
class TreeHasRows extends DefaultCondition {

	/**
	 * The row count.
	 */
	private final int			rowCount;
	/**
	 * The tree (SWTBotTree) instance to check.
	 */
	private final SWTBotTree	tree;

	/**
	 * Constructs an instance of the condition for the given tree. The row count is used to know how many rows it needs
	 * to satisfy the condition.
	 * 
	 * @param tree the tree
	 * @param rowCount the number of rows needed.
	 * @throws NullPointerException Thrown if the tree is <code>null</code>.
	 * @throws IllegalArgumentException Thrown if the row count is less then 1.
	 */
	TreeHasRows(SWTBotTree tree, int rowCount) {
		Assert.isNotNull(tree, "The tree can not be null"); //$NON-NLS-1$
		Assert.isLegal(rowCount >= 0, "The node count must be greater than zero (0)"); //$NON-NLS-1$
		this.tree = tree;
		this.rowCount = rowCount;
	}

	/**
	 * Performs the check to see if the condition is satisfied.
	 * 
	 * @see org.eclipse.swtbot.swt.finder.waits.ICondition#test()
	 * @return <code>true</code> if the condition node count equals the number of nodes in the tree. Otherwise
	 *         <code>false</code> is returned.
	 */
	@Override
	public boolean test() {
		return tree.rowCount() == rowCount;
	}

	/**
	 * Gets the failure message if the test is not satisfied.
	 * 
	 * @see org.eclipse.swtbot.swt.finder.waits.ICondition#getFailureMessage()
	 * @return The failure message.
	 */
	@Override
	public String getFailureMessage() {
		return "Timed out waiting for " + tree + " to contain " + rowCount + " rows."; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
}
