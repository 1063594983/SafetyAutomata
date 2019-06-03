package org.modelio.safetyautomata.command;

import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.modelio.api.module.IModule;
import org.modelio.api.module.command.DefaultModuleCommandHandler;
import org.modelio.api.module.context.IModuleContext;
import org.modelio.api.module.context.log.ILogService;
import org.modelio.metamodel.uml.behavior.stateMachineModel.EntryPointPseudoState;
import org.modelio.metamodel.uml.behavior.stateMachineModel.InternalTransition;
import org.modelio.metamodel.uml.behavior.stateMachineModel.State;
import org.modelio.metamodel.uml.behavior.stateMachineModel.StateMachine;
import org.modelio.metamodel.uml.behavior.stateMachineModel.StateVertex;
import org.modelio.metamodel.uml.behavior.stateMachineModel.Transition;
import org.modelio.vcore.smkernel.mapi.MObject;

import utils.graph.Graph;
import utils.graph.MyTransition;
import utils.graph.Node;

public class AnalyseStateMachine extends DefaultModuleCommandHandler {
	/**
	 * Constructor.
	 */
	public AnalyseStateMachine() {
		super();
	}

	/**
	 * @see org.modelio.api.module.commands.DefaultModuleContextualCommand#accept(java.util.List,
	 *      org.modelio.api.module.IModule)
	 */
	@Override
	public boolean accept(List<MObject> selectedElements, IModule module) {
		// Check that there is only one selected element
		return selectedElements.size() == 1;
	}

	@Override
	public void actionPerformed(List<MObject> selectedElements, IModule module) {

		StateMachine stateMachine = (StateMachine) selectedElements.get(0);
		IModuleContext context = module.getModuleContext();
		ILogService logService = context.getLogService();
		// getEntry
		EntryPointPseudoState entry = stateMachine.getEntryPoint().get(0);
		/*
		 * for(Transition t : entry.getOutGoing()) { StateVertex target = t.getTarget();
		 * logService.info(entry.getName() + " ->" + target.getName() + " [" +
		 * t.getGuard() + "]");
		 * 
		 * for(Transition t2 : target.get) }
		 */
		try {
			analyse(entry, logService);
		} catch (ScriptException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void analyse(StateVertex entry, ILogService logService) throws ScriptException {

		/*
		 * for(Transition t : entry.getOutGoing()) { StateVertex target = t.getTarget();
		 * logService.info(entry.getName() + " ->" + target.getName() + " [" +
		 * t.getGuard() + "]");
		 * 
		 * if(target instanceof State) { State state = (State) target;
		 * for(InternalTransition action : state.getInternal()) { logService.info("do" +
		 * action.getEffect()); }
		 * 
		 * }
		 */
		
		Graph graph = new Graph(entry);
		
		

		int a = 10, b = 20;
		String init = "var a = " + a + ";";
		init += "var b = " + b + ";";
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine se = manager.getEngineByName("js");
		se.eval(init);
		logService.info("a = " + a + "; b = " + b);
		
		Node head = graph.getHead();
		Node head2 = graph.getHead();
		
		//printGraph(head2, logService);

		
		while (true) {
			for (MyTransition mt : head.getOutgoing()) {
				if (mt.getCondition() == "") {
					head = mt.getTarget();
				} else {
					boolean flag = (boolean) se.eval(mt.getCondition());
					if (flag) {
						head = mt.getTarget();
						for (String action : head.getActions()) {
							se.eval(action);
						}
						logService.info("a = " + se.eval("a") + "; b = " + se.eval("b"));
						break;
					}
				}
			}
			if (head.getOutgoing().size() == 0 ) {
				break;
			}
		}

		// analyse(target, logService);
		// }
	}

	private void printGraph(Node head2, ILogService logService) {
		for(MyTransition mt : head2.getOutgoing()) {
			Node target = mt.getTarget();
			logService.info(head2.getType() + " -> " + target.getType() + "[" + mt.getCondition() + "]");
			for(String action : target.getActions()) {
				logService.info("do " + action);
			}
			printGraph(target, logService);
			
		}
	}

}
