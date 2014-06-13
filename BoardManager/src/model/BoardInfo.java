package model;

import java.sql.Date;

public class BoardInfo {

	Integer id_board;
	String battery_level;
	Date last_access;
	
	public BoardInfo(){
		
	}
	
	public BoardInfo(Integer id_board, String battery_level, Date last_access) {
	
		this.id_board = id_board;
		this.battery_level = battery_level;
		this.last_access = last_access;
	}
	public Integer getId_board() {
		return id_board;
	}
	public String getBattery_level() {
		return battery_level;
	}
	public Date getLast_access() {
		return last_access;
	}
	public void setId_board(Integer id_board) {
		this.id_board = id_board;
	}
	public void setBattery_level(String battery_level) {
		this.battery_level = battery_level;
	}
	public void setLast_access(Date last_access) {
		this.last_access = last_access;
	}
	
	
	
}
