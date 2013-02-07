package edu.uwm.JStateDrawer;

/*
 * @(#)DrawerMain.java
 * 
 * Based on the work of the original authors and contributers of JHotDraw,
 * with minor alterations. Most of the source code is unaltered from the
 * original file org.jhotdraw.samples.pert.Main.java
 * 
 * Copyright (c) 1996-2010 by the original authors of JHotDraw and all its
 * contributors. All rights reserved.
 *
 * You may not use, copy or modify this file, except in compliance with the 
 * license agreement you entered into with the copyright holders. For details
 * see accompanying license terms.
 */

//TODO: Define a JStateDrawer application model. Models define the bars/buttons on the window

import org.jhotdraw.app.Application;
import org.jhotdraw.app.DefaultApplicationModel;
import org.jhotdraw.app.OSXApplication;
import org.jhotdraw.app.SDIApplication;
import org.jhotdraw.samples.pert.PertApplicationModel;

public class DrawerMain {
	/**
	 * Main.
	 *
	 * @author Werner Randelshofer.
	 * @version $Id: Main.java 717 2010-11-21 12:30:57Z rawcoder $
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
        
        
        DefaultApplicationModel model = new PertApplicationModel();
        model.setName("JStateDrawer");
        model.setVersion(DrawerMain.class.getPackage().getImplementationVersion());
        model.setCopyright("Copyright 2006-2010 (c) by the authors of JHotDraw and all its contributors.\n" +
                "This software is licensed under LGPL and Creative Commons 3.0 Attribution.");
        model.setViewClassName("org.jhotdraw.samples.pert.PertView");
        app.setModel(model);
        app.launch(args);
    }
	    
}
