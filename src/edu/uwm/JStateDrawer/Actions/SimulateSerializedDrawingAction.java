package edu.uwm.JStateDrawer.Actions;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

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
import edu.uwm.JStateDrawer.DrawerView;
import edu.uwm.JStateDrawer.DrawingChecker;
import edu.uwm.JStateDrawer.DrawingSimulator;

@SuppressWarnings("serial")
public class SimulateSerializedDrawingAction extends AbstractViewAction {

	public final static String ID = "file.SimulateLoadedDrawing";
	private Component oldFocusOwner;
	
	public SimulateSerializedDrawingAction(Application app, @Nullable View view) {
		super(app, view);
		ResourceBundleUtil labels = ResourceBundleUtil.getBundle("edu.uwm.JStateDrawer.Actions.Labels");
        labels.configureAction(this, ID);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		final View view = getActiveView();
		view.setEnabled(false);
		
		URIChooser chooser = getApplication().getOpenChooser(null);
		
		JOptionPane.showMessageDialog(getActiveView().getComponent(), "First select a drawing file to load, then select an input file.", "How to simulate from file", JOptionPane.INFORMATION_MESSAGE);
		
		JSheet.showOpenSheet(chooser, view.getComponent(), new SheetListener() {

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
						final Drawing drawing = ((DrawerView)view).importDrawing(uri);
						
						DrawingChecker checker = new DrawingChecker();
	        			if(drawing != null)
	        			{
	        				if(!checker.validateCurrentDrawing(drawing, true))
	        				{
	        					JOptionPane.showMessageDialog(getActiveView().getComponent(),
	        							"The drawing is invalid and cannot be simulated for the following reason:\n" + checker.getErrorString(),
	        							"Drawing Invalid", JOptionPane.ERROR_MESSAGE);
	        					cleanUp(view);
	                			return;
	        				}
	        				URIChooser inputChooser = getApplication().getOpenChooser(null);
	        				
	        				JSheet.showOpenSheet(inputChooser, view.getComponent(), new SheetListener() {

	        		        	@Override
	        		        	public void optionSelected(final SheetEvent evt) {
	        		        		if (evt.getOption() == JFileChooser.APPROVE_OPTION) {
	        		        			final URI uri;
	        		        			if ((evt.getChooser() instanceof JFileURIChooser) && (evt.getFileChooser().getFileFilter() instanceof ExtensionFileFilter)) {
	        		        				uri = ((ExtensionFileFilter) evt.getFileChooser().getFileFilter()).makeAcceptable(evt.getFileChooser().getSelectedFile()).toURI();
	        		        			} else {
	        		        				uri = evt.getChooser().getSelectedURI();
	        		        			}
	        		        			
	        		        			try
	        		        			{
	        		        				new DrawingSimulator().simulateD(drawing, uri);
	        		        			}
	        		        			catch(FileNotFoundException e)
	        		        			{
	        		        				System.err.println("The specified input file could not be opened or does not exist.");
	        		        			}
	        		        			finally
	        		        			{
	        		        				cleanUp(view);
	        		        			}
	        		        		} else {
	        		        			cleanUp(view);
	        		        		}
	        		        	}
	        		        });
	        				
	        			}
	        			else
	        			{
	        				cleanUp(view);
	        			}
					} catch (IOException e) {
						// TODO Create message box that says why this failed.
						e.printStackTrace();
					}
        			finally
        			{
        				cleanUp(view);
        			}

        		} else {
        			cleanUp(view);
        		}
        	}
        });

	}
	
	private void cleanUp(View view)
	{
		view.setEnabled(true);
		if (oldFocusOwner != null) {
			oldFocusOwner.requestFocus();
		}
	}

}
