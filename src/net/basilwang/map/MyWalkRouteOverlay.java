package net.basilwang.map;

import android.content.Context;
import android.graphics.Color;

import com.amap.api.maps.AMap;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.overlay.WalkRouteOverlay;
import com.amap.api.services.route.WalkPath;

public class MyWalkRouteOverlay extends WalkRouteOverlay {

	private String startTitle;
	private String endTitle;

	public MyWalkRouteOverlay(Context arg0, AMap arg1, WalkPath arg2,
			LatLonPoint arg3, LatLonPoint arg4, String start, String end) {
		super(arg0, arg1, arg2, arg3, arg4);
		startTitle=start;
		endTitle=end;
	}

	@Override
	protected int getWalkColor() {

		return Color.argb(250, 0, 0, 255);
	}

	@Override
	public void addToMap() {
		super.addToMap();
		for (int i = 0; i < stationMarkers.size(); i++) {
			stationMarkers.get(i).destroy();
		}
	}

	public void setStartMarker() {

	}

	@Override
	public void addStartAndEndMarker() {
		super.addStartAndEndMarker();
		startMarker.setTitle(startTitle);
		endMarker.setTitle(endTitle);
	}

}
