package net.basilwang.map;

import net.basilwang.R;
import net.basilwang.dao.Preferences;
import net.basilwang.fresh.GetMapPoints;
import net.basilwang.fresh.PointOfSegmentService;
import net.basilwang.fresh.PointOfStructureService;
import net.basilwang.listener.ShowTipListener;
import net.basilwang.utils.NetworkUtils;
import net.basilwang.utils.PreferenceUtils;
import net.basilwang.utils.TipUtils;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.InfoWindowAdapter;
import com.amap.api.maps.AMap.OnCameraChangeListener;
import com.amap.api.maps.AMap.OnInfoWindowClickListener;
import com.amap.api.maps.AMap.OnMapClickListener;
import com.amap.api.maps.AMap.OnMapLoadedListener;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.RouteSearch.OnRouteSearchListener;
import com.amap.api.services.route.RouteSearch.WalkRouteQuery;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkRouteResult;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

public class SchoolMapFragment extends Fragment implements
		OnMarkerClickListener, OnMapClickListener, OnInfoWindowClickListener,
		InfoWindowAdapter, OnMapLoadedListener, OnGestureListener,
		AMapLocationListener, LocationSource, ShowTipListener,
		OnCameraChangeListener, OnRouteSearchListener {

	private View schoolView;
	private MapView mMapView;
	private AMap mMap;// 高德地图
	private OnLocationChangedListener mListener;// 定位
	private LocationManagerProxy mAMapLocationManager;
	private AMapLocation mLocation;
	private UiSettings uiSettings;// 地图用户界面控制
	private GestureDetector detector;// 多任务手势

	private ImageButton routeSearchImagebtn;
	private AutoCompleteTextView startTextView;
	private AutoCompleteTextView endTextView;

	private int walkMode = RouteSearch.WalkDefault;// 步行默认模式
	private WalkRouteResult walkRouteResult;// 步行模式查询结果
	private RouteSearch routeSearch;
	private ProgressDialog progDialog;

	private String strStart;// 起点title
	private String strEnd;// 终点title
	private LatLonPoint startPoint = null;// 起点
	private LatLonPoint endPoint = null;// 终点
	private boolean isClickStart = true;
	private boolean isClickTarget = false;
	private float zoom_current;// 当前地图的缩放级别
	GetMapPoints mapPoints;// 读取数据库中校区地图的数据
	private ViewFlipper flipper;// 实现滑动切换校区
	int flag = 0;// viewFlipper的标识
	// 四个校区的位置:明水 圣井 燕山 舜耕
	private LatLng[] position_schoolLatLngs = {
			new LatLng(36.657536, 117.506961),
			new LatLng(36.670541, 117.374297),
			new LatLng(36.644941, 117.074136),
			new LatLng(36.625672, 117.024036) };

	// private SearchRouteResult getRoute;
	private GetMapPoints getMapPoints;
	boolean isDestroy = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		showTipIfNecessary();
		schoolView = inflater.inflate(R.layout.amap_main, container, false);
		mMapView = (MapView) schoolView.findViewById(R.id.map);
		mMapView.onCreate(savedInstanceState);
		initListener();
		init();
		return schoolView;
	}

	/**
	 * 绑定监听事件
	 * */
	private void initListener() {
		// 绑定多手势任务
		detector = new GestureDetector(getActivity(), this);
		flipper = (ViewFlipper) schoolView.findViewById(R.id.flipper);
		((SlidingFragmentActivity) getActivity()).getSlidingMenu()
				.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		// 绑定ViewFlipper的onTouchListener
		flipper.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return detector.onTouchEvent(event);
			}
		});

	}

	@Override
	public void onDestroyView() {
		deactivate();
		mMap = null;
		((SlidingFragmentActivity) getActivity()).getSlidingMenu()
				.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		super.onDestroyView();
	}

	/**
	 * 初始化AMap对象
	 */
	private void init() {
		// 获得地图
		if (mMap == null) {
			mMap = mMapView.getMap();
			if (checkReady(this.getActivity(), mMap)) {
				setUpMap();
			}
		}
		initViewFlipper();
	}

	/**
	 * 设置地图
	 * */
	private void setUpMap() {
		mMap.setOnMarkerClickListener(this);// 设置点击marker事件监听器
		mMap.setInfoWindowAdapter(this);
		mMap.setOnInfoWindowClickListener(this);// 设置点击infoWindow事件监听器
		mMap.setOnMapLoadedListener(this);// 设置map载入成功事件监听器
		mMap.setOnCameraChangeListener(this);
		routeSearch = new RouteSearch(getActivity());
		routeSearch.setRouteSearchListener(this);
		setLocation();
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
				position_schoolLatLngs[0], 17.904732f));
		startTextView = (AutoCompleteTextView) schoolView
				.findViewById(R.id.autotextview_roadsearch_start);
		startTextView.setFocusable(false);
		endTextView = (AutoCompleteTextView) schoolView
				.findViewById(R.id.autotextview_roadsearch_goals);
		endTextView.setFocusable(false);
		routeSearchImagebtn = (ImageButton) schoolView
				.findViewById(R.id.imagebtn_roadsearch_search);
		routeSearchImagebtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				strStart = startTextView.getText().toString().trim();
				strEnd = endTextView.getText().toString().trim();
				if (strStart == null || strStart.length() == 0) {
					Toast.makeText(getActivity(), "请选择起点", Toast.LENGTH_SHORT)
							.show();
					return;
				}
				if (strEnd == null || strEnd.length() == 0) {
					Toast.makeText(getActivity(), "请选择终点", Toast.LENGTH_SHORT)
							.show();
					return;
				}
				if (startPoint != null && endPoint != null) {
					if (NetworkUtils.isConnect(getActivity())) {
						progDialog = ProgressDialog.show(getActivity(), null,
								"正在搜索", true, true);
						searchRoute();
					} else {
						Toast.makeText(getActivity(), "亲，请检查网络连接",
								Toast.LENGTH_SHORT).show();
					}

					// searchRouteResult(startPoint, endPoint);
				}
			}
		});
	}

	/**
	 * 设置定位相关
	 * */
	private void setLocation() {
		// 自定义系统定位小蓝点
		MyLocationStyle myLocationStyle = new MyLocationStyle();
		myLocationStyle.myLocationIcon(BitmapDescriptorFactory
				.fromResource(R.drawable.location_marker));
		myLocationStyle.strokeColor(Color.BLACK);
		myLocationStyle.radiusFillColor(Color.alpha(0));
		myLocationStyle.strokeWidth(1);
		mMap.setMyLocationStyle(myLocationStyle);
		mAMapLocationManager = LocationManagerProxy.getInstance(getActivity());
		mMap.setLocationSource(this);
	}

	/**
	 * 初始化ViewFlipper
	 * */
	private void initViewFlipper() {
		// 添加四个校区的view
		flipper.addView(addTextView("山东财经大学明水校区", Constants.MapId_Mingshui),
				convertMapIdToFlag(Constants.MapId_Mingshui));
		flipper.addView(addTextView("山东财经大学圣井校区", Constants.MapId_Shengjing),
				convertMapIdToFlag(Constants.MapId_Shengjing));
		flipper.addView(addTextView("山东财经大学燕山校区", Constants.MapId_Yanshan),
				convertMapIdToFlag(Constants.MapId_Yanshan));
		flipper.addView(addTextView("山东财经大学舜耕校区", Constants.MapId_Shungeng),
				convertMapIdToFlag(Constants.MapId_Shungeng));
	}

	/**
	 * AMap对象判断是否为null
	 */
	public static boolean checkReady(Context context, AMap aMap) {
		if (aMap == null) {
			Toast.makeText(context, R.string.map_not_ready, Toast.LENGTH_LONG)
					.show();
			return false;
		}
		return true;
	}

	/**
	 * 添加ViewFlipper的View
	 * */
	private View addTextView(String name, final int Id) {
		LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
		LinearLayout resultView = (LinearLayout) layoutInflater.inflate(
				R.layout.amap_viewflipper_context, null);
		((TextView) resultView.findViewById(R.id.tv_viewflipper_context))
				.setText(name);
		return resultView;
	}

	/**
	 * 设置点击Marker的InfoWindow事件
	 * */
	@Override
	public void onInfoWindowClick(Marker arg0) {
		if (isClickStart) {
			startTextView.setText(arg0.getTitle());
			startPoint = convertToLatLonPoint(arg0.getPosition());
			isClickStart = false;
			isClickTarget = true;

		} else if (isClickTarget) {
			endTextView.setText(arg0.getTitle());
			endPoint = convertToLatLonPoint(arg0.getPosition());
			isClickStart = true;
			isClickTarget = false;

		}
		arg0.hideInfoWindow();
	}

	/**
	 * 搜索路线
	 * */
	// public void searchRouteResult(LatLonPoint startPoint, LatLonPoint
	// endPoint) {
	// getRoute = new SearchRouteResult();
	// if (NetworkUtils.isConnect(getActivity())) {
	// progDialog = ProgressDialog.show(getActivity(), null, "正在搜索", true,
	// true);
	// getRoute.execute(getActivity(), mode, startPoint, endPoint, mMap,
	// getMapPoints, isClickStart, isClickTarget, progDialog);
	// } else {
	// Toast.makeText(getActivity(), "亲，请检查网络连接", Toast.LENGTH_SHORT)
	// .show();
	// }
	// }

	public void showToast(String showString) {
		Toast.makeText(getActivity().getApplicationContext(), showString,
				Toast.LENGTH_SHORT).show();
	}

	@Override
	public boolean onMarkerClick(Marker arg0) {
		if (isClickStart) {
			arg0.setSnippet("点击此处选择为起点");
		} else if (isClickTarget) {
			arg0.setSnippet("点击此处选择为终点");
		}
		arg0.showInfoWindow();
		return false;
	}

	/**
	 * 把LatLng对象转化为LatLonPoint对象
	 */
	public static LatLonPoint convertToLatLonPoint(LatLng latlon) {
		return new LatLonPoint(latlon.latitude, latlon.longitude);
	}

	@Override
	public void onResume() {
		super.onResume();
		setUpMapIfNeeded();
	}

	@Override
	public void onPause() {
		super.onPause();
		deactivate();
	}

	private void setUpMapIfNeeded() {
		if (mMap == null) {
			mMap = mMapView.getMap();
			if (mMap != null) {
				setUpMap();
			}
		}
	}

	/**
	 * 一定要return true，否则onFling方法不生效
	 * */
	@Override
	public boolean onDown(MotionEvent e) {
		return true;
	}

	/**
	 * 单击下方ViewFlipper回到校区中心点
	 * */
	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		mMap.clear();
		startTextView.setText(null);
		endTextView.setText(null);
		mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
				position_schoolLatLngs[flag], 17));
		isClickStart = true;
		isClickTarget = false;
		loadMap(flag + 2, true);
		return true;
	}

	/**
	 * 实现左右滑动切换校区
	 * */
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		float zoom = 17;
		if (e1.getX() - e2.getX() > 100) {
			if (flag < position_schoolLatLngs.length - 1) {
				flag++;
			} else {
				flag = 0;
			}
			this.flipper.setInAnimation(AnimationUtils.loadAnimation(
					this.getActivity(), R.anim.push_left_in));
			this.flipper.setOutAnimation(AnimationUtils.loadAnimation(
					this.getActivity(), R.anim.push_left_out));
		} else if (e1.getX() - e2.getX() < -100) {
			if (flag > 0) {
				flag--;
			} else {
				flag = position_schoolLatLngs.length - 1;
			}
			this.flipper.setInAnimation(AnimationUtils.loadAnimation(
					this.getActivity(), R.anim.push_right_in));
			this.flipper.setOutAnimation(AnimationUtils.loadAnimation(
					this.getActivity(), R.anim.push_right_out));
		}
		mMap.clear();
		loadMap(flag + 2, true);
		mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
				position_schoolLatLngs[flag], zoom));
		this.flipper.setDisplayedChild(flag);

		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return true;
	}

	@Override
	public void onShowPress(MotionEvent e) {

	}

	@Override
	public void onMapClick(LatLng arg0) {

	}

	@Override
	public View getInfoContents(Marker arg0) {
		return null;
	}

	@Override
	public View getInfoWindow(Marker arg0) {
		return null;
	}

	public void loadMap(int MapId, boolean isDrawRoute) {
		// 读取数据库中校区地图的数据，并进行绘制
		getMapPoints = new GetMapPoints();
		getMapPoints.execute(new PointOfSegmentService(this.getActivity()),
				new PointOfStructureService(this.getActivity()), mMap, MapId,
				isDrawRoute);
	}

	@Override
	public void onMapLoaded() {
		uiSettings = mMap.getUiSettings();
		uiSettings.setZoomControlsEnabled(false);
		uiSettings.setMyLocationButtonEnabled(true);
		loadMap(Constants.MapId_Mingshui, true);
	}

	/**
	 * 此方法已经废弃
	 */
	@Override
	public void onLocationChanged(Location location) {
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	/**
	 * 定位成功后回调函数
	 */
	@Override
	public void onLocationChanged(AMapLocation aLocation) {
		if (mListener != null) {
			zoom_current = mMap.getCameraPosition().zoom;
			mListener.onLocationChanged(aLocation);
			mLocation = aLocation;
			judge_position();
			deactivate();
		}
	}

	/**
	 * 判断当前位置位于哪个校区
	 * */
	private void judge_position() {
		if (mLocation.getLatitude() < 36.648152
				&& mLocation.getLongitude() > 117.068195
				&& mLocation.getLatitude() > 36.642666
				&& mLocation.getLongitude() < 117.080217) {

			flag = 2;// 燕山校区
		} else if (mLocation.getLatitude() < 36.66187
				&& mLocation.getLongitude() > 117.501132
				&& mLocation.getLatitude() > 36.651857
				&& mLocation.getLongitude() < 117.520319) {
			flag = 0;// 明水校区
		} else if (mLocation.getLatitude() < 36.674867
				&& mLocation.getLongitude() > 117.370944
				&& mLocation.getLatitude() > 36.665148
				&& mLocation.getLongitude() < 117.380147) {
			flag = 1;// 圣井校区
		} else if (mLocation.getLatitude() < 36.631209
				&& mLocation.getLongitude() > 117.019616
				&& mLocation.getLatitude() > 36.619802
				&& mLocation.getLongitude() < 117.026629) {
			flag = 3;// 舜耕校区
		}
		mMap.clear();
		this.flipper.setDisplayedChild(flag);
		mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
				position_schoolLatLngs[flag], zoom_current));
		startTextView.setText(null);
		endTextView.setText(null);
		isClickStart = true;
		isClickTarget = false;
		loadMap(flag + 2, true);
	}

	/**
	 * 激活定位
	 */
	@Override
	public void activate(OnLocationChangedListener listener) {
		mListener = listener;
		if (mAMapLocationManager == null) {
			mAMapLocationManager = LocationManagerProxy
					.getInstance(getActivity());
		}
		/*
		 * mAMapLocManager.setGpsEnable(false);//
		 * 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true
		 */
		// Location API定位采用GPS和网络混合定位方式，时间最短是5000毫秒
		mAMapLocationManager.requestLocationUpdates(
				LocationProviderProxy.AMapNetwork, 5000, 3, this);
		Toast.makeText(getActivity(), "正在定位...", Toast.LENGTH_SHORT).show();

	}

	/**
	 * 停止定位
	 */
	@Override
	public void deactivate() {
		mListener = null;
		if (mAMapLocationManager != null) {
			mAMapLocationManager.removeUpdates(this);
			mAMapLocationManager.destory();
		}
		mAMapLocationManager = null;
	}

	@Override
	public void showTipIfNecessary() {
		int curriculumTip = PreferenceManager.getDefaultSharedPreferences(
				this.getActivity()).getInt(Preferences.SCHOOLMAP_TIP_SHOW, 0);
		if (curriculumTip == 0) {
			TipUtils.showTipIfNecessary(this.getActivity(),
					R.drawable.schoolmap_tip, this);
		}

	}

	@Override
	public void dismissTip() {
		PreferenceUtils.modifyIntValueInPreferences(getActivity(),
				Preferences.SCHOOLMAP_TIP_SHOW, 1);
	}

	// 将MapIdea转换为当前地图标识
	public int convertMapIdToFlag(int id) {
		int mflag = id - Constants.MapId_Mingshui;
		return mflag;
	}

	@Override
	public void onCameraChange(CameraPosition arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCameraChangeFinish(CameraPosition arg0) {
		zoom_current = mMap.getCameraPosition().zoom;
		Log.i("zoom", "" + zoom_current);
		if (zoom_current < 17 && getMapPoints != null && !isDestroy) {
			changeMarkerToRedPoint(getMapPoints);
			isDestroy = true;
		} else if (zoom_current >= 17 && isDestroy) {
			getMapPoints.addMarkers();
			isDestroy = false;
		}
		Log.i("isDestroy", "" + isDestroy);

	}

	private void changeMarkerToRedPoint(GetMapPoints points) {
		for (int i = 0; i < points.getMarkers().size(); i++) {
			points.getMarkers()
					.get(i)
					.setIcon(
							BitmapDescriptorFactory
									.fromResource(R.drawable.red_point));
		}
	}

	@Override
	public void onBusRouteSearched(BusRouteResult arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDriveRouteSearched(DriveRouteResult arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onWalkRouteSearched(WalkRouteResult result, int rCode) {
		progDialog.dismiss();
		if (rCode == 0) {
			if (result != null && result.getPaths() != null
					&& result.getPaths().size() > 0) {
				walkRouteResult = result;
				WalkPath walkPath = walkRouteResult.getPaths().get(0);
				mMap.clear();// 清理地图上的所有覆盖物
				MyWalkRouteOverlay walkRouteOverlay = new MyWalkRouteOverlay(
						getActivity(), mMap, walkPath,
						walkRouteResult.getStartPos(),
						walkRouteResult.getTargetPos(), startTextView.getText()
								.toString(), endTextView.getText().toString());
				walkRouteOverlay.removeFromMap();
				walkRouteOverlay.addToMap();
				walkRouteOverlay.zoomToSpan();
				loadMap(flag + 2, false);
			}
		}

	}

	/**
	 * 点击搜索按钮开始Route搜索
	 */
	public void searchRoute() {
		strStart = startTextView.getText().toString().trim();
		strEnd = endTextView.getText().toString().trim();
		if (strStart == null || strStart.length() == 0) {
			Toast.makeText(getActivity(), "请选择起点", Toast.LENGTH_SHORT).show();
			return;
		}
		if (strEnd == null || strEnd.length() == 0) {
			Toast.makeText(getActivity(), "请选择终点", Toast.LENGTH_SHORT).show();
			return;
		}
		searchRouteResult(startPoint, endPoint);
	}

	/**
	 * 开始搜索路径规划方案
	 */
	public void searchRouteResult(LatLonPoint startPoint, LatLonPoint endPoint) {
		final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
				startPoint, endPoint);// 步行路径规划
		WalkRouteQuery query = new WalkRouteQuery(fromAndTo, walkMode);
		routeSearch.calculateWalkRouteAsyn(query);// 异步路径规划步行模式查询

	}
}
