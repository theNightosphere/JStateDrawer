package edu.uwm.JStateDrawer.figures;

import java.awt.Color;
import java.awt.geom.*;
import java.util.HashSet;

import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.draw.CompositeFigure;
import org.jhotdraw.draw.EllipseFigure;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.GraphicalCompositeFigure;
import org.jhotdraw.draw.layouter.*;
import org.jhotdraw.geom.*;

import edu.umd.cs.findbugs.annotations.Nullable;

/**
 * 
 * @author Reed Johnson
 * 
 */
//TODO: StartState can have 0 incoming transitions and 1 outgoing transitions.

@SuppressWarnings("serial")
public class StartStateFigure extends StateFigure{

	private final int CIRCLE_DIAMETER = 40;
	private int x, y;
	
	public StartStateFigure()
	{
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
				System.out.println("anchor.x:" + Double.toString(anchor.x) + " anchor.y:" + Double.toString(anchor.y) + " width:" + Double.toString(preferredSize.width) + " height:" + Double.toString(preferredSize.height));
				return new Rectangle2D.Double(anchor.x, anchor.y, preferredSize.width, preferredSize.height);
			}
		});
	}
	
	public StartStateFigure clone()
	{
		StartStateFigure myClone = (StartStateFigure) super.clone();
		myClone.children.clear();
		
		Rectangle2D.Double myBounds = this.getBounds();
		
		myClone.dependencies = new HashSet<TransitionFigure>();
		return myClone;
	}
	
	public String getName()
	{
		return "StartStateFigure";
	}
	
	/*@Override
	public void draw(Graphics2D g)
	{
		
	}*/
	public Dimension2DDouble getPreferredSize()
	{
		return new Dimension2DDouble(CIRCLE_DIAMETER, CIRCLE_DIAMETER);
	}
}
