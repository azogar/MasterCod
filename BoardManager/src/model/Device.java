package model;

public class Device {
	Integer id;
	Integer id_board;
	String type;
	String period;
	String uri;
	String description;
	int actuator;
	
	public Device(){
		
	}
	
	public Device(String type){
		this.type=type;
	}
	
	public Device(Integer id, Integer id_board, String type, String period,
			String uri, String description, int actuator) {
		this.id = id;
		this.id_board = id_board;
		this.type = type;
		this.period = period;
		this.uri = uri;
		this.description = description;
		this.actuator = actuator;
	}

	public Integer getId() {
		return id;
	}

	public Integer getId_board() {
		return id_board;
	}

	public String getType() {
		return type;
	}

	public String getPeriod() {
		return period;
	}

	public String getUri() {
		return uri;
	}

	public String getDescription() {
		return description;
	}

	public int getActuator() {
		return actuator;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setId_board(Integer id_board) {
		this.id_board = id_board;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setActuator(int actuator) {
		this.actuator = actuator;
	}
	
	

}
