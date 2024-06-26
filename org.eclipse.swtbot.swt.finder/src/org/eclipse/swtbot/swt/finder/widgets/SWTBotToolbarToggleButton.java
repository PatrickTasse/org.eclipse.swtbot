/*******************************************************************************
 * Copyright (c) 2009, 2015 Ketan Padegaonkar and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Ketan Padegaonkar - initial API and implementation
 *     Patrick Tasse - support click with modifiers
 *******************************************************************************/
package org.eclipse.swtbot.swt.finder.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swtbot.swt.finder.ReferenceBy;
import org.eclipse.swtbot.swt.finder.SWTBotWidget;
import org.eclipse.swtbot.swt.finder.Style;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.results.BoolResult;
import org.eclipse.swtbot.swt.finder.results.VoidResult;
import org.eclipse.swtbot.swt.finder.utils.SWTUtils;
import org.eclipse.swtbot.swt.finder.utils.internal.Assert;
import org.hamcrest.SelfDescribing;

/**
 * Represents a tool item of type checkbox
 *
 * @author Ketan Padegaonkar &lt;KetanPadegaonkar [at] gmail [dot] com&gt;
 * @version $Id$
 */
@SWTBotWidget(clasz = ToolItem.class, preferredName = "toolbarToggleButton", style = @Style(name = "SWT.CHECK", value = SWT.CHECK), referenceBy = {
		ReferenceBy.MNEMONIC, ReferenceBy.TOOLTIP }, returnType = SWTBotToolbarToggleButton.class)
public class SWTBotToolbarToggleButton extends SWTBotToolbarButton {

	/**
	 * Constructs an new instance of this item.
	 *
	 * @param w the tool item.
	 * @throws WidgetNotFoundException if the widget is <code>null</code> or widget has been disposed.
	 */
	public SWTBotToolbarToggleButton(ToolItem w) throws WidgetNotFoundException {
		this(w, null);
	}

	/**
	 * Constructs an new instance of this item.
	 *
	 * @param w the tool item.
	 * @param description the description of the widget, this will be reported by {@link #toString()}
	 * @throws WidgetNotFoundException if the widget is <code>null</code> or widget has been disposed.
	 */
	public SWTBotToolbarToggleButton(ToolItem w, SelfDescribing description) throws WidgetNotFoundException {
		super(w, description);
		Assert.isTrue(SWTUtils.hasStyle(w, SWT.CHECK), "Expecting a checkbox."); //$NON-NLS-1$
	}

	/**
	 * Click on the tool item. This will toggle the tool item.
	 *
	 * @return itself
	 */
	public SWTBotToolbarToggleButton toggle() {
		log.debug("Clicking on {}", this); //$NON-NLS-1$
		waitForEnabled();
		internalToggle();
		sendNotifications();
		log.debug("Clicked on {}", this); //$NON-NLS-1$
		return this;
	}

	/**
	 * @since 2.3
	 */
	@Override
	public SWTBotToolbarToggleButton click(int stateMask) {
		log.debug("Clicking on {}" + (stateMask != 0 ? " with stateMask=0x{1}" : ""), this, Integer.toHexString(stateMask)); //$NON-NLS-1$
		waitForEnabled();
		internalToggle();
		sendNotifications(stateMask);
		log.debug("Clicked on {}" + (stateMask != 0 ? " with stateMask=0x{1}" : ""), this, Integer.toHexString(stateMask)); //$NON-NLS-1$
		return this;
	}

	private void internalToggle() {
		syncExec(new VoidResult() {
			@Override
			public void run() {
				widget.setSelection(!widget.getSelection());
			}
		});
	}

	/**
	 * Selects the checkbox button.
	 */
	public void select() {
		if (!isChecked())
			toggle();
	}

	/**
	 * Deselects the checkbox button.
	 */
	public void deselect() {
		if (isChecked())
			toggle();
	}

	/**
	 * @return <code>true</code> if the button is checked, <code>false</code> otherwise.
	 */
	public boolean isChecked() {
		return syncExec(new BoolResult() {
			@Override
			public Boolean run() {
				return widget.getSelection();
			}
		});
	}
}
