package org.modelio.safetyautomata.command;

import java.util.List;

import org.modelio.api.modelio.model.IModelingSession;
import org.modelio.api.module.IModule;
import org.modelio.api.module.command.DefaultModuleCommandHandler;
import org.modelio.api.module.context.IModuleContext;
import org.modelio.api.module.context.log.ILogService;
import org.modelio.vcore.smkernel.mapi.MObject;
import org.modelio.metamodel.uml.behavior.commonBehaviors.Behavior;
import org.modelio.metamodel.uml.behavior.stateMachineModel.Region;
import org.modelio.metamodel.uml.behavior.stateMachineModel.StateMachine;
import org.modelio.metamodel.uml.behavior.stateMachineModel.Transition;
import org.modelio.metamodel.uml.infrastructure.UmlModelElement;
import org.modelio.metamodel.uml.statik.Package;

public class ShowStateMachine extends DefaultModuleCommandHandler {

	
	@Override
	public boolean accept(List<MObject> selectedElements, IModule module) {
		return selectedElements.size() == 1;
	}
	
	@Override
	public void actionPerformed(List<MObject> selectedElements, IModule module) {
		StateMachine stateMachine = (StateMachine) selectedElements.get(0);
		IModuleContext context = module.getModuleContext();
		IModelingSession session = context.getModelingSession();
		ILogService logService = context.getLogService();
		
		logService.info("analyse StateMachine");
		
		/*
		for (UmlModelElement element : stateMachine.getOwnedCollaboration()) {
			logService.info("OwnedCollaboration : " + element.getName());
		}
		
		for (Transition t : stateMachine.getEffectOf()) {
			logService.info("EffectOf : " + t.getGuard());
		}*/
		
		logService.info("OwnedCollaboration : " + stateMachine.getOwnedCollaboration().size());
		logService.info("Top : " + stateMachine.getTop());
		Region region = stateMachine.getTop();
		
		logService.info("region-Sub : " + region.getSub().size());
		if(region.getParent() != null) {
			logService.info("region Parent : " + region.getParent().getName());
		} else {
			logService.info("region Parent : null");
		}
		if(region.getRepresented() != null) {
			logService.info("region Represented : " + region.getRepresented().getName());
		} else {
			logService.info("region Represented : null");
		}
		
		
		logService.info("SubmachineState : " + stateMachine.getSubmachineState().size());
		logService.info("EntryPoint : " + stateMachine.getEntryPoint().size());
		logService.info("ExitPoint : " + stateMachine.getExitPoint().size());
		
		logService.info("Owner : " + stateMachine.getOwner().getName());
		logService.info("OwnerOperation : " + (stateMachine.getOwnerOperation() == null));
		logService.info("Parameter : " + stateMachine.getParameter());
		logService.info("OwnedCollaboration : " + stateMachine.getOwnedCollaboration().size());
		logService.info("Caller : " + stateMachine.getCaller().size());
		logService.info("EComponent : " + stateMachine.getEComponent().size());
		logService.info("EffectOf : " + stateMachine.getEffectOf().size());
		
		
	}

}
