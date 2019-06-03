package org.modelio.safetyautomata.command;

import org.eclipse.emf.common.util.EList;
import org.modelio.api.modelio.model.IModelingSession;
import org.modelio.api.modelio.model.ITransaction;
import org.modelio.api.modelio.model.IUmlModel;
import org.modelio.api.module.IModule;
import org.modelio.api.module.context.log.ILogService;
import org.modelio.metamodel.diagrams.BehaviorDiagram;
import org.modelio.metamodel.diagrams.StateMachineDiagram;
import org.modelio.metamodel.mda.Project;
import org.modelio.metamodel.uml.behavior.activityModel.Activity;
import org.modelio.metamodel.uml.behavior.stateMachineModel.State;
import org.modelio.metamodel.uml.behavior.stateMachineModel.StateMachine;
import org.modelio.vcore.smkernel.mapi.MObject;
import org.modelio.metamodel.uml.statik.Package;
import org.modelio.metamodel.uml.statik.Class;
import org.modelio.metamodel.uml.statik.Connector;

public class StateCreator {

	public void createState(MObject object, IModule module) {
		/*
		ILogService logService = module.getModuleContext().getLogService();
		for(MObject o : object.getCompositionChildren()) {
			logService.info("createState - " + o.getName());
		}
		*/
		
		IModelingSession session = module.getModuleContext().getModelingSession();
		
		try (ITransaction transaction = session.createTransaction("create a state")) {
			IUmlModel factory = session.getModel();
			
			
			for (MObject root : factory.getModelRoots()) {
				if (root instanceof Project) {
					Package rootPackage = ((Project) root).getModel().get(0);
					//Activity newActivity = factory.createActivity();
					org.modelio.metamodel.uml.statik.Class newClass = factory.createClass();
					rootPackage.getOwnedElement().add(newClass);
					newClass.setName("Class");
					
					//创建状态机
					StateMachine stateMachine = factory.createStateMachine();
					stateMachine.setName("State Machine");
					//StateMachineDiagram stateMachineDiagram = factory.createStateMachineDiagram(arg0, arg1, arg2);
					
					
					//State state = factory.createState();
					
					//stateMachine.getRepresents().add(state);
					/*
					state.setSubMachine(stateMachine);
					state.setName("State");*/
					
					
					rootPackage.getOwnedBehavior().add(stateMachine);
					//Connector connect = factory.createConnector();
					//BehaviorDiagram diagram = factory.createState();
					/*
					stateMachine
					
					transaction.commit();
					*/
					break;
				}
			}
			
			
			
		}
	}
	

}
