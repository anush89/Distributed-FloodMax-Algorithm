package cs6380simulator.algorithms;

import java.util.HashMap;

import cs6380simulator.links.Link;

public class TestCommunication extends Algorithm {

	public String result;
	
	public TestCommunication(int nodeId) {
		super(nodeId);
	}

	@Override
	public boolean isTerminated() {
		return this.result != null;
	}

	@Override
	public void processMessages(HashMap<Integer, Link> incoming) {
		logMessage("Processing messages...");
		
		for (Link link : incoming.values()){
			String message = link.getMessage();
			
			if (message != null){
				logMessage("Received messsage!");
				result = message;
			}
		}

	}

	@Override
	public void sendMessages(HashMap<Integer, Link> outgoing) {
		logMessage("Sending messages...");
		
		for (Link link : outgoing.values()){
			link.putMessage(new Integer(nodeId).toString());
		}

	}

	@Override
	public Object getResult() {
		return this.result;
	}

}
