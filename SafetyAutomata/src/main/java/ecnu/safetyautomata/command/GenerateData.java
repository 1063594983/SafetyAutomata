package ecnu.safetyautomata.command;

import java.util.ArrayList;
import java.util.List;
import org.modelio.api.module.IModule;
import org.modelio.api.module.command.DefaultModuleCommandHandler;
import org.modelio.api.module.context.log.ILogService;
import org.modelio.metamodel.uml.behavior.stateMachineModel.StateMachine;
import org.modelio.metamodel.uml.behavior.stateMachineModel.StateVertex;
import org.modelio.metamodel.uml.behavior.stateMachineModel.Transition;
import org.modelio.vcore.smkernel.mapi.MObject;

public class GenerateData extends DefaultModuleCommandHandler{
	
	private List <StateVertex> states = new ArrayList<StateVertex>();
	private List <Transition> trans = new ArrayList<Transition>();
	
	@Override
    public boolean accept(List<MObject> selectedElements, IModule module) {
        // Check that there is only one selected element
        return selectedElements.size() == 1 && (selectedElements.get(0) instanceof StateMachine);
    }

    /**
     * @see org.modelio.api.module.commands.DefaultModuleContextualCommand#actionPerformed(java.util.List,
     *      org.modelio.api.module.IModule)
     */
    @Override
    public void actionPerformed(List<MObject> selectedElements, IModule module) {
    	
        ILogService logService = module.getModuleContext().getLogService();
        
    	states.clear();
    	trans.clear();
    	StateMachine stateMachine = (StateMachine) selectedElements.get(0);
    	for (StateVertex state : stateMachine.getTop().getSub()) {
    		states.add(state);
    		for (Transition t : state.getOutGoing()) {
    			trans.add(t);
    		}
    	}
    	
    	//write to state.csv
    	String result = "";
    	for (int i = 0, li = states.size(); i < li; i++) {
    		result = "";
    		result += i + "; ";
    		result += "\t" + states.get(i).getLocalProperty("StateName") + "; \t";
    		for (Transition t : states.get(i).getOutGoing()) {
    			result += trans.indexOf(t) + " ";
    		}
    		result += ";";
    		result += "\t" + states.get(0).getLocalProperty("StateValues") + "; ";
    		logService.info(result);
    	}
    	
    	//write to transition.csv
    	for (int i = 0, li = trans.size(); i < li; i++) {
    		result = "";
    		result += i + "; ";
    		result += "\t" + trans.get(i).getLocalProperty("Priority") + "; ";
    		result += "\t" + states.indexOf(trans.get(i).getSource()) + "; ";
    		result += "\t" + states.indexOf(trans.get(i).getTarget()) + "; ";
    		result += "\t" + trans.get(i).getGuard() + "; ";
    		logService.info(result);
    	}
    	
    	//logService.info("the num of tran : " + trans.size());
    	
    }
}
