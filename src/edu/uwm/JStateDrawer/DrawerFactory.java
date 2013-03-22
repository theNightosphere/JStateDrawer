package edu.uwm.JStateDrawer;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;

import org.jhotdraw.draw.liner.CurvedLiner;
import org.jhotdraw.draw.locator.RelativeLocator;
import org.jhotdraw.draw.connector.LocatorConnector;
import org.jhotdraw.draw.connector.ChopRectangleConnector;
import org.jhotdraw.draw.decoration.ArrowTip;

import edu.uwm.JStateDrawer.Models.EndStateModel;
import edu.uwm.JStateDrawer.Models.StartStateModel;
import edu.uwm.JStateDrawer.Models.StateFigureModel;
import edu.uwm.JStateDrawer.Models.TransitionModel;
import edu.uwm.JStateDrawer.figures.*;

import org.jhotdraw.draw.*;
import org.jhotdraw.xml.*;
/**
 * DrawerFactory, based originally on the PertFactory class from JHotDraw's 
 * samples package
 * 
 */
public class DrawerFactory extends DefaultDOMFactory {
	public static boolean serializeFile = false;
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
        
        { StateFigureModel.class, "stateModel"},
        { StartStateModel.class, "startModel" },
        { EndStateModel.class, "endModel" },
        { TransitionModel.class, "transitionModel" },
        
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
        } else if (isStateFigureModel(o)) {
        	((StateFigureModel) o).write(out);
        } else if (o instanceof TransitionModel) {
        	((TransitionModel) o).write(out);
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
    
    /**
     * Determines whether the object passed as a parameter is one of the state model objects.
     * @param o
     * @return
     */
    private boolean isStateFigureModel(Object o)
    {
    	return (o instanceof StateFigureModel || o instanceof StartStateModel || o instanceof EndStateModel);
    }
    
    //TODO Tell DOM to scan the next element in various locations.
    @Override
    public Object read(DOMInput in) throws IOException{
    	Object o;
    	
    	String tagName = in.getTagName();
        if (tagName.equals("null")) {
            o = null;
        } else if (tagName.equals("boolean")) {
            o = Boolean.valueOf(in.getText());
        } else if (tagName.equals("byte")) {
            o = Byte.decode(in.getText());
        } else if (tagName.equals("short")) {
            o = Short.decode(in.getText());
        } else if (tagName.equals("int")) {
            o = Integer.decode(in.getText());
        } else if (tagName.equals("long")) {
            o = Long.decode(in.getText());
        } else if (tagName.equals("float")) {
            o = new Float(Float.parseFloat(in.getText()));
        } else if (tagName.equals("double")) {
            o = new Double(Double.parseDouble(in.getText()));
        } else if (tagName.equals("string")) {
            o = in.getText();
        } else if (tagName.equals("enum")) {
            o = createEnum(in.getAttribute("type", (String) null), in.getText());
        } else if (tagName.equals("color")) {
            o = new Color(in.getAttribute("rgba", 0xff));
        } else if (tagName.equals("font")) {
            o = new Font(in.getAttribute("name", "Dialog"), in.getAttribute("style", 0), in.getAttribute("size", 0));
        } else if (tagName.equals("byteArray")) {
            byte[] a = new byte[in.getElementCount()];
            for (int i = 0; i < a.length; i++) {
                a[i] = ((Byte) in.readObject(i)).byteValue();
            }
            o = a;
        } else if (tagName.equals("shortArray")) {
            short[] a = new short[in.getElementCount()];
            for (int i = 0; i < a.length; i++) {
                a[i] = ((Short) in.readObject(i)).shortValue();
            }
            o = a;
        } else if (tagName.equals("intArray")) {
            int[] a = new int[in.getElementCount()];
            for (int i = 0; i < a.length; i++) {
                a[i] = ((Integer) in.readObject(i)).intValue();
            }
            o = a;
        } else if (tagName.equals("longArray")) {
            long[] a = new long[in.getElementCount()];
            for (int i = 0; i < a.length; i++) {
                a[i] = ((Long) in.readObject(i)).longValue();
            }
            o = a;
        } else if (tagName.equals("floatArray")) {
            float[] a = new float[in.getElementCount()];
            for (int i = 0; i < a.length; i++) {
                a[i] = ((Float) in.readObject(i)).floatValue();
            }
            o = a;
        } else if (tagName.equals("doubleArray")) {
            double[] a = new double[in.getElementCount()];
            for (int i = 0; i < a.length; i++) {
                a[i] = ((Double) in.readObject(i)).doubleValue();
            }
            o = a;
        } else if (tagName.equals("stateModel") ||
        		tagName.equals("startModel")||
        		tagName.equals("endModel")){
            StateFigureModel a = new StateFigureModel();
            a.read(in);
            o = a;
        } else if (tagName.equals("transitionModel"))
        {
        	TransitionModel a = new TransitionModel();
        	a.read(in);
        	o = a;
        }
        else
        {
        	o = create(in.getTagName());
        }
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
