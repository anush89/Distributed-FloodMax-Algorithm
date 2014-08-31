package cs6380simulator.links;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

/**
 * Class representing a simulated asynchronous link.
 * The class extends the synchronous link class by wrapping by intercepting calls to putMessage
 * and holding them until a later time when goToNextRound determines the messages should be delivered.
 *
 */
public class AsyncLink extends Link {
	public static final int MaxAsyncWait = 24;
	
	private List<DelayedMessage> inTransitMessages;
	private Random randGenerator;
	
	public AsyncLink(Integer id, Double weight, Queue<String> messageQueue) {
		super(id, weight, messageQueue);
		
		initialize(new LinkedList<DelayedMessage>());
		
	}
	
	public AsyncLink(Integer id, Double weight, Queue<String> messageQueue, List<DelayedMessage> inTransitMessages) {
		super(id, weight, messageQueue);
		
		initialize(inTransitMessages);
	}
	
	protected void initialize(List<DelayedMessage> inTransitMessages){
		randGenerator = new Random();
		this.inTransitMessages = inTransitMessages;
	}
	
	@Override
	public List<DelayedMessage> getInTransitList(){
		return this.inTransitMessages;
	}
	
	@Override
	public void putMessage(String message) {
		inTransitMessages.add(new DelayedMessage(message,randGenerator.nextInt(MaxAsyncWait) + 1));
	}

	@Override
	public void goToNextRound() {
		// Loop through all messages currently in-transit and decrement the time to delivery value
		
		for (DelayedMessage message : inTransitMessages){
			if (message.timeToDelivery != 0){
				message.timeToDelivery = message.timeToDelivery - 1;
			}
		}
		
		LinkedList<DelayedMessage> readyMessages = new LinkedList<DelayedMessage>();
		
		// Loop through all the in-transit messages and add any that are ready to be delivered to
		// the readyMessages list. Stop whenever a non-zero timeToDelivery is found to preserve
		// the FIFO quality of the link.
		for (int i = 0; i < inTransitMessages.size(); i++){
			DelayedMessage msg = inTransitMessages.get(i);
			
			if (msg.timeToDelivery == 0){
				readyMessages.add(msg);
			}else{
				break;
			}
		}
		
		// For each message in readyMessages, remove the message from the in-transit list and
		// then add the message to the actual link's queue for processing.
		for (DelayedMessage msg : readyMessages){
			inTransitMessages.remove(msg);
			Messages.add(msg.message);
		}
	}
	
	/**
	 * Class representing a message to be delivered at a later time.
	 *
	 */
	public class DelayedMessage{
		public String message;
		public int timeToDelivery;
		
		public DelayedMessage(String message, int timeToDelivery){
			this.message = message;
			this.timeToDelivery = timeToDelivery;
		}
	}

}
