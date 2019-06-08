package utils.graph;

import java.util.ArrayList;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.modelio.api.modelio.model.IUmlModel;
import org.modelio.metamodel.uml.behavior.stateMachineModel.EntryPointPseudoState;
import org.modelio.metamodel.uml.behavior.stateMachineModel.InternalTransition;
import org.modelio.metamodel.uml.behavior.stateMachineModel.State;
import org.modelio.metamodel.uml.behavior.stateMachineModel.StateVertex;
import org.modelio.metamodel.uml.behavior.stateMachineModel.Transition;

public class Graph {
	
    private Node head = new Node("initial");
    
    public Graph() {
    	
    }
    
	public Graph(StateVertex entry) {
		analyse(head, entry);
	}
	
	public Node getHead() {
		return this.head;
	}
	
	private void analyse(Node head, StateVertex entry) {
		
		for(Transition t : entry.getOutGoing()) {
			StateVertex target = t.getTarget();
			
			Node nodeTo = new Node("normal");
			
			//如果是进入点，得到初始化语句
			if(entry instanceof EntryPointPseudoState) {
				nodeTo.setInit(entry.getDescriptor().get(0).getContent());
			}
			
			if(target instanceof State) {
				for(InternalTransition it : ((State) target).getInternal()) {
					nodeTo.addAction(it.getEffect());
				}
			}
			
			MyTransition mt = new MyTransition(head, nodeTo, t.getGuard());
			head.addOutGoing(mt);
			analyse(nodeTo, target);
		}
		
	}
	
	public Graph generateDataFlowGraph() throws ScriptException {
		Graph graph = new Graph();
		ScriptEngineManager manager = new ScriptEngineManager();
	    ScriptEngine se = manager.getEngineByName("js");
	    
	    String init = this.head.getInit();
	    String[] lines = init.split("\n");
	    List <String> params = new ArrayList<>();
	    for (String line : lines) {
	    	params.add(line.split(" ")[1]);
	    }
	    
	    se.eval(init);
	    
	    for (String param : params) {
	    	graph.head.addAction(param + " = " + se.eval(param)); 
	    }
	    
		generateDataFlowGraphR(graph.head, se, this.head, params);
		
		return graph;
	}

	private void generateDataFlowGraphR(Node head2, ScriptEngine se, Node head3, List<String> params) throws ScriptException {
		Node newGraph = head2;
		Node oldGraph = head3;
		
		for (String param : params) {

			newGraph.addAction(param + " = " + se.eval(param));
		}

		while (oldGraph.getOutgoing().size() != 0) {
			for (MyTransition mt : oldGraph.getOutgoing()) {
				boolean flag = true;
				if ((mt.getCondition() != null && !("".equals(mt.getCondition().trim())))) {
					try {
						flag = (boolean) se.eval(mt.getCondition());
					} catch (Exception e) {
						flag = false;
						oldGraph = mt.getTarget();
						break;
					}
					
					if (flag) {
						// 执行动作
						for (String action: mt.getTarget().getActions()) {
							se.eval(action);
						}
						oldGraph = mt.getTarget();
						Node tmpNode = new Node("normal");
						for (String param : params) {
							tmpNode.addAction(param + " = " + se.eval(param));
						}
						
						MyTransition tmpTran = new MyTransition(newGraph, tmpNode, mt.getCondition());
						newGraph.addOutGoing(tmpTran);
						break;
					}
				} else {
					oldGraph = mt.getTarget();
					break;
				}
			}
		}
	}
	
}
