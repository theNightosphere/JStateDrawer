package edu.uwm.JStateDrawer;

import java.util.LinkedList;
import java.util.Scanner;
import java.io.*;

import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.Figure;

import edu.uwm.JStateDrawer.figures.StateFigure;
import edu.uwm.JStateDrawer.figures.TransitionFigure;

public class DrawingSimulator {
	
	public DrawingSimulator(){}
	
	public String simulate(Drawing view) throws FileNotFoundException{
		String r = "";
		
		LinkedList<StateFigure> statelist = new LinkedList<StateFigure>();
		for(Figure s : view.getFiguresFrontToBack()){
			if (s instanceof StateFigure) statelist.add((StateFigure) s);
		}
		
		
		String e = "";
		
		StateFigure currentState = null;
		
		Scanner f = new Scanner(new FileReader("INPUT"));

		if (new DrawingChecker().validateCurrentDrawing(view)){
			for (StateFigure s : statelist){
				if (s.getName().equals("Start")){
					currentState = s;
					break;
				}
			}
			r += currentState.getOutgoingTransitions().iterator().next().getModel().getTrigger();
			r += " to " + currentState.getOutgoingTransitions().iterator().next().getEndStateFigure().getName() + "\n";
			currentState = currentState.getOutgoingTransitions().iterator().next().getEndStateFigure();
			for (String s : currentState.getModel().getActionsByTrigger("ENTRY")) r += "Entering action: " + s + "\n";
			while(true){
				e = f.next();
				if (currentState.getModel().getAllActions().containsValue(e)) {
					r += "Action Performed: " + e + "\n";
					for (TransitionFigure s : currentState.getOutgoingTransitions()){
						if (currentState.getModel().getActionsByTrigger(s.getModel().getTrigger()).contains(e)){
							for (String d : currentState.getModel().getActionsByTrigger("EXIT")) r += "Exiting action: " + d + "\n";
							currentState = s.getEndStateFigure();
							r += "Transition to: " + currentState.getName() + "\n";
							for (String d : currentState.getModel().getActionsByTrigger("ENTRY")) r += "Entering action: " + d + "\n";
							break;
						}
					}
				}
				else{ r += "Action NOT Performed: " + e + "\n"; }
				if (currentState.getName().equals("End")) break;
			}
		}
		f.close();
		return r;
	}
}
