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

	final int sysWait = 1500;

	public DrawingSimulator(){
		//TODO Tell the view to repaint after changing the attribute or sleeping the Thread.
	}

	public void simulateD(Drawing view, URI u, boolean inDrawing) throws FileNotFoundException, InterruptedException{
		StateFigureModel parent = null;
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
		StateFigureModel currentState = null;

		Scanner f = new Scanner(new FileReader(u.getPath()));
		PrintWriter p = new PrintWriter(new File("JSimOutput.txt"));

		for (StateFigure s : statelist){
			if (s instanceof StartStateFigure){
				if(!s.getModel().getIsInternalState())
				{
					currentState = s.getModel();
					break;
				}
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
			System.out.println("Read Action: " + readString);
			if (currentState.getActionsByEvent(readString) != null) {
				actionList = currentState.getActionsByEvent(readString);
				if (inDrawing) {
					recolor(currentState.getFigure(), Color.MAGENTA);
					Thread.sleep(sysWait/4);
					recolor(currentState.getFigure(), Color.BLUE);
					Thread.sleep(sysWait/4);
				}
				for(String action : actionList)
				{
					printString = "Action Performed: " + action + "\n";
					p.println(printString);
					p.flush();
				}
				if(currentState.getParentState() != null){
					if (currentState.getParentState().getTransitionByEvent(readString) != null){
						currentState = parent;
						parent = null;
						printString = "Exiting Nest of " + currentState.getName();
						p.println(printString);
						p.flush();
					}
				}
				if(currentState.getTransitionByEvent(readString) != null)
				{
					printExitActions(currentState, p);

					TransitionModel triggeredTransitionFigure = null;
					triggeredTransitionFigure = findTriggeredTransition(
							readString, currentState, triggeredTransitionFigure);
					printString = "Transition from " + currentState.getName() + " to " + triggeredTransitionFigure.getEndState().getName();
					p.println(printString);
					p.flush();

					if (parent == null){
						if (triggeredTransitionFigure.getEndState().getParentState() != null){
							parent = currentState;
							printString = "Entering Nest of " + currentState.getName();
							p.println(printString);
							p.flush();
						}
					}
					
					for (String s : triggeredTransitionFigure.getAllActions()){
						printString = "During Transition:  " + s;
						p.println(printString);
						p.flush();
					}

					if (inDrawing){
						currentState = changeStateColorForTransition(parent,
								currentState, triggeredTransitionFigure);
					}
					else
					{
						currentState = triggeredTransitionFigure.getEndState();
					}

					ArrayList<String> entryActions = (ArrayList<String>) currentState.getActionsByEvent("ENTRY");
					printEntryActions(p, entryActions);

				}

			}
			else if (parent != null)
			{
				if (parent.getTransitionByEvent(readString) != null){
					if (inDrawing) recolor(currentState.getFigure(), Color.GREEN);
					currentState = parent;
					parent = null;

					printString = "Exiting Nest of " + currentState.getName();
					p.println(printString);
					p.flush();
				}
				

				TransitionModel triggeredTransitionFigure = null;
				triggeredTransitionFigure = findTriggeredTransition(readString,
						currentState, triggeredTransitionFigure);
				if (triggeredTransitionFigure != null){
				
				printExitActions(currentState, p);

				printString = "Transition from " + currentState.getName() + " to " + triggeredTransitionFigure.getEndState().getName();
				p.println(printString);
				p.flush();

				if (parent == null){
					if (triggeredTransitionFigure.getEndState().getParentState() != null){
						parent = currentState;
						printString = "Entering Nest of " + currentState.getName();
						p.println(printString);
						p.flush();
					}
				}
				
				for (String s : triggeredTransitionFigure.getAllActions()){
					printString = "During Transition:  " + s;
					p.println(printString);
					p.flush();
				}

				if (inDrawing){
					currentState = changeStateColorForTransition(parent,
							currentState, triggeredTransitionFigure);
				}
				else currentState = triggeredTransitionFigure.getEndState();

				ArrayList<String> entryActions = (ArrayList<String>) currentState.getActionsByEvent("ENTRY");
				printEntryActions(p, entryActions);
				}
				else{
					if (inDrawing){
						for(int i = 0; i < 4; ++i){
							recolor(currentState.getFigure(), Color.RED);
							Thread.sleep(sysWait/16);
							recolor(currentState.getFigure(), Color.BLUE);
							Thread.sleep(sysWait/16);
						}
					}

					printString = "Nothing triggered by event: " + readString + "\n";
					p.println(printString);
					p.flush();
				}
			}
			else if(currentState.getTransitionByEvent(readString) != null)
			{
				printExitActions(currentState, p);
				System.out.println("read string: " + readString);
				TransitionModel triggeredTransitionFigure = null;
				System.out.println("outgoing transitions" + currentState.getOutgoingTransitions());
				triggeredTransitionFigure = findTriggeredTransition(readString,
						currentState, triggeredTransitionFigure);
				System.out.println("from: " + currentState);
				System.out.println("to: " + triggeredTransitionFigure);
				printString = "Transition from " + currentState.getName() + " to " + triggeredTransitionFigure.getEndState().getName();
				p.println(printString);
				p.flush();

				if (parent == null){
					if (triggeredTransitionFigure.getEndState().getParentState() != null){
						parent = currentState;
						printString = "Entering Nest of " + currentState.getName();
						p.println(printString);
						p.flush();
					}
				}

				for (String s : triggeredTransitionFigure.getAllActions()){
					printString = "During Transition:  " + s;
					p.println(printString);
					p.flush();
				}

				if (inDrawing){
					currentState = changeStateColorForTransition(parent,
							currentState, triggeredTransitionFigure);
				}
				else currentState = triggeredTransitionFigure.getEndState();

				ArrayList<String> entryActions = (ArrayList<String>) currentState.getActionsByEvent("ENTRY");
				printEntryActions(p, entryActions);

			}
			else{ 
				//Red flashing occurs over 0.30 seconds
				if (inDrawing){
					for(int i = 0; i < 4; ++i){
						recolor(currentState.getFigure(), Color.RED);
						Thread.sleep(sysWait/16);
						recolor(currentState.getFigure(), Color.BLUE);
						Thread.sleep(sysWait/16);
					}
				}

				printString = "Nothing triggered by event: " + readString + "\n";
				p.println(printString);
				p.flush();
			}
			if (currentState.getName().equals("end")) 
			{
				break;
			}

		}
		if (inDrawing){
			for (Figure s : view.getFiguresFrontToBack()){
				recolor(s, Color.BLACK);
			}
		}
		f.close();
		p.close();
	}

	/**
	 * Takes the current state, determines if it has any exit actions, 
	 * and prints them to the given printwriter if it has exit actions.
	 * @param currentState
	 * @param p
	 */
	private void printExitActions(StateFigureModel currentState, PrintWriter p) {
		String printString;
		ArrayList<String> exitActions = (ArrayList<String>) currentState.getActionsByEvent("EXIT");
		if(exitActions != null)
		{
			for(String action : exitActions)
			{
				printString = "Exit action: " + action + "\n";
				p.println(printString);
				p.flush();
			}
		}
	}

	/**
	 * Finds the transition that was triggered by a given event.
	 * @param readString
	 * @param currentState
	 * @param triggeredTransitionFigure
	 * @return The {@link TransitionModel} that was triggered by the event in {@code readString}
	 */
	private TransitionModel findTriggeredTransition(String readString,
			StateFigureModel currentState,
			TransitionModel triggeredTransitionFigure) {
		for(TransitionModel s : currentState.getOutgoingTransitions()){
			if (s.getEvent().equals(readString)){
				triggeredTransitionFigure = s;
				break;
			}
		}
		return triggeredTransitionFigure;
	}

	private void printEntryActions(PrintWriter p, ArrayList<String> entryActions) {
		String printString;
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

	/**
	 * Convenience method that changes the color of the old and new state during simulation
	 * 
	 * @param parent
	 * @param currentState
	 * @param triggeredTransitionFigure
	 * @return
	 * @throws InterruptedException
	 */
	private StateFigureModel changeStateColorForTransition(
			StateFigureModel parent, StateFigureModel currentState,
			TransitionModel triggeredTransitionFigure)
			throws InterruptedException {
		recolor(currentState.getFigure(), Color.GREEN);
		recolor(triggeredTransitionFigure.getFigure(), Color.BLUE);

		currentState = triggeredTransitionFigure.getEndState();

		if (parent != null) recolor(parent.getFigure(), Color.YELLOW);
		recolor(currentState.getFigure(), Color.BLUE);
		Thread.sleep(sysWait);
		recolor(triggeredTransitionFigure.getFigure(), Color.GREEN);
		return currentState;
	}

	/**
	 * Convenience method that calls {@code x.willChange()} and {@code x.changed()}, 
	 * while changing the color of {@code x} to {@code y}.
	 * @param x
	 * @param y
	 */
	private void recolor(Figure x, Color y){
		x.willChange();
		x.set(STROKE_COLOR, y);
		x.changed();
	}
}

