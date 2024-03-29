package edu.uwm.JStateDrawer;

import org.jhotdraw.app.action.edit.RedoAction;
import org.jhotdraw.app.action.edit.UndoAction;
import org.jhotdraw.draw.io.OutputFormat;
import org.jhotdraw.draw.io.InputFormat;
import org.jhotdraw.draw.io.ImageOutputFormat;
import org.jhotdraw.draw.print.DrawingPageable;
import org.jhotdraw.draw.io.DOMStorableInputOutputFormat;

import java.awt.print.Pageable;
import java.util.*;
import org.jhotdraw.gui.*;
import org.jhotdraw.undo.*;
import org.jhotdraw.util.*;
import java.awt.*;
import java.beans.*;
import java.io.*;
import java.lang.reflect.*;
import java.net.URI;
import javax.swing.*;
import javax.swing.border.*;
import org.jhotdraw.app.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.draw.action.*;
import org.jhotdraw.gui.URIChooser;

/**
 * A view for JStateDrawer diagrams based on the JHotDraw PertView class.
 *
 */
@SuppressWarnings("serial")
public class DrawerView extends AbstractView{
	
    public final static String GRID_VISIBLE_PROPERTY = "gridVisible";
    /**
     * Each view uses its own undo redo manager.
     * This allows for undoing and redoing actions per view.
     */
    private UndoRedoManager undo;
    /**
     * Depending on the type of an application, there may be one editor per
     * view, or a single shared editor for all views.
     */
    private DrawingEditor editor;

    /**
     * Creates a new view.
     */
    public DrawerView() {
        initComponents();

        scrollPane.setLayout(new PlacardScrollPaneLayout());
        scrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
        
        setEditor(new DefaultDrawingEditor());
        undo = new UndoRedoManager();
        
        // The following line should set states with 4 handles?
        ((DefaultDrawingView)view).setHandleDetailLevel(0);
        view.setDrawing(createDrawing());
        view.getDrawing().addUndoableEditListener(undo);
        initActions();
        undo.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                setHasUnsavedChanges(undo.hasSignificantEdits());
            }
        });

        ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");

        JPanel placardPanel = new JPanel(new BorderLayout());
        javax.swing.AbstractButton pButton;
        pButton = ButtonFactory.createZoomButton(view);
        pButton.putClientProperty("Quaqua.Button.style", "placard");
        pButton.putClientProperty("Quaqua.Component.visualMargin", new Insets(0, 0, 0, 0));
        pButton.setFont(UIManager.getFont("SmallSystemFont"));
        placardPanel.add(pButton, BorderLayout.WEST);
        pButton = ButtonFactory.createToggleGridButton(view);
        pButton.putClientProperty("Quaqua.Button.style", "placard");
        pButton.putClientProperty("Quaqua.Component.visualMargin", new Insets(0, 0, 0, 0));
        pButton.setFont(UIManager.getFont("SmallSystemFont"));
        labels.configureToolBarButton(pButton, "view.toggleGrid.placard");
        placardPanel.add(pButton, BorderLayout.EAST);
        scrollPane.add(placardPanel, JScrollPane.LOWER_LEFT_CORNER);

        //setGridVisible(preferences.getBoolean("view.gridVisible", false));
        //setScaleFactor(preferences.getDouble("view.scaleFactor", 1d));
    }

    /**
     * Creates a new Drawing for this view.
     */
    protected Drawing createDrawing() {
        DefaultDrawing drawing = new DefaultDrawing();
        DOMStorableInputOutputFormat ioFormat =
                new DOMStorableInputOutputFormat(new DrawerFactory());
        LinkedList<InputFormat> inputFormats = new LinkedList<InputFormat>();
        inputFormats.add(ioFormat);
        drawing.setInputFormats(inputFormats);
        LinkedList<OutputFormat> outputFormats = new LinkedList<OutputFormat>();
        outputFormats.add(ioFormat);
        outputFormats.add(new ImageOutputFormat());
        drawing.setOutputFormats(outputFormats);
        return drawing;
    }

    /**
     * Creates a Pageable object for printing this view.
     */
    public Pageable createPageable() {
        return new DrawingPageable(view.getDrawing());

    }

    public DrawingEditor getEditor() {
        return editor;
    }

    public void setEditor(DrawingEditor newValue) {
        DrawingEditor oldValue = editor;
        if (oldValue != null) {
            oldValue.remove(view);
        }
        editor = newValue;
        if (newValue != null) {
            newValue.add(view);
        }
    }

    public void setGridVisible(boolean newValue) {
        boolean oldValue = isGridVisible();
        view.setConstrainerVisible(newValue);
        firePropertyChange(GRID_VISIBLE_PROPERTY, oldValue, newValue);
        preferences.putBoolean("view.gridVisible", newValue);
    }

    public boolean isGridVisible() {
        return view.isConstrainerVisible();
    }

    public double getScaleFactor() {
        return view.getScaleFactor();
    }

    public void setScaleFactor(double newValue) {
        double oldValue = getScaleFactor();
        view.setScaleFactor(newValue);

        firePropertyChange("scaleFactor", oldValue, newValue);
        preferences.putDouble("view.scaleFactor", newValue);
    }
    
    public Drawing getDrawing()
    {
    	return view.getDrawing();
    }

    /**
     * Initializes view specific actions.
     */
    private void initActions() {
        getActionMap().put(UndoAction.ID, undo.getUndoAction());
        getActionMap().put(RedoAction.ID, undo.getRedoAction());
    }
    

    @Override
    protected void setHasUnsavedChanges(boolean newValue) {
        super.setHasUnsavedChanges(newValue);
        undo.setHasSignificantEdits(newValue);
    }
    
    /**
     * Functionally very similar to {@link write(URI f, URIChooser chooser)},
     * except diagram checking is included. If the diagram is invalid, the file is not serialized.
     * @param f
     * @param chooser
     * @throws IOException
     */
    public void serialize(URI f, URIChooser chooser) throws IOException
    {
    	Drawing drawing = view.getDrawing();
    	DrawingChecker checker = new DrawingChecker();
    	if(checker.validateCurrentDrawing(drawing, false))
    	{
    		OutputFormat outputFormat = drawing.getOutputFormats().get(0);
        	outputFormat.write(f, drawing);
    	}
    	else
    	{
    		JOptionPane.showMessageDialog(editor.getActiveView().getComponent(),
					"The drawing is invalid and was not serialized for the following reason:\n" + checker.getErrorString(),
					"Drawing Invalid", JOptionPane.ERROR_MESSAGE);
    	}
    }

    /**
     * Writes the view to the specified uri.
     */
    @Override
    public void write(URI f, URIChooser chooser) throws IOException {
        Drawing drawing = view.getDrawing();
        OutputFormat outputFormat = drawing.getOutputFormats().get(0);
        outputFormat.write(f, drawing);
    }

    /**
     * Reads the view from the specified uri.
     */
    @Override
    public void read(URI f, URIChooser chooser) throws IOException {
        try {
            final Drawing drawing = createDrawing();
            InputFormat inputFormat = drawing.getInputFormats().get(0);
            inputFormat.read(f, drawing, true);
           
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    view.getDrawing().removeUndoableEditListener(undo);
                    view.setDrawing(drawing);
                    view.getDrawing().addUndoableEditListener(undo);
                    undo.discardAllEdits();
                }
            });
        } catch (InterruptedException e) {
            InternalError error = new InternalError();
            e.initCause(e);
            throw error;
        } catch (InvocationTargetException e) {
            InternalError error = new InternalError();
            e.initCause(e);
            throw error;
        }
    }
    
    /**
     * Loads a serialized version of a drawing for use in simulation. Calling
     * methods should check whether the returned value is null. When null is returned,
     * the drawing to be loaded contained one or more errors that is shown to the user via a
     * message box. 
     * @param f
     * @return A {@link Drawing} if loading is successful, but null if the loaded drawing is not valid.
     * @throws IOException
     */
    public Drawing importDrawing(URI f) throws IOException
    {
    	final Drawing drawing = createDrawing();
    	DrawerFactory.importFile = true;
    	InputFormat inputFormat = drawing.getInputFormats().get(0);
    	inputFormat.read(f, drawing);
    	
    	DrawingChecker checker = new DrawingChecker();
    	if(!checker.validateCurrentDrawing(drawing, true))
    	{
    		JOptionPane.showMessageDialog(editor.getActiveView().getComponent(),
					"The drawing is invalid and cannot be loaded for the following reason:\n" + checker.getErrorString(),
					"Drawing Invalid", JOptionPane.ERROR_MESSAGE);
    		return null;
    	}
    	DrawerFactory.importFile = false;
    	
    	return drawing;
    }
    
    /**
     * Clears the view.
     */
    @Override
    public void clear() {
        final Drawing newDrawing = createDrawing();
        try {
            SwingUtilities.invokeAndWait(new Runnable() {

                @Override
                public void run() {
                    view.getDrawing().removeUndoableEditListener(undo);
                    view.setDrawing(newDrawing);
                    view.getDrawing().addUndoableEditListener(undo);
                    undo.discardAllEdits();
                }
            });
        } catch (InvocationTargetException ex) {
            ex.printStackTrace();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean canSaveTo(URI uri) {
        return uri.getPath().endsWith(".xml");
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scrollPane = new javax.swing.JScrollPane();
        view = new org.jhotdraw.draw.DefaultDrawingView();

        setLayout(new java.awt.BorderLayout());

        scrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setViewportView(view);

        add(scrollPane, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane scrollPane;
    private org.jhotdraw.draw.DefaultDrawingView view;
    // End of variables declaration//GEN-END:variables
}
