package manager;

import java.sql.Connection;

import config.Config;

public class DBHandler extends Config{

	public MySQLHandler handler;
	


	
	

	public DBHandler(){
		handler = new MySQLHandler(DB_SCHEMA,DB_USER,DB_PWD);
	}

	public String getDBInfo(){
		return DB_SCHEMA+"-"+DB_USER+"-"+DB_PWD;
	}

	public boolean connetti(){
		return handler.connetti();
	}
	public void disconnetti(){
		 handler.disconnetti();
	}
	public boolean isConnesso(){
		return handler.connesso;
	}
	
	public Connection getConnection(){
		return handler.getConnection();
	}
}