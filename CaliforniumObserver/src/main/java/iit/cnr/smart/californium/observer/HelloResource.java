package iit.cnr.smart.californium.observer;

import java.net.URI;
import java.net.URISyntaxException;
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
import ch.ethz.inf.vs.californium.server.resources.DiscoveryResource;
import ch.ethz.inf.vs.californium.server.resources.Resource;
import ch.ethz.inf.vs.californium.server.resources.ResourceBase;

public class HelloResource extends ResourceBase {
	private Resource discovery;
	private Vector<ResourceObserverThread> devices;
	
	
    public HelloResource() {
        this("hello");
    }

    public HelloResource(String name) {
        super(name);
        devices = new Vector<ResourceObserverThread>();
        discovery = new ResourceBase("disco") {
            @Override
            public void handleGET(Exchange exchange) {
            	StringBuilder builder = new StringBuilder();
            	
            	for (ResourceObserverThread device : devices) {
            		builder.append(String.valueOf(device));
            		builder.append("\n");
            	}
            	
            	exchange.respond(String.valueOf(builder));
            }
       	
        };
        add(discovery);
        getAttributes().setTitle(name + " Resource");
    }
    
    @Override
    public void handleGET(Exchange exchange) {
    	String hostname = String.valueOf(exchange.getCurrentRequest().getSource()).substring(1);
    	System.out.println(ExampleObserver.now() + " [HelloResource] Ricevuta richiesta di partenza da: " + hostname);
    	
    	// salvare il nuovo board nel db
    	Manager dbmanager = new Manager();
		dbmanager.connetti();
		if (dbmanager.isConnesso()) {
			Board board;
			Integer board_id = dbmanager.existIpBoard(hostname);
			if (board_id == null) {		
				board = new Board(0, hostname, "aula10", "defaultposition", "defaultdescprition", 0);
				board_id = dbmanager.createBoard(board);
			} else {
				board = dbmanager.getBoard(board_id);
			}
			
			if (board.getActive() == 0) {
		    	System.out.println(ExampleObserver.now() + " [HelloResource] Board con id: " + board_id + " Richiedo device");
				dbmanager.updateBoardActivation(board_id, 1);
		    	
		    	// chiedere device
				Vector<Device> devices = askForDevices(hostname, board_id);
				System.out.println(ExampleObserver.now() + " [HelloResource] Board id: " + board_id + " Trovati device: "+ devices.size());
				for (Device device : devices) {
					Integer device_id = dbmanager.existIpDevice(device.getUri());
					if (dbmanager.existIpDevice(device.getUri()) == null) {
						device_id = dbmanager.createDevice(device);
					}
					
					boolean find = false;
					for (ResourceObserverThread thread : this.devices) {
						if (thread.getDeviceId().compareTo(device_id) == 0)
							find = true;
					}
					
					if ((device.getActuator() == 0) && (!find)) {
	 					ResourceObserverThread observer = new ResourceObserverThread(device.getUri(), device.getUri(), device_id);
						this.devices.add(observer);
	 					observer.start();
					}

				}
				
			} else {
		    	System.out.println(ExampleObserver.now() + " [HelloResource] Board con id: " + board_id + " gia attivata.");
			}
		}
		dbmanager.disconnetti();
		
    	// gestire device
        exchange.respond("Request handled.");
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
				String message = response.getPayloadString();
				
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
	
	
	public Resource getDiscovery() {
		return this.discovery;
	}
	
}