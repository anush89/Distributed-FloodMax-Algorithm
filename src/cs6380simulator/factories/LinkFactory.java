package cs6380simulator.factories;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Queue;

import cs6380simulator.links.AsyncLink;
import cs6380simulator.links.AsyncLink.DelayedMessage;
import cs6380simulator.links.Link;

public class LinkFactory{
	private boolean isAsync;
	
	public LinkFactory(boolean isAsync){
		this.isAsync = isAsync;
	}
	
	public Link create(Integer id, Double weight, Queue<String> messageQueue) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException{
		if (!isAsync){
			return new Link(id, weight, messageQueue);
		}else{
			return new AsyncLink(id, weight, messageQueue);
		}
	}
	
	public Link create(Integer id, Double weight, Queue<String> messageQueue, List<DelayedMessage> delayedMessages) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException{
		if (!isAsync){
			return new Link(id, weight, messageQueue);
		}else{
			return new AsyncLink(id, weight, messageQueue, delayedMessages);
		}
	}
}
