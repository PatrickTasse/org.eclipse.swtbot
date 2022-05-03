/*******************************************************************************
 * Copyright (c) 2008, 2020 Ketan Padegaonkar and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Ketan Padegaonkar - initial API and implementation
 *     Paulin - http://swtbot.org/bugzilla/show_bug.cgi?id=36
 *     Aparna Argade - Bug 509723
 *******************************************************************************/
package org.eclipse.swtbot.swt.finder.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swtbot.swt.finder.ReferenceBy;
import org.eclipse.swtbot.swt.finder.SWTBotAssert;
import org.eclipse.swtbot.swt.finder.SWTBotWidget;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.results.VoidResult;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.utils.internal.Assert;
import org.hamcrest.SelfDescribing;

/**
 * This represents a {@link Text} widget.
 *
 * @author Ketan Padegaonkar &lt;KetanPadegaonkar [at] gmail [dot] com&gt;
 * @version $Id$
 */
@SWTBotWidget(clasz = Text.class, preferredName = "text", referenceBy = { ReferenceBy.LABEL, ReferenceBy.TEXT, ReferenceBy.TOOLTIP, ReferenceBy.MESSAGE })
public class SWTBotText extends AbstractSWTBotControl<Text> {

	/**
	 * Constructs a new instance of this object.
	 *
	 * @param w the widget.
	 * @throws WidgetNotFoundException if the widget is <code>null</code> or widget has been disposed.
	 */
	public SWTBotText(Text w) throws WidgetNotFoundException {
		this(w, null);
	}

	/**
	 * Constructs a new instance of this object.
	 *
	 * @param w the widget.
	 * @param description the description of the widget, this will be reported by {@link #toString()}
	 * @throws WidgetNotFoundException if the widget is <code>null</code> or widget has been disposed.
	 */
	public SWTBotText(Text w, SelfDescribing description) throws WidgetNotFoundException {
		super(w, description);
	}

	/**
	 * Sets the text of the widget.
	 *
	 * @param text the text to be set.
	 * @return the same instance.
	 */
	public SWTBotText setText(final String text) {
		waitForEnabled();
		assertWritable();
		syncExec(new VoidResult() {
			@Override
			public void run() {
				widget.setText(text);
			}
		});
		return this;
	}

	/**
	 * Types the string in the text box.
	 *
	 * @param text the text to be typed.
	 * @return the same instance.
	 * @since 1.2
	 */
	public SWTBotText typeText(final String text) {
		return typeText(text, SWTBotPreferences.TYPE_INTERVAL);
	}

	/**
	 * Types the string in the text box.
	 *
	 * @param text the text to be typed.
	 * @param interval the interval between consecutive key strokes.
	 * @return the same instance.
	 * @since 1.2
	 */
	public SWTBotText typeText(final String text, int interval) {
		log.debug("Inserting text:{} into text {}", text, this); //$NON-NLS-1$
		assertWritable();
		setFocus();
		keyboard().typeText(text, interval);
		return this;
	}

	/**
	 * Select the contents of the entire widget.
	 * @return the same instance.
	 */
	public SWTBotText selectAll() {
		syncExec(new VoidResult() {
			@Override
			public void run() {
				widget.selectAll();
			}
		});
		return this;
	}

	private void assertWritable() {
		assertEnabled();
		SWTBotAssert.assertVisible(this);
		Assert.isLegal(!hasStyle(widget, SWT.READ_ONLY), "TextBox is read-only"); //$NON-NLS-1$
	}

	/**
	 * Returns true if the TextBox is read-only.
	 *
	 * @return <code>true</code> if the TextBox is read-only. Otherwise <code>false</code>.
	 * @since 2.6
	 */
	public boolean isReadOnly() {
		return hasStyle(widget, SWT.READ_ONLY);
	}

}
