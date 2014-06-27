package model;

import java.util.ArrayList;
import java.util.List;

public class Board {

	Integer id;
	String ip_addr;
	String room;
	String position;
	String description;
	int active;
	String name;

	List<Device> sensors;
	List<Device> acuators;

	public Board() {

		sensors = new ArrayList<Device>();
		acuators = new ArrayList<Device>();
	}

	public Board(Integer id, String ip_addr, String room, String position,
			String description, int active) {

		this.id = id;
		this.ip_addr = ip_addr;
		this.room = room;
		this.position = position;
		this.description = description;
		this.active = active;
		sensors = new ArrayList<Device>();
		acuators = new ArrayList<Device>();
	}

	public Integer getId() {
		return id;
	}

	public String getIp_addr() {
		return ip_addr;
	}

	public String getRoom() {
		return room;
	}

	public String getPosition() {
		return position;
	}

	public String getDescription() {
		return description;
	}

	public int getActive() {
		return active;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setIp_addr(String ip_addr) {
		this.ip_addr = ip_addr;
	}

	public void setRoom(String room) {
		this.room = room;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setActive(int active) {
		this.active = active;
	}

	public List<Device> getSensors() {
		return sensors;
	}

	public List<Device> getAcuators() {
		return acuators;
	}

	public void setSensors(List<Device> sensors) {
		this.sensors = sensors;
	}

	public void setAcuators(List<Device> acuators) {
		this.acuators = acuators;
	}
	
	public void addSensors(Device sensor) {
		this.sensors.add(sensor);
	}

	public void addAcuators(Device acuator) {
		this.acuators.add(acuator);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


}
