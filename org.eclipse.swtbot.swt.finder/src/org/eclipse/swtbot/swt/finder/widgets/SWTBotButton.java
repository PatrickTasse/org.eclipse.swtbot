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
package org.eclipse.swtbot.swt.finder.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swtbot.swt.finder.ReferenceBy;
import org.eclipse.swtbot.swt.finder.SWTBotWidget;
import org.eclipse.swtbot.swt.finder.Style;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.utils.SWTUtils;
import org.eclipse.swtbot.swt.finder.utils.internal.Assert;
import org.hamcrest.SelfDescribing;

/**
 * This represents a {@link Button} widget of type {@link SWT#PUSH}.
 *
 * @author Ketan Padegaonkar &lt;KetanPadegaonkar [at] gmail [dot] com&gt;
 * @version $Id$
 * @see SWTBotCheckBox
 * @see SWTBotRadio
 * @see SWTBotToggleButton
 */
@SWTBotWidget(clasz = Button.class, style = @Style(name = "SWT.PUSH", value = SWT.PUSH), preferredName = "button", referenceBy = { ReferenceBy.LABEL, ReferenceBy.MNEMONIC, ReferenceBy.TOOLTIP })  //$NON-NLS-1$
public class SWTBotButton extends AbstractSWTBotControl<Button> {

	/**
	 * Constructs an instance of this object with the given button
	 *
	 * @param button the widget.
	 * @throws WidgetNotFoundException if the widget is <code>null</code> or widget has been disposed.
	 * @since 2.0
	 */
	public SWTBotButton(Button button) {
		this(button, null);
	}

	/**
	 * Constructs an instance of this object with the given button
	 *
	 * @param button the widget.
	 * @param description the description of the widget, this will be reported by {@link #toString()}
	 * @throws WidgetNotFoundException if the widget is <code>null</code> or widget has been disposed.
	 * @since 2.0
	 */
	public SWTBotButton(Button button, SelfDescribing description) {
		super(button, description);
		Assert.isTrue(SWTUtils.hasStyle(button, SWT.PUSH), "Expecting a push button."); //$NON-NLS-1$
	}

	/**
	 * Click on the button.
	 */
	@Override
	public SWTBotButton click() {
		log.debug("Clicking on {}", SWTUtils.getText(widget)); //$NON-NLS-1$
		waitForEnabled();
		notify(SWT.MouseEnter);
		notify(SWT.MouseMove);
		notify(SWT.Activate);
		notify(SWT.FocusIn);
		notify(SWT.MouseDown);
		notify(SWT.MouseUp);
		notify(SWT.Selection);
		notify(SWT.MouseHover);
		notify(SWT.MouseMove);
		notify(SWT.MouseExit);
		notify(SWT.Deactivate);
		notify(SWT.FocusOut);
		log.debug("Clicked on {}", SWTUtils.getText(widget)); //$NON-NLS-1$
		return this;
	}

}
