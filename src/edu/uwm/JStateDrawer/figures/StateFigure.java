
package edu.uwm.JStateDrawer.figures;

import org.jhotdraw.draw.locator.RelativeLocator;
import org.jhotdraw.draw.handle.MoveHandle;
import org.jhotdraw.draw.handle.Handle;
import org.jhotdraw.draw.event.FigureAdapter;
import org.jhotdraw.draw.event.FigureEvent;
import org.jhotdraw.draw.layouter.HorizontalLayouter;
import org.jhotdraw.draw.layouter.VerticalLayouter;
import org.jhotdraw.draw.connector.LocatorConnector;
import org.jhotdraw.draw.handle.ConnectorHandle;
import java.io.IOException;
import java.awt.geom.*;
import static org.jhotdraw.draw.AttributeKeys.*;
import java.util.*;

import javax.swing.Action;

import org.jhotdraw.draw.*;
import org.jhotdraw.draw.handle.BoundsOutlineHandle;
import org.jhotdraw.geom.*;
import org.jhotdraw.util.*;
import org.jhotdraw.xml.*;

import edu.uwm.JStateDrawer.DrawerFactory;
import edu.uwm.JStateDrawer.Actions.SerializeFileAction;
import edu.uwm.JStateDrawer.Models.StateFigureModel;

/**
 * StateFigure.
 *
 * @author Reed Johnson, Scott Gill, and Chad Fisher.
 * @version $Id: StateFigure.java 727 2013-3-14 11:45:59Z reed $
 */
@SuppressWarnings("serial")
public class StateFigure extends GraphicalCompositeFigure {

    protected HashSet<TransitionFigure> myIncomingTransitions, myOutgoingTransitions;
    protected StateFigureModel myModel;
    protected static HashMap<String, Action> myActions = new HashMap<String, Action>();
    private boolean actionsShouldBeSorted = true;

    /**
     * This adapter is used, to connect a TextFigure with the name of
     * the StateFigure model.
     */
    private static class NameAdapter extends FigureAdapter {

        private StateFigure target;

        public NameAdapter(StateFigure target) {
            this.target = target;
        }

        @Override
        public void attributeChanged(FigureEvent e) {
        	target.setNameIfValid(e);
        	
        }
    }

    public class EventTextFigure extends TextFigure
    {
    	
    	private ActionTextFigure myAction;
    	
    	public EventTextFigure(String text)
    	{
    		super(text);
    		myAction = null;
    	}
    	
    	@Override
    	public void setText(String newText)
    	{
    		StateFigure.this.willChange();
    		// If trigger has an action, it must also update the action.
    		if(myAction != null)
    		{
    			String oldTrigger = getText();
    			ArrayList<String> actions = (ArrayList<String>)myModel.getActionsByEvent(oldTrigger);
    			// If 
    			if(actions.contains(myAction.getText()))
    			{
    				try
    				{
    					
    					myModel.removeAction(oldTrigger, myAction.getText());
    					myModel.addAction(newText, myAction.getText());
    					willChange();
    					super.setText(newText);
    					if(actionsShouldBeSorted)
    					{
    						sortActionEventTextFigures();
    					}
    					changed();
    				}
    				catch(IllegalArgumentException e)
    				{
    					// The trigger was bad. Because all actions are added via the addAction function,
    					// Exception thrown should never be from ill-formed action name.'
    					willChange();
    					super.setText(oldTrigger);
    					myModel.addAction(oldTrigger, myAction.getText());
    					changed();
    				}
    			}
    		}
    		// If trigger has no associated action, it is not responsible for updating the model's
    		// record of that action. This occurs usually during initialization of a TriggerTextFigure.
    		else
    		{
    			super.setText(newText);
    		}
    		StateFigure.this.changed();
    	}
    	
    	public void setActionTextFigure(ActionTextFigure actionText)
    	{
    		if(actionText != null)
    		{
    			myAction = actionText;
    		}
    	}
    	
    }
    
    /**
     * A text figure meant to be used to store Actions.
     */
    public class ActionTextFigure extends TextFigure
    {
    	private EventTextFigure myTrigger;
    	
    	/**
    	 * Constructs a text figure meant for Actions. Takes in the text for the figure 
    	 * and a TriggerTextFigure that it is associated with. 
    	 * @param text
    	 * @param triggerText
    	 */
    	public ActionTextFigure(String text, EventTextFigure triggerText)
    	{
    		
    		super(text);
    		myTrigger = triggerText;
    		myTrigger.setActionTextFigure(this);
    		setText(text);
    	}

		@Override
    	public void setText(String newText)
    	{
			StateFigure.this.willChange();
    		willChange();
    		if(myTrigger != null)
    		{
    			String trigger = myTrigger.getText();
    			String oldText = getText();
    			try
    			{
    				
    				super.setText(newText);
    				if(myModel.removeAction(trigger, oldText))
    				{
    					myModel.addAction(trigger, newText);	
    				}    				
    			}
    			catch(Exception e)
    			{
    				// Add action failed. add the old action again
    				super.setText(oldText);
    				myModel.addAction(trigger, oldText);

    			}
    		}
    		else
    		{
    			super.setText(myModel.DEFAULT_ACTION_NAME);
    		}
    		changed();
    		StateFigure.this.changed();
    	}
    }

    /**
     * Constructor calls implicit constructor to initialize StateFigure to default values
     * and then sets the associated {@link StateFigureModel}'s boolean value indicating
     * whether or not it is an internal state. The most common use of this constructor
     * is for creating a nested state.
     * @param isInternalState
     */
    public StateFigure(boolean isInternalState)
    {
    	this();
    	this.myModel.setIsInternalState(isInternalState);
    	this.myModel.setFigure(this);
    }
    
    /**
     * Initializes a state figure with the {@link RoundRectangleFigure} as the presentation figure. 
     */
    public StateFigure() {
        super(new RoundRectangleFigure());

        setLayouter(new VerticalLayouter());

        RectangleFigure nameCompartmentPF = new RectangleFigure();
        nameCompartmentPF.set(STROKE_COLOR, null);
        nameCompartmentPF.setAttributeEnabled(STROKE_COLOR, false);
        nameCompartmentPF.set(FILL_COLOR, null);
        nameCompartmentPF.setAttributeEnabled(FILL_COLOR, false);
        ListFigure nameCompartment = new ListFigure(nameCompartmentPF);
        ListFigure attributeCompartment = new ListFigure();
        SeparatorLineFigure separator1 = new SeparatorLineFigure();

        add(nameCompartment);
        add(separator1);
        add(attributeCompartment);

        Insets2D.Double insets = new Insets2D.Double(4, 8, 4, 8);
        nameCompartment.set(LAYOUT_INSETS, insets);
        attributeCompartment.set(LAYOUT_INSETS, insets);

        TextFigure nameFigure;
        nameCompartment.add(nameFigure = new TextFigure());
        nameFigure.set(FONT_BOLD, true);
        nameFigure.setAttributeEnabled(FONT_BOLD, false);

        setAttributeEnabled(STROKE_DASHES, false);

        myModel = new StateFigureModel();
        
        myModel.setFigure(this);
        
        //TODO Decide whether to throw this statement out.
        @SuppressWarnings("unused")
		ResourceBundleUtil labels =
        		ResourceBundleUtil.getBundle("org.jhotdraw.samples.pert.Labels");

        setName(myModel.getName());


        myIncomingTransitions = new HashSet<TransitionFigure>();
        myOutgoingTransitions = new HashSet<TransitionFigure>();
        nameFigure.addFigureListener(new NameAdapter(this));
    }

    
    /**
     * Returns a list of {@link Action}s to be used in the population of a 
     * popup menu. 
     */
    @Override
    public Collection<Action> getActions(Point2D.Double p)
    {
    	return myActions.values(); 	
    }
    
    /**
     * Adds a new action to the StateFigure and stores the data in the {@link StateFigureModel}
     * @param newTrigger The String that will be the new action's trigger
     * @param newAction The String that will be the new action.
     */
    public void addActionTextFigureUpdateModel(String newTrigger, String newAction)
    {
    	createActionTextFigure(newTrigger, newAction);
    	myModel.addAction(newTrigger, newAction);
    }

    /**
     * Helper method that creates a pairing of {@link ActionTextFigure} and {@link EventTextFigure}
     * to visually represent an action trigger pairing. 
     * @param newTrigger The display string of the new trigger.
     * @param newAction The display string of the new action.
     */
	private void createActionTextFigure(String newTrigger, String newAction) {
		willChange();
    	ListFigure actionToAdd = new ListFigure();
    	actionToAdd.setLayouter(new HorizontalLayouter());
    	EventTextFigure trigger = new EventTextFigure(newTrigger);
    	
    	trigger.set(AttributeKeys.FONT_ITALIC, true);
    	trigger.setAttributeEnabled(AttributeKeys.FONT_ITALIC, false);
    	
    	TextFigure divider = new TextFigure("/");
    	divider.set(AttributeKeys.FONT_BOLD, true);
    	divider.setAttributeEnabled(AttributeKeys.FONT_BOLD, false);
    	divider.setEditable(false);
    	ActionTextFigure action = new ActionTextFigure(newAction, trigger);

    	
    	actionToAdd.add(trigger);
    	actionToAdd.add(divider);
    	actionToAdd.add(action);
    	
    	getActionTextFigures().add(actionToAdd);
    	changed();
	}
    
	/**
     * Adds a new action to the StateFigure but does not update the {@link StateFigureModel}.
     * This is most likely to be used when a model has the action/trigger pairing but the figure
     * has not been updated yet (e.g. while reading from a file).
     * @param newTrigger The String that will be the new action's trigger
     * @param newAction The String that will be the new action.
     */
    public void addActionTextFigureNoUpdate(String newTrigger, String newAction)
    {
    	createActionTextFigure(newTrigger, newAction);
    }
    
    
    /**
     * Adds a new action to the StateFigure and stores the data in the {@link StateFigureModel}
     * with default values.
     */
    public void addActionTextFigure()
    {
    	addActionTextFigureUpdateModel(myModel.DEFAULT_ACTION_TRIGGER, myModel.DEFAULT_ACTION_NAME);
    }
    
    /**
     * A function that stable sorts the List of Event/Action pairs in the order
     * ENTRY->(All others)->EXIT.
     * The order of multiple actions belonging to a single event is preserved because
     * Java's {@code Collections.sort()} function is a stable sort. 
     */
    public void sortActionEventTextFigures()
    {
    	ArrayList<Figure> actions = new ArrayList<Figure>(getActionTextFigures().getChildren());
    	Collections.sort(actions,
    			new Comparator<Figure>(){
    		@Override
			public int compare(Figure o1, Figure o2)
    		{
    			String actionO1 = ((ActionTextFigure)((ListFigure)o1).getChild(2)).getText();
    			String eventO1 = ((EventTextFigure)((ListFigure)o1).getChild(0)).getText();
    			String actionO2 = ((ActionTextFigure)((ListFigure)o2).getChild(2)).getText();
    			String eventO2 = ((EventTextFigure)((ListFigure)o2).getChild(0)).getText();
    			// IF the event is ENTRY, either it is being compared to another entry
    			// action, and their ordering is preserved, or it is being compared
    			// to a non-entry action and it is placed before event/action2
    			if(eventO1.equals("ENTRY") || eventO2.equals("ENTRY"))
    			{
    				if(eventO1.equals(eventO2))
    				{
    					return 0;
    				}
    				else if (eventO1.equals("ENTRY"))
    				{
    					return -1;
    				}
    				else
    				{
    					return 1;
    				}
    			}
    			// Same logic as with ENTRY, except EXIT actions must be after all
    			// other actions
    			else if(eventO1.equals("EXIT") || eventO2.equals("EXIT"))
    			{
    				if(eventO1.equals(eventO2))
    				{
    					return 0;
    				}
    				else if (eventO1.equals("EXIT"))
    				{
    					return 1;
    				}
    				else
    				{
    					return -1;
    				}
    			}
    			// Else sort the strings by their natural ordering. 
    			else
    			{
    				return eventO1.compareTo(eventO2);
    			}
    		}
    	});
    	ListFigure newFigure = new ListFigure();
    	for(Figure pair : actions)
    	{
    		newFigure.add((ListFigure) pair);
    	}
    	// Create a new ListFigure, fill it with the sorted stuff, delete the old list figure
    	// replace it with the new list figure because I hate JHotDraw and getChildren()
    	// returns an unmodifiable collection so I can't just sort it.
    	this.removeChild(2);
    	this.add(2, newFigure);
    }
    
    /**
     * Returns the {@link EventTextFigure} corresponding to the index of the {@code indexOfChild} TextFigure.
     * This child is taken from the list of {@link ListFigure}s that store the trigger/action pairs.
     * @param listOfActionFigures
     * @param indexOfChild
     * @return The ({@code indexOfChild})th {@link EventTextFigure}.
     */
    public EventTextFigure getIthChildEventFigure(ListFigure listOfActionFigures, int indexOfChild)
    {
    	 return (EventTextFigure)(((ListFigure)listOfActionFigures.getChild(indexOfChild)).getChild(0));
    }
    
    /**
     * Returns the {@link ActionTextFigure} corresponding to the index of the {@code indexOfChild} TextFigure.
     * This child is taken from the list of {@link ListFigure}s that store the trigger/action pairs.
     * @param listOfActionFigures
     * @param indexOfChild
     * @return The ({@code indexOfChild})th {@link ActionTextFigure}.
     */
    public ActionTextFigure getIthChildActionTextFigure(ListFigure listOfActionFigures, int indexOfChild)
    {
    	return (ActionTextFigure)(((ListFigure)listOfActionFigures.getChild(indexOfChild)).getChild(2));
    }
    
    /**
     * Accesses the {@link ListFigure} containing the {@link TextFigure}s that hold
     * the {@link StateFigureModel}'s actions.
     * @return A {@link ListFigure}
     */
    public ListFigure getActionTextFigures()
    {
    	return (ListFigure)getChild(2);
    }
    
    /**
     * Removes a trigger/action pair from a state corresponding to the index passed to the function.
     * The indices start at 0 and are ordered in the state from top to bottom. False is returned if
     * the indexofAction parameter is less than 0 or greater than or equal to the number of actions.
     * Also updates the associated {@link StateFigureModel} if the index value is acceptable.
     * @param indexOfAction The integer index of the trigger/action pair to remove.
     * @return True if the action is successfully removed, false if it is not.
     */
    public boolean removeActionByIndex(int indexOfAction)
    {
    	ListFigure actions = getActionTextFigures();
    	if(indexOfAction >= 0 && indexOfAction < actions.getChildCount())
    	{
    		willChange();
    		
    		String event = getIthChildEventFigure(actions, indexOfAction).getText();
    		String action = getIthChildActionTextFigure(actions, indexOfAction).getText();
    		myModel.removeAction(event, action);
    		
    		actions.removeChild(indexOfAction);
    		changed();
        	return true;
    	}
    	return false;
    }
    
    @Override
    public Collection<Handle> createHandles(int detailLevel) {
        java.util.List<Handle> handles = new LinkedList<Handle>();
        switch (detailLevel) {
            case -1:
                handles.add(new BoundsOutlineHandle(getPresentationFigure(), false, true));
                break;
            case 0:
                handles.add(new MoveHandle(this, RelativeLocator.northWest()));
                handles.add(new MoveHandle(this, RelativeLocator.northEast()));
                handles.add(new MoveHandle(this, RelativeLocator.southWest()));
                handles.add(new MoveHandle(this, RelativeLocator.southEast()));
                ConnectorHandle ch;
                handles.add(ch = new ConnectorHandle(new LocatorConnector(this, RelativeLocator.east()), new TransitionFigure()));
                ch.setToolTipText("Drag the connector to a connected state.");
                break;
        }
        return handles;
    }

    public void setName(String newValue) {
        getNameFigure().setText(newValue);
    }

    public String getName() {
        return getNameFigure().getText();
    }

    private TextFigure getNameFigure() {
        return (TextFigure) ((ListFigure) getChild(0)).getChild(0);
    }
    
    @Override
    public StateFigure clone() {
        StateFigure that = (StateFigure) super.clone();
        that.clearStateFigureModel();
        that.myIncomingTransitions = new HashSet<TransitionFigure>();
        that.myOutgoingTransitions = new HashSet<TransitionFigure>();
        that.myModel.setIsInternalState(myModel.getIsInternalState());
        if(!((ListFigure)getChild(0)).getChildren().isEmpty())
        {
        	that.getNameFigure().addFigureListener(new NameAdapter(that));
        }
        that.getModel().setFigure(that);
        return that;
        
    }
    
    /**
     * Method that assigns the myModel variable to a new, blank StateFigureModel. 
     * This is used by clone to ensure that all statefigures do not share one statemodel. 
     */
    private void clearStateFigureModel()
    {
    	myModel = new StateFigureModel();
    }

    /**
     * Adds a new {@link Action} by its String actionID to this {@link StateFigure}'s map of actions. 
     * @param newAction
     */
    public static void addAction(String actionID, Action newAction)
    {
    	myActions.put(actionID, newAction);
    }
    
    /**
     * Checks whether the {@link StateFigure} has the specified {@link Action} based on its String
     * action ID.
     * @param actionID
     * @return true if the actionToCheck is in the list of the {@link StateFigure}'s actions,
     * false if it is not. 
     */
    public static boolean containsAction(String actionID)
    {
    	return myActions.containsKey(actionID);
    }
    
    @Override
    public void read(DOMInput in) throws IOException {
        double x = in.getAttribute("x", 0d);
        double y = in.getAttribute("y", 0d);
        double w = in.getAttribute("w", 0d);
        double h = in.getAttribute("h", 0d);
        setBounds(new Point2D.Double(x, y), new Point2D.Double(x + w, y + h));
        readAttributes(in);
        in.openElement("stateContainer");
        myModel = (StateFigureModel) in.readObject();
        in.closeElement();
        
        willChange();
        setName(myModel.getName());
        changed();
        myModel.setFigure(this);
        // Boolean is set here to assure that sort isn't called on each insertion, which would 
        // make this roughly O(n^2) in the number of action/event pairs. 
        // Sort is called once, however, after all pairs have been added.
        actionsShouldBeSorted = false;
        ArrayList<String> events = new ArrayList<String>(myModel.getAllActions().keySet());
        for(String event : events)
        {
        	ArrayList<String> actions = (ArrayList<String>) myModel.getActionsByEvent(event);
        	for(int i = 0; i < actions.size(); i++)
        	{
        		addActionTextFigureNoUpdate(event, actions.get(i));
        	}
        }
        sortActionEventTextFigures();
        actionsShouldBeSorted = true;
    }

    /**
     * If the value {@code DrawerFactory.serializeFile} is true, then write will 
     * output a stripped-down version of the figure meant for running simulations but not
     * reconstructing diagrams. {@code DrawerFactory.serializeFile} is set to true by the
     * {@link SerializeFileAction} and reset to false once the writing is finished or fails. 
     */
    @Override
    public void write(DOMOutput out) throws IOException {
    	// If serializeFile is set to false, we also want to store the location of the state
    	// and its display attributes. 
    	if(!DrawerFactory.serializeFile)
    	{
    		Rectangle2D.Double r = getBounds();
    		out.addAttribute("x", r.x);
    		out.addAttribute("y", r.y);
    		writeAttributes(out);
    	}
    	out.openElement("stateContainer");
    	out.writeObject(myModel);
    	out.closeElement();
    }

    @Override
    public int getLayer() {
        return 0;
    }

    /**
     * Sets the name of StateFigure's associated {@link StateFigureModel} if the name is valid.
     * If the name is not valid, the {@link StateFigureModel} remains unchanged and the StateFigure's name is returned
     * to the old value. If the {@link StateFigure} has a nested start state, its name is ALSO
     * updated in order to properly update its display.
     * @param evt A {@link FigureEvent} created when the State's name is changed.
     */
    public void setNameIfValid(FigureEvent evt)
    {
    	try
    	{
    		String newName = (String)evt.getNewValue();
    		myModel.setName(newName);
    		if(!myModel.getIsInternalState() && !myModel.getInternalStates().isEmpty())
    		{
    			StateFigureModel nestedStart = myModel.getNestedStartState();
    			if(nestedStart != null)
    			{
    				nestedStart.getFigure().setName(newName);
    			}
    		}
    		
    	}
    	catch(Exception e)
    	{
    		setName((String)evt.getOldValue());
    	}
    }
    
    /**
     * Returns an unmodifiable set of the incoming {@link TransitionFigure}s.
     * @return
     */
    public Set<TransitionFigure> getIncomingTransitions() {
        return Collections.unmodifiableSet(myIncomingTransitions);
    }

    /**
     * Adds the specified {@link TransitionFigure} to the set of incoming {@link TransitionFigure}s.
     * @param f
     */
    public void addIncomingTransition(TransitionFigure f) {
        myIncomingTransitions.add(f);
    }

    /**
     * Removes the specified {@link TransitionFigure} from the set of incoming {@link TransitionFigure}s.
     * @param f
     */
    public void removeIncomingTransition(TransitionFigure f) {
        myIncomingTransitions.remove(f);
    }
    
    /**
     * Returns an unmodifiable set of outgoing {@link TransitionFigure}s.
     * @return
     */
    public Set<TransitionFigure> getOutgoingTransitions()
    {
    	return Collections.unmodifiableSet(myOutgoingTransitions);
    }
    
    /**
     * Adds the passed {@link TransitionFigure} to the Set of {@link TransitionFigures}
     * @param f
     */
    public void addOutgoingTransition(TransitionFigure f)
    {
    	myOutgoingTransitions.add(f);
    }
    
    /**
     * Removes the specified {@link TransitionFigure} from the set of outgoing TransitionFigures.
     * @param f
     */
    public void removeOutgoingTransition(TransitionFigure f)
    {
    	myModel.removeOutgoingTransition(f.getModel());
    	myOutgoingTransitions.remove(f);
    }
    
    /**
     * Signals to the StateFigure that says it should display its name. Only implemented
     * for {@link StartStateFigure}. Calling this on an {@link EndStateFigure} or
     * {@link StateFigure} does nothing.
     */
    public void showName()
    {
    	//Nothing right now.
    }
    
    /**
     * Returns this StateFigure's associated {@link StateFigureModel}.
     * @return {@link StateFigureModel}
     */
    public StateFigureModel getModel()
    {
    	return myModel;
    }

    @Override
    public String toString() {
        return "StateFigure#" + hashCode() + " " + getName();
    }
    
    /**
     * Adds a nested start state to this parent state and connects it with a 
     * {@link TransitionFigure}.
     */
    public StartStateFigure addNestedStartState()
    {
    	StartStateFigure nestedStart = new StartStateFigure(true);
    	addNestedState(nestedStart);
    	return nestedStart;
    }
    
    /**
     * Adds the {@link StateFigure} nestedState to this figure's model's list of 
     * nested states.
     * @param nestedState
     */
    public void addNestedState(StateFigure nestedState)
    {
    	myModel.addInternalState(nestedState.getModel());
    }
    
    /**
     * Starting at the calling {@link StateFigure}, all nested {@link StateFigure}s that
     * share the starting {@link StateFigure} as an ancestor are updated to 
     * {@code parentFigure} as the parent. This is performed via a depth
     * first search.
     * @param parentFigure
     */
    public void cascadeUpdateParentFigure(StateFigureModel parentFigure)
    {
    	HashSet<StateFigure> closedList = new HashSet<StateFigure>();
    	Stack<StateFigure> openList = new Stack<StateFigure>();
    	
    	StateFigure currentFigure = this;
    	openList.push(currentFigure);
    	
    	while(openList.size() > 0)
    	{
    		currentFigure = openList.pop();
    		closedList.add(currentFigure);
    		
    		currentFigure.getModel().setParentState(parentFigure);
    		
    		HashSet<TransitionFigure> currentTransitions = new HashSet<TransitionFigure>(currentFigure.getOutgoingTransitions());
    		for(TransitionFigure t : currentTransitions)
			{
				if (!(closedList.contains(t.getEndFigure())))
				{
					openList.push(t.getEndStateFigure());
				}
			}
    	}
    }
}

