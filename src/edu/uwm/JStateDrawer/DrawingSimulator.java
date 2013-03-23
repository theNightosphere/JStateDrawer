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
	
	public String simulateD(Drawing view, URI u) throws FileNotFoundException{
		String r = "";
		LinkedList<StateFigure> statelist = new LinkedList<StateFigure>();
		for(Figure s : view.getFiguresFrontToBack()){
			if (s instanceof StateFigure) statelist.add((StateFigure) s);
		}
		String e = "";
		String w = "";
		StateFigure currentState = null;
		
		Scanner f = new Scanner(new FileReader(u.getPath()));
		PrintWriter p = new PrintWriter(new File("JSimOutput.txt"));

		for (StateFigure s : statelist){
			if (s instanceof StartStateFigure){
				currentState = s;
				break;
			}
		}
		r += currentState.getOutgoingTransitions().iterator().next().getModel().getTrigger();
		r += " to " + currentState.getOutgoingTransitions().iterator().next().getEndStateFigure().getName() + "\n";
		currentState = currentState.getOutgoingTransitions().iterator().next().getEndStateFigure();
		
		w = r;
		
		p.println(w);
		
		List<String> i;
		
		i = currentState.getModel().getActionsByTrigger("ENTRY");
		if (i != null){
			for (String s : currentState.getModel().getActionsByTrigger("ENTRY")) {
				w = "Entering action: " + s + "\n";
				r += w;
				p.println(w);
			}
		}
		//Iterator<String> i;
		//i = currentState.getModel().getActionsByTrigger("ENTRY").iterator();
		//while (i.hasNext()) r += "Entering action: " + i.next() + "\n";
		
		while(true){
			if (f.hasNext()){ 
				e = f.nextLine();
				e = e.substring(0, e.length());
			}
			else{
				w = "Incomplete action list.\n";
				r += w;
				p.println(w);
				break;
			}
			System.out.println("Read Action: " + e);
			if (currentState.getModel().getAllActions().containsValue(e)) {
				w = "Action Performed: " + e + "\n";
				r += w;
				p.println(w);
				for (TransitionFigure s : currentState.getOutgoingTransitions()){
					if (currentState.getModel().getActionsByTrigger(s.getModel().getTrigger()).contains(e)){
						i = currentState.getModel().getActionsByTrigger("EXIT");
						if (i != null){
							for (String d : i) {
								w = "Exiting action: " + d + "\n";
								r += w;
								p.println(w);
							}
						}
						
						currentState = s.getEndStateFigure();
						w = "Transition to: " + currentState.getName() + "\n";
						r += w;
						p.println(w);
						
						i = currentState.getModel().getActionsByTrigger("ENTRY");
						if (i != null){
							for (String d : i) {
								w = "Entering action: " + d + "\n";
								r += w;
								p.println(w);
							}
						}
						
						break;
					}
				}
			}
			else{ 
				w = "Action NOT Performed: " + e + "\n";
				r += w;
				p.println(w);
			}
			if (currentState instanceof EndStateFigure) break;
		}
		
		f.close();
		p.close();
		return r;
	}
}
