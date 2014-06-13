package test;

import manager.Manager;

public class ConnectionTest {

	public static void main(String[] args) {
		
		Manager manager = new Manager();
		manager.connetti();
		System.out.println(manager.isConnesso());
		manager.disconnetti();
	}
}
