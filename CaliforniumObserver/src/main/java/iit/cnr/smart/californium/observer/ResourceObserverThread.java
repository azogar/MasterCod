package iit.cnr.smart.californium.observer;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Date;
import java.text.DateFormat;

import javax.swing.text.DateFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import manager.Manager;
import model.Measurement;
import ch.ethz.inf.vs.californium.coap.Request;
import ch.ethz.inf.vs.californium.coap.Response;

public class ResourceObserverThread extends Thread {
	private String hostname;
	private String uriname;
	private Integer deviceid;
	private Manager dbmanager;
	private int tryal;

	private final static int MAXTRYAL = 5;
	
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
		this.tryal = 0;
	}
	
	@Override
	public void run() {
		this.setName("ThreadObserver-" + hostname);
		try {
			System.out.println(ExampleObserver.now() + " [ThreadObserver-" + hostname + "] Richiesta di abbonamento iniziata");		
			
			URI uri = new URI(uriname);

			Request request = createAndSendMessage(uri);
			
			do {
				try {
					Response response = request.waitForResponse(60000);

					if (response != null) {
						tryal = 0;
						String message = response.getPayloadString();
						System.out.println(ExampleObserver.now() + " [ThreadObserver-" + hostname + "] PAYLOAD  " + message);
				
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
									System.err.println(ExampleObserver.now() + " [ThreadObserver-" + hostname + "] Db connection problems");	
								}
								
							}
							dbmanager.disconnetti();
						} else {
							//TODO errore non e' un array
						}
					} else {	
						System.err.print(ExampleObserver.now() + " [ThreadObserver-" + hostname + "] [ERROR] Request timed out " + ++tryal + "/" + MAXTRYAL);
						if (tryal != MAXTRYAL) {
							System.err.println(" Retry");
							request = createAndSendMessage(uri);
						} else {
							System.err.println(" Exit");
							return;
						}
					}
				} catch (InterruptedException e) {
					System.err.println(ExampleObserver.now() + " [ThreadObserver-" + hostname + "] [ERROR] Interrupted Received: " + e.getMessage());
				} catch (JSONException e) {
					System.err.println(ExampleObserver.now() + " [ThreadObserver-" + hostname + "] [ERROR] Invalid json: " + e.getMessage());
				}	
			} while (true);			
			
		} catch (URISyntaxException e) {
			System.err.println(ExampleObserver.now() + " [ThreadObserver-" + hostname + "] [ERROR] Failed to parse URI: " + e.getMessage());
		}

	}
	
	private Request createAndSendMessage(URI uri) {
		Request request = Request.newGet();
		request.setURI(uri);
		request.setPayload("");
		request.getOptions().setObserve(1);
		request.send();
		
		return request;
	}
	
}
