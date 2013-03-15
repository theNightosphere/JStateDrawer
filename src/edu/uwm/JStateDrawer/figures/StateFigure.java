/*
 * @(#)TaskFigure.java
 *
 * Copyright (c) 1996-2010 by the original authors of JHotDraw and all its
 * contributors. All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the 
 * license agreement you entered into with the copyright holders. For details
 * see accompanying license terms.
 */
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

import edu.uwm.JStateDrawer.Actions.SerializeFileAction;
import edu.uwm.JStateDrawer.Models.StateFigureModel;

/**
 * TaskFigure.
 *
 * @author Werner Randelshofer.
 * @version $Id: TaskFigure.java 727 2011-01-09 13:23:59Z rawcoder $
 */
@SuppressWarnings("serial")
public class StateFigure extends GraphicalCompositeFigure {

    protected HashSet<TransitionFigure> dependencies;
    protected StateFigureModel myModel;
    protected static ArrayList<Action> myActions = new ArrayList<Action>();

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

    private class TriggerTextFigure extends TextFigure
    {
    	public TriggerTextFigure()
    	{
    		super();
    	}
    	
    	public TriggerTextFigure(String text)
    	{
    		super(text);
    	}
    	
    	@Override
    	public void setText(String newText)
    	{
    		String oldTrigger = getText();
    		ArrayList<String> actions = (ArrayList<String>)myModel.getActionsByTrigger(oldTrigger);
    		try
    		{
    			myModel.removeAllActionsOfTrigger(oldTrigger);
    			myModel.addActionsForTrigger(newText, actions);
    			super.setText(newText);
    		}
    		catch(Exception e)
    		{
    			// The trigger was bad. Because all actions are added via the addAction function,
    			// Exception thrown should never be from ill-formed action name.'
    			super.setText(oldTrigger);
    			myModel.addActionsForTrigger(oldTrigger, actions);
    		}
    	}
    }
    
    /**
     * A text figure meant to be used to store Actions.
     */
    private class ActionTextFigure extends TextFigure
    {
    	private TriggerTextFigure myTrigger;
    	
    	/**
    	 * Constructs a text figure meant for Actions. Takes in the text for the figure 
    	 * and a TriggerTextFigure that it is associated with. 
    	 * @param text
    	 * @param triggerText
    	 */
    	public ActionTextFigure(String text, TriggerTextFigure triggerText)
    	{
    		super(text);
    		myTrigger = triggerText;
    	}
    	
    	@Override
    	public void setText(String newText)
    	{
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
    	}
    }

    
    /**
     * Initializes a state figure with the shape given by the AbstractAttributedFigure. 
     */
    public StateFigure() {
        super(new RectangleFigure());

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
        
        //TODO Decide whether to throw this statement out.
        @SuppressWarnings("unused")
		ResourceBundleUtil labels =
        		ResourceBundleUtil.getBundle("org.jhotdraw.samples.pert.Labels");

        setName(myModel.getName());


        dependencies = new HashSet<TransitionFigure>();
        nameFigure.addFigureListener(new NameAdapter(this));
        
        System.out.println("Testing add default action");
        addActionTextFigure();
        
    }

    
    /**
     * Returns a list of {@link Action}s to be used in the population of a 
     * popup menu. 
     */
    @Override
    public Collection<Action> getActions(Point2D.Double p)
    {
    	return myActions; 	
    }
    
    /**
     * Adds a new action to the StateFigure and stores the data in the {@link StateFigureModel}
     * @param newTrigger The String that will be the new action's trigger
     * @param newAction The String that will be the new action.
     */
    public void addActionTextFigure(String newTrigger, String newAction)
    {
    	willChange();
    	ListFigure actionToAdd = new ListFigure();
    	actionToAdd.setLayouter(new HorizontalLayouter());
    	TriggerTextFigure trigger = new TriggerTextFigure(newTrigger);
    	
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
    	myModel.addAction(newTrigger, newAction);
    }
    
    /**
     * Adds a new action to the StateFigure and stores the data in the {@link StateFigureModel}
     * with default values.
     */
    public void addActionTextFigure()
    {
    	addActionTextFigure(myModel.DEFAULT_ACTION_TRIGGER, myModel.DEFAULT_ACTION_NAME);
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

    /*public void setDuration(int newValue) {
        int oldValue = getDuration();
        getDurationFigure().setText(Integer.toString(newValue));
        if (oldValue != newValue) {
            for (StateFigure succ : getSuccessors()) {
                succ.updateStartTime();
            }

        }
    }

    public int getDuration() {
        try {
            return Integer.valueOf(getDurationFigure().getText());
        } catch (NumberFormatException e) {
            return 0;
        }

    }*/

    // Good example for how to make changes to internal figures.
    /*public void updateStartTime() {
        willChange();
        int oldValue = getStartTime();
        int newValue = 0;
        for (StateFigure pre : getPredecessors()) {
            newValue = Math.max(newValue,
                    pre.getStartTime() + pre.getDuration());
        }

        getStartTimeFigure().setText(Integer.toString(newValue));
        if (newValue != oldValue) {
            for (StateFigure succ : getSuccessors()) {
                // The if-statement here guards against
                // cyclic task dependencies. 
                if (!this.isDependentOf(succ)) {
                    succ.updateStartTime();
                }

            }
        }
        changed();
    }*/

    private TextFigure getNameFigure() {
        return (TextFigure) ((ListFigure) getChild(0)).getChild(0);
    }
    
    @Override
    public StateFigure clone() {
        StateFigure that = (StateFigure) super.clone();
        that.dependencies = new HashSet<TransitionFigure>();
        that.getNameFigure().addFigureListener(new NameAdapter(that));
        return that;
    }

    /**
     * Adds a new {@link Action} to this StateFigure's list of actions. 
     * @param newAction
     */
    public static void addAction(Action newAction)
    {
    	myActions.add(newAction);
    }
    
    @Override
    public void read(DOMInput in) throws IOException {
        double x = in.getAttribute("x", 0d);
        double y = in.getAttribute("y", 0d);
        double w = in.getAttribute("w", 0d);
        double h = in.getAttribute("h", 0d);
        setBounds(new Point2D.Double(x, y), new Point2D.Double(x + w, y + h));
        readAttributes(in);
        in.openElement("state");
        in.openElement("name");
        setName((String) in.readObject());
        in.closeElement();
        in.closeElement();
    }

    @Override
    public void write(DOMOutput out) throws IOException {
        Rectangle2D.Double r = getBounds();
        out.addAttribute("x", r.x);
        out.addAttribute("y", r.y);
        writeAttributes(out);
        out.openElement("state");
        out.openElement("name");
        out.writeObject(getName());
        out.closeElement();
        out.closeElement();
    }

    @Override
    public int getLayer() {
        return 0;
    }

    /**
     * Sets the name of StateFigure's associated {@link StateFigureModel} if the name is valid.
     * If the name is not valid, the {@link StateFigureModel} remains unchanged and the StateFigure's name is returned
     * to the old value.
     * @param evt A {@link FigureEvent} created when the State's name is changed.
     */
    public void setNameIfValid(FigureEvent evt)
    {
    	try
    	{
    		myModel.setName((String)evt.getNewValue());
    	}
    	catch(Exception e)
    	{
    		setName((String)evt.getOldValue());
    	}
    }
    
    public Set<TransitionFigure> getDependencies() {
        return Collections.unmodifiableSet(dependencies);
    }

    public void addDependency(TransitionFigure f) {
        dependencies.add(f);
    }

    public void removeDependency(TransitionFigure f) {
        dependencies.remove(f);
    }

    /**
     * Returns dependent StateFigure which are directly connected via a
     * TransitionFigure to this StateFigure.
     */
    public List<StateFigure> getSuccessors() {
        LinkedList<StateFigure> list = new LinkedList<StateFigure>();
        for (TransitionFigure c : getDependencies()) {
            if (c.getStartFigure() == this) {
                list.add((StateFigure) c.getEndFigure());
            }

        }
        return list;
    }

    /**
     * Returns predecessor StateFigures which are directly connected via a
     * TransitionFigure to this StateFigure.
     */
    public List<StateFigure> getPredecessors() {
        LinkedList<StateFigure> list = new LinkedList<StateFigure>();
        for (TransitionFigure c : getDependencies()) {
            if (c.getEndFigure() == this) {
                list.add((StateFigure) c.getStartFigure());
            }

        }
        return list;
    }
    
    /**
     * Returns this StateFigure's associated {@link StateFigureModel}.
     * @return {@link StateFigureModel}
     */
    public StateFigureModel getModel()
    {
    	return myModel;
    }

    /**
     * Returns true, if the current task is a direct or
     * indirect dependent of the specified task.
     * If the dependency is cyclic, then this method returns true
     * if <code>this</code> is passed as a parameter and for every other
     * task in the cycle.
     */
    public boolean isDependentOf(StateFigure t) {
        if (this == t) {
            return true;
        }

        for (StateFigure pre : getPredecessors()) {
            if (pre.isDependentOf(t)) {
                return true;
            }

        }
        return false;
    }

    @Override
    public String toString() {
        return "StateFigure#" + hashCode() + " " + getName();
    }

	public void serialize(DOMOutput out) throws IOException {
		 Rectangle2D.Double r = getBounds();
	        out.addAttribute("x", r.x);
	        out.addAttribute("y", r.y);
	        writeAttributes(out);
	        out.openElement("model");
	        out.openElement("name");
	        out.writeObject(getName());
	        out.closeElement();
	        out.closeElement();
		
	}
}

