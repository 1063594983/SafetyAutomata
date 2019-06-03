package org.test;

import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;

import utils.graph.MyTransition;
import utils.graph.Node;

public class Test {
	public static void main(String[] args) throws ScriptException {
		//System.out.println("hello");
		ScriptEngineManager manager = new ScriptEngineManager();
	    ScriptEngine se = manager.getEngineByName("js");
	    
		String str = "123 > 1 && 4 < 2";
		boolean result = true;
		try {
			result = (boolean) ((ScriptEngine) se).eval(str);
		} catch (ScriptException e) {
			e.printStackTrace();
		}
		
		//System.out.println(result);
		
		Node entry = new Node("inital");
		Node n1 = new Node("normal");
		n1.addAction("a = a + 2");
		Node n2 = new Node("normal");
		n2.addAction("b = b + 2");
		MyTransition mt1 = new MyTransition(entry, n1, "a > 10");
		MyTransition mt2 = new MyTransition(entry, n2, "a <= 10");
		entry.addOutGoing(mt1);
		entry.addOutGoing(mt2);
		Node exit = new Node("exit");
		MyTransition mt10 = new MyTransition(n1, exit, null);
		MyTransition mt20 = new MyTransition(n2, exit, null);
		n1.addOutGoing(mt10);
		n2.addOutGoing(mt20);
		
		//analyse(entry);
		String hello = null;
		System.out.println(hello);
	}

	private static void analyse(Node entry) throws ScriptException {
		int a = 10, b = 20;
		String init = "var a = " + a + ";";
		init += "var b = " + b + ";";
		ScriptEngineManager manager = new ScriptEngineManager();
	    ScriptEngine se = manager.getEngineByName("js");
	    se.eval(init);
	    System.out.println("a = " + a + "; b = " + b);
	    Node head = entry;
	    
	    while(true) {
	    	for(MyTransition mt : head.getOutgoing()) {
	    		if(mt.getCondition() == null) {
	    			head = mt.getTarget();
	    		} else {
	    			boolean flag = (boolean) se.eval(mt.getCondition());
	    			if(flag) {
	    				head = mt.getTarget();
	    				for(String action : head.getActions()) {
	    					se.eval(action);
	    				}
	    				System.out.println("a = " + se.eval("a") + "; b = " + se.eval("b"));
	    				break;
	    			}
	    		}
	    	}
	    	if(head.getOutgoing().size() == 1 && head.getOutgoing().get(0).getTarget().getType() == "exit") {
	    		break;
	    	}
	    }
	    
	    //System.out.println(se.eval("a"));
	}
}
