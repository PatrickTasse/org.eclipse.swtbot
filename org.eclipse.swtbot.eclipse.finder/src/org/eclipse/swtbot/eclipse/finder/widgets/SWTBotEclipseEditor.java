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
 *     Ketan Patel - https://bugs.eclipse.org/bugs/show_bug.cgi?id=259837
 *     Ralf Ebert www.ralfebert.de - (bug 271630) SWTBot Improved RCP / Workbench support
 *     Ingo Mohr - Bug 416859
 *     Lorenzo Bettini - https://bugs.eclipse.org/bugs/show_bug.cgi?id=435390
 *     Patrick Tasse - Improve SWTBot menu API and implementation (Bug 479091)
 *     Aparna Argade - API to consider Tab width for column (Bug 536131)
 *******************************************************************************/
package org.eclipse.swtbot.eclipse.finder.widgets;

import static org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable.syncExec;
import static org.eclipse.swtbot.swt.finder.matchers.WidgetMatcherFactory.widgetOfType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.Bullet;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.exceptions.QuickFixNotFoundException;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.keyboard.Keyboard;
import org.eclipse.swtbot.swt.finder.keyboard.Keystrokes;
import org.eclipse.swtbot.swt.finder.matchers.AbstractMatcher;
import org.eclipse.swtbot.swt.finder.results.Result;
import org.eclipse.swtbot.swt.finder.results.VoidResult;
import org.eclipse.swtbot.swt.finder.results.WidgetResult;
import org.eclipse.swtbot.swt.finder.utils.Position;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.waits.WaitForObjectCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotStyledText;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.eclipse.swtbot.swt.finder.widgets.TimeoutException;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.texteditor.ITextEditor;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.SelfDescribing;
import org.hamcrest.core.IsAnything;

/**
 * This represents an eclipse editor item.
 *
 * @author Ketan Padegaonkar &lt;KetanPadegaonkar [at] gmail [dot] com&gt;
 * @author Ralf Ebert www.ralfebert.de (bug 271630)
 * @version $Id$
 */
public class SWTBotEclipseEditor extends SWTBotEditor {

	private final SWTBotStyledText	   styledText;
	private final Matcher<SWTBotTable> anyTable = new IsAnything<SWTBotTable>();

	/**
	 * Constructs an instance of the given object.
	 *
	 * @param editorReference the editor reference.
	 * @param bot the instance of {@link SWTWorkbenchBot} which will be used to drive operations on behalf of this
	 *            object.
	 * @throws WidgetNotFoundException if the widget is <code>null</code> or widget has been disposed.
	 * @since 2.0
	 */
	public SWTBotEclipseEditor(IEditorReference editorReference, SWTWorkbenchBot bot) throws WidgetNotFoundException {
		super(editorReference, bot);
		this.styledText = new SWTBotStyledText(widget());
	}

	/**
	 * Constructs an instance for the given editorReference.
	 *
	 * @param editorReference the part reference.
	 * @param bot the helper bot.
	 * @param description the description of the editor part.
	 */
	public SWTBotEclipseEditor(IEditorReference editorReference, SWTWorkbenchBot bot, SelfDescribing description) {
		super(editorReference, bot, description);
		this.styledText = new SWTBotStyledText(widget());
	}

	private StyledText widget() {
		List<? extends Widget> findWidgets = findWidgets(widgetOfType(StyledText.class));
		return (StyledText) findWidgets.get(findWidgets.size() - 1);
	}

	/**
	 * @return the styledText
	 */
	public SWTBotStyledText getStyledText() {
		return styledText;
	}

	/**
	 * Applys a quick fix item at the given index.
	 *
	 * @param quickFixIndex the index of the quickfix item to apply.
	 * @throws WidgetNotFoundException if the quickfix could not be found.
	 */
	public void quickfix(int quickFixIndex) {
		WaitForObjectCondition<SWTBotTable> quickFixTableCondition = quickFixAppears(anyTable);
		waitUntil(quickFixTableCondition);
		SWTBotTable quickFixTable = quickFixTableCondition.get(0);
		selectProposal(quickFixTable, quickFixIndex);
	}

	/**
	 * Applys a quick fix item with the given name.
	 *
	 * @param quickFixName the name of the quick fix to apply.
	 */
	public void quickfix(String quickFixName) {
		WaitForObjectCondition<SWTBotTable> quickFixTable = quickFixAppears(tableWithRow(quickFixName));
		waitUntil(quickFixTable);
		selectProposal(quickFixTable.get(0), quickFixName);
	}

	/**
	 * Finds all the quickfixes in the quickfix list.
	 *
	 * @return the list of all available quickfixes.
	 * @since 1.2
	 */
	public List<String> getQuickFixes() {
		WaitForObjectCondition<SWTBotTable> quickFixTableCondition = quickFixAppears(anyTable);
		waitUntil(quickFixTableCondition);
		SWTBotTable quickFixTable = quickFixTableCondition.get(0);
		List<String> proposals = getRows(quickFixTable);
		makeProposalsDisappear(quickFixTable);
		return proposals;
	}

	/**
	 * Gets the quick fix item count.
	 *
	 * @return the number of quickfix items in the quickfix proposals.
	 * @since 1.2
	 */
	public int getQuickfixListItemCount() {
		WaitForObjectCondition<SWTBotTable> quickFixTableCondition = quickFixAppears(anyTable);
		waitUntil(quickFixTableCondition);
		SWTBotTable quickFixTable = quickFixTableCondition.get(0);
		return quickFixTable.rowCount();
	}

	/**
	 * Attempts to apply the quick fix.
	 * <p>
	 * FIXME: this needs a lot of optimization.
	 * </p>
	 *
	 * @param proposalTable the table containing the quickfix.
	 * @param proposalText the name of the quickfix to apply.
	 */
	private void selectProposal(SWTBotTable proposalTable, String proposalText) {
		log.debug("Trying to select proposal {}", proposalText); //$NON-NLS-1$
		if (proposalTable.containsItem(proposalText)) {
			selectProposal(proposalTable, proposalTable.indexOf(proposalText));
			return;
		}
		throw new QuickFixNotFoundException("Quickfix options not found. Giving up."); //$NON-NLS-1$
	}

	/**
	 * Applies the specified quickfix.
	 *
	 * @param proposalTable the table containing the quickfix.
	 * @param proposalIndex the index of the quickfix.
	 */
	private void selectProposal(final SWTBotTable proposalTable, final int proposalIndex) {
		log.debug("Trying to select proposal with index {}", proposalIndex); //$NON-NLS-1$
		UIThreadRunnable.asyncExec(new VoidResult() {
			@Override
			public void run() {
				Table table = proposalTable.widget;
				log.debug("Selecting row [{}] {} in {}", proposalIndex, table.getItem(proposalIndex).getText(), //$NON-NLS-1$
						table);
				table.setSelection(proposalIndex);
				Event event = new Event();
				event.type = SWT.Selection;
				event.widget = table;
				event.item = table.getItem(proposalIndex);
				table.notifyListeners(SWT.Selection, event);
				table.notifyListeners(SWT.DefaultSelection, event);
			}
		});
	}

	/**
	 * Gets the quick fix table.
	 *
	 * @return the table containing the quickfix.
	 */
	private SWTBotTable getProposalTable() {
		log.debug("Finding table containing proposals.");
		try {
			Table table = bot.widget(widgetOfType(Table.class), activatePopupShell().widget);
			SWTBotTable swtBotTable = new SWTBotTable(table);
			log.debug("Found table containing proposals -- {}", getRows(swtBotTable));
			return swtBotTable;
		} catch (Exception e) {
			throw new QuickFixNotFoundException("Proposal options not found. Giving up.", e); //$NON-NLS-1$
		}
	}

	/**
	 * Gets the auto completion proposal matching the given text..
	 *
	 * @param insertText the proposal text to type before auto completing
	 * @return the list of proposals
	 * @throws TimeoutException if the autocomplete shell did not close in time.
	 * @since 1.2
	 */
	@SuppressWarnings("all")
	public List<String> getAutoCompleteProposals(String insertText) {
		typeText(insertText);
		WaitForObjectCondition<SWTBotTable> autoCompleteAppears = autoCompleteAppears(tableWithRowIgnoringCase(insertText));
		waitUntil(autoCompleteAppears);
		final SWTBotTable autoCompleteTable = autoCompleteAppears.get(0);
		List<String> proposals = getRows(autoCompleteTable);
		makeProposalsDisappear(autoCompleteTable);
		return proposals;
	}

	/**
	 * Auto completes the given proposal.
	 *
	 * @param insertText the text to be inserted before activating the auto-complete.
	 * @param proposalText the auto-completion proposal to select from the list.
	 */
	public void autoCompleteProposal(String insertText, String proposalText) {
		typeText(insertText);
		WaitForObjectCondition<SWTBotTable> autoCompleteTableAppears = autoCompleteAppears(tableWithRow(proposalText));
		waitUntil(autoCompleteTableAppears);
		final SWTBotTable autoCompleteTable = autoCompleteTableAppears.get(0);
		selectProposal(autoCompleteTable, proposalText);
	}

	/**
	 * Gets the context menu item matching the given text in the editor.
	 *
	 * @param text the text on the menu item that is to be found.
	 * @return the menu item that has the given text.
	 * @throws WidgetNotFoundException if the menu with the specified text could not be found.
	 */
	public SWTBotMenu contextMenu(String text) throws WidgetNotFoundException {
		return styledText.contextMenu().menu(text);
	}

	/**
	 * Gets the current position of the cursor. The returned position will contain a 0-based line and column.
	 *
	 * @return the position of the cursor.
	 * @see SWTBotStyledText#cursorPosition()
	 * @see SWTBotEclipseEditor#cursorPosition(boolean)
	 */
	public Position cursorPosition() {
		return styledText.cursorPosition();
	}

	/**
	 * Gets the current position of the cursor. The returned position will contain a 0-based line and column.
	 *
	 * @param withTabWidth <code>true</code> if column in the returned position should consider tab width preference;
	 *                     <code>false</code> if column in the returned position should count tab as 1.
	 * @return the position of the cursor.
	 * @see SWTBotStyledText#cursorPosition(boolean)
	 * @since 2.8
	 */
	public Position cursorPosition(boolean withTabWidth) {
		return styledText.cursorPosition(withTabWidth);
	}

	/**
	 * Gets if the object's widget is enabled.
	 *
	 * @return <code>true</code> if the widget is enabled.
	 * @see org.eclipse.swtbot.swt.finder.widgets.AbstractSWTBot#isEnabled()
	 */
	public boolean isEnabled() {
		return styledText.isEnabled();
	}

	/**
	 * Gets the current selection.
	 *
	 * @return The selected string.
	 */
	public String getSelection() {
		return styledText.getSelection();
	}

	/**
	 * Gets the style text.
	 *
	 * @param line the line number, 0 based.
	 * @param column the column number, 0 based. Here Tab needs to be counted as 1.
	 * @return the {@link StyleRange} at the specified location
	 * @see SWTBotEclipseEditor#getStyle(int, int, boolean)
	 * @see SWTBotStyledText#getStyle(int, int)
	 */
	public StyleRange getStyle(int line, int column) {
		return styledText.getStyle(line, column);
	}

	/**
	 * Gets the style text.
	 *
	 * @param line the line number, 0 based.
	 * @param column the column number, 0 based.
	 * @param withTabWidth <code>true</code> if column is specified considering tab width preference;
	 *                     <code>false</code> if column is specified counting tab as 1.
	 * @return the {@link StyleRange} at the specified location
	 * @see SWTBotStyledText#getStyle(int, int, boolean)
	 * @since 2.8
	 */
	public StyleRange getStyle(int line, int column, boolean withTabWidth) {
		return styledText.getStyle(line, column, withTabWidth);
	}

	/**
	 * Gets the text of this object's widget.
	 *
	 * @return the text on the styledtext.
	 * @see org.eclipse.swtbot.swt.finder.widgets.AbstractSWTBot#getText()
	 */
	public String getText() {
		return styledText.getText();
	}

	/**
	 * @param line the line number, 0 based.
	 * @param column the column number, 0 based. Here Tab needs to be counted as 1.
	 * @param text the text to be typed at the specified location
	 * @see SWTBotEclipseEditor#typeText(int, int, String, boolean)
	 * @see SWTBotStyledText#typeText(int, int, java.lang.String)
	 * @since 1.0
	 */
	public void typeText(int line, int column, String text) {
		styledText.typeText(line, column, text);
	}

	/**
	 * @param line the line number, 0 based.
	 * @param column the column number, 0 based.
	 * @param text the text to be typed at the specified location
	 * @param withTabWidth <code>true</code> if column is specified considering tab width preference;
	 *                     <code>false</code> if column is specified counting tab as 1.
	 * @see SWTBotStyledText#typeText(int, int, String, boolean)
	 * @since 2.8
	 */
	public void typeText(int line, int column, String text, boolean withTabWidth) {
		styledText.typeText(line, column, text, withTabWidth);
	}

	/**
	 * @param text the text to be typed at the location of the caret. *
	 * @see SWTBotStyledText#typeText(java.lang.String)
	 * @since 1.0
	 */
	public void typeText(String text) {
		styledText.typeText(text);
	}

	/**
	 * @param line the line number, 0 based.
	 * @param column the column number, 0 based. Here Tab needs to be counted as 1.
	 * @param text the text to be inserted at the specified location
	 * @see SWTBotEclipseEditor#insertText(int, int, String, boolean)
	 * @see SWTBotStyledText#insertText(int, int, java.lang.String)
	 */
	public void insertText(int line, int column, String text) {
		styledText.insertText(line, column, text);
	}

	/**
	 * @param line the line number, 0 based.
	 * @param column the column number, 0 based.
	 * @param text the text to be inserted at the specified location.
	 * @param withTabWidth <code>true</code> if column is specified considering tab width preference;
	 *                     <code>false</code> if column is specified counting tab as 1.
	 * @see SWTBotStyledText#insertText(int, int, String, boolean)
	 * @since 2.8
	 */
	public void insertText(int line, int column, String text, boolean withTabWidth) {
		styledText.insertText(line, column, text, withTabWidth);
	}

	/**
	 * @param text the text to be inserted at the location of the caret.
	 * @see SWTBotStyledText#insertText(java.lang.String)
	 */
	public void insertText(String text) {
		styledText.insertText(text);
	}

	/**
	 * @param text the text to be typed at the location of the caret.
	 * @param interval the interval between consecutive key strokes.
	 * @see SWTBotStyledText#typeText(java.lang.String, int)
	 * @since 1.0
	 */
	public void typeText(String text, int interval) {
		styledText.typeText(text, interval);
	}

	/**
	 * Sets the caret at the specified location.
	 *
	 * @param position the position of the caret.
	 */
	public void navigateTo(Position position) {
		styledText.navigateTo(position);
	}

	/**
	 * @param line the line number, 0 based.
	 * @param column the column number, 0 based. Here Tab needs to be counted as 1.
	 * @see SWTBotEclipseEditor#navigateTo(int, int, boolean)
	 * @see SWTBotStyledText#navigateTo(int, int)
	 */
	public void navigateTo(int line, int column) {
		styledText.navigateTo(line, column);
	}

	/**
	 * @param line the line number, 0 based.
	 * @param column the column number, 0 based.
	 * @param withTabWidth <code>true</code> if column is specified considering tab width preference;
	 *                     <code>false</code> if column is specified counting tab as 1.
	 * @see SWTBotStyledText#navigateTo(int, int, boolean)
	 * @since 2.8
	 */
	public void navigateTo(int line, int column, boolean withTabWidth) {
		styledText.navigateTo(line, column, withTabWidth);
	}

	/**
	 * Presses the shortcut specified by the given keys.
	 *
	 * @param modificationKeys the combination of {@link SWT#ALT} | {@link SWT#CTRL} | {@link SWT#SHIFT} |
	 *            {@link SWT#COMMAND}.
	 * @param c the character.
	 * @see Keyboard#pressShortcut(KeyStroke...)
	 * @see Keystrokes#toKeys(int, char)
	 */
	public void pressShortcut(int modificationKeys, char c) {
		styledText.pressShortcut(modificationKeys, c);
	}

	/**
	 * Presses the shortcut specified by the given keys.
	 *
	 * @param modificationKeys the combination of {@link SWT#ALT} | {@link SWT#CTRL} | {@link SWT#SHIFT} |
	 *            {@link SWT#COMMAND}.
	 * @param keyCode the keyCode, these may be special keys like F1-F12, or navigation keys like HOME, PAGE_UP
	 * @param c the character
	 * @see Keystrokes#toKeys(int, char)
	 */
	public void pressShortcut(int modificationKeys, int keyCode, char c) {
		styledText.pressShortcut(modificationKeys, keyCode, c);
	}

	/**
	 * Presses the shortcut specified by the given keys.
	 *
	 * @param keys the keys to press
	 * @see Keyboard#pressShortcut(KeyStroke...)
	 * @see Keystrokes
	 */
	public void pressShortcut(KeyStroke... keys) {
		styledText.pressShortcut(keys);
	}

	/**
	 * @param line the line number, 0 based.
	 * @param column the column number, 0 based. Here Tab needs to be counted as 1.
	 * @param length the length of the selection.
	 * @see SWTBotEclipseEditor#selectRange(int, int, int, boolean)
	 * @see SWTBotStyledText#selectRange(int, int, int)
	 */
	public void selectRange(int line, int column, int length) {
		styledText.selectRange(line, column, length);
	}

	/**
	 * @param line the line number, 0 based.
	 * @param column the column number, 0 based.
	 * @param length the length of the selection.
	 * @param withTabWidth <code>true</code> if column is specified considering tab width preference;
	 *                     <code>false</code> if column is specified counting tab as 1.
	 * @see SWTBotStyledText#selectRange(int, int, int, boolean)
	 * @since 2.8
	 */
	public void selectRange(int line, int column, int length, boolean withTabWidth) {
		styledText.selectRange(line, column, length, withTabWidth);
	}

	/**
	 * @param line the line number to select, 0 based.
	 * @see SWTBotStyledText#selectLine(int)
	 * @since 1.1
	 */
	public void selectLine(int line) {
		styledText.selectLine(line);
	}

	/**
	 * Selects the text on the current line.
	 *
	 * @see SWTBotStyledText#selectCurrentLine()
	 * @since 1.1
	 */
	public void selectCurrentLine() {
		styledText.selectCurrentLine();
	}

	/**
	 * @see SWTBotStyledText#setFocus()
	 */
	@Override
	public void setFocus() {
		styledText.setFocus();
	}

	/**
	 * @param text the text to set.
	 * @see SWTBotStyledText#setText(java.lang.String)
	 */
	public void setText(String text) {
		styledText.setText(text);
	}

	/**
	 * @return the bullet on the current line.
	 * @see SWTBotStyledText#getBulletOnCurrentLine()
	 */
	public Bullet getBulletOnCurrentLine() {
		return styledText.getBulletOnCurrentLine();
	}

	/**
	 * @param line the line number, 0 based.
	 * @return the bullet on the given line.
	 * @see SWTBotStyledText#getBulletOnLine(int)
	 */
	public Bullet getBulletOnLine(int line) {
		return styledText.getBulletOnLine(line);
	}

	/**
	 * @param line the line number, 0 based.
	 * @param column the column number, 0 based. Here Tab needs to be counted as 1.
	 * @param length the length.
	 * @return the styles in the specified range.
	 * @see SWTBotEclipseEditor#getStyles(int, int, int, boolean)
	 * @see SWTBotStyledText#getStyles(int, int, int)
	 */
	public StyleRange[] getStyles(int line, int column, int length) {
		return styledText.getStyles(line, column, length);
	}

	/**
	 * @param line the line number, 0 based.
	 * @param column the column number, 0 based.
	 * @param length the length.
	 * @param withTabWidth <code>true</code> if column is specified considering tab width preference;
	 *                     <code>false</code> if column is specified counting tab as 1.
	 * @return the styles in the specified range.
	 * @see SWTBotStyledText#getStyles(int, int, int, boolean)
	 * @since 2.8
	 */
	public StyleRange[] getStyles(int line, int column, int length, boolean withTabWidth) {
		return styledText.getStyles(line, column, length, withTabWidth);
	}

	/**
	 * @return the text on the current line, without the line delimiters.
	 * @see SWTBotStyledText#getTextOnCurrentLine()
	 */
	public String getTextOnCurrentLine() {
		return styledText.getTextOnCurrentLine();
	}

	/**
	 * Get the text on line number given as parameter.
	 * Note that in eclipse editors, folding has no incidence on line numbers.
	 * @param line the line number, 0 based.
	 * @return the text on the given line number, without the line delimiters.
	 * @see SWTBotStyledText#getTextOnLine(int)
	 */
	public String getTextOnLine(int line) {
		return styledText.getTextOnLine(line);
	}

	/**
	 * @return <code>true</code> if the styledText has a bullet on the given line, <code>false</code> otherwise.
	 * @see SWTBotStyledText#hasBulletOnCurrentLine()
	 */
	public boolean hasBulletOnCurrentLine() {
		return styledText.hasBulletOnCurrentLine();
	}

	/**
	 * @param line the line number, 0 based.
	 * @return <code>true</code> if the styledText has a bullet on the given line, <code>false</code> otherwise.
	 * @see SWTBotStyledText#hasBulletOnLine(int)
	 */
	public boolean hasBulletOnLine(int line) {
		return styledText.hasBulletOnLine(line);
	}

	/**
	 * Gets the background color of the widget.
	 *
	 * @return the background color on the widget, or <code>null</code> if the widget is not an instance of
	 *         {@link Control}.
	 * @since 1.3
	 * @see org.eclipse.swtbot.swt.finder.widgets.AbstractSWTBot#backgroundColor()
	 */
	public Color backgroundColor() {
		return styledText.backgroundColor();
	}

	/**
	 * Gets the foreground color of the widget.
	 *
	 * @return the foreground color on the widget, or <code>null</code> if the widget is not an instance of
	 *         {@link Control}.
	 * @since 1.3
	 * @see org.eclipse.swtbot.swt.finder.widgets.AbstractSWTBot#foregroundColor()
	 */
	public Color foregroundColor() {
		return styledText.foregroundColor();
	}

	/**
	 * Gets the color of the background on the specified line.
	 *
	 * @param line the line number, 0 based.
	 * @return the RGB of the line background color of the specified line.
	 * @since 1.3
	 * @see SWTBotStyledText#getLineBackground(int)
	 */
	public RGB getLineBackground(int line) {
		return styledText.getLineBackground(line);
	}

	/**
	 * Gets the number of lines in the {@link StyledText}.
	 *
	 * @return the number of lines in the {@link StyledText}.
	 */
	public int getLineCount(){
		return styledText.getLineCount();
	}

	/**
	 * Gets all the lines in the editor.
	 *
	 * @return the lines in the editor.
	 */
	public List<String> getLines() {
		return styledText.getLines();
	}

	/**
	 * Gets the tooltip of this object's widget.
	 *
	 * @return the tooltip on the widget.
	 * @see org.eclipse.swtbot.swt.finder.widgets.AbstractSWTBot#getToolTipText()
	 * @since 1.3
	 */
	public String getToolTipText() {
		return styledText.getToolTipText();
	}

	private void makeProposalsDisappear(final SWTBotTable table) {
		// clear away all content assists for next retry.
		log.debug("Making proposals disappear.");
		SWTBotShell proposalShell = syncExec(new Result<SWTBotShell>() {
			@Override
			public SWTBotShell run() {
				if (table != null && !table.widget.isDisposed()) {
					Shell shell = table.widget.getShell();
					if (!shell.isDisposed() && shell.isVisible()) {
						return new SWTBotShell(shell);
					}
				}
				return null;
			}
		});
		syncExec(new VoidResult() {
			@Override
			public void run() {
				Event event = new Event();
				event.keyCode = SWT.ESC;
				styledText.widget.notifyListeners(SWT.KeyDown, event);
			}
		});
		syncExec(new VoidResult() {
			@Override
			public void run() {
				Event event = new Event();
				event.keyCode = SWT.ESC;
				styledText.widget.notifyListeners(SWT.KeyUp, event);
			}
		});
		if (proposalShell != null) {
			bot.waitUntil(Conditions.shellCloses(proposalShell));
		}
	}

	private Matcher<SWTBotTable> tableWithRow(final String itemText) {
		return new AbstractMatcher<SWTBotTable>() {

			@Override
			protected boolean doMatch(Object item) {
				return ((SWTBotTable) item).containsItem(itemText);
			}

			@Override
			public void describeTo(Description description) {
				description.appendText("table with item (").appendText(itemText).appendText(")");
			}
		};
	}

	private Matcher<SWTBotTable> tableWithRowIgnoringCase(final String itemText) {
		final String lowerCaseText = itemText.toLowerCase();
		return new AbstractMatcher<SWTBotTable>() {

			@Override
			protected boolean doMatch(Object item) {
				List<String> rows = getRows((SWTBotTable) item);
				for (String row : rows) {
					if (row.toLowerCase().startsWith(lowerCaseText)) {
						return true;
					}
				}
				return false;
			}

			@Override
			public void describeTo(Description description) {
				description.appendText("table with item (").appendText(itemText).appendText(")");
			}
		};
	}

	private WaitForObjectCondition<SWTBotTable> quickFixAppears(Matcher<SWTBotTable> tableMatcher) {
		return new WaitForObjectCondition<SWTBotTable>(tableMatcher) {
			@Override
			protected List<SWTBotTable> findMatches() {
				try {
					activateQuickFixShell();
					SWTBotTable quickFixTable = getProposalTable();
					if (matcher.matches(quickFixTable)) {
						return Arrays.asList(quickFixTable);
					} else {
						makeProposalsDisappear(quickFixTable);
					}
				} catch (QuickFixNotFoundException e) {
					makeProposalsDisappear(null);
				}
				return null;
			}

			@Override
			public String getFailureMessage() {
				return "Could not find auto complete proposal using matcher " + matcher;
			}

		};
	}

	/**
	 * This activates the proposal shell.
	 *
	 * @return The shell.
	 */
	private SWTBotShell activatePopupShell() {
		log.debug("Activating proposal shell."); //$NON-NLS-1$
		try {
			Shell mainWindow = syncExec(new WidgetResult<Shell>() {
				@Override
				public Shell run() {
					return styledText.widget.getShell();
				}
			});

			final List<Shell> shells = bot.shells("", mainWindow);
			Shell widgetShell = syncExec(new WidgetResult<Shell>() {
				@Override
				public Shell run() {
					for(int j=0; j<shells.size(); j++) {
						Shell s = shells.get(j);
						Control[] children = s.getChildren();
						for (int i = 0; i < children.length; i++) {
							//Select shell which has content assist table
							if(children[i] instanceof Table) {
								return s;
							}
						}
					}
					return shells.get(0);
				}
			});
			SWTBotShell shell = new SWTBotShell(widgetShell);
			shell.activate();
			log.debug("Activated proposal shell."); //$NON-NLS-1$
			return shell;
		} catch (Exception e) {
			throw new QuickFixNotFoundException("Proposal shell not found. Giving up.", e); //$NON-NLS-1$
		}
	}

	private WaitForObjectCondition<SWTBotTable> autoCompleteAppears(Matcher<SWTBotTable> tableMatcher) {
		return new WaitForObjectCondition<SWTBotTable>(tableMatcher) {
			@Override
			protected List<SWTBotTable> findMatches() {
				try {
					activateAutoCompleteShell();
					SWTBotTable autoCompleteTable = getProposalTable();
					if (matcher.matches(autoCompleteTable)) {
						SWTBotEclipseEditor.this.log.debug("matched table, returning");
						return Arrays.asList(autoCompleteTable);
					} else {
						makeProposalsDisappear(autoCompleteTable);
					}
				} catch (QuickFixNotFoundException e) {
					makeProposalsDisappear(null);
				}
				return null;
			}

			@Override
			public String getFailureMessage() {
				return "Could not find auto complete proposal using matcher " + matcher;
			}

		};
	}

	private void activateAutoCompleteShell() {
		invokeAction("ContentAssistProposal");
	}

	private void invokeAction(final String actionId) {
		final IAction action = ((ITextEditor) partReference.getEditor(false)).getAction(actionId);
		syncExec(new VoidResult() {
			@Override
			public void run() {
				log.debug("Activating action with id {}", actionId);
				action.run();
			}
		});
	}

	private List<String> getRows(SWTBotTable table) {
		int rowCount = table.rowCount();
		List<String> result = new ArrayList<String>();
		for (int i = 0; i < rowCount; i++)
			result.add(table.cell(i, 0));
		return result;
	}

	private void activateQuickFixShell() {
		invokeAction("QuickAssist");
	}

	private void waitUntil(WaitForObjectCondition<SWTBotTable> table) {
		bot.waitUntil(table, SWTBotPreferences.TIMEOUT);
	}

	/**
	 * Click on the editor at the given line and column.
	 *
	 * @param line the line number, 0 based.
	 * @param column the column number considering tab spaces, 0 based.
	 * @see SWTBotStyledText#click(int, int)
	 * @since 2.8
	 */
	public void click(final int line, final int column) {
		styledText.click(line, column);
	}

	/**
	 * Double-clicks on the editor at the given line and column.
	 *
	 * @param line the line number, 0 based.
	 * @param column the column number considering tab spaces, 0 based.
	 * @see SWTBotStyledText#doubleClick(int, int)
	 * @since 2.8
	 */
	public void doubleClick(final int line, final int column) {
		styledText.doubleClick(line, column);
	}

	/**
	 * Clicks on the editor at the given line and column with modifier key.
	 *
	 * @param line the line number, 0 based.
	 * @param column the column number considering tab spaces, 0 based.
	 * @param modifierKey modifier key or zero.
	 * @see SWTBotStyledText#click(int, int, int)
	 * @since 2.8
	 */
	public void click(final int line, final int column, final int modifierKey) {
		styledText.click(line, column, modifierKey);
	}

}
