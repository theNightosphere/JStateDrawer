package edu.uwm.JStateDrawer.Actions;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.jhotdraw.app.Application;
import org.jhotdraw.app.View;
import org.jhotdraw.app.action.AbstractViewAction;
import org.jhotdraw.draw.DefaultDrawingEditor;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.ListFigure;
import org.jhotdraw.draw.TextFigure;
import org.jhotdraw.util.ResourceBundleUtil;

import edu.umd.cs.findbugs.annotations.Nullable;
import edu.uwm.JStateDrawer.DrawerView;
import edu.uwm.JStateDrawer.figures.TransitionFigure;

/**
 * Action class for removing an action from {@link TransitionFigure}s.
 *
 */

@SuppressWarnings("serial")
public class RemoveTransitionActionAction extends AbstractViewAction {

	public final static String ID = "transition.removeAction";
	
	
	public RemoveTransitionActionAction(Application app, @Nullable View view) {
		super(app, view);
		ResourceBundleUtil labels = ResourceBundleUtil.getBundle("edu.uwm.JStateDrawer.Actions.Labels");
        labels.configureAction(this, ID);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		DefaultDrawingEditor editor = (DefaultDrawingEditor) ((DrawerView)getActiveView()).getEditor();
		JComponent containingComponent = editor.getActiveView().getComponent();
		
		JPopupMenu removeActionsMenu = new JPopupMenu("Select an action to remove");
		Iterator<Figure> figures = editor.getActiveView().getSelectedFigures().iterator();
		while(figures.hasNext())
		{
			TransitionFigure currentFig = (TransitionFigure)figures.next();
			ListFigure actionChildren = currentFig.getAllActions();
	    	for(int i = 0; i < actionChildren.getChildCount(); i++)
	    	{
	    		TextFigure action = currentFig.getIthAction(i);
	    		
	    		// Create a menu item to remove the corresponding trigger/action pair
	    		JMenuItem removeActionI = new JMenuItem(action.getText());
	    		removeActionI.addActionListener(new RemoveActionActionListener(i, currentFig));
	    		removeActionsMenu.add(removeActionI);
	    	}
	    	
	    	// All items are added. Get mouse position and show popup menu.
	    	Point mouseP = containingComponent.getMousePosition();
	    	removeActionsMenu.show(containingComponent, (int)mouseP.getX()+10, (int)mouseP.getY()+10);
		}

	}
	
	/**
	 * Extension of ActionListener that is designed to remove a given Action/Trigger pair
	 * when the actionPerformed is called.
	 * @author Reed
	 *
	 */
	private class RemoveActionActionListener implements ActionListener
	{
		private int myChildNumber;
		private TransitionFigure myFigureToRemoveFrom;
		
		
		public RemoveActionActionListener(int i, TransitionFigure figureToRemoveFrom)
		{
			myChildNumber = i;
			myFigureToRemoveFrom = figureToRemoveFrom;
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
    		myFigureToRemoveFrom.removeActionByIndex(myChildNumber);
		}
	}

}
