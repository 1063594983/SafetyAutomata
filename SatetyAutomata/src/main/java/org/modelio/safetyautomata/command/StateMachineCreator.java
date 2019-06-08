package org.modelio.safetyautomata.command;

import java.util.ArrayList;
import java.util.List;

import org.modelio.api.modelio.diagram.IDiagramService;
import org.modelio.api.modelio.model.IModelingSession;
import org.modelio.api.modelio.model.ITransaction;
import org.modelio.api.modelio.model.IUmlModel;
import org.modelio.api.module.IModule;
import org.modelio.api.module.context.IModuleContext;
import org.modelio.api.module.context.log.ILogService;
import org.modelio.metamodel.diagrams.StateMachineDiagram;
import org.modelio.metamodel.uml.behavior.stateMachineModel.InternalTransition;
import org.modelio.metamodel.uml.behavior.stateMachineModel.State;
import org.modelio.metamodel.uml.behavior.stateMachineModel.StateMachine;
import org.modelio.metamodel.uml.behavior.stateMachineModel.Transition;
import org.modelio.metamodel.uml.infrastructure.Stereotype;
import org.modelio.metamodel.uml.statik.Class;

import utils.graph.Graph;
import utils.graph.MyTransition;
import utils.graph.Node;

public class StateMachineCreator {
	
	static ILogService logService;
	static IModelingSession session;
	
	public static StateMachine createStateMachine(Graph graph, IModule module) {
		IModuleContext context = module.getModuleContext();
		IDiagramService diagramServices = context.getModelioServices().getDiagramService();
		session = context.getModelingSession();
		logService = context.getLogService();
		IUmlModel factory = session.getModel();
		List <State> states = new ArrayList<State>();
		
		logService.info("start analyse");
		analyse(graph.getHead(), states, factory);
		logService.info("analyse complete");
		
		
		StateMachine stateMachine = factory.createStateMachine();
		stateMachine.setName("new stateMachine");
		
		
		
		Stereotype s = session.getMetamodelExtensions().getStereotype(module.getName(), 
				"JUnit", 
				module.getModuleContext().
				getModelioServices().getMetamodelService().
				getMetamodel().getMClass(Class.class));
		StateMachineDiagram diagram = 
				factory.createStateMachineDiagram("new diagram", stateMachine, s);
		
		
		
		for(State state : states) {
			state.setParent(stateMachine.getTop());
			diagram.getRepresented().add(state);
		}
		
		return stateMachine;
	}
	
	private static void analyse(Node head, List<State> states, IUmlModel factory) {
		List <Node> visited = new ArrayList<Node>();
		tmp(head, states, factory, visited);
		
	}	
	
	private static State tmp(Node head, List<State> states, IUmlModel factory, List<Node> visited) {
		State newState = null;
		try (ITransaction itran = session.createTransaction("generate state machine")) {
			newState = factory.createState();
			newState.setName("" + states.size());
			logService.info("states.size()" + states.size());
			for(String action : head.getActions()) {
				InternalTransition it = factory.createInternalTransition();
				it.setEffect(action);
				logService.info("action" + action);
				newState.getInternal().add(it);
			}
			
			visited.add(head);
			states.add(newState);
			
			for(MyTransition mt : head.getOutgoing()) {
				Transition t = factory.createTransition();
				t.setGuard(mt.getCondition());
				
				logService.info("condition" + mt.getCondition());
				t.setSource(newState);
				
				if (visited.contains(mt.getTarget())) {
					t.setTarget(states.get(visited.indexOf(mt.getTarget())));
				} else {
					t.setTarget(tmp(mt.getTarget(), states, factory, visited));
				}				
			}
			
			itran.commit();
		}

		return newState;
		
	}
	
}
