package edu.uwm.JStateDrawer;

import edu.umd.cs.findbugs.annotations.Nullable;
import org.jhotdraw.app.action.view.ViewPropertyAction;
import org.jhotdraw.app.action.view.ToggleViewPropertyAction;
import org.jhotdraw.app.action.file.ExportFileAction;
import org.jhotdraw.draw.tool.Tool;
import org.jhotdraw.draw.tool.CreationTool;
import org.jhotdraw.draw.tool.TextAreaCreationTool;
import org.jhotdraw.draw.tool.ConnectionTool;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import org.jhotdraw.app.*;
import org.jhotdraw.app.action.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.draw.action.*;
import org.jhotdraw.gui.JFileURIChooser;
import org.jhotdraw.gui.URIChooser;
import org.jhotdraw.gui.filechooser.ExtensionFileFilter;
import org.jhotdraw.util.*;
import edu.uwm.JStateDrawer.figures.*;
import edu.uwm.JStateDrawer.Actions.*;

/**
 * DrawerApplicationModel, based originally on the PertApplicationModel class in the
 * JHotDraw samples package.
 */
public class DrawerApplicationModel extends DefaultApplicationModel {

    private final static double[] scaleFactors = {5, 4, 3, 2, 1.5, 1.25, 1, 0.75, 0.5, 0.25, 0.10};

    public static TransitionFigure transitionTemplate = new TransitionFigure();

    private static class ToolButtonListener implements ItemListener {

        private Tool tool;
        private DrawingEditor editor;

        public ToolButtonListener(Tool t, DrawingEditor editor) {
            this.tool = t;
            this.editor = editor;
        }

        @Override
        public void itemStateChanged(ItemEvent evt) {
            if (evt.getStateChange() == ItemEvent.SELECTED) {
                editor.setTool(tool);
            }
        }
    }
    
    /**
     * This editor is shared by all views.
     */
    private DefaultDrawingEditor sharedEditor;
    private HashMap<String, Action> actions;

    /** Creates a new instance. */
    public DrawerApplicationModel() {
    }

    @Override
    public ActionMap createActionMap(Application a, @Nullable View v) {
        ActionMap m = super.createActionMap(a, v);
        ResourceBundleUtil drawLabels = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");
        AbstractAction aa;

        m.put(ExportFileAction.ID, new ExportFileAction(a, v));
        m.put("view.toggleGrid", aa = new ToggleViewPropertyAction(a, v, DrawerView.GRID_VISIBLE_PROPERTY));
        m.put(SerializeFileAction.ID, new SerializeFileAction(a, v));
        m.put(RemoveActionAction.ID, new RemoveActionAction(a, v));
        m.put(CheckCurrentDrawingAction.ID, new CheckCurrentDrawingAction(a, v));
        m.put(SimulateCurrentDrawingAction.ID, new SimulateCurrentDrawingAction(a,v));
        m.put(SimulateSerializedDrawingAction.ID, new SimulateSerializedDrawingAction(a,v));
//        m.put(AddTransitionActionAction.ID, new AddTransitionActionAction(a,v));
//        m.put(RemoveTransitionActionAction.ID, new RemoveTransitionActionAction(a,v));
//        m.put(AddNestedStartAction.ID, new AddNestedStartAction(a,v));
        drawLabels.configureAction(aa, "view.toggleGrid");
        for (double sf : scaleFactors) {
            m.put((int) (sf * 100) + "%",
                    aa = new ViewPropertyAction(a, v, DrawingView.SCALE_FACTOR_PROPERTY, Double.TYPE, new Double(sf)));
            aa.putValue(Action.NAME, (int) (sf * 100) + " %");

        }
        
        // Lines 86-91 comes from SVGApplicationModel's createActionMap method.
        DrawingEditor editor;
        if (a.isSharingToolsAmongViews()) {
            editor=getSharedEditor();
        } else {
           editor = (v == null) ? null : ((DrawerView)v).getEditor();
        }
        
        m.put(AddNewActionAction.ID, new AddNewActionAction(editor));
        
        return m;
    }

    public DefaultDrawingEditor getSharedEditor() {
        if (sharedEditor == null) {
            sharedEditor = new DefaultDrawingEditor();
        }
        return sharedEditor;
    }

    @Override
    public void initView(Application a, @Nullable View p) {
        if (a.isSharingToolsAmongViews()) {
            ((DrawerView) p).setEditor(getSharedEditor());
        }
    }

    /**
     * Creates top tool bar.
     */
    private void addCreationButtonsTo(JToolBar tb, final DrawingEditor editor) {
        // AttributeKeys for the entity sets
        HashMap<AttributeKey, Object> attributes;

        ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.samples.pert.Labels");
        ResourceBundleUtil drawLabels = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");
        ResourceBundleUtil StateLabels = ResourceBundleUtil.getBundle("edu.uwm.JStateDrawer.Actions.Labels");

        ButtonFactory.addSelectionToolTo(tb, editor);
        tb.addSeparator();

        //StateFigure button
        attributes = new HashMap<AttributeKey, Object>();
        attributes.put(AttributeKeys.FILL_COLOR, Color.white);
        attributes.put(AttributeKeys.STROKE_COLOR, Color.black);
        attributes.put(AttributeKeys.TEXT_COLOR, Color.black);
        ButtonFactory.addToolTo(tb, editor, new CreationTool(new StateFigure(false), attributes), "edit.createTask", labels);

        //StartStateFigure button
        attributes = new HashMap<AttributeKey, Object>();
        attributes.put(AttributeKeys.STROKE_COLOR, Color.black);
        attributes.put(AttributeKeys.FILL_COLOR, Color.black);
        ButtonFactory.addToolTo(tb, editor, new CreationTool(new StartStateFigure(), attributes), "edit.StartState", StateLabels);
        
        //EndStateFigure button
        attributes = new HashMap<AttributeKey, Object>();
        attributes.put(AttributeKeys.STROKE_COLOR,  Color.black);
        attributes.put(AttributeKeys.FILL_COLOR, Color.white);
        attributes.put(AttributeKeys.STROKE_WIDTH, 3d);
        ButtonFactory.addToolTo(tb, editor, new CreationTool(new EndStateFigure(), attributes), "edit.EndState", StateLabels);

     // Creates a nested state creation button.
        attributes = new HashMap<AttributeKey, Object>();
        attributes.put(AttributeKeys.FILL_COLOR, Color.white);
        attributes.put(AttributeKeys.STROKE_COLOR, Color.black);
        attributes.put(AttributeKeys.STROKE_WIDTH, 2d);
        ButtonFactory.addToolTo(tb, editor, new CreationTool(new StateFigure(true), attributes), "edit.internalState", StateLabels);
        
        attributes = new HashMap<AttributeKey, Object>();
        attributes.put(AttributeKeys.STROKE_COLOR, new Color(0x000099));
        ButtonFactory.addToolTo(tb, editor, new ConnectionTool(transitionTemplate, attributes), "edit.createDependency", labels);
        tb.addSeparator();
        ButtonFactory.addToolTo(tb, editor, new TextAreaCreationTool(new TextAreaFigure()), "edit.createTextArea", drawLabels);
        
        

    }

    /**
     * Creates toolbars for the application.
     * This class always returns an empty list. Subclasses may return other
     * values.
     */
    @Override
    public java.util.List<JToolBar> createToolBars(Application a, @Nullable View pr) {
        ResourceBundleUtil drawLabels = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");
        DrawerView p = (DrawerView) pr;

        DrawingEditor editor;
        if (p == null) {
            editor = getSharedEditor();
        } else {
            editor = p.getEditor();
        }

        LinkedList<JToolBar> list = new LinkedList<JToolBar>();
        JToolBar tb;
        tb = new JToolBar();
        addCreationButtonsTo(tb, editor);
        tb.setName(drawLabels.getString("window.drawToolBar.title"));
        list.add(tb);
        return list;
    }

    /** Creates the MenuBuilder. */
    @Override
    protected MenuBuilder createMenuBuilder() {
        return new DefaultMenuBuilder() {

            @Override
            public void addOtherViewItems(JMenu m, Application app, @Nullable View v) {
                ActionMap am = app.getActionMap(v);
                JCheckBoxMenuItem cbmi;
                cbmi = new JCheckBoxMenuItem(am.get("view.toggleGrid"));
                ActionUtil.configureJCheckBoxMenuItem(cbmi, am.get("view.toggleGrid"));
                m.add(cbmi);
                JMenu m2 = new JMenu("Zoom");
                for (double sf : scaleFactors) {
                    String id = (int) (sf * 100) + "%";
                    cbmi = new JCheckBoxMenuItem(am.get(id));
                    ActionUtil.configureJCheckBoxMenuItem(cbmi, am.get(id));
                    m2.add(cbmi);
                }
                m.add(m2);
                
            }
            
            @Override
            public void addOtherFileItems(JMenu m, Application app, @Nullable View v)
            {
            	ActionMap am = app.getActionMap(v);
            	Action serialize;
            	serialize = am.get(SerializeFileAction.ID);
                if(null != serialize)
                {
                	m.add(serialize); 	
                }
                Action simD;
                simD = am.get(SimulateCurrentDrawingAction.ID);
                if(null != simD)
                {
                	m.add(simD);
                }
                
                Action simulateSerializedDrawing;
                simulateSerializedDrawing = am.get(SimulateSerializedDrawingAction.ID);
                if(null != simulateSerializedDrawing)
                {
                	m.add(simulateSerializedDrawing);
                }
                
                Action checkCurrentDrawing;
                checkCurrentDrawing = am.get(CheckCurrentDrawingAction.ID);
                if(null != checkCurrentDrawing)
                {
                	m.add(checkCurrentDrawing);
                }
                
                Action addDefaultAction;
                addDefaultAction = am.get(AddNewActionAction.ID);
                if(null != addDefaultAction && (!StateFigure.containsAction(AddNewActionAction.ID)))
                {
                	StateFigure.addAction(AddNewActionAction.ID, addDefaultAction);
                }
                
                Action addRemoveAction;
                addRemoveAction = am.get(RemoveActionAction.ID);
                if(null != addRemoveAction && (!StateFigure.containsAction(RemoveActionAction.ID)))
                {
                	StateFigure.addAction(RemoveActionAction.ID, addRemoveAction);
                }
                
                Action addNestedStart;
//                addNestedStart = am.get(AddNestedStartAction.ID);
//                if(null != addNestedStart && (!StateFigure.containsAction(AddNestedStartAction.ID)))
//                {
//                	StateFigure.addAction(AddNestedStartAction.ID, addNestedStart);
//                }
//                
//                Action addTransitionAction;
//                addTransitionAction = am.get(AddTransitionActionAction.ID);
//                if(null != addTransitionAction)
//                {
//                	TransitionFigure.addAction(AddTransitionActionAction.ID, addTransitionAction);
//                }
//                
//                Action removeTransitionAction;
//                removeTransitionAction = am.get(RemoveTransitionActionAction.ID);
//                if(null != removeTransitionAction)
//                {
//                	TransitionFigure.addAction(RemoveTransitionActionAction.ID, removeTransitionAction);
//                }
               
            }
        };
    }

    @Override
    public URIChooser createOpenChooser(Application a, @Nullable View v) {
        JFileURIChooser c = new JFileURIChooser();
        c.addChoosableFileFilter(new ExtensionFileFilter("JStateDrawer Diagram", "xml"));
        return c;
    }

    @Override
    public URIChooser createSaveChooser(Application a, @Nullable View v) {
        JFileURIChooser c = new JFileURIChooser();
        c.addChoosableFileFilter(new ExtensionFileFilter("JStateDrawer Diagram", "xml"));
        return c;
    }
    
    public URIChooser createInputFileChooser(Application a, @Nullable View v)
    {
    	JFileURIChooser c = new JFileURIChooser();
    	c.addChoosableFileFilter(new ExtensionFileFilter("JStateDrawer Diagram Input", "txt"));
    	return c;
    }
    
}
