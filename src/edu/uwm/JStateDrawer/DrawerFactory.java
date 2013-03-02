package edu.uwm.JStateDrawer;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;

import org.jhotdraw.draw.locator.RelativeLocator;
import org.jhotdraw.draw.connector.LocatorConnector;
import org.jhotdraw.draw.connector.ChopRectangleConnector;
import org.jhotdraw.draw.decoration.ArrowTip;
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
        	if(serializeFile)
        	{
        		((StateFigure) o).serialize(out);
        	}
        	else{
        		((DOMStorable) o).write(out);
        	}
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
