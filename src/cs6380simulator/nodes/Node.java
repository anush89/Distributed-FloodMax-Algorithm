package cs6380simulator.nodes;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import cs6380simulator.algorithms.Algorithm;
import cs6380simulator.links.Link;


public class Node implements Runnable {
	
	private int id;
	private HashMap<Integer,Link> incoming;
	private HashMap<Integer,Link> outgoing;
	private List<Algorithm> algorithms;
	
	protected CyclicBarrier sentinel;
	protected Object finalResult = null;
		
	/**
	 * Instantiates a new Node
	 * @param id The node Id
	 * @param sentinel The sentinel to use for synchronization
	 * @param algorithms The algorithms for the node to execute
	 */
	public Node(int id, List<Algorithm> algorithms, CyclicBarrier sentinel){
		this.id = id;
		this.incoming = new HashMap<Integer, Link>();
		this.outgoing = new HashMap<Integer, Link>();
		this.algorithms = algorithms;
		this.sentinel = sentinel;
	}
	
	/**
	 * Retrieves the Id of the node
	 * @return The node ID
	 */
	public int getId(){
		return this.id;
	}
	
	/**
	 * Returns the final result after the node has finished execution
	 * @return The final result value from the last algorithm executed
	 */
	public Object getFinalResult(){
		return this.finalResult;
	}
	
	/**
	 * Adds an incoming link to this node from another node
	 * @param link The incoming link
	 */
	public void addIncomingLink(Link link){
		addLink(link, this.incoming);
	}
	
	/**
	 * Adds an outgoing link to this node to another node
	 * @param link The outgoing link to add
	 */
	public void addOutgoingLink(Link link){
		addLink(link, this.outgoing);
	}
	
	/**
	 * Adds a link to the specified link collection. If a link with the same Id already exists in the collection, only the weight is updated.
	 * @param link The link to add 
	 * @param linkCollection The link collection to add the link to
	 */
	private void addLink(Link link, HashMap<Integer, Link> linkCollection){
		if (linkCollection.containsKey(link.getId())){
			Link existingLink = linkCollection.get(link.getId());
			existingLink.Weight = link.Weight;
		}else{
			linkCollection.put(link.getId(), link);
		}
	}

	/**
	 * Perform any required synchronization tasks
	 * @throws InterruptedException
	 * @throws BrokenBarrierException
	 */
	protected void await() throws InterruptedException, BrokenBarrierException {
		sentinel.await();
	}
	
	/**
	 * Starts the execution of the node. Each algorithm is executed one after another. 
	 * Once all algorithms have executed, the final result value can be retrieved by calling GetFinalResult()
	 */
	@Override
	public void run() {
		this.finalResult = null;
		Object lastResult = null;
		
		try {
			for (Algorithm algo : algorithms){
				// Set the last result as the input parameter of the next algorithm
				algo.setInputParameter(lastResult);
				
				while (!algo.isTerminated()){
					// Perform the process messages phase to update state after messages are received
					algo.processMessages(incoming);
					
					// Perform any necessary waiting...
					await();
					
					// Send all outgoing messages
					algo.sendMessages(outgoing);
					
					// Perform any necessary waiting...
					await();
					
					// Tell the links to take any actions required when the round progresses
					for (Link link : incoming.values()){
						link.goToNextRound();
					}
				}
				
				// Once the algorithm terminates, save the result for later
				lastResult = algo.getResult();
				
			}
			
			// Once all algorithms have completed, set the final result to the result from the last algorithm
			this.finalResult = lastResult;
			
			while (true){
				await();
			}
			
			
		} catch (InterruptedException e) {
			return;
		} catch (BrokenBarrierException e) {
			return;
		}
	}

}
