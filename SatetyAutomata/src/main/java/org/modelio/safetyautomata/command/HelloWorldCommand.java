package org.modelio.safetyautomata.command;

import java.util.ArrayList;
import java.util.List;

import javax.script.ScriptException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.modelio.api.modelio.diagram.IDiagramHandle;
import org.modelio.api.modelio.diagram.IDiagramService;
import org.modelio.api.modelio.editor.IEditionService;
import org.modelio.api.modelio.model.IModelingSession;
import org.modelio.api.modelio.model.ITransaction;
import org.modelio.api.modelio.model.IUmlModel;
import org.modelio.api.module.IModule;
import org.modelio.api.module.command.DefaultModuleCommandHandler;
import org.modelio.api.module.context.IModuleContext;
import org.modelio.api.module.context.configuration.IModuleUserConfiguration;
import org.modelio.api.module.context.log.ILogService;
import org.modelio.command.GenerateAutomata;
import org.modelio.metamodel.diagrams.StateMachineDiagram;
import org.modelio.metamodel.diagrams.StaticDiagram;
import org.modelio.metamodel.uml.behavior.stateMachineModel.EntryPointPseudoState;
import org.modelio.metamodel.uml.behavior.stateMachineModel.InternalTransition;
import org.modelio.metamodel.uml.behavior.stateMachineModel.State;
import org.modelio.metamodel.uml.behavior.stateMachineModel.StateMachine;
import org.modelio.metamodel.uml.behavior.stateMachineModel.StateVertex;
import org.modelio.metamodel.uml.behavior.stateMachineModel.Transition;
import org.modelio.metamodel.uml.infrastructure.ModelElement;
import org.modelio.metamodel.uml.infrastructure.Stereotype;
import org.modelio.metamodel.uml.statik.Class;
import org.modelio.vcore.smkernel.mapi.MObject;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * Implementation of the IModuleContextualCommand interface. <br>
 * The module contextual commands are displayed in the contextual menu and in
 * the specific toolbar of each module property page. <br>
 * The developer may inherit the DefaultModuleContextualCommand class which
 * contains a default standard contextual command implementation.
 *
 */
public class HelloWorldCommand extends DefaultModuleCommandHandler {
	/**
	 * Constructor.
	 */
	public HelloWorldCommand() {
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

	/**
	 * @see org.modelio.api.module.commands.DefaultModuleContextualCommand#actionPerformed(java.util.List,
	 *      org.modelio.api.module.IModule)
	 */
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
		
		String json = "{\"condition\":\"a > 5\",\"ifStmt\":[{\"condition\":\"a > 10\",\"ifStmt\":[\"a += 2\"],\"elseStmt\":[\"c += 3\"]},\"a = a + 2\"],\"elseStmt\":[\"a += 3\"]}";
		JSONObject object = JSON.parseObject(json);
		stateMachine = generateAutomata.generateStateMachineByJSONObject(object, tmp.getOwner());
		Stereotype s = session.getMetamodelExtensions().getStereotype(module.getName(), 
				"JUnit", 
				module.getModuleContext().
				getModelioServices().getMetamodelService().
				getMetamodel().getMClass(Class.class));
		
	
		StateMachineDiagram diagram;
		try (ITransaction t = session.createTransaction("create state machine")) {
			
			diagram = factory.createStateMachineDiagram("dataflow diagram", stateMachine, s);
			logService.info("add diagram");
			stateMachine.setName("new statemachine");
			int index = 0;
			for (StateVertex state : stateMachine.getTop().getSub()) {
				String name = "" + index++ + "\n";
				if (state instanceof State) {
					for (InternalTransition it : ((State)state).getInternal()) {
						name += it.getEffect() + "\n";
					}
				}
				state.setName(name);
				diagram.getRepresented().add(state);
			}
			//show diagram
			try (IDiagramHandle dh = module.getModuleContext().getModelioServices().getDiagramService().getDiagramHandle(diagram)) {
				EntryPointPseudoState start = stateMachine.getEntryPoint().get(0);
				List <StateVertex> visited = new ArrayList<StateVertex>();
				logService.info("start show");
				showImg(start, 400, 10, visited, dh);
				logService.info("end show");
				
				dh.save();
				dh.close();
			}
			
			
			
			t.commit();
			
		}
		

	}

	private void showImg(StateVertex start, int i, int j, List<StateVertex> visited, IDiagramHandle dh) {
		dh.unmask(start, i, j);
		if (visited.contains(start)) {
			return;
		}
		visited.add(start);
		if (start instanceof State) {
			for (InternalTransition it : ((State) start).getInternal()) {
				dh.unmask(it, i, j);
			}
		}
		int size = start.getOutGoing().size();
		int index = 0;
		for (Transition t : start.getOutGoing()) {
			dh.unmask(t, i + 200 * index, j + 100);
			showImg(t.getTarget(), i + 200 * index, j + 100, visited, dh);
			index++;
		}
	}

}
