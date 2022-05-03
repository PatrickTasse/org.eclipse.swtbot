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
 *     Patrick Tasse - fix click behavior and support click with modifiers
 *******************************************************************************/
package org.eclipse.swtbot.swt.finder.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.swtbot.swt.finder.ReferenceBy;
import org.eclipse.swtbot.swt.finder.SWTBotWidget;
import org.eclipse.swtbot.swt.finder.Style;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.results.BoolResult;
import org.eclipse.swtbot.swt.finder.results.VoidResult;
import org.eclipse.swtbot.swt.finder.results.WidgetResult;
import org.eclipse.swtbot.swt.finder.utils.SWTUtils;
import org.eclipse.swtbot.swt.finder.utils.internal.Assert;
import org.hamcrest.SelfDescribing;

/**
 * Represents a tool item of type checkbox
 *
 * @author Ketan Padegaonkar &lt;KetanPadegaonkar [at] gmail [dot] com&gt;
 * @version $Id$
 */
@SWTBotWidget(clasz = ToolItem.class, preferredName = "toolbarRadioButton", style = @Style(name = "SWT.RADIO", value = SWT.RADIO), referenceBy = {
		ReferenceBy.MNEMONIC, ReferenceBy.TOOLTIP }, returnType = SWTBotToolbarRadioButton.class)
public class SWTBotToolbarRadioButton extends SWTBotToolbarButton {

	/**
	 * Constructs an new instance of this item.
	 *
	 * @param w the tool item.
	 * @throws WidgetNotFoundException if the widget is <code>null</code> or widget has been disposed.
	 */
	public SWTBotToolbarRadioButton(ToolItem w) throws WidgetNotFoundException {
		this(w, null);
	}

	/**
	 * Constructs an new instance of this item.
	 *
	 * @param w the tool item.
	 * @param description the description of the widget, this will be reported by {@link #toString()}
	 * @throws WidgetNotFoundException if the widget is <code>null</code> or widget has been disposed.
	 */
	public SWTBotToolbarRadioButton(ToolItem w, SelfDescribing description) throws WidgetNotFoundException {
		super(w, description);
		Assert.isTrue(SWTUtils.hasStyle(w, SWT.RADIO), "Expecting a radio button."); //$NON-NLS-1$
	}

	/**
	 * Toggle the tool item.
	 *
	 * @return itself
	 */
	public SWTBotToolbarRadioButton toggle() {
		log.debug("Toggling {}", this); //$NON-NLS-1$
		waitForEnabled();
		internalSetSelection(!isChecked());
		sendNotifications();
		log.debug("Toggled {}", this); //$NON-NLS-1$
		return this;
	}

	/**
	 * @since 2.3
	 */
	@Override
	public SWTBotToolbarRadioButton click(int stateMask) {
		log.debug("Clicking on {}" + (stateMask != 0 ? " with stateMask=0x{1}" : ""), this, Integer.toHexString(stateMask)); //$NON-NLS-1$
		waitForEnabled();
		internalSetSelection(true);
		sendNotifications(stateMask);
		log.debug("Clicked on {}" + (stateMask != 0 ? " with stateMask=0x{1}" : ""), this, Integer.toHexString(stateMask)); //$NON-NLS-1$
		return this;
	}

	private void internalSetSelection(final boolean selected) {
		if (selected) {
			final SWTBotToolbarRadioButton otherSelectedButton = otherSelectedButton();
			if (otherSelectedButton != null) {
				otherSelectedButton.notify(SWT.Deactivate);
				asyncExec(new VoidResult() {
					@Override
					public void run() {
						otherSelectedButton.widget.setSelection(false);
					}
				});
				otherSelectedButton.notify(SWT.Selection);
			}
		}
		syncExec(new VoidResult() {
			@Override
			public void run() {
				widget.setSelection(selected);
			}
		});
	}

	private SWTBotToolbarRadioButton otherSelectedButton() {
		ToolItem other = syncExec(new WidgetResult<ToolItem>() {
			@Override
			public ToolItem run() {
				Widget[] siblings = SWTUtils.siblings(widget);
				boolean ownGroup = false;
				ToolItem selected = null;
				for (Widget sibling : siblings) {
					if (sibling == widget) {
						ownGroup = true;
					} else if (((sibling instanceof ToolItem) && hasStyle(sibling, SWT.RADIO))) {
						if (((ToolItem) sibling).getSelection()) {
							selected = (ToolItem) sibling;
						}
					} else if ((sibling instanceof ToolItem) && hasStyle(sibling, SWT.SEPARATOR)) {
						ownGroup = false;
						selected = null;
					}
					if (ownGroup && selected != null) {
						return selected;
					}
				}
				return null;
			}
		});

		if (other != null)
			return new SWTBotToolbarRadioButton(other);
		return null;
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
