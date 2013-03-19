package edu.uwm.JStateDrawer;

import java.util.LinkedList;

import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.Figure;

import edu.uwm.JStateDrawer.figures.StartStateFigure;
import edu.uwm.JStateDrawer.figures.StateFigure;
import edu.uwm.JStateDrawer.figures.TransitionFigure;

public class DrawingSimulator {
	private LinkedList<StateFigure> statelist;
	
	private Drawing view;
	
	public DrawingSimulator(){
		view = null;
		statelist = new LinkedList<StateFigure>();
	}
	
	public DrawingSimulator(Drawing x){
		view = x;
		statelist = new LinkedList<StateFigure>();
		
		for (Figure s : view.getFiguresFrontToBack()){
			if (s instanceof StateFigure) statelist.add((StateFigure)s);
		}
	}
	
	public String simulate(){
		String r = "";
		
		boolean end = false;
		
		String e = "";
		
		StateFigure currentState = null;
		
		if (view != null){
			if (new DrawingChecker().validateCurrentDrawing(view)){
				for (StateFigure s : statelist){
					if (s instanceof StartStateFigure){
						currentState = s;
						break;
					}
				}
				while(true){
					//e = stringReadFromFile
					if (currentState.getModel().getAllActions().containsValue(e)) r += "Action Performed: " + e + "\n";
					for (TransitionFigure s : currentState.getOutgoingTransitions()){
						if (currentState.getModel().getActionsByTrigger(s.getModel().getTrigger()) != null){
							currentState = s.getEndStateFigure();
							r += "Transition to: " + currentState.getName() + "\n";
							break;
						}
					}
					if (currentState.getName().equals("End")) break;
				}
			}
			else{
				
			}
		}
		else{
			
		}
		
		return r;
	}
}
