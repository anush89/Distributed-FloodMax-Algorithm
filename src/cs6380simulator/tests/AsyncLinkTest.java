package cs6380simulator.tests;

import static org.junit.Assert.*;

import java.util.LinkedList;

import org.junit.Test;

import cs6380simulator.links.AsyncLink;

public class AsyncLinkTest {

	@Test
	public void asyncLinkShouldNotDeliverImmediately() {
		AsyncLink link = new AsyncLink(1,1.0, new LinkedList<String>());
		link.putMessage("test");
		
		String message = link.getMessage();
		
		assertNull(message);
	}
	
	@Test
	public void asyncLinkShouldDeliverWithinMaxWaits() {
		String message = "test";
		
		AsyncLink link = new AsyncLink(1,1.0, new LinkedList<String>());
		link.putMessage(message);
		
		int i = 0;
		String result = null;
		
		while (i < AsyncLink.MaxAsyncWait && (result == null)){
			link.goToNextRound();
			result = link.getMessage();
			i++;
		}
		
		assertEquals(message, result);
	}

}
