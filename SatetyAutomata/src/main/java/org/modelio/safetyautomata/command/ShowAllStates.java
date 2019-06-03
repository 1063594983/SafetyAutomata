package org.modelio.safetyautomata.command;

import java.util.List;

import org.modelio.api.modelio.diagram.IDiagramService;
import org.modelio.api.modelio.model.IModelingSession;
import org.modelio.api.module.IModule;
import org.modelio.api.module.command.DefaultModuleCommandHandler;
import org.modelio.api.module.context.IModuleContext;
import org.modelio.api.module.context.log.ILogService;
import org.modelio.metamodel.diagrams.StaticDiagram;
import org.modelio.metamodel.uml.behavior.stateMachineModel.State;
import org.modelio.metamodel.uml.behavior.stateMachineModel.StateMachine;
import org.modelio.metamodel.uml.infrastructure.Element;
import org.modelio.vcore.smkernel.mapi.MObject;

public class ShowAllStates extends DefaultModuleCommandHandler{

	@Override
	public boolean accept(List<MObject> selectedElements, IModule module) {
		if (super.accept(selectedElements, module) == false) {
			return false;
		}
		return selectedElements.size() == 1;
	}
	
	@Override
	public void actionPerformed(List<MObject> selectedElements, IModule module) {
		//MObject object = selectedElements.get(0);
		StateMachine stateMachine = (StateMachine) selectedElements.get(0);

		
		IModuleContext context = module.getModuleContext();
		ILogService logService = context.getLogService();
		IDiagramService diagramService = context.getModelioServices().getDiagramService();
		IModelingSession session = context.getModelingSession();
		
		for (State state : stateMachine.getSubmachineState()) {
			logService.info(state.getName());
			
			for(MObject object : state.getCompositionChildren()) {
				logService.info(object.getName());
			}
		}
		
	}

}
