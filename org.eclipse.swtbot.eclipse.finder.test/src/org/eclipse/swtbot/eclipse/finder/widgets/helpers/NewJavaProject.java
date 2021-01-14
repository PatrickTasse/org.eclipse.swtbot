/*******************************************************************************
 * Copyright (c) 2008, 2021 Ketan Padegaonkar and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     Ketan Padegaonkar - initial API and implementation
 *     Patrick Tasse - Speed up SWTBot tests
 *     Lorenzo Bettini - https://bugs.eclipse.org/bugs/show_bug.cgi?id=479317
 *******************************************************************************/
package org.eclipse.swtbot.eclipse.finder.widgets.helpers;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.TimeoutException;

/**
 * Screen object to create a new java project
 * 
 * @author Ketan Padegaonkar &lt;KetanPadegaonkar [at] gmail [dot] com&gt;
 * @version $Id$
 */
public class NewJavaProject {

	private SWTWorkbenchBot	bot	= new SWTWorkbenchBot();

	public void createProject(String projectName) throws Exception {
		bot.shell().activate();

		if (!bot.activePerspective().getLabel().equals("Java")) {
			// In Mars "Open Perspective" is nested in "Perspective", so use this
			// method to recursively find "Open Perspective" and don't assume it is
			// nested in "Window" as it used to be in versions earlier than Mars
			SWTBotMenu perspectiveMenu = bot.menu("Open Perspective");
			SWTBotMenu javaPerspectiveMenu = perspectiveMenu.menu("Java");
			if (javaPerspectiveMenu.isVisible() && javaPerspectiveMenu.isEnabled()) { 
				javaPerspectiveMenu.click();
			}
		}
		bot.menu("File").menu("New").menu("Java Project").click();

		SWTBotShell shell = bot.shell("New Java Project");
		shell.activate();
		bot.textWithLabel("Project name:").setText(projectName);
		bot.button("Finish").click();
		try {
			bot.waitUntil(Conditions.shellIsActive("New module-info.java"), 1000);
			bot.shell("New module-info.java").close();
		} catch (TimeoutException e) {
			// do nothing
		}
		bot.waitUntil(Conditions.shellCloses(shell));

		bot.shell().activate();
	}

}
