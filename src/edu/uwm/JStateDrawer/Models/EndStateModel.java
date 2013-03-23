package edu.uwm.JStateDrawer.Models;

import java.io.IOException;

import org.jhotdraw.xml.DOMOutput;

public class EndStateModel extends StateFigureModel {
	//End states must end at their state.
	public EndStateModel(){super("End");}
	public void addOutgoingTransition(TransitionModel outgoingTransition){}
	
	public void write(DOMOutput out) throws IOException
	{
		out.openElement("state");
		out.openElement("name");
		out.writeObject("end");
		out.closeElement();

		out.openElement("actions");
		out.addAttribute("triggers", myActions.keySet().size());
		int i = 0;
		for(String trigger : myActions.keySet())
		{
			// I number the actions so I can read multiple 'action's without screwing up the XML parser
			out.openElement("action" + Integer.toString(i));
			out.addAttribute("trigger", trigger);
			out.addAttribute("numActions", myActions.get(trigger).size());
			for(String action : myActions.get(trigger))
			{

				out.writeObject(action);

			}
			out.closeElement();
			i++;
		}
		out.closeElement();
		
		out.closeElement();
	}
}
