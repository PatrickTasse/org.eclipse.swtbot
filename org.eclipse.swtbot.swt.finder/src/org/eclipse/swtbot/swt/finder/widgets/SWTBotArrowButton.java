/*******************************************************************************
 * Copyright (c) 2008 Ketan Padegaonkar and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
import org.eclipse.swtbot.swt.finder.utils.MessageFormat;
import org.eclipse.swtbot.swt.finder.utils.SWTUtils;
import org.eclipse.swtbot.swt.finder.utils.internal.Assert;
import org.hamcrest.SelfDescribing;

/**
 * This represents a {@link Button} widget of type {@link SWT#ARROW}.
 *
 * @author Ketan Padegaonkar &lt;KetanPadegaonkar [at] gmail [dot] com&gt;
 * @version $Id$
 * @see SWTBotCheckBox
 * @see SWTBotRadio
 * @see SWTBotToggleButton
 */
@SWTBotWidget(clasz = Button.class, style = @Style(name = "SWT.ARROW", value = SWT.ARROW), preferredName = "arrowButton", referenceBy = { ReferenceBy.LABEL, ReferenceBy.TOOLTIP })  //$NON-NLS-1$
public class SWTBotArrowButton extends AbstractSWTBotControl<Button> {

	/**
	 * Constructs an instance of this object with the given button
	 *
	 * @param button the widget.
	 * @throws WidgetNotFoundException if the widget is <code>null</code> or widget has been disposed.
	 * @since 2.0
	 */
	public SWTBotArrowButton(Button button) {
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
	public SWTBotArrowButton(Button button, SelfDescribing description) {
		super(button, description);
		Assert.isTrue(SWTUtils.hasStyle(button, SWT.ARROW), "Expecting an arrow button."); //$NON-NLS-1$
	}

	/**
	 * Click on the button.
	 */
	public SWTBotArrowButton click() {
		log.debug(MessageFormat.format("Clicking on {0}", SWTUtils.getText(widget))); //$NON-NLS-1$
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
		log.debug(MessageFormat.format("Clicked on {0}", SWTUtils.getText(widget))); //$NON-NLS-1$
		return this;
	}

}
