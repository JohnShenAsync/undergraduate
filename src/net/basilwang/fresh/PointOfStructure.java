package net.basilwang.fresh;

public class PointOfStructure {

	private int Id;
	private String Name;
	private double Latitude;
	private double Longitude;
	private double Width;
	private double Height;
	private int Mode;
	private int MapId;
	private int color;
	
	public void setId(int id) {
		this.Id = id;
	}

	public int getId() {
		return this.Id;
	}

	public void setName(String name) {
		this.Name = name;
	}

	public String getName() {
		return this.Name;
	}

	public void setLatitude(double la) {
		this.Latitude = la;
	}

	public double getLatitude() {
		return this.Latitude;
	}

	public void setLongitude(double lo) {
		this.Longitude = lo;
	}

	public double getLongitude() {
		return this.Longitude;
	}

	public void setWidth(double width) {
		this.Width = width;
	}

	public double getWidth() {
		return this.Width;
	}

	public void setHeight(double height) {
		this.Height = height;
	}

	public double getHeight() {
		return this.Height;
	}

	public void setMode(int mode) {
		this.Mode = mode;
	}

	public int getMode() {
		return this.Mode;
	}
	public void setMapId(int mapid){
		this.MapId=mapid;
	}
	public int getMapId(){
		return this.MapId;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}
}
