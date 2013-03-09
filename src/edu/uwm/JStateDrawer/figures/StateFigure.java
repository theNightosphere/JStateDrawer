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
import org.jhotdraw.draw.layouter.VerticalLayouter;
import org.jhotdraw.draw.connector.LocatorConnector;
import org.jhotdraw.draw.handle.ConnectorHandle;
import java.io.IOException;
import java.awt.geom.*;
import static org.jhotdraw.draw.AttributeKeys.*;
import java.util.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.draw.handle.BoundsOutlineHandle;
import org.jhotdraw.geom.*;
import org.jhotdraw.util.*;
import org.jhotdraw.xml.*;

import edu.uwm.JStateDrawer.Models.StateFigureModel;

/**
 * TaskFigure.
 *
 * @author Werner Randelshofer.
 * @version $Id: TaskFigure.java 727 2011-01-09 13:23:59Z rawcoder $
 */
public class StateFigure extends GraphicalCompositeFigure {

    protected HashSet<TransitionFigure> dependencies;
    protected StateFigureModel myModel;

    /**
     * This adapter is used, to connect a TextFigure with the name of
     * the TaskFigure model.
     */
    private static class NameAdapter extends FigureAdapter {

        private StateFigure target;

        public NameAdapter(StateFigure target) {
            this.target = target;
        }

        @Override
        public void attributeChanged(FigureEvent e) {
            // We could fire a property change event here, in case
            // some other object would like to observe us.
            //target.firePropertyChange("name", e.getOldValue(), e.getNewValue());
        }
    }

    /*private static class DurationAdapter extends FigureAdapter {

        private StateFigure target;

        public DurationAdapter(StateFigure target) {
            this.target = target;
        }

        @Override
        public void attributeChanged(FigureEvent evt) {
            // We could fire a property change event here, in case
            // some other object would like to observe us.
            //target.firePropertyChange("duration", e.getOldValue(), e.getNewValue());
            for (StateFigure succ : target.getSuccessors()) {
                succ.updateStartTime();
            }
        }
    }*/

    
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

        /*TextFigure durationFigure;
        attributeCompartment.add(durationFigure = new TextFigure());
        durationFigure.set(FONT_BOLD, true);
        durationFigure.setText("0");
        durationFigure.setAttributeEnabled(FONT_BOLD, false);*/

        setAttributeEnabled(STROKE_DASHES, false);

        myModel = new StateFigureModel();
        
        ResourceBundleUtil labels =
        		ResourceBundleUtil.getBundle("org.jhotdraw.samples.pert.Labels");

        setName(myModel.getName());


        dependencies = new HashSet<TransitionFigure>();
        nameFigure.addFigureListener(new NameAdapter(this));
        
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
                ch.setToolTipText("Drag the connector to a dependent task.");
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

    /**
     * This provides a method for {@link StartStateFigure} and {@link EndStateFigure}
     * to call the clone method of {@link GraphicalCompositeFigure} without cloning
     * the attributes found in StateFigure.
     * @param cloneSuperSkipStateFigure
     * @return
     */
    public StateFigure clone(boolean cloneSuperSkipStateFigure)
    {
    	if(cloneSuperSkipStateFigure)
    	{
    		return (StateFigure) super.clone();
    	}
    	else
    	{
    		return this.clone();
    	}
    }
    
    @Override
    public StateFigure clone() {
        StateFigure that = (StateFigure) super.clone();
        that.dependencies = new HashSet<TransitionFigure>();
        that.getNameFigure().addFigureListener(new NameAdapter(that));
        return that;
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
     * Returns dependent PertTasks which are directly connected via a
     * PertDependency to this TaskFigure.
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
     * Returns predecessor PertTasks which are directly connected via a
     * PertDependency to this TaskFigure.
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
