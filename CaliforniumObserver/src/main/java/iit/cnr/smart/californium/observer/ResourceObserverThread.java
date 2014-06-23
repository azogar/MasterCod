package iit.cnr.smart.californium.observer;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import manager.Manager;
import model.Measurement;
import ch.ethz.inf.vs.californium.Utils;
import ch.ethz.inf.vs.californium.coap.MediaTypeRegistry;
import ch.ethz.inf.vs.californium.coap.Request;
import ch.ethz.inf.vs.californium.coap.Response;

public class ResourceObserverThread extends Thread {
	private String hostname;
	private String uriname;
	private Integer deviceid;
	private Manager dbmanager;

	public ResourceObserverThread() {
		this("localhost");
	}
	
	public ResourceObserverThread(String hostname) {
		this(hostname, "coap://[" + hostname + "]:5683/sensors/button", Integer.valueOf(0));
	}
	
	public ResourceObserverThread(String hostname, String uriname, Integer deviceid) {
		this(hostname, uriname, deviceid, new Manager());
	}
	
	public ResourceObserverThread(String hostname, String uriname, Integer deviceid, Manager dbmanager) {
		this.hostname = hostname;
		this.uriname = uriname;
		this.deviceid = deviceid;
		this.dbmanager = dbmanager;
	}
	
	@Override
	public void run() {
		this.setName("ThreadObserver-" + hostname);
		try {
			URI uri = new URI(uriname);

			Request request = Request.newGet();
			request.setURI(uri);
			request.setPayload("");
			request.getOptions().setObserve(1);
			request.send();

			do {
				try {
					Response response = request.waitForResponse();

					if (response != null) {
						System.out.println(Utils.prettyPrint(response));
						System.out.println("[ThreadObserver-" + hostname + "] Time elapsed (ms): " + response.getRTT());
						String message = response.getPayloadString();
						System.out.println("[ThreadObserver-" + hostname + "] PAYLOAD  " + message);
				
						JSONObject jsonmessage = new JSONObject(message);
						
						if (jsonmessage.get("sample") instanceof JSONArray) {
							JSONArray jsonarray = (JSONArray) jsonmessage.get("sample");
							dbmanager.connetti();
							for (int i = 0; i < jsonarray.length(); i++) {
								if (dbmanager.isConnesso()) {
									java.util.Calendar calendar = java.util.Calendar.getInstance();
									java.util.Date utildate = calendar.getTime();
									java.sql.Date sqldate = new Date(utildate.getTime());
									
									Measurement measurement = new Measurement(Integer.valueOf(0), deviceid, sqldate, String.valueOf(jsonarray.getJSONObject(i).get("value")));
									
									dbmanager.addMeasurement(measurement);		
								} else {
									System.err.println("[ThreadObserver-" + hostname + "] Db connection problems");	
								}
								
							}
							dbmanager.disconnetti();
						} else {
							//TODO errore non e' un array
						}
					} else {	
						System.err.println("[ThreadObserver-" + hostname + "] Request timed out");
					}
				} catch (InterruptedException e) {
					System.err.println("[ThreadObserver-" + hostname + "] Interrupted Received: " + e.getMessage());
					System.exit(-1);
				} catch (JSONException e) {
					System.err.println("[ThreadObserver-" + hostname + "] Invalid json: " + e.getMessage());
				}	
			} while (true);			
			
		} catch (URISyntaxException e) {
			System.err.println("[ThreadObserver-" + hostname + "] Failed to parse URI: " + e.getMessage());
			System.exit(-1);
		}

	}
	
}
