
package edu.uwm.JStateDrawer.figures;

import org.jhotdraw.draw.connector.Connector;
import org.jhotdraw.draw.decoration.ArrowTip;
import org.jhotdraw.draw.event.FigureAdapter;
import org.jhotdraw.draw.event.FigureEvent;
import org.jhotdraw.draw.layouter.LocatorLayouter;
import org.jhotdraw.draw.liner.CurvedLiner;
import org.jhotdraw.draw.locator.RelativeLocator;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Action;

import static org.jhotdraw.draw.AttributeKeys.*;

import org.jhotdraw.draw.*;
import org.jhotdraw.xml.DOMInput;
import org.jhotdraw.xml.DOMOutput;

import edu.uwm.JStateDrawer.DrawerApplicationModel;
import edu.uwm.JStateDrawer.DrawerFactory;
import edu.uwm.JStateDrawer.Models.StateFigureModel;
import edu.uwm.JStateDrawer.Models.TransitionModel;

/**
 * TransitionFigure.
 *
 * @author Reed Johnson
 * @author Scott Gill
 * @author Chad Fisher
 * @version $Id: TransitionFigure.java 
 */
@SuppressWarnings("serial")
public class TransitionFigure extends LabeledLineConnectionFigure {
	public static HashMap<String, Action> myActions = new HashMap<String, Action>();
	
	private final String DEFAULT_NAME = "EVENT";
	private final String DEFAULT_ACTION = "default";
	private TransitionModel myModel;
	private StateFigure myStartFigure, myEndFigure;
    
    private class TransitionActionTextFigure extends TextFigure
    {
    	private TransitionFigure myTransition;
    	
		public TransitionActionTextFigure()
    	{
    		super();
    	}
    	
    	public TransitionActionTextFigure(String text, TransitionFigure tFigure)
    	{
    		super(text);
    		myTransition = tFigure;
    	}
    	
    	/**
    	 * Sets the text for the transition action and updates the transition model. 
    	 */
    	@Override
    	public void setText(String newText)
    	{
    		String oldText = getText();
    		try
    		{
    			if(myTransition != null)
    			{	// If action removal succeeds, this evaluates true and the name is updated.
    				if(myTransition.myModel.removeAction(oldText))
    				{
    					myTransition.myModel.addAction(newText);
    				}
    			}
    			else
    			{
    				if(myModel.removeAction(oldText))
    				{
    					myModel.addAction(newText);
    				}
    			}
    			super.setText(newText);
    		}
    		catch(Exception e)
    		{
    			// Nothing happens as of now.
    		}
    	}
    }

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
    				myTransition.myModel.setEvent(newText);
    			}
    			else
    			{
    				myModel.setEvent(newText);
    			}
    			super.setText(newText);
    		}
    		catch(Exception e)
    		{
    			// Nothing happens as of now.
    		}
    	}
    }
    
    //TODO: CLEAN UP ADDITION OF LIST FIGURES.
    /** Creates a new instance. */
    public TransitionFigure() {
    	myModel = new TransitionModel(DEFAULT_NAME, new StateFigureModel(), new StateFigureModel());
        set(STROKE_COLOR, new Color(0x000099));
        set(STROKE_WIDTH, 1d);
        set(END_DECORATION, new ArrowTip());
        
        setLayouter(new LocatorLayouter());
        RectangleFigure transitionInfoDisplay = new RectangleFigure();
        transitionInfoDisplay.set(STROKE_COLOR, null);
        transitionInfoDisplay.setAttributeEnabled(STROKE_COLOR, false);
        transitionInfoDisplay.set(FILL_COLOR, null);
        transitionInfoDisplay.setAttributeEnabled(FILL_COLOR, false);
        
        ListFigure transitionInfoCompartment = new ListFigure(transitionInfoDisplay);
        TransitionTextFigure nameFigure = new TransitionTextFigure("DEFAULT", this);
        nameFigure.set(FONT_BOLD, true);
        nameFigure.setAttributeEnabled(FONT_BOLD, false);
        
        transitionInfoCompartment.add(nameFigure);
        
        ListFigure transitionActionList = new ListFigure();
        
        transitionInfoCompartment.add(transitionActionList);
        LocatorLayouter.LAYOUT_LOCATOR.set(transitionInfoCompartment, new RelativeLocator(.5, .5, false));
        
        this.add(transitionInfoCompartment);

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
     * Updated 4/21 to allow {@link StartStateFigure}s to be the end point as
     * long as they are a nested start state.
     */
    @Override
    public boolean canConnect(Connector start, Connector end) {
    	// IF both figures are StateFigures
        if ((start.getOwner() instanceof StateFigure)
                && (end.getOwner() instanceof StateFigure)) {

        	StateFigure sf = (StateFigure) start.getOwner();
        	StateFigure ef = (StateFigure) end.getOwner();
        	// If the end is an internal start state
        	if(ef instanceof StartStateFigure)
        	{
        		if(ef.getModel().getIsInternalState())
        		{
        			return true;
        		}
        		else
        		{
        			return false;
        		}
        	}
        	// If the start is an end state, it can connect.
        	else if(sf instanceof EndStateFigure)
        	{
        		return false;
        	}
        	// If both states are nested states, they can connect. 
        	else if(sf.getModel().getIsInternalState())
        	{
        		if(ef.getModel().getIsInternalState())
        		{
        			return true;
        		}
        		else
        		{
        			return false;
        		}
        	}
        	// If the start state is internal, but the end is not, do not connect
        	else if(!sf.getModel().getIsInternalState())
        	{
        		if(ef.getModel().getIsInternalState())
        		{
        			return false;
        		}
        		else
        		{
        			return true;
        		}
        	}
        	else
        	{
        		return true;
        	}
        }
        return false;
    }

    public void setName(String newName)
    {
    	((TransitionTextFigure)((ListFigure)getChild(0)).getChild(0)).setText(newName);
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

        if(myStartFigure.getModel().getIsInternalState())
        {
        	// Both STart and end are nested states. Update the end with the parent of the start.
        	// Also updates all ancestors of the end figure with 
        	if(myEndFigure.getModel().getIsInternalState())
        	{
        		myEndFigure.getModel().setParentState(myStartFigure.getModel().getParentState());
        		myEndFigure.cascadeUpdateParentFigure(myStartFigure.getModel().getParentState());
        	}
        }
        
        myModel.setStartState(sf.getModel());
        myModel.setEndState(ef.getModel());

        sf.getModel().addTransition(myModel.getEvent(), myModel);
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
        
        RectangleFigure transitionInfoDisplay = new RectangleFigure();
        transitionInfoDisplay.set(STROKE_COLOR, null);
        transitionInfoDisplay.setAttributeEnabled(STROKE_COLOR, false);
        transitionInfoDisplay.set(FILL_COLOR, null);
        transitionInfoDisplay.setAttributeEnabled(FILL_COLOR, false);
        
        ListFigure transitionInfoCompartment = new ListFigure(transitionInfoDisplay);
        TransitionTextFigure nameFigure = new TransitionTextFigure("DEFAULT", that);
        nameFigure.set(FONT_BOLD, true);
        nameFigure.setAttributeEnabled(FONT_BOLD, false);
        
        transitionInfoCompartment.add(nameFigure);
        
        ListFigure transitionActionList = new ListFigure();
        
        transitionInfoCompartment.add(transitionActionList);
        LocatorLayouter.LAYOUT_LOCATOR.set(transitionInfoCompartment, new RelativeLocator(.5, .5, false));

        that.add(transitionInfoCompartment);
        
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
    	return super.toString() + " " + myModel.getEvent();
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

		setName(myModel.getEvent());
		ArrayList<String> actions = (ArrayList<String>) myModel.getAllActions();
		for(int i = 0; i < actions.size(); i++)
		{
			addActionNoUpdate(actions.get(i));
		}
		

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
		out.writeObject(myModel.getEvent());
		out.closeElement();
		
	}
	
	/**
	 * Creates an action figure with the text 'default' and updates the associated
	 * {@link TransitionModel}.
	 */
	public void addDefaultAction()
	{
		createActionFigure(DEFAULT_ACTION);
		myModel.addAction(DEFAULT_ACTION);
	}
	
	/**
	 * Creates an action with the text specified by the string {@code newAction}.
	 * This method updates the associated {@link TransitionModel}.
	 * @param newAction {@link String} with the desired text of the new action.
	 */
	public void addActionUpdateModel(String newAction)
	{
		createActionFigure(newAction);
		myModel.addAction(newAction);
	}
	
	/**
	 * Creates an action with the text specified by the string {@code newAction}.
	 * This method DOES NOT update the associated {@link TransitionModel}.
	 * This should be called when the {@link TransitionModel} already contains the action,
	 * and calling {@link addActionUpdateModel} would erroneously duplicate the action.
	 * This only really occurs during file loading.
	 * @param newAction {@link String} with the desired text of the new action.
	 */
	public void addActionNoUpdate(String newAction)
	{
		createActionFigure(newAction);
	}
	
	/**
	 * Creates an action figure with the text specified by {@code actionText} and adds it 
	 * to the TransitionFigure's {@link ListFigure} of Actions. 
	 * @param actionText A {@link String} that will be the text for the action figure.
	 */
	public void createActionFigure(String actionText)
	{
		willChange();
		TransitionActionTextFigure newAction = new TransitionActionTextFigure(actionText, this);
		getAllActions().add(newAction);
		changed();
	}
	
	/**
	 * Removes the {@code index}th action from the {@link TransitionFigure} and updates
	 * the associated {@link TransitionModel}. 
	 * @param index {@code int} The index of the action to be removed
	 * @return {@code true} if the action is successfully removed, {@code false} if it is not.
	 */
	public boolean removeActionByIndex(int index)
	{
		ListFigure actions = getAllActions();
		if(index >= 0 && index < actions.getChildCount())
		{
			String actionToRemove = getIthAction(index).getText();
			myModel.removeAction(actionToRemove);
			
			willChange();
			actions.removeChild(index);
			changed();
			
			return true;
		}
		
		return false;
	}
	
	/**
	 * Returns the ith action {@link TextFigure}. No bounds checking is performed,
	 * calling function should ensure that {@code index} is within the proper bounds.
	 * @param index
	 * @return The ith {@link TextFigure}
	 */
	public TextFigure getIthAction(int index)
	{
		return (TextFigure)(getAllActions().getChild(index));
	}
	
	/**
	 * Returns the ListFigure containing all of this action's TextFigures.
	 * @return
	 */
	public ListFigure getAllActions()
	{
		return (ListFigure) ((ListFigure)this.getChildren().get(0)).getChild(1);
	}
	
	/**
	 * Returns the actions that will be presented in a popup menu. 
	 */
	@Override
    public Collection<Action> getActions(Point2D.Double p) {
        return myActions.values();
    }
	
	/**
     * Adds a new {@link Action} by its String actionID to this {@link TransitionFigure}'s map of actions.
     * If the map of actions already contains the action being added, then nothing happens.
     * @param newAction
     */
	public static void addAction(String actionID, Action newAction)
    {
		if(!myActions.containsKey(actionID))
		{
			myActions.put(actionID, newAction);
		}
    }
}
