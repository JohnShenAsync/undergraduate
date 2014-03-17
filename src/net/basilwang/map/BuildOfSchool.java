package net.basilwang.map;

import java.util.Arrays;
import java.util.List;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolygonOptions;

public class BuildOfSchool {
	private String title;
	private LatLng position;
	private double width;
	private double height;
	public static final int CENTER = 1;// 点在中心
	public static final int EDGE_UP = 2;// 点在上边缘
	public static final int EDGE_DOWN = 3;// 点在下边缘
	public static final int EDGE_LEFT = 4;// 点在左边缘
	public static final int EDGE_RIGHT = 5;// 点在右边缘
	private int MODE = CENTER;
	private int Color;

	public BuildOfSchool(String title, double latitude, double longitude,
			double width, double height, int mODE, int color) {
		super();
		this.title = title;
		this.position = new LatLng(latitude, longitude);
		this.width = width;
		this.height = height;
		MODE = mODE;
		Color = color;
	}

	public int getColor() {
		return Color;
	}

	public void setColor(int color) {
		Color = color;
	}

	public BuildOfSchool(String name, double latitude, double longitude) {
		super();
		this.title = name;
		this.position = new LatLng(latitude, longitude);
	}

	public String getTitle() {
		return title;
	}

	public LatLng getPosition() {
		return position;
	}

	public void drawBuilds(AMap map, int color) {
		switch (MODE) {
		case CENTER:
			map.addPolygon(new PolygonOptions()
					.addAll(createRectangle(position, height / 2, height / 2,
							width / 2, width / 2)).fillColor(color)
					.strokeWidth(1));
			return;
		case EDGE_UP:
			map.addPolygon(new PolygonOptions()
					.addAll(createRectangle(position, 0, height, width / 2,
							width / 2)).fillColor(color).strokeWidth(1));
			return;
		case EDGE_DOWN:
		case EDGE_LEFT:
			map.addPolygon(new PolygonOptions()
					.addAll(createRectangle(position, height / 2, height / 2,
							0, width)).fillColor(color).strokeWidth(1));
			return;
		case EDGE_RIGHT:
			map.addPolygon(new PolygonOptions()
					.addAll(createRectangle(position, height / 2, height / 2,
							width, 0)).fillColor(color).strokeWidth(1));
			return;
		}

	}

	public Marker addMarkerToMap(AMap map, int icon) {
		new BitmapDescriptorFactory();
		return map.addMarker(new MarkerOptions().position(position).title(title)
				.icon(BitmapDescriptorFactory.fromResource(icon)));
		
	}

	private List<LatLng> createRectangle(LatLng center, double up, double down,
			double left, double right) {
		return Arrays.asList(new LatLng(center.latitude + up, center.longitude
				- left), new LatLng(center.latitude + up, center.longitude
				+ right), new LatLng(center.latitude - down, center.longitude
				+ right), new LatLng(center.latitude - down, center.longitude
				- left));
	}
}
