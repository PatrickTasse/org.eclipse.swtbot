/*******************************************************************************
 * Copyright (c) 2010,2011 Ketan Padegaonkar and others.
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
package org.eclipse.swtbot.swt.finder.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swtbot.swt.finder.RunUIThreadRule;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.finders.ControlFinder;
import org.eclipse.swtbot.swt.finder.finders.Finder;
import org.eclipse.swtbot.swt.finder.finders.MenuFinder;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.hamcrest.Matcher;
import org.hamcrest.core.IsAnything;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.MethodRule;
import org.junit.runner.RunWith;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

/**
 *
 * @author Ketan Padegaonkar &lt;KetanPadegaonkar [at] gmail [dot] com&gt;
 * @version $Id$
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public abstract class AbstractSWTTest {

	static {
		java.lang.System.setProperty("org.eclipse.swt.internal.carbon.smallFonts", "");
	}

	protected final Logger	    log	= LoggerFactory.getLogger(getClass());
	protected SWTBot		    bot;
	protected ControlFinder	    controlFinder;
	protected Finder		    finder;
	protected MenuFinder	    menuFinder;
	protected Matcher<MenuItem> anyMenuItem = new IsAnything<MenuItem>();

	@Rule
	public MethodRule runner = new MethodRule(){
		@Override
		public Statement apply(Statement base, FrameworkMethod method, Object target) {
			return new RunUIThreadRule(target).apply(base, method, target);
		}
	};

	@Before
	public final void setupSWTBot() {
		bot = new SWTBot();
		controlFinder = new ControlFinder();
		menuFinder = new MenuFinder();
		finder = new Finder(controlFinder, menuFinder);
	}
}
