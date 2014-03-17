package net.basilwang.fresh;

public class PointOfSegment {

	private int Id;
	private double Latitude;
	private double Longitude;
	private int SegmentId;
	private int MapId;

	public void setId(int id){
		this.Id=id;
	}
	public int getId(){
		return this.Id;
	}
	public void setLatitude(double latitude){
		this.Latitude=latitude;
	}
	public double getLatitude(){
		return this.Latitude;
	}
	public void setLongitude(double longitude){
		this.Longitude=longitude;
	}
	public double getLongitude(){
		return this.Longitude;
	}
	public void setSegmentId(int segmentId){
		this.SegmentId=segmentId;
	}
	public int getSegmentId(){
		return this.SegmentId;
	}
	public void setMapId(int mapId){
		this.MapId=mapId;
	}
	public int getMapId(){
		return this.MapId;
	}
}
