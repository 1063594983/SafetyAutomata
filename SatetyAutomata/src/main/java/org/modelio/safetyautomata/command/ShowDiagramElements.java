package org.modelio.safetyautomata.command;

import java.util.List;

import org.modelio.api.modelio.diagram.IDiagramService;
import org.modelio.api.modelio.model.IModelingSession;
import org.modelio.api.module.IModule;
import org.modelio.api.module.command.DefaultModuleCommandHandler;
import org.modelio.api.module.context.IModuleContext;
import org.modelio.api.module.context.log.ILogService;
import org.modelio.metamodel.diagrams.StaticDiagram;
import org.modelio.metamodel.uml.behavior.stateMachineModel.EntryPointPseudoState;
import org.modelio.metamodel.uml.behavior.stateMachineModel.ExitPointPseudoState;
import org.modelio.metamodel.uml.behavior.stateMachineModel.Region;
import org.modelio.metamodel.uml.behavior.stateMachineModel.State;
import org.modelio.metamodel.uml.behavior.stateMachineModel.StateMachine;
import org.modelio.vcore.smkernel.mapi.MObject;

public class ShowDiagramElements extends DefaultModuleCommandHandler {

	@Override
	public void actionPerformed(List<MObject> selectedElements, IModule module) {
		
		StateMachine stateMachine = (StateMachine) selectedElements.get(0);
		IModuleContext context = module.getModuleContext();
		ILogService logService = context.getLogService();
		IDiagramService diagramService = context.getModelioServices().getDiagramService();
		IModelingSession session = context.getModelingSession();

		/*
		for (MObject o : object.getCompositionChildren()) {
			if(o instanceof StaticDiagram) {
				logService.info(o.getName());
			} else {
				StateMachine stateMachine = (StateMachine) o;
				logService.info("stateMachine : " + stateMachine.getName());
				
				for(State state : stateMachine.getSubmachineState()) {
					logService.info("SubmachineState :  " + state.getName());
				}
				
				Region region = stateMachine.getTop();
				logService.info("Top : " + region.getName());
				
				for(EntryPointPseudoState entry : stateMachine.getEntryPoint()) {
					logService.info("entryPoint : " + entry.getName());
				}
				
				for(ExitPointPseudoState exit : stateMachine.getExitPoint()) {
					logService.info("exitPoint : " + exit.getName());
				}
				
				for(MObject state : stateMachine.getCompositionChildren()) {
					logService.info("4 " + state.getName());
				}
				
			}
		}
		*/
	}

}
