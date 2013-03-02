package edu.uwm.JStateDrawer;



import org.jhotdraw.app.Application;
import org.jhotdraw.app.DefaultApplicationModel;
import org.jhotdraw.app.OSXApplication;
import org.jhotdraw.app.SDIApplication;

public class DrawerMain {
	/**
	 * Main.
	 *
	 */
	    
	/** Creates a new instance. */
    public static void main(String[] args) {
        Application app;
        String os = System.getProperty("os.name").toLowerCase();
        if (os.startsWith("mac")) {
            app = new OSXApplication();
        } else if (os.startsWith("win")) {
          //  app = new DefaultMDIApplication();
            app = new SDIApplication();
        } else {
            app = new SDIApplication();
        }
        
        
        DefaultApplicationModel model = new DrawerApplicationModel();
        model.setName("JStateDrawer");
        model.setVersion(DrawerMain.class.getPackage().getImplementationVersion());
        model.setCopyright("Copyright 2013 (c) Reed Johnson, Chad Fisher, and Scott Gill");
        model.setViewClassName("edu.uwm.JStateDrawer.DrawerView");
        app.setModel(model);
        app.launch(args);
    }
	    
}
