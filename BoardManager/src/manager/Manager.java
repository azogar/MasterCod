package manager;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Board;
import model.BoardInfo;
import model.Device;
import model.Measurement;
import model.Services;

public class Manager extends DBHandler {

	/*
	 * BOARD MANAGER
	 */

	public Board getBoard(int id) {
		Board result = new Board();
		String query = "select * from board where id = ?";
		try {
			PreparedStatement stmt = getConnection().prepareStatement(query);
			stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {

				result.setId(id);
				result.setIp_addr(rs.getString(2));
				result.setRoom(rs.getString(3));
				result.setPosition(rs.getString(4));
				result.setDescription(rs.getString(5));
				result.setActive(rs.getInt(6));

			}
			rs.close();
		} catch (SQLException e) {
		}
		return result;
	}

	public List<Board> getActiveBoards() {
		return getBoardList("select * from board where active = 1");
	}
	
	public Integer existIpBoard(String ip_addr) {
		Integer pk = null;
		String query = "select * from board where ip_addr = ?";
		try {
			PreparedStatement stmt = getConnection().prepareStatement(query);
			stmt.setString(1, ip_addr);
			ResultSet rs = stmt.executeQuery();
			
			rs.next();			
			pk = rs.getInt(1);
			
			rs.close();
		} catch (SQLException e) {
		}
		return pk;

	}

	public Integer existIpDevice(String uri) {
		Integer pk = null;
		String query = "select * from device where uri = ?";
		try {
			PreparedStatement stmt = getConnection().prepareStatement(query);
			stmt.setString(1, uri);
			ResultSet rs = stmt.executeQuery();
			
			rs.next();			
			pk = rs.getInt(1);
			
			rs.close();
		} catch (SQLException e) {
		}
		return pk;

	}

	public List<Board> getAllBoards() {
		return getBoardList("select * from board");
	}

	private List<Board> getBoardList(String query) {
		List<Board> list = new ArrayList<Board>();
		try {
			PreparedStatement stmt = getConnection().prepareStatement(query);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Board result = new Board();
				result.setId(rs.getInt(1));
				result.setIp_addr(rs.getString(2));
				result.setRoom(rs.getString(3));
				result.setPosition(rs.getString(4));
				result.setDescription(rs.getString(5));
				result.setActive(rs.getInt(6));
				list.add(result);

			}
			rs.close();
		} catch (SQLException e) {
		}
		return list;

	}

	public Integer createBoard(Board b) {
		String query = "insert into board"
				+ " (ip_addr,room,position,description) values (?,?,?,?)";

		try {
			PreparedStatement preparedStatement = getConnection()
					.prepareStatement(query);
			preparedStatement.setString(1, b.getIp_addr());
			preparedStatement.setString(2, b.getRoom());
			preparedStatement.setString(3, b.getPosition());
			preparedStatement.setString(4, b.getDescription());
			preparedStatement.executeUpdate();
			ResultSet rs = preparedStatement.getGeneratedKeys();
			if (rs.next()) {
				return rs.getInt(1);
			}
		}

		catch (SQLException e) {
			e.printStackTrace();

		}
		return -1;
	}

	public boolean deleteBoard(int id) {
		return delete("board", id);
	}

	public BoardInfo getBoardInfo(int id) {
		BoardInfo result = new BoardInfo();
		String query = "select * from board_info where id_board = ?";
		try {
			PreparedStatement stmt = getConnection().prepareStatement(query);
			stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {

				result.setId_board(id);
				result.setBattery_level(rs.getString(2));
				result.setLast_access(rs.getDate(3));

			}
			rs.close();
		} catch (SQLException e) {
		}
		return result;
	}

	public boolean updateBoardActivation(Integer board_id, Integer active) {
		String query = "update board set active = ? where id = ?";
		try {
			PreparedStatement preparedStatement = getConnection()
					.prepareStatement(query);
			preparedStatement.setInt(1, active);
			preparedStatement.setInt(2, board_id);
			preparedStatement.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

	}
	
	public boolean updateBoardInfo(BoardInfo b) {
		String query = "update board_info"
				+ " set battery_level = ?, last_access = ? where id_board = ?";
		try {
			PreparedStatement preparedStatement = getConnection()
					.prepareStatement(query);
			preparedStatement.setString(1, b.getBattery_level());
			preparedStatement.setDate(2, b.getLast_access());
			preparedStatement.setInt(3, b.getId_board());
			preparedStatement.executeUpdate();
			return true;
		} catch (SQLException e) {
			return false;
		}

	}

	/*
	 * DEVICE MANAGER
	 */

	public List<Device> getDeviceList(int id_board) {
		List<Device> list = new ArrayList<Device>();
		try {
			PreparedStatement stmt = getConnection().prepareStatement(
					"select * from device where id_board = " + id_board);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Device result = new Device();
				result.setId(rs.getInt(1));
				result.setId_board(rs.getInt(2));
				result.setType(rs.getString(3));
				result.setPeriod(rs.getString(4));
				result.setUri(rs.getString(5));
				result.setDescription(rs.getString(6));
				result.setActuator(rs.getInt(7));
				list.add(result);

			}
			rs.close();
		} catch (SQLException e) {
		}
		return list;

	}

	public Integer createDevice(Device d) {
		String query = "insert into device"
				+ " (id_board,type,period,uri,description,actuator) values (?,?,?,?,?,?)";

		try {
			PreparedStatement preparedStatement = getConnection()
					.prepareStatement(query);
			preparedStatement.setInt(1, d.getId_board());
			preparedStatement.setString(2, d.getType());
			preparedStatement.setString(3, d.getPeriod());
			preparedStatement.setString(4, d.getUri());
			preparedStatement.setString(5, d.getDescription());
			preparedStatement.setInt(6, d.getActuator());
			preparedStatement.executeUpdate();
			ResultSet rs = preparedStatement.getGeneratedKeys();
			if (rs.next()) {
				return rs.getInt(1);
			}
		}

		catch (SQLException e) {
			e.printStackTrace();

		}
		return -1;
	}

	/*
	 * SERVICES MANAGER
	 */

	public List<Services> getServicesList(int id_board) {
		List<Services> list = new ArrayList<Services>();
		try {
			PreparedStatement stmt = getConnection().prepareStatement(
					"select * from services where id_board = " + id_board);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Services result = new Services();
				result.setId(rs.getInt(1));
				result.setId_board(rs.getInt(2));
				result.setUri(rs.getString(3));
				result.setDescription(rs.getString(4));
				list.add(result);

			}
			rs.close();
		} catch (SQLException e) {
		}
		return list;

	}

	public Integer createServices(Services s) {
		String query = "insert into services"
				+ " (id_board,uri,description) values (?,?,?)";

		try {
			PreparedStatement preparedStatement = getConnection()
					.prepareStatement(query);
			preparedStatement.setInt(1, s.getId_board());
			preparedStatement.setString(2, s.getUri());
			preparedStatement.setString(3, s.getDescription());
			preparedStatement.executeUpdate();
			ResultSet rs = preparedStatement.getGeneratedKeys();
			if (rs.next()) {
				return rs.getInt(1);
			}
		}

		catch (SQLException e) {
			e.printStackTrace();

		}
		return -1;
	}

	/*
	 * MEASUREMENT MANAGER
	 */
	private List<Measurement> getMeasurementList(String query) {
		List<Measurement> list = new ArrayList<Measurement>();
		try {
			PreparedStatement stmt = getConnection().prepareStatement(query);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				Measurement result = new Measurement();
				result.setId(rs.getInt(1));
				result.setId_device(rs.getInt(2));
				result.setTimestamp(rs.getDate(3));
				result.setValue(rs.getString(4));
				list.add(result);

			}
			rs.close();
		} catch (SQLException e) {
		}
		return list;

	}

	public List<Measurement> getMeasurements(int device) {
		return getMeasurementList("select * from measurement where id_device = "
				+ device);
	}

	public List<Measurement> getMeasurements(int device, Date start_time,
			Date end_time) {
		if (end_time == null) {
			return getMeasurementList("select * from measurement where id_device = "
					+ device
					+ "and timestamp > "
					+ start_time);

		} else {
			return getMeasurementList("select * from measurement where id_device = "
					+ device
					+ "and timestamp > "
					+ start_time
					+ " and timestamp < " + end_time);

		}
	}
	
	public Integer addMeasurement(Measurement m) {
		String query = "insert into measurement"
				+ " (id_device,timestamp,value) values (?,?,?)";

		try {
			PreparedStatement preparedStatement = getConnection()
					.prepareStatement(query);
			preparedStatement.setInt(1, m.getId_device());
			preparedStatement.setDate(2, m.getTimestamp());
			preparedStatement.setString(3, m.getValue());
			preparedStatement.executeUpdate();
			ResultSet rs = preparedStatement.getGeneratedKeys();
			if (rs.next()) {
				return rs.getInt(1);
			}
		}

		catch (SQLException e) {
			e.printStackTrace();

		}
		return -1;
	}
	

	private boolean delete(String table, int id) {
		String query = "delete from " + table + " where id = " + id;
		try {
			PreparedStatement preparedStatement = getConnection()
					.prepareStatement(query);

			preparedStatement.executeUpdate();
			return true;
		} catch (SQLException e) {
			return false;

		}
	}
	
	public List<Board> getBoardListByRoom(String room){
		List<Board> result = new ArrayList<Board>();
		String query = "SELECT b.id,b.name, d.type, d.actuator FROM device d, board b where b.id=d.id_board and active=1 and b.room='"+room+"'";
		Board tmp = new Board();
		int currentId = -1;
		try {
			PreparedStatement stmt = getConnection().prepareStatement(query);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				
				int id = rs.getInt(1);
				if(id==currentId){
					Device d = new Device(rs.getString(3));
					if(rs.getInt(4)==0)tmp.addSensors(d);
					else tmp.addAcuators(d);
				}
				else{
					if(currentId!=-1)
					result.add(tmp);
					tmp = new Board();
					tmp.setId(id);
					tmp.setName(rs.getString(2));
					currentId = id;
					Device d = new Device(rs.getString(3));
					if(rs.getInt(4)==0)tmp.addSensors(d);
					else tmp.addAcuators(d);
				}

			}
			if(currentId!=-1)result.add(tmp);
			rs.close();
		} catch (SQLException e) {
		}
		return result;
	}
	
	public String getMeasurements(String room, String deviceType, int limit){
		String query = "SELECT m.timestamp, m.value FROM measurement m, device d, board b where b.room= ? and d.id_board=b.id and d.type= ? and d.id=m.id_device limit "+limit;
		String result = "";
		try {
			PreparedStatement preparedStatement = getConnection()
					.prepareStatement(query);
			preparedStatement.setString(1, room);
			preparedStatement.setString(2, deviceType);
			ResultSet rs = preparedStatement.executeQuery();
			while (rs.next()) {
				result=result+rs.getString(2)+",";
			}

		} catch (SQLException e) {
			
		}
		
		
		return result.substring(0,result.length()-1);
		
	}
	
	
	public int[] getValoriSopraSoglia(String room, String deviceType,int min, int max){
		int[] result = new int[3];
		String query = "SELECT count(*) FROM measurement m, device d, board b where b.room=? and d.id_board=b.id and d.type=? and d.id=m.id_device and m.value < "+min;
		try {
			PreparedStatement preparedStatement = getConnection()
					.prepareStatement(query);
			preparedStatement.setString(1, room);
			preparedStatement.setString(2, deviceType);
			ResultSet rs = preparedStatement.executeQuery();
			while (rs.next()) {
				result[0]=rs.getInt(1);
			}

		} catch (SQLException e) {
			
		}
		
		query = "SELECT count(*) FROM measurement m, device d, board b where b.room=? and d.id_board=b.id and d.type=? and d.id=m.id_device and m.value>"+min+" and m.value <="+max;
		try {
			PreparedStatement preparedStatement = getConnection()
					.prepareStatement(query);
			preparedStatement.setString(1, room);
			preparedStatement.setString(2, deviceType);
			ResultSet rs = preparedStatement.executeQuery();
			while (rs.next()) {
				result[1]=rs.getInt(1);
			}

		} catch (SQLException e) {
			
		}
		
		query = "SELECT count(*) FROM measurement m, device d, board b where b.room=? and d.id_board=b.id and d.type=? and d.id=m.id_device and m.value>"+max;
		try {
			PreparedStatement preparedStatement = getConnection()
					.prepareStatement(query);
			preparedStatement.setString(1, room);
			preparedStatement.setString(2, deviceType);
			ResultSet rs = preparedStatement.executeQuery();
			while (rs.next()) {
				result[2]=rs.getInt(1);
			}

		} catch (SQLException e) {
			
		}
		
		System.out.println(result[0]);
		System.out.println(result[1]);
		System.out.println(result[2]);
		return result;
	
	
	}

}
