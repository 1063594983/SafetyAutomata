package utils.graph;

import org.modelio.metamodel.uml.behavior.stateMachineModel.InternalTransition;
import org.modelio.metamodel.uml.behavior.stateMachineModel.State;
import org.modelio.metamodel.uml.behavior.stateMachineModel.StateVertex;
import org.modelio.metamodel.uml.behavior.stateMachineModel.Transition;

public class Graph {
	
    private Node head = new Node("initial");
    
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
	
	
	
}
