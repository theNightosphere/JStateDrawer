/*
 * @(#)DependencyFigure.java
 *
 * Copyright (c) 1996-2010 by the original authors of JHotDraw and all its
 * contributors. All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the 
 * license agreement you entered into with the copyright holders. For details
 * see accompanying license terms.
 */
package edu.uwm.JStateDrawer.figures;

import org.jhotdraw.draw.connector.Connector;
import org.jhotdraw.draw.decoration.ArrowTip;
import org.jhotdraw.draw.event.FigureAdapter;
import org.jhotdraw.draw.event.FigureEvent;
import org.jhotdraw.draw.layouter.LocatorLayouter;
import org.jhotdraw.draw.liner.CurvedLiner;
import org.jhotdraw.draw.locator.RelativeLocator;

import java.awt.*;
import java.io.IOException;
import java.util.Map;

import static org.jhotdraw.draw.AttributeKeys.*;

import org.jhotdraw.draw.*;
import org.jhotdraw.xml.DOMInput;
import org.jhotdraw.xml.DOMOutput;

import edu.uwm.JStateDrawer.DrawerApplicationModel;
import edu.uwm.JStateDrawer.DrawerFactory;
import edu.uwm.JStateDrawer.Models.StateFigureModel;
import edu.uwm.JStateDrawer.Models.TransitionModel;

/**
 * DependencyFigure.
 *
 * @author Werner Randelshofer.
 * @version $Id: DependencyFigure.java 718 2010-11-21 17:49:53Z rawcoder $
 */
@SuppressWarnings("serial")
public class TransitionFigure extends LabeledLineConnectionFigure {
	private final String DEFAULT_NAME = "DEFAULT";
	private TransitionModel myModel;
	private StateFigure myStartFigure, myEndFigure;
    
    @SuppressWarnings("unused")
    private class TransitionTextFigure extends TextFigure
    {
    	private TransitionFigure myTransition;
    	
		public TransitionTextFigure()
    	{
    		super();
    	}
    	
    	public TransitionTextFigure(String text, TransitionFigure tFigure)
    	{
    		super(text);
    		myTransition = tFigure;
    	}
    	
    	/**
    	 * Sets the text for the transition and updates the transition model. 
    	 */
    	@Override
    	public void setText(String newText)
    	{
    		String oldText = getText();
    		try
    		{
    			if(myTransition != null)
    			{
    				myTransition.myModel.setTrigger(newText);
    			}
    			else
    			{
    				myModel.setTrigger(newText);
    			}
    			super.setText(newText);
    		}
    		catch(Exception e)
    		{
    			// Nothing happens as of now.
    		}
    	}
    }
	
    /** Creates a new instance. */
    public TransitionFigure() {
    	myModel = new TransitionModel(DEFAULT_NAME, new StateFigureModel(), new StateFigureModel());
        set(STROKE_COLOR, new Color(0x000099));
        set(STROKE_WIDTH, 1d);
        set(END_DECORATION, new ArrowTip());
        
        setLayouter(new LocatorLayouter());
        TransitionTextFigure nameFigure = new TransitionTextFigure("DEFAULT", this);
        nameFigure.set(FONT_BOLD, true);
        nameFigure.setAttributeEnabled(FONT_BOLD, false);
        add(nameFigure);

        LocatorLayouter.LAYOUT_LOCATOR.set(nameFigure, new RelativeLocator(.5, .5, false));

        setAttributeEnabled(END_DECORATION, false);
        setAttributeEnabled(START_DECORATION, false);
        setAttributeEnabled(STROKE_DASHES, false);
        setAttributeEnabled(FONT_ITALIC, false);
        setAttributeEnabled(FONT_UNDERLINE, false);
    }

    /**
     * Called by org.jhotdraw.draw.handle.ConnectorHandle
     * Checks if two figures can be connected. Implement this method
     * to constrain the allowed connections between figures.
     * Updated 3/6 to ensure no connections can come from end state or go to start state.
     */
    @Override
    public boolean canConnect(Connector start, Connector end) {
        if ((start.getOwner() instanceof StateFigure)
                && (end.getOwner() instanceof StateFigure)) {

        	if((start.getOwner() instanceof EndStateFigure) ||
        			(end.getOwner() instanceof StartStateFigure))
        	{
        		return false;
        	}
        	else
        	{
        		return true;
        	}
            /*StateFigure sf = (StateFigure) start.getOwner();
            StateFigure ef = (StateFigure) end.getOwner();

            // Disallow multiple connections to same dependent
            if (ef.getPredecessors().contains(sf)) {
                return false;
            }

            // Disallow cyclic connections
            return !sf.isDependentOf(ef);*/
        }

        return false;
    }

    public void setName(String newName)
    {
    	((TransitionTextFigure)getChild(0)).setText(newName);
    }
    
    /**
     * Determines if a state can connect. If the start state is an {@link EndStateFigure}
     * this returns false. If the start state is an instance of a StateFigure, this returns true.
     */
    @Override
    public boolean canConnect(Connector start) {
    	if(start.getOwner() instanceof EndStateFigure)
    	{
    		return false;
    	}
    	else{
    		return (start.getOwner() instanceof StateFigure);
    	}
        
    }
    
    /**
     * Sets the starting {@link StateFigure} of this {@link TransitionFigure} to the 
     * {@link StateFigure} specified by the start parameter. This updates by the {@link TransitionFigure}
     * and its associated {@link TransitionModel}.
     * @param start
     */
    public void setStartFigure(StateFigure start)
    {
    	if(start != null)
    	{
    		myStartFigure = start;
    		myModel.setStartState(start.getModel());
    	}
    }
    
    public void setEndFigure(StateFigure end)
    {
    	if(end != null)
    	{
    		myEndFigure = end;
    		myModel.setEndState(end.getModel());
    	}
    }

    /**
     * Handles the disconnection of a connection.
     * Override this method to handle this event.
     */
    @Override
    protected void handleDisconnect(Connector start, Connector end) {
        StateFigure sf = (StateFigure) start.getOwner();
        StateFigure ef = (StateFigure) end.getOwner();

        sf.removeOutgoingTransition(this);
        ef.removeIncomingTransition(this);
    }

    /**
     * Handles the connection of a connection.
     * Initializes the TransitionFigure's {@link TransitionModel} with the models
     * from start and end. 
     * Override this method to handle this event.
     */
    @Override
    protected void handleConnect(Connector start, Connector end) {
        StateFigure sf = (StateFigure) start.getOwner();
        StateFigure ef = (StateFigure) end.getOwner();
        
        myStartFigure = sf;
        myEndFigure = ef;

        myModel.setStartState(sf.getModel());
        myModel.setEndState(ef.getModel());

        sf.getModel().addTransition(myModel.getTrigger(), myModel);
        sf.addOutgoingTransition(this);
        
        ef.addIncomingTransition(this);
        
        if(sf == ef)
        {
        	setLiner(new CurvedLiner());
        	LocatorLayouter.LAYOUT_LOCATOR.set(getChild(0), new RelativeLocator(1, .5, false));
        }
    }

    @Override
    public TransitionFigure clone() {
        TransitionFigure that = (TransitionFigure) super.clone();
        // that.myModel = new TransitionModel();
        TransitionModel tm = new TransitionModel();
        myModel = tm;
        that.myModel = tm;
        that.basicRemoveAllChildren();
        TransitionTextFigure nameFigure = new TransitionTextFigure("DEFAULT", that);
        nameFigure.set(FONT_BOLD, true);
        nameFigure.setAttributeEnabled(FONT_BOLD, false);
        that.add(nameFigure);
        LocatorLayouter.LAYOUT_LOCATOR.set(nameFigure, new RelativeLocator(.5, .5, false));

        return that;
    }

    @Override
    public int getLayer() {
        return 1;
    }
    
    /**
     * Returns the {@link StateFigure} at the start of the {@link TransitionFigure}
     */
    public StateFigure getStartStateFigure()
    {
    	return myStartFigure;
    }
    
    /**
     * Returns the {@link StateFigure} at the end of this {@link TransitionFigure}
     */
    public StateFigure getEndStateFigure()
    {
    	return myEndFigure;
    }
    

    @Override
    public void removeNotify(Drawing d) {
        if (getStartFigure() != null) {
            ((StateFigure) getStartFigure()).removeOutgoingTransition(this);
        }
        if (getEndFigure() != null) {
            ((StateFigure) getEndFigure()).removeIncomingTransition(this);
        }
        super.removeNotify(d);
    }
    
    @Override
    public String toString()
    {
    	return super.toString() + " " + myModel.getTrigger();
    }

	public TransitionModel getModel() {
		return myModel;
	}
	
	/**
	 * Overrides write method of {@link LineConnectionFigure} to include the ability to write
	 * a stripped-down version of the {@link TransitionFigure} meant for use in diagram simulations.
	 */
	@Override
	public void write(DOMOutput out) throws IOException
	{
		if(DrawerFactory.serializeFile)
		{
			out.openElement("transitionContainer");
			out.writeObject(myModel);
			out.closeElement();
		}
		else
		{
			super.write(out);
			out.openElement("transitionContainer");
			out.writeObject(myModel);
			out.closeElement();
		}
		
	}
	
	@Override
	public void read(DOMInput in) throws IOException
	{
		if(!DrawerFactory.importFile)
		{
			super.read(in);
		}
			
		in.openElement("transitionContainer");
		myModel = (TransitionModel)in.readObject();
		in.closeElement();

		setName(myModel.getTrigger());
		

	}
	/**
	 * Overrides the writeAttributes of {@link AbstractAttributedFigure} to prevent the rewriting of the
	 * name for this {@link TransitionFigure}.
	 */
	@Override
	protected void writeAttributes(DOMOutput out) throws IOException {
        
        boolean isElementOpen = false;
        for (Map.Entry<AttributeKey, Object> entry : getAttributes().entrySet()) {
            AttributeKey key = entry.getKey();
            if (isAttributeEnabled(key)) {
                @SuppressWarnings("unchecked")
                Object prototypeValue = this.get(key);
                @SuppressWarnings("unchecked")
                Object attributeValue = get(key);
                if (prototypeValue != attributeValue ||
                        (prototypeValue != null && attributeValue != null &&
                        ! prototypeValue.equals(attributeValue))) {
                    if (! isElementOpen) {
                        out.openElement("a");
                        isElementOpen = true;
                    }
                    out.openElement(key.getKey());
                    out.writeObject(entry.getValue());
                    out.closeElement();
                }
            }
        }
        if (isElementOpen) {
            out.closeElement();
        }
    }

	public void serialize(DOMOutput out) throws IOException {
		out.openElement("name");
		out.writeObject(myModel.getTrigger());
		out.closeElement();
		
	}
}
