package org.modelio.safetyautomata.command;

import java.util.List;

import javax.script.ScriptException;

import org.modelio.api.modelio.model.IModelingSession;
import org.modelio.api.modelio.model.ITransaction;
import org.modelio.api.module.IModule;
import org.modelio.api.module.command.DefaultModuleCommandHandler;
import org.modelio.api.module.context.IModuleContext;
import org.modelio.api.module.context.log.ILogService;
import org.modelio.metamodel.uml.behavior.stateMachineModel.EntryPointPseudoState;
import org.modelio.metamodel.uml.behavior.stateMachineModel.StateMachine;
import org.modelio.vcore.smkernel.mapi.MObject;

import utils.graph.Graph;

public class GenerateStateMachine extends DefaultModuleCommandHandler {

	@Override
	public boolean accept(List<MObject> selectedElements, IModule module) {
		return selectedElements.size() == 1;
	}
	
	@Override
	public void actionPerformed(List<MObject> selectedElements, IModule module) {
		StateMachine stateMachine = (StateMachine) selectedElements.get(0);

		IModuleContext context = module.getModuleContext();
		ILogService logService = context.getLogService();
		EntryPointPseudoState entry = stateMachine.getEntryPoint().get(0);
		IModelingSession session = context.getModelingSession();
		
		Graph graph = new Graph(entry);
		Graph graph2 = null;
		try {
			graph2 = graph.generateDataFlowGraph();
		} catch (ScriptException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logService.info("graph complete");
		
		try (ITransaction t = session.createTransaction("generate")) {
			StateMachine stateMachine2 = StateMachineCreator.createStateMachine(graph2, module);
			stateMachine.getOwner().getOwnedBehavior().add(stateMachine2);
			
			t.commit();
		}
		
		logService.info("generate complete");
	}
	
}
