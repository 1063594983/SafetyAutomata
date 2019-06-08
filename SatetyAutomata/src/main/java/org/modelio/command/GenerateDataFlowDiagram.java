package org.modelio.command;

import java.util.List;

import javax.script.ScriptException;

import org.modelio.api.modelio.diagram.IDiagramHandle;
import org.modelio.api.modelio.model.IModelingSession;
import org.modelio.api.modelio.model.ITransaction;
import org.modelio.api.modelio.model.IUmlModel;
import org.modelio.api.module.IModule;
import org.modelio.api.module.command.DefaultModuleCommandHandler;
import org.modelio.api.module.context.log.ILogService;
import org.modelio.metamodel.diagrams.StateMachineDiagram;
import org.modelio.metamodel.uml.behavior.stateMachineModel.EntryPointPseudoState;
import org.modelio.metamodel.uml.behavior.stateMachineModel.StateMachine;
import org.modelio.metamodel.uml.behavior.stateMachineModel.StateVertex;
import org.modelio.metamodel.uml.behavior.stateMachineModel.Transition;
import org.modelio.metamodel.uml.infrastructure.Stereotype;
import org.modelio.metamodel.uml.statik.Class;
import org.modelio.vcore.smkernel.mapi.MObject;

public class GenerateDataFlowDiagram extends DefaultModuleCommandHandler {
	@Override
	public boolean accept(List<MObject> selectedElements, IModule module) {
		
		return selectedElements.size() == 1 && (selectedElements.get(0) instanceof StateMachine);
	}

	@Override
	public void actionPerformed(List<MObject> selectedElements, IModule module) {
		StateMachine tmp = (StateMachine) selectedElements.get(0);
		EntryPointPseudoState entry = tmp.getEntryPoint().get(0);
		ILogService logService = module.getModuleContext().getLogService();
		IModelingSession session = module.getModuleContext().getModelingSession();
		IUmlModel factory = session.getModel();
		String str = entry.getOutGoing().get(0).getGuard();

		GenerateAutomata generateAutomata = new GenerateAutomata(module);
		StateMachine stateMachine = null;

		try {
			stateMachine = generateAutomata.generateDataFlowDiagram(entry, tmp.getOwner());
		} catch (ScriptException e) {
			e.printStackTrace();
		}
		logService.info("generate datadiagram complete");
		
		try (ITransaction t = session.createTransaction("add automata")) {
			
			Stereotype s = session.getMetamodelExtensions().getStereotype(module.getName(), 
					"JUnit", 
					module.getModuleContext().
					getModelioServices().getMetamodelService().
					getMetamodel().getMClass(Class.class));
			
		
			StateMachineDiagram diagram = factory.createStateMachineDiagram("dataflow diagram", stateMachine, s);
			
			stateMachine.setName("new stateMachine2");
			
			for (StateVertex state : stateMachine.getTop().getSub()) {
				diagram.getRepresented().add(state);
			}
			//tmp.getOwner().getOwnedBehavior().add(stateMachine);
			//tmp.getOwner().getOwnedBehavior().add(stateMachine);
			//显示图像
			
			try (IDiagramHandle dh = module.getModuleContext().getModelioServices().getDiagramService().getDiagramHandle(diagram)) {
				dh.unmask(diagram.getRepresented().get(0), 250, 20);
				for (int i = 0; i < diagram.getRepresented().size(); i++) {
					//dh.unmask(diagram.getRepresented().get(i), 400, 100 * i + 20);
					for (Transition tran : ((StateVertex)diagram.getRepresented().get(i)).getOutGoing()) {
						dh.unmask(tran, 400, 100 * i + 20);
					}
					
				}
				
				dh.save();
				dh.close();
			}

			
			t.commit();
		}
	}
}
