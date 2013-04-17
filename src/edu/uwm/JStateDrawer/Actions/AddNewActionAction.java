package edu.uwm.JStateDrawer.Actions;


import java.awt.event.ActionEvent;
import java.util.LinkedList;

import org.jhotdraw.app.View;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.action.AbstractSelectedAction;
import org.jhotdraw.util.ResourceBundleUtil;

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
		if(view == null)
		{
			return;
		}
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
