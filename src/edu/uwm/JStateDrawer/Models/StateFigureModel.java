package edu.uwm.JStateDrawer.Models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class StateFigureModel {
	
	protected String myName;
	protected HashSet<TransitionModel> myIncomingTransitions, myOutgoingTransitions;
	protected ArrayList<String> myActions;
	protected HashMap<String, TransitionModel> myTransitionTriggers;
	
	public StateFigureModel()
	{
		this("default", new HashSet<TransitionModel>(), new HashSet<TransitionModel>(),
				new ArrayList<String>(), new HashMap<String, TransitionModel>());
	}
	
	public StateFigureModel(String name, HashSet<TransitionModel> incomingTransitions,
			HashSet<TransitionModel> outgoingTransitions, ArrayList<String> actions,
			HashMap<String, TransitionModel> transitionTriggers)
	{
		myName = name;
		myIncomingTransitions = incomingTransitions;
		myOutgoingTransitions = outgoingTransitions;
		myActions = actions;
		myTransitionTriggers = transitionTriggers;
	}
	
}
