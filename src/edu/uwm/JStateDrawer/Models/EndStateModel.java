package edu.uwm.JStateDrawer.Models;

public class EndStateModel extends StateFigureModel {
	//End states must end at their state.
	public EndStateModel(){super("End");}
	public void addOutgoingTransition(TransitionModel outgoingTransition){}
}
