package net.basilwang.fresh;

import java.util.ArrayList;
import java.util.List;

import net.basilwang.dao.DAOHelper;
import net.basilwang.dao.IDAOService;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.amap.api.maps.model.LatLng;

public class PointOfSegmentService implements IDAOService {

	private DAOHelper daoHelper;

	public PointOfSegmentService(Context context) {
		this.daoHelper = new DAOHelper(context);
	}

	public void save(PointOfSegment point) {

		String sql = "INSERT INTO PointOfSegment(Id,Latitude,Longitude,SegmentId,MapId) VALUES (?,?,?,?,?)";
		Object[] bindArgs = { point.getId(), point.getLatitude(),
				point.getLongitude(), point.getSegmentId(), point.getMapId() };
		daoHelper.insert(sql, bindArgs);
	}

	public List<String> getSegmentId(int id){
		String sql = "SELECT DISTINCT  SegmentId FROM PointOfSegment where mapId=?";
		String sa[]={String.valueOf(id)};
		Cursor result = daoHelper.query(sql, sa);
		List<String> list = new ArrayList<String>();
		while (result.moveToNext()) {
			String str=result.getString(0);
			list.add(str);
		}
		daoHelper.closeDB();
		return list;
	}
	public List<PointOfSegment> getAll(int id){
		String sql="select * from pointofsegment where mapid=?";
		String sa[]={String.valueOf(id)};
		Cursor result = daoHelper.query(sql, sa);
		List<PointOfSegment> list=new ArrayList<PointOfSegment>();
		while (result.moveToNext()) {
			PointOfSegment point=new PointOfSegment();
			point.setId(result.getInt(0));
			point.setLatitude(result.getDouble(1));
			point.setLongitude(result.getDouble(2));
			point.setSegmentId(result.getInt(3));
			point.setMapId(result.getInt(4));
			list.add(point);
		}
		return list;
	}
	public ArrayList<ArrayList<LatLng>> getMapList(int id){
		List<String> SegMentIdList=getSegmentId(id);
		List<PointOfSegment> PointList=getAll(id);
		ArrayList<ArrayList<LatLng>> list =new ArrayList<ArrayList<LatLng>>();
		for(int i=0;i<SegMentIdList.size();i++){
			ArrayList<LatLng> list2=new ArrayList<LatLng>();
			int segmentId=Integer.parseInt(SegMentIdList.get(i));
			Log.v("tag", "sId"+segmentId);
			for(int j=0;j<PointList.size();j++){
				if(PointList.get(j).getSegmentId()==segmentId){
					LatLng point=new LatLng(PointList.get(j).getLatitude(),PointList.get(j).getLongitude());
					list2.add(point);
				}
			}
			list.add(list2);
		}
		return list;
	}

	@Override
	public void deleteAccount() {
		// TODO Auto-generated method stub
	}

}
