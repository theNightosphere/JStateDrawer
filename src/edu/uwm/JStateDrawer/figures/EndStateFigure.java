package edu.uwm.JStateDrawer.figures;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashSet;

import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.draw.CompositeFigure;
import org.jhotdraw.draw.EllipseFigure;
import org.jhotdraw.draw.layouter.AbstractLayouter;
import org.jhotdraw.geom.Dimension2DDouble;

@SuppressWarnings("serial")
public class EndStateFigure extends StateFigure{

	private final int CIRCLE_DIAMETER = 40;
	private final int INNER_CIRCLE_OFFSET = CIRCLE_DIAMETER/10;
	
	public EndStateFigure()
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
				return new Rectangle2D.Double(anchor.x, anchor.y, preferredSize.width, preferredSize.height);
			}
		});
	}
	
	public EndStateFigure clone()
	{
		EndStateFigure myClone = (EndStateFigure) super.clone();
		myClone.children.clear();
		
		// Color of inner circle is set here but color of outer circle is not.
		// When the createFigure method is called, it sets the outer circle to the default color.
		// To draw an end state we want this to be a white circle with a black inner circle.
		EllipseFigure ellipse = new EllipseFigure(0,0, CIRCLE_DIAMETER-INNER_CIRCLE_OFFSET,
				CIRCLE_DIAMETER-INNER_CIRCLE_OFFSET);
		ellipse.set(AttributeKeys.FILL_COLOR, Color.black);
		myClone.add(ellipse);
		set(AttributeKeys.STROKE_WIDTH, 3d);
		
		myClone.dependencies = new HashSet<TransitionFigure>();
		return myClone;
	}
	
	@Override
	public void setBounds(Point2D.Double anchor, Point2D.Double lead)
	{
		//set(AttributeKeys.FILL_COLOR, Color.white);
		if (getLayouter() == null) {
            super.setBounds(anchor, lead);
            basicSetPresentationFigureBounds(anchor, lead);
        } else {
            Rectangle2D.Double r = getLayouter().layout(this, anchor, lead);
            basicSetPresentationFigureBounds(new Point2D.Double(r.getX(), r.getY()),
                    new Point2D.Double(lead.x, lead.y));
            getChild(0).setBounds(new Point2D.Double(r.getX()+4,  r.getY()+4),
            		new Point2D.Double(lead.x-4, lead.y-4));
            invalidate();
        }
	}
	
	public Dimension2DDouble getPreferredSize()
	{
		return new Dimension2DDouble(CIRCLE_DIAMETER, CIRCLE_DIAMETER);
	}
	
}
