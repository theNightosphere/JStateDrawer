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
import java.awt.*;
import static org.jhotdraw.draw.AttributeKeys.*;
import org.jhotdraw.draw.*;

/**
 * DependencyFigure.
 *
 * @author Werner Randelshofer.
 * @version $Id: DependencyFigure.java 718 2010-11-21 17:49:53Z rawcoder $
 */
public class TransitionFigure extends LineConnectionFigure {

    /** Creates a new instance. */
    public TransitionFigure() {
        set(STROKE_COLOR, new Color(0x000099));
        set(STROKE_WIDTH, 1d);
        set(END_DECORATION, new ArrowTip());

        setAttributeEnabled(END_DECORATION, false);
        setAttributeEnabled(START_DECORATION, false);
        setAttributeEnabled(STROKE_DASHES, false);
        setAttributeEnabled(FONT_ITALIC, false);
        setAttributeEnabled(FONT_UNDERLINE, false);
    }

    /**
     * Checks if two figures can be connected. Implement this method
     * to constrain the allowed connections between figures.
     */
    @Override
    public boolean canConnect(Connector start, Connector end) {
        if ((start.getOwner() instanceof StateFigure)
                && (end.getOwner() instanceof StateFigure)) {

            StateFigure sf = (StateFigure) start.getOwner();
            StateFigure ef = (StateFigure) end.getOwner();

            // Disallow multiple connections to same dependent
            if (ef.getPredecessors().contains(sf)) {
                return false;
            }

            // Disallow cyclic connections
            return !sf.isDependentOf(ef);
        }

        return false;
    }

    @Override
    public boolean canConnect(Connector start) {
        return (start.getOwner() instanceof StateFigure);
    }

    /**
     * Handles the disconnection of a connection.
     * Override this method to handle this event.
     */
    @Override
    protected void handleDisconnect(Connector start, Connector end) {
        StateFigure sf = (StateFigure) start.getOwner();
        StateFigure ef = (StateFigure) end.getOwner();

        sf.removeDependency(this);
        ef.removeDependency(this);
    }

    /**
     * Handles the connection of a connection.
     * Override this method to handle this event.
     */
    @Override
    protected void handleConnect(Connector start, Connector end) {
        StateFigure sf = (StateFigure) start.getOwner();
        StateFigure ef = (StateFigure) end.getOwner();

        sf.addDependency(this);
        ef.addDependency(this);
    }

    @Override
    public TransitionFigure clone() {
        TransitionFigure that = (TransitionFigure) super.clone();

        return that;
    }

    @Override
    public int getLayer() {
        return 1;
    }

    @Override
    public void removeNotify(Drawing d) {
        if (getStartFigure() != null) {
            ((StateFigure) getStartFigure()).removeDependency(this);
        }
        if (getEndFigure() != null) {
            ((StateFigure) getEndFigure()).removeDependency(this);
        }
        super.removeNotify(d);
    }
}
