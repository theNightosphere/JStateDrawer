package edu.uwm.JStateDrawer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.awt.Color;
import java.awt.Graphics2D;
import java.io.*;
import java.net.URI;

import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.ListFigure;
import org.jhotdraw.draw.TextFigure;
import org.jhotdraw.draw.event.FigureAdapter;
import org.jhotdraw.draw.event.FigureEvent;

import static org.jhotdraw.draw.AttributeKeys.*;

import edu.uwm.JStateDrawer.Models.StateFigureModel;
import edu.uwm.JStateDrawer.Models.EndStateModel;
import edu.uwm.JStateDrawer.Models.TransitionModel;
import edu.uwm.JStateDrawer.figures.StateFigure;
import edu.uwm.JStateDrawer.figures.StartStateFigure;
import edu.uwm.JStateDrawer.figures.StateFigure.ActionTextFigure;
import edu.uwm.JStateDrawer.figures.TransitionFigure;

/**
 * The class responsible for performing state diagram simulations
 * @author Scott Gill
 * @author Reed Johnson
 * @author Chad Fisher
 */
public class DrawingSimulator {
	
	final int sysWait = 2000;
	
	public DrawingSimulator(){
		//TODO Tell the view to repaint after changing the attribute or sleeping the Thread.
	}

	public void simulateD(Drawing view, URI u, boolean inDrawing) throws FileNotFoundException, InterruptedException{
		StateFigure parent = null;
		LinkedList<StateFigure> statelist = new LinkedList<StateFigure>();
		view.addFigureListener(new FigureAdapter());
		for(Figure s : view.getFiguresFrontToBack()){
			s.addFigureListener(new FigureAdapter());
			if (s instanceof StateFigure) 
			{
				statelist.add((StateFigure) s);
			}
		}
		String readString = "";
		String printString = "";
		StateFigure currentState = null;

		Scanner f = new Scanner(new FileReader(u.getPath()));
		PrintWriter p = new PrintWriter(new File("JSimOutput.txt"));

		for (StateFigure s : statelist){
			if (s instanceof StartStateFigure){
				currentState = s;
				break;
			}
		}

		List<String> actionList;

		while(true){
			// Empty the print string for each iteration through the loop.
			printString = "";
			if (f.hasNext()){ 
				readString = f.nextLine();
				readString = readString.substring(0, readString.length());
			}
			else{
				printString = "Incomplete action list.\n";
				p.println(printString);
				p.flush();
				break;
			}
			//System.out.println("Read Action: " + readString);
			if (currentState.getModel().getActionsByEvent(readString) != null) {
				actionList = currentState.getModel().getActionsByEvent(readString);
				//if (inDrawing) colorChanging(currentState.getActionTextFigures(), actionList);
				for(String action : actionList)
				{
					printString = "Action Performed: " + action + "\n";
					p.println(printString);
					p.flush();
				}
				if(currentState.getModel().getParentState() != null){
					if (currentState.getModel().getParentState().getTransitionByEvent(readString) != null){
						currentState = parent;
					}
				}
				if(currentState.getModel().getTransitionByEvent(readString) != null)
				{
					ArrayList<String> exitActions = (ArrayList<String>) currentState.getModel().getActionsByEvent("EXIT");
					if(exitActions != null)
					{
						for(String action : exitActions)
						{
							printString = "Exit action: " + action + "\n";
							p.println(printString);
							p.flush();
						}
					}

					TransitionFigure triggeredTransitionFigure = null;
					for (TransitionFigure s : currentState.getOutgoingTransitions()){
						if (s.getModel().getEvent().equals(readString)){
							triggeredTransitionFigure = s;
							break;
						}
					}
					printString = "Transition from " + currentState.getModel().getName() + " to " + triggeredTransitionFigure.getModel().getEndState().getName();
					p.println(printString);
					p.flush();

					if(inDrawing){
						view.willChange();
						recolor(currentState.getPresentationFigure(), Color.GREEN);
						recolor(triggeredTransitionFigure, Color.BLUE);
						if (triggeredTransitionFigure.getEndStateFigure().getModel().getParentState() != null){
							recolor(currentState.getPresentationFigure(), Color.YELLOW);
							parent = currentState;
						}
						view.changed();
						
						currentState = triggeredTransitionFigure.getEndStateFigure();

						view.willChange();
						recolor(currentState.getPresentationFigure(), Color.BLUE);
						view.changed();
						Thread.sleep(sysWait);
						view.willChange();
						recolor(triggeredTransitionFigure, Color.GREEN);
						view.changed();
					}

					ArrayList<String> entryActions = (ArrayList<String>) currentState.getModel().getActionsByEvent("ENTRY");
					if(entryActions != null)
					{
						for(String action : entryActions)
						{
							printString = "Entry action: " + action + "\n\n";
							p.println(printString);
							p.flush();
						}
					}

				}

			}else if (parent != null)
			{
				if (parent.getModel().getTransitionByEvent(readString) != null){
					if (inDrawing) recolor(currentState.getPresentationFigure(), Color.GREEN);
					currentState = parent;
					ArrayList<String> exitActions = (ArrayList<String>) currentState.getModel().getActionsByEvent("EXIT");
					if(exitActions != null)
					{
						for(String action : exitActions)
						{
							printString = "Exit action: " + action + "\n";
							p.println(printString);
							p.flush();
						}
					}

					TransitionFigure triggeredTransitionFigure = null;
					for(TransitionFigure s : currentState.getOutgoingTransitions()){
						if (s.getModel().getEvent().equals(readString)){
							triggeredTransitionFigure = s;
							break;
						}
					}
					printString = "Transition from " + currentState.getModel().getName() + " to " + triggeredTransitionFigure.getEndStateFigure().getName();
					p.println(printString);
					p.flush();

					if (inDrawing){
						view.willChange();
						recolor(currentState.getPresentationFigure(), Color.GREEN);
						triggeredTransitionFigure.set(STROKE_COLOR, Color.BLUE);
						if (triggeredTransitionFigure.getEndStateFigure().getModel().getParentState() != null){
							recolor(currentState.getPresentationFigure(), Color.YELLOW);
						}
						view.changed();
						
						currentState = triggeredTransitionFigure.getEndStateFigure();

						view.willChange();
						recolor(currentState.getPresentationFigure(), Color.BLUE);
						view.changed();
						Thread.sleep(sysWait);
						view.willChange();
						recolor(triggeredTransitionFigure, Color.GREEN);
						view.changed();
					}

					ArrayList<String> entryActions = (ArrayList<String>) currentState.getModel().getActionsByEvent("ENTRY");
					if(entryActions != null)
					{
						for(String action : entryActions)
						{
							printString = "Entry action: " + action + "\n\n";
							p.println(printString);
							p.flush();
						}
					}
					parent = null;
				}
			}
			else if(currentState.getModel().getTransitionByEvent(readString) != null)
			{
				ArrayList<String> exitActions = (ArrayList<String>) currentState.getModel().getActionsByEvent("EXIT");
				if(exitActions != null)
				{
					for(String action : exitActions)
					{
						printString = "Exit action: " + action + "\n";
						p.println(printString);
						p.flush();
					}
				}

				TransitionFigure triggeredTransitionFigure = null;
				for(TransitionFigure s : currentState.getOutgoingTransitions()){
					if (s.getModel().getEvent().equals(readString)){
						triggeredTransitionFigure = s;
						break;
					}
				}
				printString = "Transition from " + currentState.getModel().getName() + " to " + triggeredTransitionFigure.getEndStateFigure().getName();
				p.println(printString);
				p.flush();

				if (inDrawing){
					view.willChange();
					recolor(currentState.getPresentationFigure(), Color.GREEN);
					triggeredTransitionFigure.set(STROKE_COLOR, Color.BLUE);
					if (triggeredTransitionFigure.getEndStateFigure().getModel().getParentState() != null){
						recolor(currentState.getPresentationFigure(), Color.YELLOW);
						parent = currentState;
					}
					view.changed();
					
					currentState = triggeredTransitionFigure.getEndStateFigure();

					view.willChange();
					recolor(currentState, Color.BLUE);
					view.changed();
					Thread.sleep(sysWait);
					view.willChange();
					recolor(triggeredTransitionFigure, Color.GREEN);
					view.changed();
				}

				ArrayList<String> entryActions = (ArrayList<String>) currentState.getModel().getActionsByEvent("ENTRY");
				if(entryActions != null)
				{
					for(String action : entryActions)
					{
						printString = "Entry action: " + action + "\n\n";
						p.println(printString);
						p.flush();
					}
				}

			}
			else{ 
				//Red flashing occurs over 0.30 seconds
				if (inDrawing){
					view.willChange();
					recolor(currentState.getPresentationFigure(), Color.RED);
					view.changed();
					Thread.sleep(sysWait/20);
					view.willChange();
					recolor(currentState.getPresentationFigure(), Color.BLUE);
					view.changed();
					Thread.sleep(sysWait/20);
					view.willChange();
					recolor(currentState.getPresentationFigure(), Color.RED);
					view.changed();
					Thread.sleep(sysWait/20);
					view.willChange();
					recolor(currentState.getPresentationFigure(), Color.BLUE);
					view.changed();
					Thread.sleep(sysWait/20);
					view.willChange();
					recolor(currentState.getPresentationFigure(), Color.RED);
					view.changed();
					Thread.sleep(sysWait/20);
					view.willChange();
					recolor(currentState.getPresentationFigure(), Color.BLUE);
					view.changed();
					Thread.sleep(sysWait/20);
				}

				printString = "Nothing triggered by event: " + readString + "\n";
				p.println(printString);
				p.flush();
			}
			if (currentState.getModel().getName().equals("end")) 
			{
				break;
			}
			//if (currentModel instanceof EndStateModel) break;

		}
		if (inDrawing){
			//		for (Figure s : view.getFiguresFrontToBack()){
			//			recolor(s, Color.BLACK);
			//		}
		}
		f.close();
		p.close();
	}
	
	private void recolor(Figure x, Color y){
		x.willChange();
		x.set(STROKE_COLOR, y);
		x.changed();
	}
	
	private void colorChanging(ListFigure lf, List<String> actionList) throws InterruptedException{
		//TODO Make the TextFigure reflect the change in color.
		//System.out.println(actionList.toString());
		for (Figure s : lf.getChildrenFrontToBack()){
			//System.out.println(s.getClass().toString());
			if (s instanceof ListFigure) colorChanging((ListFigure) s, actionList);
			if (s instanceof ActionTextFigure){
				for (String a : actionList){
					//System.out.println(s.getAttributes().toString());
					if (s.get(TEXT).equals(a)){
						System.out.println("In");
						s.set(STROKE_COLOR, Color.BLUE);
						Thread.sleep(sysWait/4);
						s.set(STROKE_COLOR, Color.GREEN);
						break;
					}
				}
			}
		}
	}

}

