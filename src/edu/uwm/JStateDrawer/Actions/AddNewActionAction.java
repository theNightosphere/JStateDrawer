package edu.uwm.JStateDrawer.Actions;

import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.JComponent;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;


import org.jhotdraw.app.Application;
import org.jhotdraw.app.View;
import org.jhotdraw.app.action.AbstractViewAction;
import org.jhotdraw.draw.DefaultDrawingEditor;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.action.AbstractSelectedAction;
import org.jhotdraw.draw.action.BringToFrontAction;
import org.jhotdraw.draw.action.SendToBackAction;
import org.jhotdraw.util.ResourceBundleUtil;

import edu.umd.cs.findbugs.annotations.Nullable;
import edu.uwm.JStateDrawer.DrawerView;
import edu.uwm.JStateDrawer.figures.StateFigure;

@SuppressWarnings("serial")
public class AddNewActionAction extends AbstractSelectedAction {

	public final static String ID = "edit.newAction";
	
	public AddNewActionAction(DrawingEditor editor) {
		super(editor);
		ResourceBundleUtil labels = ResourceBundleUtil.getBundle("edu.uwm.JStateDrawer.Actions.Labels");
        labels.configureAction(this, ID);
        updateEnabledState();
	}

	/**
	 * Adds a new default action to any selected state figure. 
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		final DrawingView view = getView();
        final LinkedList<Figure> figures = new LinkedList<Figure>(view.getSelectedFigures());
        for(Figure fig : figures)
        {
        	if(fig instanceof StateFigure)
        	{
        		((StateFigure)fig).addActionTextFigure();
        	}
        }
        
	}

}
