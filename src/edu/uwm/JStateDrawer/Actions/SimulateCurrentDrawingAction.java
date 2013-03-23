package edu.uwm.JStateDrawer.Actions;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.jhotdraw.app.Application;
import org.jhotdraw.app.View;
import org.jhotdraw.app.action.AbstractViewAction;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.gui.JFileURIChooser;
import org.jhotdraw.gui.JSheet;
import org.jhotdraw.gui.URIChooser;
import org.jhotdraw.gui.event.SheetEvent;
import org.jhotdraw.gui.event.SheetListener;
import org.jhotdraw.gui.filechooser.ExtensionFileFilter;
import org.jhotdraw.util.ResourceBundleUtil;

import edu.umd.cs.findbugs.annotations.Nullable;
import edu.uwm.JStateDrawer.DrawerApplicationModel;
import edu.uwm.JStateDrawer.DrawerView;
import edu.uwm.JStateDrawer.DrawingChecker;
import edu.uwm.JStateDrawer.DrawingSimulator;

public class SimulateCurrentDrawingAction extends AbstractViewAction {
	public final static String ID = "file.SimulateDrawing";
	private Component oldFocusOwner;
	
	public SimulateCurrentDrawingAction(Application app, @Nullable View view){
		super(app, view);
		ResourceBundleUtil Labels = ResourceBundleUtil.getBundle("edu.uwm.JStateDrawer.Actions.Labels");
		Labels.configureAction(this, ID);
	}

	protected URIChooser getChooser(View view) {
        URIChooser chsr = (URIChooser) (view.getComponent()).getClientProperty("saveChooser");
        if (chsr == null) {
        	DrawerApplicationModel model = (DrawerApplicationModel)(getApplication().getModel());
            chsr = model.createInputFileChooser(getApplication(), view);
            view.getComponent().putClientProperty("saveChooser", chsr);
        }
        return chsr;
    }

	@Override
    public void actionPerformed(ActionEvent evt) {
        final View view = getActiveView();
        if (view == null) {
            return;
        }
        if (view.isEnabled()) {
        	Drawing drawing = ((DrawerView) view).getDrawing();
        	DrawingChecker checker = new DrawingChecker();
        	// If the drawing isn't valid, end early. 
        	if(!checker.validateCurrentDrawing(drawing))
        	{
        		JOptionPane.showMessageDialog(((DrawerView) view).getEditor().getActiveView().getComponent(),
    					"The drawing is invalid and cannot be simulated for the following reason:\n" + checker.getErrorString(),
    					"Drawing Invalid", JOptionPane.ERROR_MESSAGE);
        		return;
        	}
            oldFocusOwner = SwingUtilities.getWindowAncestor(view.getComponent()).getFocusOwner();
            view.setEnabled(false);

            URIChooser fileChooser = getChooser(view);

            JSheet.showOpenSheet(fileChooser, view.getComponent(), new SheetListener() {

            	@Override
            	public void optionSelected(final SheetEvent evt) {
            		if (evt.getOption() == JFileChooser.APPROVE_OPTION) {
            			final URI uri;
            			if ((evt.getChooser() instanceof JFileURIChooser) && (evt.getFileChooser().getFileFilter() instanceof ExtensionFileFilter)) {
            				uri = ((ExtensionFileFilter) evt.getFileChooser().getFileFilter()).makeAcceptable(evt.getFileChooser().getSelectedFile()).toURI();
            			} else {
            				uri = evt.getChooser().getSelectedURI();
            			}
            			try {
            				new DrawingSimulator().simulateD(((DrawerView) view).getDrawing(), uri);
            			} catch (FileNotFoundException e) {
            				// TODO Auto-generated catch block
            				e.printStackTrace();
            			}
            		} else {
            			view.setEnabled(true);
            			if (oldFocusOwner != null) {
            				oldFocusOwner.requestFocus();
            			}
            		}
            	}
            });
            
        }
    }
}
