package edu.uwm.JStateDrawer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.io.*;
import java.net.URI;

import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.Figure;

import edu.uwm.JStateDrawer.Models.StateFigureModel;
import edu.uwm.JStateDrawer.Models.TransitionModel;
import edu.uwm.JStateDrawer.figures.StateFigure;
import edu.uwm.JStateDrawer.figures.StartStateFigure;

/**
 * The class responsible for performing state diagram simulations
 * @author Scott Gill
 * @author Reed Johnson
 * @author Chad Fisher
 */
public class DrawingSimulator {
	public DrawingSimulator(){}
	
	public void simulateD(Drawing view, URI u) throws FileNotFoundException{
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

		printString += currentState.getModel().getName();
		TransitionModel nextStateTransition = currentState.getModel().getOutgoingTransitions().iterator().next();
		printString += " to " + nextStateTransition.getEndState().getName() + "\n";
		
		StateFigureModel currentModel = nextStateTransition.getEndState();
		
		//r = w;
		
		p.println(printString);
		p.flush();
		
		List<String> actionList;
		
		actionList = currentModel.getActionsByTrigger("ENTRY");
		// Empty the print string
		printString = "";
		
		if (actionList != null){
			for (String s : actionList) {
				printString = "Entry action: " + s + "\n";
				p.println(printString);
				p.flush();
			}
		}
		
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
			if (currentModel.getActionsByTrigger(readString) != null) {
				for(String action : currentModel.getActionsByTrigger(readString))
				{
					printString = "Action Performed: " + action + "\n";
				}
				p.println(printString);
				p.flush();
				
			}
			else if(currentModel.getTransitionByEvent(readString) != null)
			{
				ArrayList<String> exitActions = (ArrayList<String>) currentModel.getActionsByTrigger("EXIT");
				if(exitActions != null)
				{
					for(String action : exitActions)
					{
						printString += "Exit action: " + action + "\n";
					}
				}
				
				TransitionModel triggeredTransitionModel = currentModel.getTransitionByEvent(readString);
				printString += "Transition from " + currentModel.getName() + " to " + triggeredTransitionModel.getEndState().getName() + "\n\n";
				
				currentModel = triggeredTransitionModel.getEndState();
				
				ArrayList<String> entryActions = (ArrayList<String>) currentModel.getActionsByTrigger("ENTRY");
				if(entryActions != null)
				{
					for(String action : entryActions)
					{
						printString += "Entry action: " + action + "\n\n";
					}
				}
				
				p.println(printString);
				p.flush();
			}
			else{ 
				printString = "Action NOT Performed: " + readString + "\n";
				//r += w;
				p.println(printString);
				p.flush();
			}
			if (currentModel.getName().equals("end")) break;
		}
		
		f.close();
		p.close();
	}
}
