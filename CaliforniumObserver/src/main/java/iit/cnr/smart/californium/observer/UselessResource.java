package iit.cnr.smart.californium.observer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.sql.Date;
import java.util.StringTokenizer;
import java.util.Vector;

import manager.DeviceUtil;
import manager.Manager;
import model.Board;
import model.Device;
import ch.ethz.inf.vs.californium.coap.BlockOption;
import ch.ethz.inf.vs.californium.coap.MediaTypeRegistry;
import ch.ethz.inf.vs.californium.coap.Request;
import ch.ethz.inf.vs.californium.coap.Response;
import ch.ethz.inf.vs.californium.network.Exchange;
import ch.ethz.inf.vs.californium.server.Server;
import ch.ethz.inf.vs.californium.server.resources.DiscoveryResource;
import ch.ethz.inf.vs.californium.server.resources.ResourceBase;

public class UselessResource extends ResourceBase {
    public UselessResource(String name) {
        super(name);
        getAttributes().setTitle("Useless Resource");
    }
    
    @Override
    public void handleGET(Exchange exchange) {
        exchange.respond("Hello World!");
    }
    

    public static void main (String [] args) {
 
		Response response = null;

		try {
			Request request = Request.newGet();
//			request.setURI(new URI("coap://[::1]:5683/.well-known/core"));
			//request.setURI(new URI("coap://[ff6f::c30c:0:0:1]:5683/.well-known/core"));
		    request.setURI(new URI("coap://[ff06::fd]:5683/hello/disco"));
			request.getOptions().setContentFormat(MediaTypeRegistry.TEXT_PLAIN);
			request.getOptions().setBlock2(BlockOption.size2Szx(64), false, 0);
			request.setPayload("");
			request.setMulticast(true);
			
			System.out.println(request.isMulticast());
			
			request.setConfirmable(false);
			request.send();

			response = request.waitForResponse();

			if (response != null) {
				String message = response.getPayloadString();
				System.out.println("tu ma");
				System.out.println(String.valueOf(message));
			}

		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
    	/*
    	Server server = new Server();
		server.add(new UselessResource("hello"));
		server.start();*/
    }
}