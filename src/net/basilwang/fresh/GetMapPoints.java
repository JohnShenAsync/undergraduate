package net.basilwang.fresh;

import java.util.ArrayList;
import java.util.List;

import net.basilwang.map.BuildOfSchool;
import net.basilwang.map.Constants;
import android.os.AsyncTask;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.PolylineOptions;

public class GetMapPoints extends
		AsyncTask<Object, Object, ArrayList<ArrayList<LatLng>>> {

	private PointOfSegmentService PointService;
	private PointOfStructureService StructureService;
	private AMap map;
	private int id;// 判定校区
	private BuildOfSchool[] builds;
	private ArrayList<ArrayList<LatLng>> route_schooList;
	private ArrayList<Marker> markers = new ArrayList<Marker>();
	private boolean DrawRoute;

	@Override
	protected void onPostExecute(ArrayList<ArrayList<LatLng>> result) {
		super.onPostExecute(result);
		route_schooList = result;
		if (DrawRoute) {
			addLoadToMap();
		}
//		addBuilds();
		addMarkers();
	}

	@Override
	protected ArrayList<ArrayList<LatLng>> doInBackground(Object... params) {
		PointService = (PointOfSegmentService) params[0];
		StructureService = (PointOfStructureService) params[1];
		map = (AMap) params[2];
		id = (Integer) params[3];
		DrawRoute = (Boolean) params[4];
		builds = setBuildOfSchools(StructureService.getPoint(id));
		return PointService.getMapList(id);
	}

	public ArrayList<Marker> getMarkers() {
		return markers;
	}

	private BuildOfSchool[] setBuildOfSchools(List<PointOfStructure> buildList) {
		BuildOfSchool[] builds_school = new BuildOfSchool[buildList.size()];
		for (int i = 0; i < builds_school.length; i++) {
			builds_school[i] = new BuildOfSchool(buildList.get(i).getName(),
					buildList.get(i).getLatitude(), buildList.get(i)
							.getLongitude(), buildList.get(i).getWidth(),
					buildList.get(i).getHeight(), buildList.get(i).getMode(),
					buildList.get(i).getColor());
		}
		return builds_school;
	}

	public void addLoadToMap() {
		for (int i = 0; i < route_schooList.size(); i++) {
			map.addPolyline(new PolylineOptions()
					.addAll(route_schooList.get(i)).color(Constants.color_road)
					.width(15));
		}
	}

	/**
	 * 添加Marker
	 * */
	public void addMarkers() {
		for (int i = 0; i < markers.size(); i++) {
			markers.get(i).destroy();
		}
		int[] icons = null;
		switch (id) {
		case Constants.MapId_Mingshui:
			icons = Constants.icons_mingshui;
			break;
		case Constants.MapId_Shengjing:
			icons = Constants.icons_shengjing;
			break;
		case Constants.MapId_Yanshan:
			icons = Constants.icons_yanshan;
			break;
		case Constants.MapId_Shungeng:
			icons = Constants.icons_shungeng;
			break;
		}
		for (int i = 0; i < builds.length; i++) {
			if (icons[i] != 0) {
				markers.add(builds[i].addMarkerToMap(map, icons[i]));
			}
		}
	}

	/**
	 * 添加建筑物形状
	 * */
	public void addBuilds() {
		for (int i = 0; i < builds.length; i++) {
			builds[i].drawBuilds(map,
					Constants.color_build[builds[i].getColor()]);
		}
	}
}
