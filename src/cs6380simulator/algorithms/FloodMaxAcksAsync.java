package cs6380simulator.algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;


//import cs6380simulator.algorithms.FloodMaxAcks.MessageType;
import cs6380simulator.links.Link;


public class FloodMaxAcksAsync extends Algorithm {

	private int tentative_leader;
	private int parent_node;
	private ArrayList<Integer> child_nodes;
	private HashMap<Integer, Boolean> done_status;
	private boolean isTerm;
	private boolean first_time;
	
	public FloodMaxAcksAsync(int nodeId) {
		super(nodeId);
		tentative_leader=nodeId;
		parent_node=nodeId;
		isTerm=false;
		done_status=new HashMap<Integer, Boolean>();
		child_nodes=new ArrayList<>();
		first_time=true;
	}


	@Override
	public boolean isTerminated() {
		return isTerm;
	}


	@Override
	public void processMessages(HashMap<Integer, Link> incoming) {
		boolean leader_changed=false;
		String message;
		
		for(int myKey:incoming.keySet())
		{
			if (first_time)
				done_status.put(myKey, false);
			
			//msg processing
			while((message = incoming.get(myKey).getMessage()) != null)
			{
				//split msgs
				StringTokenizer myToken = new StringTokenizer(message,",");
				
				MessageType msg_type = MessageType.values()[Integer.parseInt(myToken.nextToken())];
				int src_tentative_leader = Integer.parseInt(myToken.nextToken());				
				
				switch (msg_type){
				case EXPLORE:
					//explore msg processing
					
					if (src_tentative_leader>tentative_leader)
					{
						if(parent_node!=nodeId && myKey!=parent_node && !leader_changed)
						{
							sendPruneMsg(parent_node);
							done_status.remove(parent_node);
						}
						tentative_leader=src_tentative_leader;
						parent_node=myKey;
						leader_changed=true;
						//sendExpMsg(myKey, src_tentative_leader);
					}
					else
					{
						//send neg message
						if(myKey!=parent_node)
						{
							sendNegMsg(myKey);
						}
					}
					break;
				case DONE:
					//check this
					done_status.remove(src_tentative_leader);//remove child from hashmap
					
					if(done_status.size()==1 && done_status.containsKey(parent_node))
					{
						sendDoneMsg(parent_node);
						done_status.remove(parent_node);
					}
					
					break;
				case ACK:
					if(!child_nodes.contains(src_tentative_leader))
						child_nodes.add(src_tentative_leader);
					
					break;
				case TERMINATE:
					//terminate
					if(!child_nodes.isEmpty())
					{
						sendTerminateMsg();
					}
					
					isTerm=true;
					break;
				case PRUNE:
					//remove child pointer when parent changes
					if(child_nodes.contains(src_tentative_leader))
					{
						child_nodes.remove(child_nodes.indexOf(src_tentative_leader));
						done_status.remove(src_tentative_leader);
					}
					break;
				case NEG:
					//remove from done_status
					if(done_status.containsKey((src_tentative_leader)))
					{
						done_status.remove(src_tentative_leader);
					}
					break;
				default:
					throw new IllegalArgumentException("There is no handler for the specified message type.");				
				}
			}
		}
		
		if(parent_node != nodeId && isTerm == false && leader_changed == true)
			sendAckMsg(parent_node);
		
		if(parent_node==nodeId && done_status.isEmpty() && !first_time)
		{
			//leader election done, and we are the leader
			//send out terminate msg
			sendTerminateMsg();
			isTerm=true;
		}
		
		if((child_nodes.isEmpty() || done_status.isEmpty()) && !first_time && parent_node != nodeId && !leader_changed && isTerm==false)
		{
			//no child nodes to send data to
			sendDoneMsg(parent_node);
		}
		
		// Output message to console showing current status
		logMessage(String.format("Parent: %d, Tentative Leader: %d", parent_node, tentative_leader));
	}


	@Override
	public void sendMessages(HashMap<Integer, Link> outgoing) {
		MessageType msg_type= MessageType.EXPLORE;
		first_time=false;
		for(int myKey:outgoing.keySet())
		{
			outgoing.get(myKey).putMessage(String.format("%d,%d",msg_type.ordinal(),tentative_leader));
			sendPreparedMessages(outgoing);
		}
		
	}

	public void sendDoneMsg(int parent_node)
	{
		MessageType msg_type= MessageType.DONE;
		prepareMessage(parent_node,String.format("%d,%d",msg_type.ordinal(),nodeId));
	}
	
	public void sendAckMsg(int myKey)
	{
		MessageType msg_type = MessageType.ACK;
		prepareMessage(myKey,String.format("%d,%d",msg_type.ordinal(),nodeId));
	}
	
	public void sendTerminateMsg()
	{
		MessageType msg_type = MessageType.TERMINATE;
		
		for(int child_node : child_nodes)
		{
			prepareMessage(child_node,String.format("%d,%d",msg_type.ordinal(),nodeId));
		}
	}
	
	public void sendPruneMsg(int pNode)
	{
		MessageType msg_type = MessageType.PRUNE;
		prepareMessage(pNode,String.format("%d,%d",msg_type.ordinal(),nodeId));
	}
	
	public void sendNegMsg(int mykey)
	{
		MessageType msg_type = MessageType.NEG;
		prepareMessage(mykey,String.format("%d,%d",msg_type.ordinal(),nodeId));
	}
	
	@Override
	public Object getResult() {
		return tentative_leader;
	}
	
	private enum MessageType {
		EXPLORE, DONE, ACK, TERMINATE, PRUNE, NEG
	}
}