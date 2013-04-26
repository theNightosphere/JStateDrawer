package edu.uwm.JStateDrawer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Stack;

import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.Figure;

import edu.uwm.JStateDrawer.Models.StateFigureModel;
import edu.uwm.JStateDrawer.Models.TransitionModel;
import edu.uwm.JStateDrawer.figures.EndStateFigure;
import edu.uwm.JStateDrawer.figures.StartStateFigure;
import edu.uwm.JStateDrawer.figures.StateFigure;
import edu.uwm.JStateDrawer.figures.TransitionFigure;

/**
 * This class is used to perform error checking on state diagrams before simulation or
 * serialization.
 * @author Reed
 *
 */
public class DrawingChecker {

	private String errorString;
	private Drawing myDrawingToCheck;
	
	public DrawingChecker()
	{
		
	}
	
	public DrawingChecker(Drawing drawingToCheck)
	{
		myDrawingToCheck = drawingToCheck;
	}
	
	/**
	 * Takes a {@link Drawing} object and checks that it is a valid.
	 * @param currentDrawing
	 * @param checkModelsOnly TODO
	 * @return True if drawing is valid, false if it is not.
	 */
	public boolean validateCurrentDrawing(Drawing currentDrawing, boolean checkModelsOnly)
	{
		myDrawingToCheck = currentDrawing;
		
		ArrayList<Figure> allFigures = new ArrayList<Figure>(myDrawingToCheck.getFiguresFrontToBack());
		
		// If there are no figures, the drawing is valid. This is a trivially true example.  
		if(allFigures.size() < 1)
		{
			return true;
		}
		
		// Check that there is only one start state.
		
		int startStateCount = 0;
		
		
		if(checkModelsOnly)
		{
			StateFigureModel startState = null;
			ArrayList<StateFigureModel> allStateFigures = new ArrayList<StateFigureModel>(allFigures.size());
			for(Figure f : allFigures)
			{
				StateFigure fig = f instanceof TransitionFigure ? null : (StateFigure) f;

				if(!(f instanceof TransitionFigure))
				{
					allStateFigures.add(fig.getModel());
				}

				// Get a count of t
				if(f instanceof StartStateFigure) 
				{ 
					startStateCount++;
					startState = fig.getModel();
				}

			}
			
			// Can only have one start state.
			if(startStateCount > 1 || startStateCount <= 0)
			{
				errorString = String.format("Current diagram has %d start states. A valid diagram may only have one start state.", startStateCount);
				return false;
			}
			
			return DepthFirstGraphCheck(allStateFigures, startState);
		}
		else
		{
			StateFigure startState = null;
			ArrayList<StateFigure> allStateFigures = new ArrayList<StateFigure>(allFigures.size());
			for(Figure f : allFigures)
			{
				StateFigure fig = f instanceof TransitionFigure ? null : (StateFigure) f;

				if(!(f instanceof TransitionFigure))
				{
					allStateFigures.add(fig);
				}

				// Get a count of t
				if(f instanceof StartStateFigure) 
				{ 
					if(!((StartStateFigure)f).getModel().getIsInternalState())
					{
						startStateCount++;
						startState = fig;
					}
				}

			}

			// Can only have one start state.
			if(startStateCount > 1 || startStateCount <= 0)
			{
				errorString = String.format("Current diagram has %d start states. A valid diagram may only have one start state.", startStateCount);
				return false;
			}
			
			return DepthFirstGraphCheck(allStateFigures, startState);
		}
		
		
		
	}
	
	/**
	 * A depth first traversal of the state diagram that ensures each state is reachable
	 * and that every state that is not an {@link EndStateFigure} has at least 1 outgoing
	 * {@link TransitionFigure}. If an error is found, the {@link getErrorString} function
	 * can be used to access a string with a description of what caused the failure.
	 * @param figureGraph An ArrayList of {@link StateFigure} objects
	 * @param startState A {@link StateFigure} that is the start state of the graph.
	 * @return True if every state is reachable and there is a path from the start state to the end
	 * state. False if the diagram is incorrect in some fashion.
	 */
	private boolean DepthFirstGraphCheck(ArrayList<StateFigure> figureGraph, StateFigure startState)
	{
		Stack<StateFigure> openList = new Stack<StateFigure>();
		HashSet<StateFigure> closedList = new HashSet<StateFigure>(openList.size()*2);
		StateFigure currentState;
		openList.push(startState);
		
		// If the start state doesn't have at least 1 outgoing transition, the drawing is not valid.
		if(startState.getOutgoingTransitions().size() < 0)
		{
			errorString = String.format("There are %d transitions coming out of the start state. The start state may only have one outgoing transition.",
					startState.getOutgoingTransitions().size());
			return false;
		}
		
		while(openList.size() > 0)
		{
			currentState = openList.pop();
			closedList.add(currentState);
			HashSet<TransitionFigure> currentTransitions = new HashSet<TransitionFigure>(currentState.getOutgoingTransitions());
			// If the state is not an EndState and has no outgoing transitions, the graph is not well-formed.
			if(!(currentState instanceof EndStateFigure) && currentTransitions.size() < 1)
			{
				errorString = String.format("The state %s has no outgoing transitions but it is not an end state.", currentState.getName());
				return false;
			}
			for(TransitionFigure t : currentTransitions)
			{
				if (!(closedList.contains(t.getEndFigure())))
				{
					openList.push(t.getEndStateFigure());
				}
			}
		}
		if (closedList.size() != figureGraph.size())
		{
			errorString = String.format("%d states are not connected by transitions to the rest of the diagram. A valid state diagram contains no unreachable states.",
					figureGraph.size() - closedList.size());
			return false;
		}
		// We've gone through all transitions, everything is good right now.
		return true;
	}
	
	/**
	 * A depth first traversal of the state diagram that ensures each state is reachable
	 * and that every state that is not an {@link EndStateFigure} has at least 1 outgoing
	 * {@link TransitionFigure}. If an error is found, the {@link getErrorString} function
	 * can be used to access a string with a description of what caused the failure.
	 * @param figureGraph An ArrayList of {@link StateFigure} objects
	 * @param startState A {@link StateFigure} that is the start state of the graph.
	 * @return True if every state is reachable and there is a path from the start state to the end
	 * state. False if the diagram is incorrect in some fashion.
	 */
	private boolean DepthFirstGraphCheck(ArrayList<StateFigureModel> figureGraph, StateFigureModel startState)
	{
		Stack<StateFigureModel> openList = new Stack<StateFigureModel>();
		HashSet<StateFigureModel> closedList = new HashSet<StateFigureModel>(openList.size()*2);
		StateFigureModel currentState;
		openList.push(startState);
		
		// If the start state has more than 1 outgoing transition, the drawing is not valid.
		if(startState.getOutgoingTransitions().size() > 1)
		{
			errorString = String.format("There are %d transitions coming out of the start state. The start state may only have one outgoing transition.",
					startState.getOutgoingTransitions().size());
			return false;
		}
		
		while(openList.size() > 0)
		{
			currentState = openList.pop();
			closedList.add(currentState);
			HashSet<TransitionModel> currentTransitions = new HashSet<TransitionModel>(currentState.getOutgoingTransitions());
			// If the state is not an EndState and has no outgoing transitions, the graph is not well-formed.
			if((!currentState.getName().equals("end")) && currentTransitions.size() < 1)
			{
				errorString = String.format("The state %s has no outgoing transitions but it is not an end state.", currentState.getName());
				return false;
			}
			for(TransitionModel t : currentTransitions)
			{
				if (!(closedList.contains(t.getEndState())))
				{
					openList.push(t.getEndState());
				}
			}
		}
		if (closedList.size() != figureGraph.size())
		{
			errorString = String.format("%d states are not connected by transitions to the rest of the diagram. A valid state diagram contains no unreachable states.",
					figureGraph.size() - closedList.size());
			return false;
		}
		// We've gone through all transitions, everything is good right now.
		return true;
	}
	
	/**
	 * Accesses the string with the error from the most recent drawing check. 
	 * @return
	 */
	public String getErrorString()
	{
		return errorString;
	}
	
}
