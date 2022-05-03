/*******************************************************************************
 * Copyright (c) 2008, 2018 Ketan Padegaonkar and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Ketan Padegaonkar - initial API and implementation
 *     Patrick Tasse - Improve SWTBot menu API and implementation (Bug 479091)
 *     Aparna Argade - maximize API (Bug 532391)
 *******************************************************************************/
package org.eclipse.swtbot.swt.finder.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.SWTBotWidget;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.results.BoolResult;
import org.eclipse.swtbot.swt.finder.results.VoidResult;
import org.eclipse.swtbot.swt.finder.utils.SWTUtils;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.waits.WaitForObjectCondition;
import org.hamcrest.SelfDescribing;

/**
 * @author Ketan Padegaonkar &lt;KetanPadegaonkar [at] gmail [dot] com&gt;
 * @version $Id$
 */
@SWTBotWidget(clasz = Shell.class, preferredName = "shell")
public class SWTBotShell extends AbstractSWTBotControl<Shell> {

	/**
	 * Constructs an instance of this with the given shell.
	 *
	 * @param shell the widget.
	 * @throws WidgetNotFoundException if the widget is <code>null</code> or widget has been disposed.
	 */
	public SWTBotShell(Shell shell) throws WidgetNotFoundException {
		this(shell, null);
	}

	/**
	 * Constructs an instance of this with the given shell.
	 *
	 * @param shell the widget.
	 * @param description the description of the widget, this will be reported by {@link #toString()}
	 * @throws WidgetNotFoundException if the widget is <code>null</code> or widget has been disposed.
	 */
	public SWTBotShell(Shell shell, SelfDescribing description) throws WidgetNotFoundException {
		super(shell, description);
	}

	// @Override
	// protected Widget findWidget(int index) throws WidgetNotFoundException {
	// // could have used a matcher, but that would just slow down things
	// Shell[] shells = finder.getShells();
	// for (int i = 0; i < shells.length; i++) {
	// Shell shell = shells[i];
	// if (new SWTBotShell(shell).getText().equals(text))
	// return shell;
	// }
	// throw new WidgetNotFoundException("Cound not find shell matching text:" + text);
	// }

	/**
	 * Activates the shell.
	 * @return itself.
	 *
	 * @throws TimeoutException if the shell could not be activated
	 */
	public SWTBotShell activate() throws TimeoutException {
		new SWTBot().waitUntil(new DefaultCondition() {
			@Override
			public String getFailureMessage() {
				return "Timed out waiting for " + SWTUtils.toString(widget) + " to get activated"; //$NON-NLS-1$ //$NON-NLS-2$
			}

			@Override
			public boolean test() throws Exception {
				syncExec(new VoidResult() {
					@Override
					public void run() {
						widget.forceActive();
						widget.forceFocus();
					}
				});
				return isActive();
			}
		});
		notify(SWT.Activate);
		return this;
	}

	/**
	 * Closes the shell
	 *
	 * @throws TimeoutException if the shell does not close.
	 */
	public void close() throws TimeoutException {
		notify(SWT.Close);
		asyncExec(new VoidResult() {
			@Override
			public void run() {
				// TODO investigate bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=259895
				if (!widget.isDisposed())
					widget.close();
			}
		});
		new SWTBot().waitUntil(new DefaultCondition() {
			@Override
			public boolean test() throws Exception {
				return !isOpen();
			}

			@Override
			public String getFailureMessage() {
				return "Timed out waiting for " + SWTUtils.toString(widget) + " to close."; //$NON-NLS-1$ //$NON-NLS-2$
			}
		});
	}

	/**
	 * Checks if the shell is open.
	 *
	 * @return <code>true</code> if the shell is visible, <code>false</code> otherwise.
	 */
	public boolean isOpen() {
		return syncExec(new BoolResult() {
			@Override
			public Boolean run() {
				return !widget.isDisposed() && widget.isVisible();
			}
		});
	}

	/**
	 * Checks if the shell is active.
	 *
	 * @return <code>true</code> if the shell is active, <code>false</code> otherwise.
	 */
	@Override
	public boolean isActive() {
		return syncExec(new BoolResult() {
			@Override
			public Boolean run() {
				return display.getActiveShell() == widget;
			}
		});
	}

	/**
	 * Returns a SWTBot instance that matches the contents of this shell.
	 * 
	 * @return SWTBot
	 */
	public SWTBot bot() {
		return new SWTBot(widget);
	}

	/**
	 * Gets the menu bar of this shell.
	 *
	 * @return the menu bar.
	 * @since 2.4
	 */
	public SWTBotRootMenu menu() {
		WaitForObjectCondition<Menu> waitForMenu = Conditions.waitForMenuBar(this);
		bot().waitUntil(waitForMenu);
		return new SWTBotRootMenu(waitForMenu.get(0));
	}

	/**
	 * Sets the maximized state of the shell.
	 * <p>
	 * If the argument is <code>true</code> causes the shell to switch to the
	 * maximized state, and if the argument is <code>false</code> and the shell was
	 * previously maximized, causes the shell to switch back to either the minimized
	 * or normal state.
	 *
	 * @param maximize
	 *            the new maximized state
	 * @return itself
	 * @throws TimeoutException if the shell does not resize as per the expectation.
	 * @since 2.7
	 */
	public SWTBotShell maximize(final boolean maximize) throws TimeoutException {
		if (maximize == isMaximizedState()) {
			log.debug("{} is already in expected state, not resizing again.", this); //$NON-NLS-1$
			return this;
		}
		final Rectangle initialRectangle = getBounds();
		activate();
		syncExec(new VoidResult() {
			@Override
			public void run() {
				widget.setMaximized(maximize);
			}
		});

		new SWTBot().waitUntil(new DefaultCondition() {
			@Override
			public String getFailureMessage() {
				return "Timed out waiting for " + SWTUtils.toString(widget) + " to get maximized/unmaximized"; //$NON-NLS-1$ //$NON-NLS-2$
			}

			@Override
			public boolean test() throws Exception {
				/* Wait for maximized state and also for resize to complete. */
				if (maximize) {
					return isMaximizedState() && isResized(initialRectangle);
				} else {
					return !isMaximizedState() && isResized(initialRectangle);
				}
			}
		});
		return this;
	}

	private boolean isMaximizedState() {
		return syncExec(new BoolResult() {
			@Override
			public Boolean run() {
				return widget.getMaximized();
			}
		});
	}

	private boolean isResized(final Rectangle initialRectangle) {
		Rectangle newRectangle = getBounds();
		if ((initialRectangle.width != newRectangle.width) || (initialRectangle.height != newRectangle.height)) {
			return true;
		} else {
			return false;
		}
	}

}
