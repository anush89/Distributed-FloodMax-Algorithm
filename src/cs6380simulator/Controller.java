package cs6380simulator;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CyclicBarrier;

import cs6380simulator.factories.LinkFactory;
import cs6380simulator.factories.NodeFactory;
import cs6380simulator.factories.NodeFactory.AlgorithmSet;
import cs6380simulator.links.AsyncLink;
import cs6380simulator.links.Link;
import cs6380simulator.nodes.Node;


/**
 * A node controller for managing multiple nodes simulating a distributed system.
 *
 */
public class Controller {
	
	private HashMap<Integer, Node> nodes;
	private CyclicBarrier sentinel;
	
	/**
	 * Initializes a new node controller based on the specified adjacency matrix
	 * @param nodeCount Number of nodes to initialize
	 * @param adjacencyMatrix A n x n matrix for which (2,3) represents a link from node 2 to node 3.
	 * @throws InstantiationException 
	 * @throws SecurityException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 */
	public Controller(int nodeCount, LinkedList<LinkedList<Double>> adjacencyMatrix, NodeFactory nodeFactory, LinkFactory linkFactory, CyclicBarrier sentinel) throws InstantiationException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, SecurityException{

		// Initialize the shared barrier for synchronization
		this.sentinel = sentinel;
		
		// Initialize a master list of nodes
		nodes = new HashMap<Integer, Node>();

		// Initialize n nodes with the proper algorithms
		for (int nid = 0; nid < nodeCount; nid++){	
			nodes.put(nid, nodeFactory.create(nid));
		}
		
		createLinks(adjacencyMatrix, linkFactory);
	}
	
	/**
	 * Converts the adjacency matrix into the proper Link instances for each node.
	 * @param adjacencyMatrix The matrix representing the links 
	 * @throws InstantiationException 
	 */
	private void createLinks(LinkedList<LinkedList<Double>> adjacencyMatrix, LinkFactory linkFactory) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException{
		int nodeCount = nodes.size();
		
		// Initialize the proper links on the adjacency matrix
		for (int onode = 0; onode < nodeCount; onode++){
			// Each row represents a specific node and that node's outgoing links to other nodes
			Node outgoingNode = nodes.get(onode);
			
			List<Double> row = adjacencyMatrix.get(onode);
			
			for (int inode = 0; inode < nodeCount; inode++){
				
				double weight = row.get(inode);
				
				// If the weight of the link is 0, don't add a link.
				if (weight <= 0){
					continue;
				}
				
				// Retrieve the incoming node
				Node incomingNode = nodes.get(inode);
				
				// Assumes links are bidirectional at this point
				
				// Construct an outgoing link
				Queue<String> queue = new ConcurrentLinkedQueue<String>();
				
				Link outgoingLink = linkFactory.create(incomingNode.getId(), weight, queue);
				outgoingNode.addOutgoingLink(outgoingLink);
				
				// Construct the appropriate return incoming link using the same queue
				Link incomingLink = linkFactory.create(outgoingNode.getId(), weight, queue, outgoingLink.getInTransitList());
				incomingNode.addIncomingLink(incomingLink);
			}
		}
	}
	
	/**
	 * Start the nodes running as separate threads
	 */
	public void start(){
		LinkedList<Thread> threads = new LinkedList<Thread>();
		
		try{

			logMessage("Starting node threads...");
			
			for (Node node : this.nodes.values()){
				Thread t = new Thread(node);
				threads.add(t);
				t.start();
			}
			
			logMessage("All node threads started");
			
			boolean allNodesComplete = false;
			
			// Check to see if all nodes have completed
			while (!allNodesComplete){
				sentinel.await();
				
				int completedNodes = 0;
				
				for (Node node : this.nodes.values()){
					
					if (node.getFinalResult() != null){
						completedNodes = completedNodes + 1;
					}

				}
				
				allNodesComplete = (completedNodes == nodes.size());

			}
			
			// Iterate through all the nodes and output their results
			for (Node node : this.nodes.values()){
				logMessage(String.format("Node %d has returned result: %s", node.getId(), node.getFinalResult()));
			}
			
			logMessage("All nodes have terminated.");

		}catch (Exception e){
			e.printStackTrace();
		}finally{
			
			// Shutdown the threads now that we have final results for everything
			for (Thread t : threads){
				if (t.isAlive()){
					t.interrupt();
				}
			}
			
			logMessage("All threads have been killed.");
		}	
	}
	
	/**
	 * Writes the specified message to the log stream
	 * @param message The message to write to the log stream
	 */
	private void logMessage(String message){
		Log.logMessage(this.getClass().getSimpleName(), message);
	}
	
	/**
	 * Static Methods
	 */
	
	/**
	 * Prints out instructions on how to run the program
	 */
	private static void printUsage(){
		System.err.println();
		System.err.println("This program expects arguments in the following format: input-file algorithm-set mode");
		System.err.println();
		System.err.println("input-file: A file path pointing to a valid input file.");
		System.err.println("algorithm-set: A value which determines which algorithms for the nodes to run: either flood-max or flood-max-acks.");
		System.err.println("mode: A value determining whether or not the links should operate in synchronus or asynchronus mode. Value can be sync or async.");
		System.exit(1);
	}
	
	public static void main(String[] args) {
		
			if (args.length != 3){
				printUsage();
			}
			
			try {
				
				boolean isAsync = false;
				
				switch(args[2]){
					case "sync":
						isAsync = false;
						break;
					case "async":
						isAsync = true;
						break;
					default: 
						System.err.println("Invalid mode!");
						printUsage();
				}
				
				

				InputFile input = new InputFile(args[0]);
				
				AlgorithmSet algorithms = null;
				
				switch (args[1]){
					case "flood-max":
						algorithms = AlgorithmSet.FloodMax;
						break;
					case "flood-max-acks":
						algorithms = AlgorithmSet.FloodMaxWithAcks;
						break;
					case "test":
						algorithms = AlgorithmSet.TestComm;
						break;
					default:
						System.err.println("Invalid algorithm set!");
						printUsage();
				}

				CyclicBarrier sentinel = new CyclicBarrier(input.NodeCount + 1);
				NodeFactory nodeFactory = new NodeFactory(algorithms, sentinel, isAsync);
				LinkFactory linkFactory = new LinkFactory(isAsync);
				
				Controller c = new Controller(input.NodeCount, input.AdjacencyMatrix, nodeFactory, linkFactory, sentinel);
				c.start();
			
			}catch (IOException exception){
				System.err.println(exception.getMessage());
				printUsage();
			}catch (InputFileInvalidException exception){
				System.err.println(exception.getMessage());
				printUsage();
			}catch (Exception e){ 
				e.printStackTrace();
			}

	}
}
