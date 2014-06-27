package manager;

public class DeviceUtil {

	public static final String TEMPERATURE = "tmp";
	public static final String CO2 = "co2";
	public static final String CO = "co";
	public static final String LIGHT = "lght";
	public static final String MOVE = "move";
	public static final String SOUND = "snd";
	public static final String POWER_METER = "pm";
	public static final String RELAY = "rly";
	public static final String DIMMER = "dim";
	
	public static String getDeviceImg(String type){
		if(type.equals(TEMPERATURE))return "images/temperature.jpeg";
		if(type.equals(CO2))return "images/co2.jpg";
		if(type.equals(CO))return "images/co.jpg";
		if(type.equals(LIGHT))return "images/light.jpg";
		if(type.equals(MOVE))return "images/movement.png";
		if(type.equals(SOUND))return "images/sound.png";
		if(type.equals(POWER_METER))return "images/powermeter.jpg";
		if(type.equals(RELAY))return "images/relay.jpg";
		if(type.equals(DIMMER))return "images/dimmer.gif";
		return "";
	}
	
	public static String getDeviceTypeString(String type){
		if(type.equals(TEMPERATURE))return "Temperature Sensor";
		if(type.equals(CO2))return "CO2 Sensor";
		if(type.equals(CO))return "CO Sensor";
		if(type.equals(LIGHT))return "Light Sensor";
		if(type.equals(MOVE))return "Movement Sensor";
		if(type.equals(SOUND))return "Sound Sensor";
		if(type.equals(POWER_METER))return "Power Meter";
		if(type.equals(RELAY))return "Relay";
		if(type.equals(DIMMER))return "Dimmer";
		return "";
	}
	
	public static String getType(String type) {
		if(type.contains("temperature"))return TEMPERATURE;
		if(type.contains("co2"))return CO2;
		if(type.contains("co"))return CO;
		if(type.contains("light"))return LIGHT;
		if(type.contains("move"))return MOVE;
		if(type.contains("sound"))return SOUND;
		if(type.contains("power_meter"))return POWER_METER;
		if(type.contains("relay"))return RELAY;
		if(type.contains("dimmer"))return DIMMER;
		return "";		
	}
}
