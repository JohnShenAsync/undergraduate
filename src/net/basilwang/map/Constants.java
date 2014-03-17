package net.basilwang.map;

import android.graphics.Color;
import net.basilwang.R;

public class Constants {

	/**
	 * 明水Marker图标
	 * */
	public static final int[] icons_mingshui = { R.drawable.marker_classroom,
			R.drawable.marker_classroom, R.drawable.marker_classroom,
			R.drawable.marker_classroom, R.drawable.marker_lib,
			R.drawable.marker_classroom, R.drawable.marker_classroom,
			R.drawable.marker_gym, R.drawable.marker_tennis,
			R.drawable.marker_basketball, R.drawable.marker_playground,
			R.drawable.marker_volleyball, R.drawable.marker_football,
			R.drawable.marker_basketball, R.drawable.marker_dormitory,
			R.drawable.marker_dormitory, R.drawable.marker_dormitory,
			R.drawable.marker_dormitory, R.drawable.marker_dormitory,
			R.drawable.marker_dormitory, R.drawable.marker_dormitory,
			R.drawable.marker_dormitory, R.drawable.marker_dormitory,
			R.drawable.marker_dormitory, R.drawable.marker_dormitory,
			R.drawable.marker_bathhouse, R.drawable.marker_water,
			R.drawable.marker_market, R.drawable.marker_mess,
			R.drawable.marker_mess, R.drawable.marker_mess,
			R.drawable.marker_bathhouse, R.drawable.marker_market,
			R.drawable.marker_gate, R.drawable.red_point,
			R.drawable.red_point, 0, R.drawable.marker_dormitory,
			R.drawable.marker_dormitory, R.drawable.marker_dormitory,
			R.drawable.marker_dormitory, R.drawable.marker_dormitory,
			R.drawable.marker_dormitory, R.drawable.marker_infirmary,
			R.drawable.marker_lib };
	/**
	 * 圣井Marker图标
	 * */
	public static final int[] icons_shengjing = { R.drawable.marker_dormitory,
			R.drawable.marker_dormitory, R.drawable.marker_dormitory,
			R.drawable.marker_dormitory, R.drawable.marker_dormitory,
			R.drawable.marker_classroom, R.drawable.marker_classroom,
			R.drawable.marker_classroom, R.drawable.marker_classroom,
			R.drawable.marker_classroom, R.drawable.marker_basketball,
			R.drawable.marker_volleyball, R.drawable.marker_tennis,
			R.drawable.marker_playground, R.drawable.marker_bathhouse,
			R.drawable.marker_market, R.drawable.marker_mess,
			R.drawable.marker_mess, R.drawable.marker_infirmary };
	/**
	 * 燕山Marker图标
	 * */
	public static final int[] icons_yanshan = { R.drawable.marker_classroom,
			R.drawable.marker_classroom, R.drawable.marker_classroom,
			R.drawable.marker_classroom, R.drawable.marker_classroom,
			R.drawable.marker_s_classroom, R.drawable.marker_classroom,
			R.drawable.marker_dormitory, R.drawable.marker_dormitory,
			R.drawable.marker_dormitory, R.drawable.marker_dormitory,
			R.drawable.marker_dormitory, R.drawable.marker_dormitory,
			R.drawable.marker_dormitory, R.drawable.marker_dormitory,
			R.drawable.marker_dormitory, R.drawable.marker_dormitory,
			R.drawable.marker_dormitory, R.drawable.marker_mess,
			R.drawable.marker_water, R.drawable.marker_mess,
			R.drawable.marker_water, R.drawable.marker_mess,
			R.drawable.marker_bathhouse, R.drawable.marker_office_schoolcard,
			R.drawable.marker_infirmary, R.drawable.marker_basketball,
			R.drawable.marker_playground, R.drawable.marker_market,
			R.drawable.marker_lib, R.drawable.marker_gate,
			R.drawable.marker_gate, R.drawable.marker_market_vagetable };
	/**
	 * 舜耕Marker图标
	 * */
	public static final int[] icons_shungeng = { R.drawable.marker_classroom,
			R.drawable.marker_classroom, R.drawable.marker_classroom,
			R.drawable.marker_classroom, R.drawable.marker_classroom,
			R.drawable.marker_classroom, R.drawable.marker_classroom,
			R.drawable.marker_classroom, R.drawable.marker_classroom,
			R.drawable.marker_classroom, R.drawable.marker_dormitory,
			R.drawable.marker_dormitory, R.drawable.marker_dormitory,
			R.drawable.marker_dormitory, R.drawable.marker_dormitory,
			R.drawable.marker_dormitory, R.drawable.marker_dormitory,
			R.drawable.marker_dormitory, R.drawable.marker_dormitory,
			R.drawable.marker_dormitory, R.drawable.marker_dormitory,
			R.drawable.marker_dormitory, R.drawable.marker_dormitory,
			R.drawable.marker_dormitory, R.drawable.marker_dormitory,
			R.drawable.marker_dormitory, R.drawable.marker_dormitory,
			R.drawable.marker_dormitory, R.drawable.marker_dormitory,
			R.drawable.marker_infirmary, R.drawable.marker_dormitory,
			R.drawable.marker_mess, R.drawable.marker_mess,
			R.drawable.marker_gym, R.drawable.marker_playground,
			R.drawable.marker_basketball, R.drawable.marker_tennis,
			R.drawable.marker_volleyball, R.drawable.marker_swim,
			R.drawable.marker_dynamic, R.drawable.marker_playground,
			R.drawable.marker_lib, R.drawable.marker_hall,
			R.drawable.marker_book, R.drawable.marker_international_exchange,
			R.drawable.marker_classroom };

	public static final int MapId_Mingshui = 2;
	public static final int MapId_Shengjing = 3;
	public static final int MapId_Yanshan = 4;
	public static final int MapId_Shungeng = 5;

	public static final int color_road = Color.argb(180, 0, 191, 255);
	// 建筑物颜色：0教学楼,1运动场,2宿舍,3服务
	public static final int[] color_build = { Color.argb(180, 100, 200, 255),
			Color.argb(180, 0, 200, 0), Color.argb(180, 255, 0, 0),
			Color.argb(180, 255, 255, 0) };

	public static final int POISEARCH = 1000;

	public static final int ERROR = 1001;
	public static final int FIRST_LOCATION = 1002;

	public static final int LOCATION = 1003;

	public static final int ROUTE_START_SEARCH = 2000;
	public static final int ROUTE_END_SEARCH = 2001;
	public static final int ROUTE_SEARCH_RESULT = 2002;
	public static final int ROUTE_SEARCH_ERROR = 2004;

	public static final int REOCODER_RESULT = 3000;
	public static final int DIALOG_LAYER = 4000;
	public static final int POISEARCH_NEXT = 5000;

	public static final int BUSLINE_RESULT = 6000;
	public static final int BUSLINE_DETAIL_RESULT = 6001;
	public static final int BUSLINE_ERROR_RESULT = 6002;
}
