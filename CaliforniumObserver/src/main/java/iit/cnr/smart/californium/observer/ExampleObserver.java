package iit.cnr.smart.californium.observer;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;

import org.json.JSONObject;

import manager.Manager;
import model.Board;
import model.Device;
import model.Measurement;
import ch.ethz.inf.vs.californium.CaliforniumLogger;
import ch.ethz.inf.vs.californium.Utils;
import ch.ethz.inf.vs.californium.coap.BlockOption;
import ch.ethz.inf.vs.californium.coap.MediaTypeRegistry;
import ch.ethz.inf.vs.californium.coap.OptionSet;
import ch.ethz.inf.vs.californium.coap.Request;
import ch.ethz.inf.vs.californium.coap.Response;
import ch.ethz.inf.vs.californium.server.Server;
import ch.ethz.inf.vs.californium.server.resources.DiscoveryResource;
import ch.ethz.inf.vs.scandium.ScandiumLogger;

public class ExampleObserver {

	private String RESOURCES[] = {}; 
	private String SENSORS[] = {}; 
	
	private static HashMap<String, Integer> entrencesCounters = new HashMap<String, Integer>();
	private static HashMap<String, Integer> exitsCounters = new HashMap<String, Integer>();

	private static final int NUM_PARK = 5;
	private static boolean isFull = false;

	private static String[] ENTRANCE = new String[]{"aaaa::c30c:0:0:1"};
	private static String[] EXIT = new String[]{"aaaa::c30c:0:0:2"};

	private static URI uri = null;

	static {
		CaliforniumLogger.initialize();
		CaliforniumLogger.setLevel(Level.WARNING);

		ScandiumLogger.initializeLogger();
		ScandiumLogger.setLoggerLevel(Level.INFO);
	}


	private static void messageToHosts(boolean state) {
		System.out.println("sending...");
		for (String host : ENTRANCE) {
			sendLedMessage(host, state);

		}
		System.out.println("sent");
	}

	public static int differenceCount(HashMap<String, Integer> counters) {

		Set<String> hosts = counters.keySet();
		Iterator<String> iterator = hosts.iterator();
		int count = 0;
		while (iterator.hasNext()) {
			count += counters.get(iterator.next());

		}
		// int accesses= entrencesCounters.get(Stato.Accesso);
		// int exit= entrencesCounters.get(Stato.Uscita);

		// return NUM_PARK - (accesses-exit);
		return count;
	}

	public static void main(String[] args) {
		// discovery?
		// se si creazione/aggiornamento db
		
		// serve hostname nella tabella board

		
		Server server = new Server();
		server.add(new HelloResource());
		server.start();
		
		// creazione thread osservatori		
		Manager dbmanager = new Manager();
		dbmanager.connetti();
		if (dbmanager.isConnesso()) {
			for (Board board : dbmanager.getAllBoards()) {
				for (Device device : dbmanager.getDeviceList(board.getId())) {
					if ((device.getUri().compareTo("boh") != 0) && (device.getActuator() == 0)) {
	 					ResourceObserverThread observer = new ResourceObserverThread(device.getUri(), device.getUri(), device.getId());
						observer.start();
					}
				}
			}
		} else {
			System.err.println("[Main] Db connection problems");	
		}
		dbmanager.disconnetti();
		


	}

	private static void sendLedMessage(String hostAccess, boolean on) {
		// PUT with payload
		try {
			uri = new URI("coap://[" + hostAccess + "]:5683/actuators/leds?color=r");
		} catch (URISyntaxException e) {
			System.err.println("Uri Syntax Error: " + e.getMessage());
			System.exit(-1);
		}

		// create request according to specified method
		Request request = Request.newPut();
		request.setURI(uri);
		if(on)
			request.setPayload("mode=on");
		else
			request.setPayload("mode=off");
		request.getOptions().setContentFormat(MediaTypeRegistry.TEXT_PLAIN);

		// execute request
		try {
			request.send();

			// receive response
			Response response = null;
			try {
				response = request.waitForResponse();
			} catch (InterruptedException e) {
				System.err.println("Error in receiving the response: "
						+ e.getMessage());
				System.exit(-1);
			}

			// output response

			if (response != null) {
				System.out.println(Utils.prettyPrint(response));
				System.out.println("Time elapsed (ms): " + response.getRTT());
			} else {
				// no response received
				System.err.println("Request timed out led");
			}

		} catch (Exception e) {
			System.err.println("Failed to execute request: " + e.getMessage());
			System.exit(-1);
		}

	}

}
