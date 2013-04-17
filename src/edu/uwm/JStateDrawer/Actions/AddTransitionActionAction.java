package edu.uwm.JStateDrawer.Actions;

import java.awt.event.ActionEvent;
import java.util.LinkedList;

import org.jhotdraw.app.Application;
import org.jhotdraw.app.View;
import org.jhotdraw.app.action.AbstractViewAction;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.util.ResourceBundleUtil;

import edu.umd.cs.findbugs.annotations.Nullable;
import edu.uwm.JStateDrawer.DrawerView;
import edu.uwm.JStateDrawer.figures.StateFigure;
import edu.uwm.JStateDrawer.figures.TransitionFigure;

@SuppressWarnings("serial")
public class AddTransitionActionAction extends AbstractViewAction {

	public static final String ID = "transition.addAction";
	
	public AddTransitionActionAction(Application app, @Nullable View view) {
		super(app, view);
		ResourceBundleUtil labels = ResourceBundleUtil.getBundle("edu.uwm.JStateDrawer.Actions.Labels");
        labels.configureAction(this, ID);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		final DrawerView view = (DrawerView) getActiveView();
		DrawingView dView = view.getEditor().getActiveView();
        final LinkedList<Figure> figures = new LinkedList<Figure>(dView.getSelectedFigures());
        for(Figure fig : figures)
        {
        	if(fig instanceof TransitionFigure)
        	{
        		((TransitionFigure)fig).addDefaultAction();
        	}
        }

	}

}
