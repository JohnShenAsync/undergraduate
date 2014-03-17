package net.basilwang.dao;

import static net.basilwang.dao.Preferences.CEMESTER_INDEX_PREFERENCES;
import static net.basilwang.dao.Preferences.LOGON_ACCOUNT_ID;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.basilwang.R;
import net.basilwang.config.SAXParse;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CurriculumAdapter extends BaseAdapter {
	private List<Map<String, Object>> list;
	private Context mContext;
	private CurriculumService curriculumService;
	int dayWidth;
	int dayHeight;
	private String[] weekdays;
	private Resources res;

	public CurriculumAdapter(int weekNumber, int day, Context context) {
		this.curriculumService = new CurriculumService(context);
		int accountId = PreferenceManager.getDefaultSharedPreferences(context)
				.getInt(LOGON_ACCOUNT_ID, 0);
		// 2012-07-10 basilwang filter by cemester value ,don't need replace
		// dash here
		String semesterValue = PreferenceManager.getDefaultSharedPreferences(
				context).getString(CEMESTER_INDEX_PREFERENCES, "");
		this.list = this.curriculumService.getCurriculumByDay(semesterValue,
				day, accountId);
		filterCurriculmList(weekNumber, this.list);
		weekdays = ((Activity) context).getResources().getStringArray(
				R.array.weekdays);
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("name", weekdays[day - 1]);
		map.put("timespan", "1");
		map.put("severity", "-1");
		map.put("id", "-1");
		this.list.add(0, map);

		this.mContext = context;
		DisplayMetrics metric = new DisplayMetrics();
		((Activity) this.mContext).getWindowManager().getDefaultDisplay()
				.getMetrics(metric);
		int width = metric.widthPixels; // 屏幕宽度（像素）
		// int densityDpi = metric.densityDpi; // 屏幕密度DPI（120 / 160 / 240）
		dayWidth = width / 7;

		// int height=metric.heightPixels-140;
		// dayHeight=height/11;

		res = ((Activity) this.mContext).getResources();

	}

	private void filterCurriculmList(int weekNumber,
			List<Map<String, Object>> list) {
		for (Map<String, Object> c : list) {

			String period = (c.get("cemesterperiod") == null ? "" : c.get(
					"cemesterperiod").toString());
			Pattern periodPattern = Pattern.compile("第(\\d*)-(\\d*)周");
			Matcher periodMatcher = periodPattern.matcher(period);
			// 2012-06-08 basilwang maybe not found if no class
			if (periodMatcher.find()) {
				int periodStart = Integer.valueOf(periodMatcher.group(1));
				int periodEnd = Integer.valueOf(periodMatcher.group(2));
				int intervalType = Integer.valueOf(c.get("intervaltype")
						.toString());
				String sInterval = "";
				if (intervalType == 1)
					sInterval = "单周";
				else if (intervalType == 2)
					sInterval = "双周";
				if (weekNumber >= periodStart
						&& weekNumber <= periodEnd
						&& (intervalType == 0 || intervalType == (weekNumber % 2 == 0 ? 2
								: 1))

				) {

				} else {
					// 2012-06-08 basilwang we can't remove it
					c.put("name", "");
				}
			}

		}
	}

	@Override
	public int getCount() {
		// 指定一共包含9个选项
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// 返回指定位置的文本
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return Long.parseLong(list.get(position).get("id").toString());
	}

	// 重写该方法，该方法返回的View将作为的GridView的每个格子
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		int classCounts = SAXParse.getTAConfiguration().getSelectedCollege()
				.getCurriculumConfig().getClassindexs().size();

		dayHeight = parent.getMeasuredHeight() / classCounts - 1;
		TextView text = new TextView(mContext);
		// 使用尺度资源来设置文本框的高度、宽度
		text.setWidth(dayWidth);
		// text.setPadding(0, 0, 0, 1);
		// text.setBackgroundResource(R.drawable.cell_pixel_border);
		int timespan = Integer.valueOf(list.get(position).get("timespan")
				.toString());
		// 使用字符串资源设置文本框的内容
		text.setText(list.get(position).get("name").toString());
		// TypedArray icons = res.obtainTypedArray(R.array.colors);
		// 使用颜色资源来设置文本框的 背景色
		text.setTextColor(0xff000000);
		int severity = Integer.valueOf(list.get(position).get("severity")
				.toString());
		switch (severity) {
		case -1:
			text.setBackgroundColor(0xffffffff);
			break;
		case 0:
			text.setBackgroundColor(0xff00ff00);
			break;
		case 1:
			text.setBackgroundColor(0xff7beaff);
			break;
		case 2:
			text.setBackgroundColor(0xff828283);
			break;
		}
		// text.setBackgroundDrawable(icons.getDrawable(position));
		// 2012-04-20 basilwang add extra height for timespan equals 2
		if (timespan == 2) {
			text.setHeight(dayHeight * timespan + 1);
		} else if (timespan == 3) {
			text.setHeight(dayHeight * timespan + 2);
			
		} else {
			text.setHeight(dayHeight * timespan);
		}
		text.setTextSize(11);
		return text;
	}
}
