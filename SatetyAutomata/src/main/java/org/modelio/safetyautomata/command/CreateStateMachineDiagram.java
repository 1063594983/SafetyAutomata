package org.modelio.safetyautomata.command;

import java.util.List;

import org.modelio.api.modelio.diagram.IDiagramHandle;
import org.modelio.api.modelio.diagram.IDiagramService;
import org.modelio.api.modelio.model.IModelingSession;
import org.modelio.api.modelio.model.ITransaction;
import org.modelio.api.modelio.model.IUmlModel;
import org.modelio.api.module.IModule;
import org.modelio.api.module.command.DefaultModuleCommandHandler;
import org.modelio.api.module.context.IModuleContext;
import org.modelio.metamodel.diagrams.AbstractDiagram;
import org.modelio.metamodel.diagrams.StateMachineDiagram;
import org.modelio.metamodel.uml.behavior.stateMachineModel.Region;
import org.modelio.metamodel.uml.behavior.stateMachineModel.State;
import org.modelio.metamodel.uml.behavior.stateMachineModel.StateMachine;
import org.modelio.metamodel.uml.behavior.stateMachineModel.Transition;
import org.modelio.metamodel.uml.infrastructure.Stereotype;
import org.modelio.vcore.smkernel.mapi.MObject;
import org.modelio.metamodel.uml.statik.Package;
import org.modelio.metamodel.uml.statik.Class;
public class CreateStateMachineDiagram extends DefaultModuleCommandHandler{
	
	@Override
	public boolean accept(List<MObject> selectedElements, IModule module) {
		// TODO Auto-generated method stub
		return selectedElements.size() == 1;
	}
	
	@Override
	public void actionPerformed(List<MObject> selectedElements, IModule module) {
		
		// TODO Auto-generated method stub
		MObject object = selectedElements.get(0);
		Package myPackage = (Package) object;
		IModuleContext context = module.getModuleContext();
		IDiagramService diagramServices = context.getModelioServices().getDiagramService();
		IModelingSession session = context.getModelingSession();
		try (ITransaction t = session.createTransaction("create statemachine")) {
			IUmlModel factory = session.getModel();
			StateMachine stateMachine = factory.createStateMachine();
			stateMachine.setName("new stateMachine");
			myPackage.getOwnedBehavior().add(stateMachine);
			Stereotype s = session.getMetamodelExtensions().getStereotype(module.getName(), 
					"JUnit", 
					module.getModuleContext().
					getModelioServices().getMetamodelService().
					getMetamodel().getMClass(Class.class));
			
			State newState = factory.createState();
			newState.setName("new state");
			
			State newState2 = factory.createState();
			newState2.setName("new state2");
			
			Transition tran = factory.createTransition();
			tran.setTarget(newState2);
			tran.setSource(newState);
			
			newState.setParent(stateMachine.getTop());
			newState2.setParent(stateMachine.getTop());
			
			StateMachineDiagram diagram = factory.createStateMachineDiagram("new diagram", stateMachine, s);
			diagram.getRepresented().add(newState);
			diagram.getRepresented().add(newState2);
			
			try (IDiagramHandle dh = diagramServices.getDiagramHandle((AbstractDiagram) diagram)) {
				
				dh.unmask(newState, 100, 100);
				dh.unmask(newState2, 200, 200);
				dh.unmask(tran, 100, 100);
				
				dh.save();
				dh.close();
			}
			
			t.commit();
		}
	}
	
}
