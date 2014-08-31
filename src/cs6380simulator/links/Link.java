package cs6380simulator.links;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import cs6380simulator.links.AsyncLink.DelayedMessage;

/**
 * Represents a unidirectional link between two nodes
 */
public class Link {
	
	private int id;
	public double Weight;
	protected Queue<String> Messages;
	
	/**
	 * Instantiates a new Link instance
	 * @param id The Id for the node at the other end of the link
	 * @param weight The weight for the link
	 * @param messageQueue The message queue for the link
	 */
	public Link(Integer id, Double weight, Queue<String> messageQueue){
		this.id = id;
		this.Weight = weight;
		this.Messages = messageQueue;
	}
	
	/**
	 * Returns the node ID of the other end of the link
	 * @return The integer node ID of the other end of the link
	 */
	public int getId(){
		return id;
	}
	
	public List<DelayedMessage> getInTransitList(){
		return null;
	}
	
	/**
	 * Retrieves a message from the queue
	 * @return A message from the queue or null if there are no messages
	 */
	public String getMessage(){
		return Messages.poll();
	}
	
	/**
	 * Places a message into the queue
	 * @param message The message to place into the queue
	 */
	public void putMessage(String message){
		Messages.add(message);
	}
	
	/**
	 * Triggers actions on the link that should take place in the next round
	 */
	public void goToNextRound(){
		// Nothing interesting happens here for links in synchronous networks
	}

}
