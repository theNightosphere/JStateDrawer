package edu.uwm.JStateDrawer.Actions;

import static org.jhotdraw.draw.AttributeKeys.STROKE_COLOR;

import java.awt.Color;
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
import org.jhotdraw.draw.Figure;
import org.jhotdraw.gui.JFileURIChooser;
import org.jhotdraw.gui.JSheet;
import org.jhotdraw.gui.URIChooser;
import org.jhotdraw.gui.Worker;
import org.jhotdraw.gui.event.SheetEvent;
import org.jhotdraw.gui.event.SheetListener;
import org.jhotdraw.gui.filechooser.ExtensionFileFilter;
import org.jhotdraw.net.URIUtil;
import org.jhotdraw.util.ResourceBundleUtil;

import edu.umd.cs.findbugs.annotations.Nullable;
import edu.uwm.JStateDrawer.DrawerApplicationModel;
import edu.uwm.JStateDrawer.DrawerFactory;
import edu.uwm.JStateDrawer.DrawerView;
import edu.uwm.JStateDrawer.DrawingChecker;
import edu.uwm.JStateDrawer.DrawingSimulator;

@SuppressWarnings("serial")
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
		simulateDrawing(view);
	}

	private void simulateDrawing(View view)
	{
		final View activeView = view;
		if (activeView == null) {
			return;
		}
		if (activeView.isEnabled()) {
			Drawing drawing = ((DrawerView) activeView).getDrawing();
			DrawingChecker checker = new DrawingChecker();
			// If the drawing isn't valid, end early. 
			if(!checker.validateCurrentDrawing(drawing, false))
			{
				JOptionPane.showMessageDialog(((DrawerView) activeView).getEditor().getActiveView().getComponent(),
						"The drawing is invalid and cannot be simulated for the following reason:\n" + checker.getErrorString(),
						"Drawing Invalid", JOptionPane.ERROR_MESSAGE);
				return;
			}
			oldFocusOwner = SwingUtilities.getWindowAncestor(activeView.getComponent()).getFocusOwner();
			activeView.setEnabled(false);

			URIChooser fileChooser = getChooser(activeView);

			JSheet.showOpenSheet(fileChooser, activeView.getComponent(), new SheetListener() {

				@Override
				public void optionSelected(final SheetEvent evt) {
					if (evt.getOption() == JFileChooser.APPROVE_OPTION) {
						final URI uri;
						if ((evt.getChooser() instanceof JFileURIChooser) && (evt.getFileChooser().getFileFilter() instanceof ExtensionFileFilter)) {
							uri = ((ExtensionFileFilter) evt.getFileChooser().getFileFilter()).makeAcceptable(evt.getFileChooser().getSelectedFile()).toURI();
						} else {
							uri = evt.getChooser().getSelectedURI();
						}
						activeView.execute(new Worker(){
							@Override
							protected Object construct() throws IOException {
								try {
									new DrawingSimulator().simulateD(((DrawerView) activeView).getDrawing(), uri, true);
								} catch (InterruptedException e) {
									e.printStackTrace();
									for (Figure s : ((DrawerView) activeView).getDrawing().getFiguresFrontToBack()){
										s.willChange();
										s.set(STROKE_COLOR, Color.BLACK);
										s.changed();
									}
								} 
								return null;
							}

							@Override
							protected void done(Object value) {
								//Nothing 
							}

							@Override
							protected void failed(Throwable value) {
								//Nothing
							}

							@Override
							protected void finished() {
								activeView.setEnabled(true);
								SwingUtilities.getWindowAncestor(activeView.getComponent()).toFront();
								if (oldFocusOwner != null) {
									oldFocusOwner.requestFocus();
								}
							}
						});
					} else {
						activeView.setEnabled(true);
						if (oldFocusOwner != null) {
							oldFocusOwner.requestFocus();
						}
					}
				}
			});

		}
	}
}
