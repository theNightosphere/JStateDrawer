package edu.uwm.JStateDrawer;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;

import org.jhotdraw.draw.liner.CurvedLiner;
import org.jhotdraw.draw.locator.RelativeLocator;
import org.jhotdraw.draw.connector.LocatorConnector;
import org.jhotdraw.draw.connector.ChopRectangleConnector;
import org.jhotdraw.draw.decoration.ArrowTip;

import edu.uwm.JStateDrawer.Models.StateFigureModel;
import edu.uwm.JStateDrawer.figures.*;

import org.jhotdraw.draw.*;
import org.jhotdraw.xml.*;
/**
 * DrawerFactory, based originally on the PertFactory class from JHotDraw's 
 * samples package
 * 
 */
public class DrawerFactory extends DefaultDOMFactory {
	private boolean serializeFile = false;
    private final static Object[][] classTagArray = {
        { DefaultDrawing.class, "DrawerDiagram" },
        { StateFigure.class, "state" },
        { StartStateFigure.class, "startState"},
        { EndStateFigure.class, "endState" },
        { CurvedLiner.class, "CurvedLiner" },
        { TransitionFigure.class, "transition" },
        { ListFigure.class, "list" },
        { TextFigure.class, "text" },
        { GroupFigure.class, "g" },
        { TextAreaFigure.class, "ta" },
        { SeparatorLineFigure.class, "separator" },
        
        { ChopRectangleConnector.class, "rectConnector" },
        { LocatorConnector.class, "locConnector" },
        { RelativeLocator.class, "relativeLocator" },
        { ArrowTip.class, "arrowTip" }
    };
    
    /**
     * Extends the write function found in JavaPrimitivesDOMFactory to 
     * allow data to be 'serialized' instead of 'saved' if the DrawerFactory's
     * serializeFile member is true.
     */
    @Override
    public void write(DOMOutput out, Object o) throws IOException {
        super.write(out, o);
    }
    
    //TODO Tell DOM to scan the next element in various locations.
    @Override
    public Object read(DOMInput in) throws IOException{
    	Object o = super.read(in);
    	return o;
    }
    
    public void setSerializeFile(boolean newValue)
    {
    	serializeFile = newValue;
    }
    
    public boolean getSerializeFile()
    {
    	return serializeFile;
    }
    
    /** Creates a new instance. */
    public DrawerFactory() {
        for (Object[] o : classTagArray) {
            addStorableClass((String) o[1], (Class) o[0]);
        }
    }
}
