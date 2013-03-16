package edu.uwm.JStateDrawer.Actions;

import java.awt.event.ActionEvent;
import java.util.Iterator;


import org.jhotdraw.app.Application;
import org.jhotdraw.app.View;
import org.jhotdraw.app.action.AbstractViewAction;
import org.jhotdraw.draw.DefaultDrawingEditor;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.util.ResourceBundleUtil;

import edu.umd.cs.findbugs.annotations.Nullable;
import edu.uwm.JStateDrawer.DrawerView;
import edu.uwm.JStateDrawer.figures.StateFigure;

@SuppressWarnings("serial")
public class AddNewActionAction extends AbstractViewAction {

	public final static String ID = "edit.newAction";
	
	public AddNewActionAction(Application app, @Nullable View view) {
		super(app, view);
		ResourceBundleUtil labels = ResourceBundleUtil.getBundle("edu.uwm.JStateDrawer.Actions.Labels");
        labels.configureAction(this, ID);
	}

	/**
	 * Adds a new default action to any selected state figure. 
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		DefaultDrawingEditor editor = (DefaultDrawingEditor) ((DrawerView)getActiveView()).getEditor();
		Iterator<Figure> figures = editor.getActiveView().getSelectedFigures().iterator();
		while(figures.hasNext())
		{
			StateFigure currentFig = (StateFigure)figures.next();
			currentFig.addActionTextFigure();
		}
	}

}
