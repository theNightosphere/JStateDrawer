package edu.uwm.JStateDrawer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.awt.Color;
import java.io.*;
import java.net.URI;

import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.TextFigure;

import static org.jhotdraw.draw.AttributeKeys.*;

import edu.uwm.JStateDrawer.Models.StateFigureModel;
import edu.uwm.JStateDrawer.Models.EndStateModel;
import edu.uwm.JStateDrawer.Models.TransitionModel;
import edu.uwm.JStateDrawer.figures.StateFigure;
import edu.uwm.JStateDrawer.figures.StartStateFigure;
import edu.uwm.JStateDrawer.figures.TransitionFigure;

/**
 * The class responsible for performing state diagram simulations
 * @author Scott Gill
 * @author Reed Johnson
 * @author Chad Fisher
 */
public class DrawingSimulator {
	public DrawingSimulator(){}

	public void simulateD(Drawing view, URI u) throws FileNotFoundException, InterruptedException{
		//TODO decide on a sysWait value.
		final int sysWait = 100;
		StateFigure parent = null;
		LinkedList<StateFigure> statelist = new LinkedList<StateFigure>();
		for(Figure s : view.getFiguresFrontToBack()){
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
		System.out.println(currentState.getAttributes().toString());
		currentState.getPresentationFigure().set(STROKE_COLOR, Color.BLUE);
		currentState.wait(sysWait);

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
				for (Figure s : currentState.getChildrenFrontToBack()){
					if (s instanceof TextFigure){
						if (!(s.get(TEXT).equals(currentState.getName()))){
							for (String l : actionList){
								if (s.get(TEXT).equals(l)){
									s.set(STROKE_COLOR, Color.BLUE);
									s.wait(sysWait/4);
									s.set(STROKE_COLOR, Color.GREEN);
									break;
								}
							}
						}
					}
				}
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

					currentState.getPresentationFigure().set(STROKE_COLOR, Color.GREEN);
					triggeredTransitionFigure.set(STROKE_COLOR, Color.BLUE);
					if (triggeredTransitionFigure.getEndStateFigure().getModel().getParentState() != null){
						currentState.getPresentationFigure().set(STROKE_COLOR, Color.YELLOW);
						parent = currentState;
					}

					currentState = triggeredTransitionFigure.getEndStateFigure();
					
					currentState.set(STROKE_COLOR, Color.BLUE);
					currentState.wait(sysWait);
					triggeredTransitionFigure.set(STROKE_COLOR, Color.GREEN);

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
					currentState.set(STROKE_COLOR, Color.GREEN);
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


					currentState.getPresentationFigure().set(STROKE_COLOR, Color.GREEN);
					triggeredTransitionFigure.set(STROKE_COLOR, Color.BLUE);
					if (triggeredTransitionFigure.getEndStateFigure().getModel().getParentState() != null){
						currentState.getPresentationFigure().set(STROKE_COLOR, Color.YELLOW);
					}
					currentState = triggeredTransitionFigure.getEndStateFigure();

					currentState.set(STROKE_COLOR, Color.BLUE);
					currentState.wait(sysWait);
					triggeredTransitionFigure.set(STROKE_COLOR, Color.GREEN);

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


				currentState.getPresentationFigure().set(STROKE_COLOR, Color.GREEN);
				triggeredTransitionFigure.set(STROKE_COLOR, Color.BLUE);
				if (triggeredTransitionFigure.getEndStateFigure().getModel().getParentState() != null){
					currentState.getPresentationFigure().set(STROKE_COLOR, Color.YELLOW);
					parent = currentState;
				}
				currentState = triggeredTransitionFigure.getEndStateFigure();
				
				currentState.set(STROKE_COLOR, Color.BLUE);
				currentState.wait(sysWait);
				triggeredTransitionFigure.set(STROKE_COLOR, Color.GREEN);

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
				currentState.getPresentationFigure().set(STROKE_COLOR, Color.RED);
				currentState.wait(5);
				currentState.getPresentationFigure().set(STROKE_COLOR, Color.BLUE);
				currentState.wait(5);
				currentState.getPresentationFigure().set(STROKE_COLOR, Color.RED);
				currentState.wait(5);
				currentState.getPresentationFigure().set(STROKE_COLOR, Color.BLUE);
				currentState.wait(5);
				currentState.getPresentationFigure().set(STROKE_COLOR, Color.RED);
				currentState.wait(5);
				currentState.getPresentationFigure().set(STROKE_COLOR, Color.BLUE);
				currentState.wait(5);
				
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
		f.close();
		p.close();
	}
	
}

