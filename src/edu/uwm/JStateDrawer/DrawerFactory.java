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
        if (o == null) {
            // nothing to do
        } else if (o instanceof DOMStorable) {
        	((DOMStorable) o).write(out);
        } else if (o instanceof String) {
            out.addText((String) o);
        } else if (o instanceof Integer) {
            out.addText(o.toString());
        } else if (o instanceof Long) {
            out.addText(o.toString());
        } else if (o instanceof Double) {
            out.addText(o.toString());
        } else if (o instanceof Float) {
            out.addText(o.toString());
        } else if (o instanceof Boolean) {
            out.addText(o.toString());
        } else if (o instanceof Color) {
            Color c = (Color) o;
            out.addAttribute("rgba", "#" + Integer.toHexString(c.getRGB()));
        } else if (o instanceof byte[]) {
            byte[] a = (byte[]) o;
            for (int i = 0; i < a.length; i++) {
                out.openElement("byte");
                write(out, a[i]);
                out.closeElement();
            }
        } else if (o instanceof boolean[]) {
            boolean[] a = (boolean[]) o;
            for (int i = 0; i < a.length; i++) {
                out.openElement("boolean");
                write(out, a[i]);
                out.closeElement();
            }
        } else if (o instanceof char[]) {
            char[] a = (char[]) o;
            for (int i = 0; i < a.length; i++) {
                out.openElement("char");
                write(out, a[i]);
                out.closeElement();
            }
        } else if (o instanceof short[]) {
            short[] a = (short[]) o;
            for (int i = 0; i < a.length; i++) {
                out.openElement("short");
                write(out, a[i]);
                out.closeElement();
            }
        } else if (o instanceof int[]) {
            int[] a = (int[]) o;
            for (int i = 0; i < a.length; i++) {
                out.openElement("int");
                write(out, a[i]);
                out.closeElement();
            }
        } else if (o instanceof long[]) {
            long[] a = (long[]) o;
            for (int i = 0; i < a.length; i++) {
                out.openElement("long");
                write(out, a[i]);
                out.closeElement();
            }
        } else if (o instanceof float[]) {
            float[] a = (float[]) o;
            for (int i = 0; i < a.length; i++) {
                out.openElement("float");
                write(out, a[i]);
                out.closeElement();
            }
        } else if (o instanceof double[]) {
            double[] a = (double[]) o;
            for (int i = 0; i < a.length; i++) {
                out.openElement("double");
                write(out, a[i]);
                out.closeElement();
            }
        } else if (o instanceof Font) {
            Font f = (Font) o;
            out.addAttribute("name", f.getName());
            out.addAttribute("style", f.getStyle());
            out.addAttribute("size", f.getSize());
        } else if (o instanceof Enum) {
            Enum e = (Enum) o;
            out.addAttribute("type", getEnumName(e));
            out.addText(getEnumValue(e));
        } else {
            throw new IllegalArgumentException("Unsupported object type:" + o);
        }
    }
    
    //TODO Tell DOM to scan the next element in various locations.
    @Override
    public Object read(DOMInput in) throws IOException{
    	Object o = super.read(in);
    	/*Object r = null;
    	Object i = null;
    	String s = "";
    	
    	if (in.getTagName().equals("state")){
    		r = new StateFigure();
    		((StateFigure) r).setName(in.getAttribute("name", "default"));
    		//while (true){
    			if (in.getTagName().equals("action")) ((StateFigure) r).getModel().addAction(in.getAttribute("trigger", "DEFAULT"), in.getText());
    			if (in.getTagName().equals("transition")) {
    				s = in.getAttribute("trigger", "DEFAULT");
    				// Create new TransitionFigure with no associated start and end.
    				i = new TransitionFigure();
    				// Set the start for the new transition figure and model as the current StateFigure.
    				((TransitionFigure) i).setStartFigure((StateFigure) r);
    				// Set the name for the figure and the trigger for the model. This also updates start figure and model. 
    				((TransitionFigure) i).setName(s);
    				// Check whether target is current state.
    				if (s.equals(((StateFigure) r).getName())) {
    					((StateFigure) r).addIncomingTransition((TransitionFigure) i);
    					((TransitionFigure) i).setEndFigure((StateFigure) r);
    				}
    				
    				// The default here is that if a transition doesn't have a target, it points
    				// to the end state. In the future this may point to null to cause errors
    				// and force this to be handled.
    				s = in.getAttribute("target", "end");
    				
    				((TransitionFigure) i).getModel().setTarget(s);
    				
    			}
    		//}
    	}
    	else if(in.getTagName().equals("startState"))
    	{
    		r = new StartStateFigure();
    		((StartStateFigure) r).setName(in.getAttribute("name", "start"));
    		/*while (true)
    		{
    			if(in.getTagName().equals("transition"))
    			{
    				s = in.getAttribute("trigger", "DEFAULT");
    				i = new TransitionFigure();
    				// Set the start for the new transition figure and model as the current StateFigure.
    				((TransitionFigure) i).setStartFigure((StateFigure) r);
    				// Set the name for the figure and the trigger for the model. This also updates start figure and model. 
    				((TransitionFigure) i).setName(s);
    				
    				s = in.getAttribute("target", "end");
    				
    				((TransitionFigure) i).getModel().setTarget(s);
    			}
    		//}
    	}
    	else if(in.getTagName().equals("endState"))
    	{
    		r = new EndStateFigure();
    		((EndStateFigure) r).setName(in.getAttribute("name", "end"));
    	}*/
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
