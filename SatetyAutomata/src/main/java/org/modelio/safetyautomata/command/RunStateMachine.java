package org.modelio.safetyautomata.command;

import java.util.ArrayList;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.modelio.api.module.IModule;
import org.modelio.api.module.command.DefaultModuleCommandHandler;
import org.modelio.api.module.context.IModuleContext;
import org.modelio.api.module.context.log.ILogService;
import org.modelio.metamodel.uml.behavior.stateMachineModel.EntryPointPseudoState;
import org.modelio.metamodel.uml.behavior.stateMachineModel.StateMachine;
import org.modelio.vcore.smkernel.mapi.MObject;

import utils.graph.Graph;
import utils.graph.MyTransition;
import utils.graph.Node;

public class RunStateMachine extends DefaultModuleCommandHandler {
	
	@Override
	public boolean accept(List<MObject> selectedElements, IModule module) {
		// TODO Auto-generated method stub
		return selectedElements.size() == 1;
	}

	@Override
	public void actionPerformed(List<MObject> selectedElements, IModule module) {
		// TODO Auto-generated method stub
		StateMachine stateMachine = (StateMachine) selectedElements.get(0);
		IModuleContext context = module.getModuleContext();
		ILogService logService = context.getLogService();
		
		//get entry
		EntryPointPseudoState entry = stateMachine.getEntryPoint().get(0);
		
		try {
			analyse(entry, logService);
		} catch (ScriptException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void analyse(EntryPointPseudoState entry, ILogService logService) throws ScriptException {
		// TODO Auto-generated method stub
		Graph graph = new Graph(entry);
		String init = entry.getDescriptor().get(0).getContent();
		
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine se = manager.getEngineByName("js");
		
		se.eval(init);
		
		//parse the params
		String[] lines = init.split("\n");
		List <String> params = new ArrayList<String>();
		for(String line : lines) {
			params.add(line.split(" ")[1]);
		}
		
		
		String result = "";
		for(String param : params) {
			result += param + " = " + se.eval(param) + " ; ";
		}
		
		logService.info(result);
		result = "";
		Node head = graph.getHead();
		
		while (true) {
			for (MyTransition mt : head.getOutgoing()) {
				if (mt.getCondition() == "") {
					head = mt.getTarget();
				} else {
					boolean flag = false;
					try {
						flag = (boolean) se.eval(mt.getCondition());
					} catch (Exception e) {
						flag = false;
						head = mt.getTarget();
						break;
					}
					
					if (flag) {
						head = mt.getTarget();
						for (String action : head.getActions()) {
							se.eval(action);
							logService.info("do " + action);
						}
						for(String param : params) {
							result += param + " = " + se.eval(param) + " ; ";
						}
						logService.info(result);
						result = "";
						break;
					}
				}
			}
			if (head.getOutgoing().size() == 0) {
				break;
			}
		}
		
	}

}
