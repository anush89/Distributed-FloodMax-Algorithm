package cs6380simulator.algorithms;

import java.util.HashMap;

import cs6380simulator.links.Link;

public class FloodMax extends Algorithm {

	int round;
	Integer maxId;
	
	public FloodMax(int nodeId) {
		super(nodeId);
		round = 0;
		maxId = nodeId;
	}

	@Override
	public boolean isTerminated() {
		return round > diameter();
	}

	@Override
	public void processMessages(HashMap<Integer, Link> incoming) {
		String message;
		
		for (Link link : incoming.values()){
			
			while ((message = link.getMessage()) != null){
				int messageId = Integer.parseInt(message);
				
				if (messageId > maxId){
					maxId = messageId;
				}
			}
		}
		
		logMessage(String.format("Current MaxId: %d", maxId));
		
	}

	@Override
	public void sendMessages(HashMap<Integer, Link> outgoing) {
		for (Link link : outgoing.values()){
			link.putMessage(maxId.toString());
		}
		
		round = round + 1;
	}

	@Override
	public Object getResult() {
		return maxId;
	}
	
	private int diameter(){
		return (int)inputParameter;
	}

}
