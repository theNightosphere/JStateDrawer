package edu.uwm.JStateDrawer.Models;

public class StartStateModel extends StateFigureModel {
	//Start states can't be revisited.
	@Override
	public void addIncomingTransition (TransitionModel incomingTransition){}
}
