package edu.uwm.JStateDrawer.figures;

import java.awt.geom.*;
import java.util.HashSet;

import org.jhotdraw.draw.CompositeFigure;
import org.jhotdraw.draw.EllipseFigure;
import org.jhotdraw.draw.layouter.*;
import org.jhotdraw.geom.*;

import edu.uwm.JStateDrawer.Models.StartStateModel;

/**
 * 
 * @author Reed Johnson
 * 
 */

@SuppressWarnings("serial")
public class StartStateFigure extends StateFigure{

	private final int CIRCLE_DIAMETER = 40;
	
	public StartStateFigure()
	{
		myModel = new StartStateModel();
		eventHandler = createEventHandler();
		setPresentationFigure(new EllipseFigure(0,0,CIRCLE_DIAMETER, CIRCLE_DIAMETER));
		setLayouter(new AbstractLayouter()
		{
			@Override
			public Rectangle2D.Double calculateLayout(CompositeFigure layoutable,
					Point2D.Double anchor, Point2D.Double lead)
			{
				return null;
			}

			@Override
			public Rectangle2D.Double layout(CompositeFigure compositeFigure,
					Point2D.Double anchor,
					Point2D.Double lead) {
				Dimension2DDouble preferredSize = getPreferredSize();
				return new Rectangle2D.Double(anchor.x, anchor.y, preferredSize.width, preferredSize.height);
			}
		});
	}
	
	/**
	 * Clones the StartStateFigure and clears all the children figures added to it by it's
	 * call to super.clone();
	 */
	public StartStateFigure clone()
	{
		StartStateFigure myClone = (StartStateFigure) super.clone();
		myClone.children.clear();
		myClone.myIncomingTransitions = new HashSet<TransitionFigure>();
		return myClone;
	}
	
	/**
	 * Returns the name of the start state. Usually just 'start'.
	 */
	@Override
	public String getName()
	{
		return myModel.getName();
	}
	
	/**
	 * Returns a {@link Dimension2DDouble} with equal width and height, specified by the 
	 * {@code CIRCLE_DIAMETER} constant. 
	 */
	public Dimension2DDouble getPreferredSize()
	{
		return new Dimension2DDouble(CIRCLE_DIAMETER, CIRCLE_DIAMETER);
	}
}
