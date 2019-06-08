package utils.graph;

public class MyTransition {
	private Node source;
	private Node target;
	private String condition;
	
	public MyTransition() {
		
	}

	public MyTransition(Node source, Node target, String condition) {
		super();
		this.source = source;
		this.target = target;
		this.condition = condition;
	}

	public Node getSource() {
		return source;
	}

	public void setSource(Node source) {
		this.source = source;
	}

	public Node getTarget() {
		return target;
	}

	public void setTarget(Node target) {
		this.target = target;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

}
