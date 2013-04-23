package edu.uwm.JStateDrawer.figures;

import java.awt.Color;
import java.awt.geom.*;
import java.io.IOException;
import java.util.HashSet;

import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.draw.CompositeFigure;
import org.jhotdraw.draw.EllipseFigure;
import org.jhotdraw.draw.ListFigure;
import org.jhotdraw.draw.TextFigure;
import org.jhotdraw.draw.layouter.*;
import org.jhotdraw.geom.*;
import org.jhotdraw.xml.DOMInput;

import edu.uwm.JStateDrawer.Models.StartStateModel;
import edu.uwm.JStateDrawer.Models.StateFigureModel;

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
		this.children.clear();
		myModel = new StartStateModel();
		eventHandler = createEventHandler();
		EllipseFigure ellipse = new EllipseFigure(0,0,CIRCLE_DIAMETER, CIRCLE_DIAMETER);
		setPresentationFigure(ellipse);
		ellipse.set(AttributeKeys.FILL_COLOR, Color.black);
		ListFigure testList = new ListFigure(ellipse);
		add(testList);
		
		setLayouter(new AbstractLayouter()
		{
			@Override
			public Rectangle2D.Double calculateLayout(CompositeFigure layoutable,
					Point2D.Double anchor, Point2D.Double lead)
			{
				Dimension2DDouble preferredSize = getPreferredSize();
				return new Rectangle2D.Double(anchor.x, anchor.y, preferredSize.width, preferredSize.height);
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
	
	
	public StartStateFigure(boolean isNestedStartState)
	{
		this();
		myModel.setIsInternalState(isNestedStartState);
		if(isNestedStartState)
		{
			this.willChange();
			TextFigure testText = new TextFigure("default");
			((ListFigure)this.getChild(0)).add(testText);
			testText.set(AttributeKeys.TEXT_COLOR, Color.red);
			testText.set(AttributeKeys.FONT_BOLD, true);
			this.changed();
		}
	}
	
	/**
	 * Clones the StartStateFigure and clears all the children figures added to it by it's
	 * call to super.clone();
	 */
	public StartStateFigure clone()
	{
		StartStateFigure myClone = (StartStateFigure) super.clone();
		myClone.children.clear();
		myClone.getModel().setName("start");
		myClone.myOutgoingTransitions = new HashSet<TransitionFigure>();
		myClone.getModel().setFigure(myClone);
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
	
	/**
	 * Sets the name of the state figure. This can only be called if 
	 * the StartState is an nested state. 
	 */
	@Override
	public void setName(String newValue)
	{
		if(myModel.getIsInternalState()){
			this.willChange();
			((TextFigure)((ListFigure)this.getChild(0)).getChild(0)).setText(newValue);
			myModel.setName(newValue);
			this.changed();
		}
	}
	
	@Override
	public void read(DOMInput in) throws IOException
	{
		double x = in.getAttribute("x", 0d);
        double y = in.getAttribute("y", 0d);
        double w = in.getAttribute("w", 0d);
        double h = in.getAttribute("h", 0d);
        setBounds(new Point2D.Double(x, y), new Point2D.Double(x + w, y + h));
        readAttributes(in);
        try {
        	in.openElement("stateContainer");
        	myModel = (StateFigureModel) in.readObject();
        	in.closeElement();
		} catch (IOException e) {
			e.printStackTrace();
		}
        myModel.setFigure(this);
        set(AttributeKeys.FILL_COLOR, Color.black);
        children.clear();
	}
}
