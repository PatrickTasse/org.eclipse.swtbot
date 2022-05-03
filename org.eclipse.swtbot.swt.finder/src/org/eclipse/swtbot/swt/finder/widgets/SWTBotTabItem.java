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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swtbot.swt.finder.ReferenceBy;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.SWTBotWidget;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.results.BoolResult;
import org.eclipse.swtbot.swt.finder.results.VoidResult;
import org.eclipse.swtbot.swt.finder.results.WidgetResult;
import org.eclipse.swtbot.swt.finder.utils.SWTUtils;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.hamcrest.SelfDescribing;

/**
 * @author Ketan Padegaonkar &lt;KetanPadegaonkar [at] gmail [dot] com&gt;
 * @author Joshua Gosse &lt;jlgosse [at] ca [dot] ibm [dot] com&gt;
 * @version $Id$
 */
@SWTBotWidget(clasz = TabItem.class, preferredName = "tabItem", referenceBy = { ReferenceBy.MNEMONIC })
public class SWTBotTabItem extends AbstractSWTBot<TabItem> {

	private TabFolder	parent;

	/**
	 * Constructs a new instance of this object.
	 * 
	 * @param w the widget.
	 * @throws WidgetNotFoundException if the widget is <code>null</code> or widget has been disposed.
	 */
	public SWTBotTabItem(TabItem w) throws WidgetNotFoundException {
		this(w, null);
	}

	/**
	 * Constructs a new instance of this object.
	 * 
	 * @param w the widget.
	 * @param description the description of the widget, this will be reported by {@link #toString()}
	 * @throws WidgetNotFoundException if the widget is <code>null</code> or widget has been disposed.
	 */
	public SWTBotTabItem(TabItem w, SelfDescribing description) throws WidgetNotFoundException {
		super(w, description);
		this.parent = syncExec(new WidgetResult<TabFolder>() {
			@Override
			public TabFolder run() {
				return widget.getParent();
			}
		});
	}

	/**
	 * Activates the tabItem.
	 * 
	 * @return itself.
	 * @throws TimeoutException if the tab does not activate
	 */
	public SWTBotTabItem activate() throws TimeoutException {
		log.trace("Activating {}", this); //$NON-NLS-1$
		waitForEnabled();
		// this runs in sync because tabFolder.setSelection() does not send a notification, and so should not block.
		asyncExec(new VoidResult() {
			@Override
			public void run() {
				widget.getParent().setSelection(widget);
				log.debug("Activated {}", this); //$NON-NLS-1$
			}
		});

		notify(SWT.Selection, createEvent(), parent);

		new SWTBot().waitUntil(new DefaultCondition() {
			@Override
			public boolean test() throws Exception {
				return isActive();
			}

			@Override
			public String getFailureMessage() {
				return "Timed out waiting for " + SWTUtils.toString(widget) + " to activate"; //$NON-NLS-1$ //$NON-NLS-2$
			}
		});
		return this;
	}

	@Override
	protected Event createEvent() {
		Event event = super.createEvent();
		event.widget = parent;
		event.item = widget;
		return event;
	}

	@Override
	public boolean isActive() {
		return syncExec(new BoolResult() {
			@Override
			public Boolean run() {
				return parent.getSelection()[0] == widget;
			}
		});
	}

	@Override
	public boolean isEnabled() {
		return syncExec(new BoolResult() {
			@Override
			public Boolean run() {
				return parent.isEnabled();
			}
		});
	}

}
