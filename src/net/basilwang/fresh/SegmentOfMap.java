package net.basilwang.fresh;

import java.util.ArrayList;
import java.util.List;

public class SegmentOfMap {

	private int Id;
	private List<PointOfSegment> PointOfSegments=new ArrayList<PointOfSegment>();
	public void setPointOfSegments(List<PointOfSegment> point){
		this.PointOfSegments=point;
	}
	public List<PointOfSegment> getPointOfSegments(){
		return this.PointOfSegments;
	}
	public void setId(int id){
		this.Id=id;
	}
	public int getId(){
		return this.Id;
	}
}
