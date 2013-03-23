package edu.uwm.JStateDrawer;

import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.io.*;
import java.net.URI;

import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.Figure;

import edu.uwm.JStateDrawer.figures.StateFigure;
import edu.uwm.JStateDrawer.figures.StartStateFigure;
import edu.uwm.JStateDrawer.figures.EndStateFigure;
import edu.uwm.JStateDrawer.figures.TransitionFigure;

public class DrawingSimulator {
	public DrawingSimulator(){}
	
	public /*String*/ void simulateD(Drawing view, URI u) throws FileNotFoundException{
		//String r = "";
		LinkedList<StateFigure> statelist = new LinkedList<StateFigure>();
		for(Figure s : view.getFiguresFrontToBack()){
			if (s instanceof StateFigure) statelist.add((StateFigure) s);
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
		printString += currentState.getOutgoingTransitions().iterator().next().getModel().getTrigger();
		printString += " to " + currentState.getOutgoingTransitions().iterator().next().getEndStateFigure().getName() + "\n";
		currentState = currentState.getOutgoingTransitions().iterator().next().getEndStateFigure();
		
		//r = w;
		
		p.println(printString);
		
		List<String> actionList;
		
		actionList = currentState.getModel().getActionsByTrigger("ENTRY");
		if (actionList != null){
			for (String s : currentState.getModel().getActionsByTrigger("ENTRY")) {
				printString = "Entering action: " + s + "\n";
				//r += w;
				p.println(printString);
			}
		}
		//Iterator<String> i;
		//i = currentState.getModel().getActionsByTrigger("ENTRY").iterator();
		//while (i.hasNext()) r += "Entering action: " + i.next() + "\n";
		
		while(true){
			if (f.hasNext()){ 
				readString = f.nextLine();
				readString = readString.substring(0, readString.length());
			}
			else{
				printString = "Incomplete action list.\n";
				//r += w;
				p.println(printString);
				break;
			}
			System.out.println("Read Action: " + readString);
			if (currentState.getModel().getAllActions().containsValue(readString)) {
				printString = "Action Performed: " + readString + "\n";
				//r += w;
				p.println(printString);
				for (TransitionFigure s : currentState.getOutgoingTransitions()){
					if (currentState.getModel().getActionsByTrigger(s.getModel().getTrigger()).contains(readString)){
						actionList = currentState.getModel().getActionsByTrigger("EXIT");
						if (actionList != null){
							for (String d : actionList) {
								printString = "Exiting action: " + d + "\n";
								//r += w;
								p.println(printString);
							}
						}
						
						currentState = s.getEndStateFigure();
						printString = "Transition to: " + currentState.getName() + "\n";
						//r += w;
						p.println(printString);
						
						actionList = currentState.getModel().getActionsByTrigger("ENTRY");
						if (actionList != null){
							for (String d : actionList) {
								printString = "Entering action: " + d + "\n";
								//r += w;
								p.println(printString);
							}
						}
						
						break;
					}
				}
			}
			else{ 
				printString = "Action NOT Performed: " + readString + "\n";
				//r += w;
				p.println(printString);
			}
			if (currentState instanceof EndStateFigure) break;
		}
		
		f.close();
		p.close();
		//return r;
	}
}
