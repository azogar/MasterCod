package ch.ethz.inf.vs.californium.examples.plugtest;

import ch.ethz.inf.vs.californium.coap.CoAP.ResponseCode;
import ch.ethz.inf.vs.californium.coap.Response;
import ch.ethz.inf.vs.californium.network.Exchange;
import ch.ethz.inf.vs.californium.server.resources.ResourceBase;

public class Shutdown extends ResourceBase {

	public Shutdown() {
		super("shutdown");
	}
	
	@Override
	public void handlePOST(Exchange exchange) {
		if (exchange.getRequest().getPayloadString().equals("sesame")) {
			exchange.respond(new Response(ResponseCode.CHANGED));
			System.out.println("Shutdown resource received POST. Exiting");
			try {
				Thread.sleep(500);
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.exit(0);
		} else {
			exchange.respond(ResponseCode.FORBIDDEN);
		}
	}
	
}
