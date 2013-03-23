package edu.uwm.JStateDrawer.Actions;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import org.jhotdraw.app.Application;
import org.jhotdraw.app.View;
import org.jhotdraw.app.action.AbstractViewAction;
import org.jhotdraw.draw.DefaultDrawingEditor;
import org.jhotdraw.util.ResourceBundleUtil;

import edu.umd.cs.findbugs.annotations.Nullable;
import edu.uwm.JStateDrawer.DrawerView;
import edu.uwm.JStateDrawer.DrawingChecker;

/**
 * This Action checks whether the drawing that is currently loaded is valid and ready for
 * simulation or serialization.
 * @author Reed Johnson
 * @author Chad Fisher
 * @author Scott Gill
 */
@SuppressWarnings("serial")
public class CheckCurrentDrawingAction extends AbstractViewAction {
	public final static String ID = "file.checkCurrentDrawing";

	public CheckCurrentDrawingAction(Application app, @Nullable View view) {
		super(app, view);
		ResourceBundleUtil labels = ResourceBundleUtil.getBundle("edu.uwm.JStateDrawer.Actions.Labels");
        labels.configureAction(this, ID);
	}

	/**
	 * Creates a {@link DrawingChecker}, determines if the {@link Drawing} in the active {@link View} is valid,
	 * and then creates a popup message informing the user either that the drawing is valid or that it is
	 * invalid and provides a reason why it is invalid. 
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		DefaultDrawingEditor editor = (DefaultDrawingEditor) ((DrawerView)getActiveView()).getEditor();
		DrawingChecker check = new DrawingChecker();
		boolean isDrawingValid = check.validateCurrentDrawing(editor.getActiveView().getDrawing(), false);
		if(!isDrawingValid)
		{
			JOptionPane.showMessageDialog(editor.getActiveView().getComponent(),
					"The drawing is invalid for the following reason:\n" + check.getErrorString(),
					"Drawing Invalid", JOptionPane.ERROR_MESSAGE);
		}
		else
		{
			JOptionPane.showMessageDialog(editor.getActiveView().getComponent(),
					"The current drawing is valid.", "Drawing Valid", JOptionPane.INFORMATION_MESSAGE);
		}
	}

}
