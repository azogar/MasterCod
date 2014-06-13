package model;

public class Services {

	Integer id;
	Integer id_board;
	String uri;
	String description;
	
	public Services(){
		
	}
	
	public Services(Integer id, Integer id_board, String uri, String description) {
	
		this.id = id;
		this.id_board = id_board;
		this.uri = uri;
		this.description = description;
	}

	public Integer getId() {
		return id;
	}

	public Integer getId_board() {
		return id_board;
	}

	public String getUri() {
		return uri;
	}

	public String getDescription() {
		return description;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setId_board(Integer id_board) {
		this.id_board = id_board;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	
	
	
}
