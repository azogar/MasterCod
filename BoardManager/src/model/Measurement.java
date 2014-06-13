package model;

import java.sql.Date;

public class Measurement {
	
	Integer id;
	Integer id_device;
	Date timestamp;
	String value;
	
	public Measurement(){
		
	}

	public Measurement(Integer id, Integer id_device, Date timestamp,
			String value) {
	
		this.id = id;
		this.id_device = id_device;
		this.timestamp = timestamp;
		this.value = value;
	}

	public Integer getId() {
		return id;
	}

	public Integer getId_device() {
		return id_device;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public String getValue() {
		return value;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setId_device(Integer id_device) {
		this.id_device = id_device;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public void setValue(String value) {
		this.value = value;
	}

	
	
	
	
}
