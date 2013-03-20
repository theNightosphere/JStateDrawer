package edu.uwm.JStateDrawer.figures;

import java.awt.Color;
import java.awt.geom.*;
import java.io.IOException;
import java.util.HashSet;

import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.draw.CompositeFigure;
import org.jhotdraw.draw.EllipseFigure;
import org.jhotdraw.draw.layouter.*;
import org.jhotdraw.geom.*;
import org.jhotdraw.xml.DOMInput;

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
	
	@Override
	public void read(DOMInput in)
	{
		double x = in.getAttribute("x", 0d);
        double y = in.getAttribute("y", 0d);
        double w = in.getAttribute("w", 0d);
        double h = in.getAttribute("h", 0d);
        setBounds(new Point2D.Double(x, y), new Point2D.Double(x + w, y + h));
        try {
        	in.openElement("state");
			in.openElement("name");
			// Read object to store the name 'default' in the idobjects list.
			// This is important if any states that are not start/end use the string 'default' because
			// NanoXML will use references to earlier objects that are duplicates. 
			in.readObject();
			in.closeElement();
			in.closeElement();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        set(AttributeKeys.FILL_COLOR, Color.black);
        children.clear();
	}
}
