package cs6380simulator.factories;

import java.util.ArrayList;
import java.util.concurrent.CyclicBarrier;

import cs6380simulator.algorithms.Algorithm;
import cs6380simulator.algorithms.BFS;
import cs6380simulator.algorithms.FloodMax;
import cs6380simulator.algorithms.FloodMaxAcks;
import cs6380simulator.algorithms.FloodMaxAcksAsync;
import cs6380simulator.algorithms.TestCommunication;
import cs6380simulator.nodes.Node;


public class NodeFactory {
	
	private AlgorithmSet algorithmSet;
	protected CyclicBarrier sentinel;
	private boolean isAsync;
	
	public NodeFactory(AlgorithmSet algorithmSet, CyclicBarrier sentinel, boolean isAsync){
		this.algorithmSet = algorithmSet;
		this.sentinel = sentinel;
		this.isAsync = isAsync;
	}
	
	protected ArrayList<Algorithm> getAlgorithmSet(int nid){
		ArrayList<Algorithm> algorithms = new ArrayList<Algorithm>();
		
		switch (algorithmSet){
			case FloodMax:
				algorithms.add(new BFS(nid));
				algorithms.add(new FloodMax(nid));
				break;
			case FloodMaxWithAcks:
				algorithms.add(isAsync ? new FloodMaxAcksAsync(nid) : new FloodMaxAcks(nid));
				break;
			case TestComm:
				algorithms.add(new TestCommunication(nid));
				break;
			default:
				throw new IllegalArgumentException("The specified algorithm set has not been implemented or is invalid.");
		}
		
		return algorithms;
	}
	
	// Instantiate an instance of the node
	public Node create(int nid){
		return new Node(nid, getAlgorithmSet(nid), sentinel);
	}
	
	/**
	 * A list of valid algorithm sets
	 *
	 */
	public enum AlgorithmSet {
		FloodMax, FloodMaxWithAcks, TestComm
	}
}
