package cs6380simulator.algorithms;
import java.util.HashMap;
import java.util.LinkedList;

import cs6380simulator.Log;
import cs6380simulator.links.Link;


public abstract class Algorithm {
	protected int nodeId;
	protected Object inputParameter;
	
	private HashMap<Integer,LinkedList<String>> preparedBuffer;
	
	public Algorithm(int nodeId){
		this.nodeId = nodeId;
		this.preparedBuffer = new HashMap<Integer, LinkedList<String>>();
	}
	
	public abstract boolean isTerminated();
	public abstract void processMessages(HashMap<Integer,Link> incoming);
	public abstract void sendMessages(HashMap<Integer,Link> outgoing);
	public abstract Object getResult();
	
	/**
	 * Stores a message that is generated during the processMessages() phase so that it can later be sent during the
	 * sendMessages() phase.
	 * This method should only be called during the processMessages() phase.
	 * 
	 * @param destinationId The destination Id of the process to send the message to
	 * @param message The message to send
	 */
	protected void prepareMessage(int destinationId, String message){
		if (!preparedBuffer.containsKey(destinationId)){
			preparedBuffer.put(destinationId, new LinkedList<String>());
		}
		
		preparedBuffer.get(destinationId).add(message);
	}
	
	/**
	 * Sends messages that were generated during the processMessages() phase and stored by previous calls to PrepareMessage()
	 * This method should only be called during the sendMessages() method.
	 * 
	 * @param outgoing The list of outgoing links
	 */
	protected void sendPreparedMessages(HashMap<Integer,Link> outgoing){
		for (Integer destinationId : preparedBuffer.keySet()){
			Link link = outgoing.get(destinationId);
			
			for (String message : preparedBuffer.get(destinationId)){
				link.putMessage(message);
			}
		}
		
		
		preparedBuffer.clear();
	}
	
	/**
	 * Sets the input parameter to be used by the algorithm. This is typically 
	 * used to pass in the result from a previously terminated algorithm.
	 * @param parameter The input parameter
	 */
	public void setInputParameter(Object parameter){
		this.inputParameter = parameter;
	}
	
	/**
	 * Writes the specified message to the log stream
	 * @param message The message to write to the log stream
	 */
	protected void logMessage(String message){
		Log.logMessage(String.format("%s[%d]", this.getClass().getSimpleName(), this.nodeId), message);
	}
}
