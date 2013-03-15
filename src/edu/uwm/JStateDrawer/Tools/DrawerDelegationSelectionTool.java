package edu.uwm.JStateDrawer.Tools;

import java.awt.Component;
import java.awt.Point;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;

import org.jhotdraw.app.action.ActionUtil;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.tool.DelegationSelectionTool;

import edu.umd.cs.findbugs.annotations.Nullable;

public class DrawerDelegationSelectionTool extends DelegationSelectionTool {

	public DrawerDelegationSelectionTool() {
		super();
	}

	public DrawerDelegationSelectionTool(Collection<Action> drawingActions,
			Collection<Action> selectionActions) {
		super(drawingActions, selectionActions);
	}

	
	protected void showPopupMenu(@Nullable Figure figure, Point p, Component c) {
        if (DEBUG) {
            System.out.println("DelegationSelectionTool.showPopupMenu " + figure);
        }
        JPopupMenu menu = new JPopupMenu();
        popupMenu = menu;
        JMenu submenu = null;
        String submenuName = null;
        LinkedList<Action> popupActions = new LinkedList<Action>();
        if (figure != null) {
            LinkedList<Action> figureActions = new LinkedList<Action>(
                    figure.getActions(viewToDrawing(p)));
            if (popupActions.size() != 0 && figureActions.size() != 0) {
                popupActions.add(null);
            }
            popupActions.addAll(figureActions);
            if (popupActions.size() != 0 && selectionActions.size() != 0) {
                popupActions.add(null);
            }
            popupActions.addAll(selectionActions);
        }
        if (popupActions.size() != 0 && drawingActions.size() != 0) {
            popupActions.add(null);
        }
        popupActions.addAll(drawingActions);

        HashMap<Object, ButtonGroup> buttonGroups = new HashMap<Object, ButtonGroup>();
        for (Action a : popupActions) {
            if (a != null && a.getValue(ActionUtil.SUBMENU_KEY) != null) {
                if (submenuName == null || !submenuName.equals(a.getValue(ActionUtil.SUBMENU_KEY))) {
                    submenuName = (String) a.getValue(ActionUtil.SUBMENU_KEY);
                    submenu = new JMenu(submenuName);
                    menu.add(submenu);
                }
            } else {
                submenuName = null;
                submenu = null;
            }
            if (a == null) {
                if (submenu != null) {
                    submenu.addSeparator();
                } else {
                    menu.addSeparator();
                }
            } else {
                AbstractButton button;

                if (a.getValue(ActionUtil.BUTTON_GROUP_KEY) != null) {
                    ButtonGroup bg = buttonGroups.get(a.getValue(ActionUtil.BUTTON_GROUP_KEY));
                    if (bg == null) {
                        bg = new ButtonGroup();
                        buttonGroups.put(a.getValue(ActionUtil.BUTTON_GROUP_KEY), bg);
                    }
                    button = new JRadioButtonMenuItem(a);
                    bg.add(button);
                    button.setSelected(a.getValue(ActionUtil.SELECTED_KEY) == Boolean.TRUE);
                } else if (a.getValue(ActionUtil.SELECTED_KEY) != null) {
                    button = new JCheckBoxMenuItem(a);
                    button.setSelected(a.getValue(ActionUtil.SELECTED_KEY) == Boolean.TRUE);
                } else {
                    button = new JMenuItem(a);
                }

                if (submenu != null) {
                    submenu.add(button);
                } else {
                    menu.add(button);
                }
            }
        }
        menu.show(c, p.x, p.y);
    }
}
