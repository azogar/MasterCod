package iit.cnr.smart.californium.observer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.sql.Date;
import java.text.DateFormat;
import java.util.Locale;
import java.util.logging.Level;

import manager.Manager;
import model.Board;
import model.Device;
import ch.ethz.inf.vs.californium.CaliforniumLogger;
import ch.ethz.inf.vs.californium.network.CoAPEndpoint;
import ch.ethz.inf.vs.californium.network.config.NetworkConfig;
import ch.ethz.inf.vs.californium.network.config.NetworkConfigDefaults;
import ch.ethz.inf.vs.californium.server.Server;
import ch.ethz.inf.vs.californium.server.resources.DiscoveryResource;


public class ExampleObserver {

	static {
		CaliforniumLogger.initialize();
		CaliforniumLogger.setLevel(Level.WARNING);
	}
	
	public static String now() {
		Date date = new Date(System.currentTimeMillis());
		return DateFormat.getDateInstance(DateFormat.FULL, Locale.ITALY).format(date) + ", " + DateFormat.getTimeInstance(DateFormat.FULL, Locale.ITALY).format(date);
	}

	public static void main(String[] args) {
		System.out.println(ExampleObserver.now() + " [Main] Sono Partito");
	/*	Server server = new Server();
		server.add(new HelloResource());
		server.start();*/
		
		Manager dbmanager = new Manager();
		dbmanager.connetti();
		if (dbmanager.isConnesso()) {
			for (Board board : dbmanager.getAllBoards()) {
				dbmanager.updateBoardActivation(board.getId(), 0);
			}
		} else {
			System.err.println(ExampleObserver.now() + " [Main] Db connection problems");	
		}
		dbmanager.disconnetti();
		
		try {
			InetAddress address = InetAddress.getByName("ff06::fd");
	
			System.out.println(address.isMulticastAddress());
			
			MultiConnector connector = new MultiConnector(new InetSocketAddress(address, 5683));
			NetworkConfig config = NetworkConfig.getStandard();
			connector.setReceiverThreadCount(config.getInt(NetworkConfigDefaults.UDP_CONNECTOR_RECEIVER_THREAD_COUNT));
			connector.setSenderThreadCount(config.getInt(NetworkConfigDefaults.UDP_CONNECTOR_SENDER_THREAD_COUNT));
			connector.setReceiveBufferSize(config.getInt(NetworkConfigDefaults.UDP_CONNECTOR_RECEIVE_BUFFER));
			connector.setSendBufferSize(config.getInt(NetworkConfigDefaults.UDP_CONNECTOR_SEND_BUFFER));
			connector.setLogPackets(config.getBoolean(NetworkConfigDefaults.UDP_CONNECTOR_LOG_PACKETS));
			connector.setReceiverPacketSize(config.getInt(NetworkConfigDefaults.UDP_CONNECTOR_DATAGRAM_SIZE));
		
			Server servermulti = new Server();
			servermulti.addEndpoint(new CoAPEndpoint(connector, config));
			
			servermulti.add(new HelloResource());
			servermulti.start(); 
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		/*
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
		dbmanager.disconnetti();*/
	}

}
