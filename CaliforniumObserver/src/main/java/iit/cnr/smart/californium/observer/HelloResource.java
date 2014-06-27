package iit.cnr.smart.californium.observer;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.StringTokenizer;
import java.util.Vector;

import manager.DeviceUtil;
import manager.Manager;
import model.Board;
import model.Device;
import ch.ethz.inf.vs.californium.Utils;
import ch.ethz.inf.vs.californium.coap.BlockOption;
import ch.ethz.inf.vs.californium.coap.MediaTypeRegistry;
import ch.ethz.inf.vs.californium.coap.Request;
import ch.ethz.inf.vs.californium.coap.Response;
import ch.ethz.inf.vs.californium.network.Exchange;
import ch.ethz.inf.vs.californium.server.resources.ResourceBase;

public class HelloResource extends ResourceBase {
    private int count;
	
    public HelloResource() {
        super("hello");
        getAttributes().setTitle("Hello Resource");
        count = 0;
    }
    
    @Override
    public void handleGET(Exchange exchange) {
    	String hostname = String.valueOf(exchange.getCurrentRequest().getSource()).substring(1);
    	
    	
    	// salvare il nuovo board nel db
    	Manager dbmanager = new Manager();
		dbmanager.connetti();
		if (dbmanager.isConnesso()) {
			Integer board_id = dbmanager.existIpBoard(hostname);
			if (board_id == null) {
				Board board = new Board(0, hostname, "aula10", "defaultposition", "defaultdescprition", 1);
				board_id = dbmanager.createBoard(board);
			}
			
	    	// chiedere device
			Vector<Device> devices = askForDevices(hostname, board_id);
			for (Device device : devices) {
				if (dbmanager.existIpDevice(device.getUri()) == null) {
					dbmanager.createDevice(device);
					if (device.getActuator() == 0) {
	 					ResourceObserverThread observer = new ResourceObserverThread(device.getUri(), device.getUri(), device.getId());
						observer.start();
					}
				}
			}
		}
		dbmanager.disconnetti();
		
    	
    	
    	// gestire device
    	
    	
        exchange.respond("Hello World!");
    }
    
	private Vector<Device> askForDevices(String hostname, Integer board_id) {
		Vector<Device> devices = new Vector<Device>();
		Response response = null;

		try {
			Request request = Request.newGet();
			request.setURI(new URI("coap://[" + hostname + "]:5683/.well-known/core"));
			request.getOptions().setContentFormat(MediaTypeRegistry.TEXT_PLAIN);
			request.getOptions().setBlock2(BlockOption.size2Szx(64), false, 0);
			request.setPayload("");
			request.send();

			response = request.waitForResponse();

			if (response != null) {
				System.out.println(Utils.prettyPrint(response));
				System.out.println("[AAA] Time elapsed (ms): " + response.getRTT());
				String message = response.getPayloadString();
				System.out.println("[AAA] PAYLOAD -" + message + "-");
				
				StringTokenizer tokenizer = new StringTokenizer(message, ",");
				
				
				while (tokenizer.hasMoreTokens()) {
					String token = tokenizer.nextToken();
				
					if ((token.indexOf("<") >= 0) && (token.indexOf(">") >= 0)) {
						String dinamicuri = token.substring(token.indexOf("<") + 1, token.indexOf(">"));
						boolean sensor = token.contains("sensors");
						boolean actuator = token.contains("actuators");
						boolean observable = token.contains("Observable");
						
						if (sensor || actuator) {
							Device device = new Device(0, board_id, DeviceUtil.getType(token), observable?"10":"0", "coap://[" + hostname + "]:5683" + dinamicuri, "defaultdescription", actuator?1:0);
							devices.add(device);
						}
					}
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return devices;
	}
}