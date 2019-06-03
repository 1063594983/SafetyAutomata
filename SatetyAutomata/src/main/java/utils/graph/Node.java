package utils.graph;

import java.util.ArrayList;
import java.util.List;

public class Node {
	private List <MyTransition> outgoing = new ArrayList<MyTransition>();
	private List <String> actions = new ArrayList<String>();
	private String type;	//normal, entry, exit 
	
	public Node(String type) {
		super();
		this.type = type;
	}
	
	public void addOutGoing(MyTransition t) {
		outgoing.add(t);
	}
	
	public void addAction(String action) {
		this.actions.add(action);
	}

	public List<MyTransition> getOutgoing() {
		return outgoing;
	}

	public void setOutgoing(List<MyTransition> outgoing) {
		this.outgoing = outgoing;
	}

	public List<String> getActions() {
		return actions;
	}

	public void setActions(List<String> actions) {
		this.actions = actions;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	
}
