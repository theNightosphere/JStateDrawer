package edu.uwm.JStateDrawer.Actions;

import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;

import org.jhotdraw.app.Application;
import org.jhotdraw.app.View;
import org.jhotdraw.app.action.AbstractViewAction;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.connector.AbstractConnector;
import org.jhotdraw.draw.connector.ChopRectangleConnector;
import org.jhotdraw.draw.connector.LocatorConnector;
import org.jhotdraw.draw.locator.RelativeLocator;
import org.jhotdraw.util.ResourceBundleUtil;

import edu.umd.cs.findbugs.annotations.Nullable;
import edu.uwm.JStateDrawer.DrawerView;
import edu.uwm.JStateDrawer.figures.EndStateFigure;
import edu.uwm.JStateDrawer.figures.StartStateFigure;
import edu.uwm.JStateDrawer.figures.StateFigure;
import edu.uwm.JStateDrawer.figures.TransitionFigure;

public class AddNestedStartAction extends AbstractViewAction {

	public final static String ID = "edit.nestedStart";
	private final int OFFSET_FROM_PARENT = 50;
	
	
	public AddNestedStartAction(Application app, @Nullable View view) {
		super(app, view);
		ResourceBundleUtil labels = ResourceBundleUtil.getBundle("edu.uwm.JStateDrawer.Actions.Labels");
        labels.configureAction(this, ID);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		final DrawerView view = (DrawerView) getActiveView();
		DrawingView dView = view.getEditor().getActiveView();
        final LinkedList<Figure> figures = new LinkedList<Figure>(dView.getSelectedFigures());
        StartStateFigure nestedStart;
        for(Figure fig : figures)
        {
        	if(isNonNestedStateFigure(fig) && isNotStartOrEndState(fig))
        	{
        		nestedStart = ((StateFigure)fig).addNestedStartState();
        		nestedStart.getModel().setFigure(nestedStart);
        		nestedStart.setName(((StateFigure)fig).getName());
        		
        		Rectangle2D.Double parentBounds = ((StateFigure)fig).getBounds();
        		nestedStart.willChange();
        		Point2D.Double anchor = new Point2D.Double(parentBounds.x + OFFSET_FROM_PARENT, parentBounds.y + OFFSET_FROM_PARENT);
        		Point2D.Double lead = new Point2D.Double(anchor.x + 40, anchor.y + 40);
        		nestedStart.setBounds(anchor, lead);
        		nestedStart.getPresentationFigure().setBounds(anchor, lead);
        		nestedStart.changed();
        		
        		RelativeLocator rl = new RelativeLocator(0.5, 1);
        		LocatorConnector start = new LocatorConnector(fig, rl);
        		ChopRectangleConnector end = new ChopRectangleConnector(nestedStart);
        		TransitionFigure tf = new TransitionFigure();
        		tf.setStartFigure((StateFigure)fig);
        		tf.setEndFigure(nestedStart);
        		tf.setStartConnector(start);
        		tf.setEndConnector(end);
        		tf.setName("INTERNAL");
        		
        		dView.getDrawing().add(nestedStart);
        		dView.getDrawing().add(tf);
        	}
        }

	}
	
	/**
	 * Checks whether the figure {@code f} is a StateFigure, and whether or not it is nested. 
	 * @param f
	 * @return {@code true} if {@code f} is a {@link StateFigure} and is not a nested figure. 
	 */
	private boolean isNonNestedStateFigure(Figure f)
	{
		return (f instanceof StateFigure) && !((StateFigure) f).getModel().getIsInternalState();
	}

	/**
	 * Checks whether or not the figure {@code f} is a {@link StartStateFigure} or 
	 * {@link EndStateFigure}.
	 * @param f
	 * @return {@code true} if the figure {@code f} is neither a {@link StartStateFigure} 
	 * nor {@link EndStateFigure}
	 */
	private boolean isNotStartOrEndState(Figure f)
	{
		return !((f instanceof StartStateFigure) || (f instanceof EndStateFigure));
	}
}
