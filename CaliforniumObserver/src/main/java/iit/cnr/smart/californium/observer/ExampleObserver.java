package iit.cnr.smart.californium.observer;

import java.sql.Date;
import java.text.DateFormat;
import java.util.Locale;
import java.util.logging.Level;

import manager.Manager;
import model.Board;
import model.Device;
import ch.ethz.inf.vs.californium.CaliforniumLogger;
import ch.ethz.inf.vs.californium.server.Server;
import ch.ethz.inf.vs.scandium.ScandiumLogger;

public class ExampleObserver {

	static {
		CaliforniumLogger.initialize();
		CaliforniumLogger.setLevel(Level.WARNING);

		ScandiumLogger.initializeLogger();
		ScandiumLogger.setLoggerLevel(Level.INFO);
	}
	
	public static String now() {
		Date date = new Date(System.currentTimeMillis());
		return DateFormat.getDateInstance(DateFormat.FULL, Locale.ITALY).format(date) + ", " + DateFormat.getTimeInstance(DateFormat.FULL, Locale.ITALY).format(date);
	}

	public static void main(String[] args) {
		System.out.println(ExampleObserver.now() + " [Main] Sono Partito");
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
			System.err.println(ExampleObserver.now() + " [Main] Db connection problems");	
		}
		dbmanager.disconnetti();
	}

}
