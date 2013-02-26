package edu.uwm.JStateDrawer;

import javax.swing.JMenu;

import org.jhotdraw.app.MenuBuilder;
import org.jhotdraw.app.SDIApplication;
import org.jhotdraw.app.View;
import org.jhotdraw.app.action.file.LoadDirectoryAction;
import org.jhotdraw.app.action.file.LoadFileAction;
import org.jhotdraw.app.action.file.OpenDirectoryAction;
import org.jhotdraw.app.action.file.OpenFileAction;

import edu.umd.cs.findbugs.annotations.Nullable;

public class DrawerApplication extends SDIApplication {

	public DrawerApplication() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
    @Nullable
    public JMenu createFileMenu(View view) {
        JMenu m = super.createFileMenu(view);

        MenuBuilder mb = model.getMenuBuilder();
        //mb.add
        mb.addClearFileItems(m, this, view);
        mb.addNewFileItems(m, this, view);
        mb.addNewWindowItems(m, this, view);

        mb.addLoadFileItems(m, this, view);
        mb.addOpenFileItems(m, this, view);

        if (getAction(view, LoadFileAction.ID) != null ||//
                getAction(view, OpenFileAction.ID) != null ||//
                getAction(view, LoadDirectoryAction.ID) != null ||//
                getAction(view, OpenDirectoryAction.ID) != null) {
            m.add(createOpenRecentFileMenu(view));
        }
        maybeAddSeparator(m);

        mb.addSaveFileItems(m, this, view);
        mb.addExportFileItems(m, this, view);
        mb.addPrintFileItems(m, this, view);

        mb.addOtherFileItems(m, this, view);

        maybeAddSeparator(m);
        mb.addCloseFileItems(m, this, view);

        return (m.getItemCount() == 0) ? null : m;
    }

}
