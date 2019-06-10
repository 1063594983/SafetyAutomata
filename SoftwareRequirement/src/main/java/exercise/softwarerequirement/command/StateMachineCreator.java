package exercise.softwarerequirement.command;
import java.util.ArrayList;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.modelio.api.modelio.IModelioContext;
import org.modelio.api.modelio.model.IModelingSession;
import org.modelio.api.modelio.model.ITransaction;
import org.modelio.api.modelio.model.IUmlModel;
import org.modelio.api.module.IModule;
import org.modelio.api.module.context.IModuleContext;
import org.modelio.api.module.context.log.ILogService;
import org.modelio.metamodel.diagrams.StateMachineDiagram;
import org.modelio.metamodel.uml.behavior.stateMachineModel.EntryPointPseudoState;
import org.modelio.metamodel.uml.behavior.stateMachineModel.ExitPointPseudoState;
import org.modelio.metamodel.uml.behavior.stateMachineModel.InternalTransition;
import org.modelio.metamodel.uml.behavior.stateMachineModel.Region;
import org.modelio.metamodel.uml.behavior.stateMachineModel.State;
import org.modelio.metamodel.uml.behavior.stateMachineModel.StateMachine;
import org.modelio.metamodel.uml.behavior.stateMachineModel.StateVertex;
import org.modelio.metamodel.uml.behavior.stateMachineModel.Transition;
import org.modelio.metamodel.uml.statik.NameSpace;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class StateMachineCreator {
	private IModule module = null;
	private IModuleContext context = null;
	private IModelingSession session = null;
	private IUmlModel factory = null;
	private ILogService logService = null;

	public StateMachineCreator(IModule module) {
		this.module = module;
		this.context = module.getModuleContext();
		this.session = context.getModelingSession();
		this.factory = session.getModel();
		this.logService = context.getLogService();
	}

	public StateMachine generateDataFlowDiagram(EntryPointPseudoState entry, NameSpace owner) throws ScriptException {
		StateMachine stateMachine = null;
		
		// 获得初始化语句
		String initStr = entry.getDescriptor().get(0).getContent();
		String[] lines = initStr.split("\n");
		List<String> params = new ArrayList<String>();
		for (String line : lines) {
			params.add(line.split(" ")[1]);
		}
		ScriptEngineManager manager = new ScriptEngineManager();
	    ScriptEngine se = manager.getEngineByName("js");
	    se.eval(initStr);
	    logService.info(initStr);
	    logService.info("init complete");
	    
		State head = null;
		String stateName = "";
		for (String param : params) {
			stateName += param + " = " + se.eval(param) + " ; ";
		}
		try (ITransaction t = session.createTransaction("generate dataflow diagram")) {
			stateMachine = factory.createStateMachine();
			owner.getOwnedBehavior().add(stateMachine);
			head = factory.createState();
			//diagram.getRepresented().add(head);
			head.setName(stateName);
			stateName = "";
			
			head.setParent(stateMachine.getTop());
			
			StateVertex current = entry;
			StateVertex tmpHead = head;
			boolean flag2 = true;
			while (current.getOutGoing().size() != 0) {
				flag2 = true;
				for (Transition tran : current.getOutGoing()) {
					if (tran.getGuard() == "") {
						current = tran.getTarget();
						flag2 = false;
						break;
					} else {
						
						logService.info("judge " + tran.getGuard());
						boolean flag;
						try {
							flag = (boolean) se.eval(tran.getGuard());
						} catch (Exception e) {
							current = tran.getTarget();
							flag2 = false;
							break;
						}
						if (flag) {

							for(InternalTransition it : ((State) tran.getTarget()).getInternal()) {
								logService.info("do " + it.getEffect());
								se.eval(it.getEffect());
							}
							
							State tmpState = factory.createState();
							for (String param : params) {
								stateName += param + " = " + se.eval(param) + " ; ";
							}
							tmpState.setName(stateName);
							logService.info("create state " + stateName);
							stateName = "";

							
							Transition tmpTran = factory.createTransition();
							tmpTran.setGuard(tran.getGuard());
							tmpTran.setSource(tmpHead);
							tmpTran.setTarget(tmpState);
							
							tmpState.setParent(stateMachine.getTop());
							logService.info("create transition " + tran.getGuard());
							tmpHead = tmpState;
							current = tran.getTarget();
							
							//diagram.getRepresented().add(tmpState);
							flag2 = false;
							break;
						} else {
							continue;
						}
					}
				}
				if(flag2) {
					break;
				}
			}

			t.commit();
		}

		return stateMachine;
	}

	public StateMachine generateStateMachineByJSONObject(JSONObject object, NameSpace owner) {
		StateMachine stateMachine = null;
		
		try (ITransaction t = this.session.createTransaction("generate StateMachine")) {
			stateMachine = factory.createStateMachine();
			owner.getOwnedBehavior().add(stateMachine);
			
			Region region = stateMachine.getTop();
			
			EntryPointPseudoState entry = factory.createEntryPointPseudoState();
			ExitPointPseudoState exit = factory.createExitPointPseudoState();
			entry.setParent(region);
			stateMachine.getEntryPoint().add(entry);
			exit.setParent(region);
			State state1 = factory.createState();
			Transition tran1 = factory.createTransition();
			tran1.setSource(entry);
			tran1.setTarget(state1);
			state1.setParent(region);
			
			logService.info("init");
			
			createSubStateMachine(state1, exit, (String)object.get("condition"), object.getJSONArray("ifStmt"), region);
			createSubStateMachine(state1, exit, "!(" + (String)object.get("condition") + ")", object.getJSONArray("elseStmt"), region);
			t.commit();
		}
		
		return stateMachine;
	}
	
	private void createSubStateMachine(StateVertex entry, StateVertex exit, String condition, JSONArray jsonArray, Region region) {
		StateVertex head = entry;
		boolean flag = true;
		if (jsonArray.size() == 0) {
			Transition tran = factory.createTransition();
			tran.setSource(entry);
			tran.setTarget(exit);
			logService.info("create 1");
		} else {
			for (Object obj : jsonArray) {
				if (obj instanceof String) {
					if (flag) {
						State state = factory.createState();
						InternalTransition it = factory.createInternalTransition();
						it.setEffect((String) obj);
						state.getInternal().add(it);
						Transition tran = factory.createTransition();
						tran.setGuard(condition);
						tran.setSource(head);
						tran.setTarget(state);
						head = state;
						flag = false;
						
						state.setParent(region);
						logService.info("create 2");
					} else {
						InternalTransition it = factory.createInternalTransition();
						it.setEffect((String) obj);
						((State) head).getInternal().add(it);
						logService.info("create 3");
					}

				} else if (obj instanceof JSONObject) {
					if (flag) {
						State state = factory.createState();
						Transition t = factory.createTransition();
						t.setGuard(condition);
						t.setSource(head);
						t.setTarget(state);
						flag = false;
						State end = factory.createState();
						
						state.setParent(region);
						end.setParent(region);
						
						createSubStateMachine(state, end, ((JSONObject)obj).getString("condition"), JSON.parseArray(((JSONObject)obj).getString("ifStmt")), region);
						createSubStateMachine(state, end, "!(" + ((JSONObject)obj).getString("condition") + ")", JSON.parseArray(((JSONObject)obj).getString("elseStmt")),region);
						
						head = end;
						
						logService.info("create 4");
					} else {
						State end = factory.createState();
						createSubStateMachine(head, end, ((JSONObject)obj).getString("condition"), JSON.parseArray(((JSONObject)obj).getString("ifStmt")), region);
						createSubStateMachine(head, end, "!(" + ((JSONObject)obj).getString("condition") + ")", JSON.parseArray(((JSONObject)obj).getString("elseStmt")), region);
						
						end.setParent(region);
						
						head = end;
						logService.info("create 5");
					}
				}
			}
			
			Transition tran = factory.createTransition();
			tran.setSource(head);
			tran.setTarget(exit);
			logService.info("end");
		}
	}
}
