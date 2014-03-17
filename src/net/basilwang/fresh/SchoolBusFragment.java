package net.basilwang.fresh;

import net.basilwang.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

public class SchoolBusFragment extends Fragment {

	private View mainView;
	private WebView webView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mainView = inflater.inflate(R.layout.question_web_view, null);
		webView = (WebView) mainView.findViewById(R.id.webView1);
		createHtml();
		return mainView;
	}

	private void createHtml() {
		StringBuilder sb = new StringBuilder();// 创建一个字符串构建器，将要显示的HTML内容放置在该构建器中
		sb.append("<table border='1dp'>");
		sb.append("<tr>");
		sb.append("<th>运行方式</th>");
		sb.append("<th>运行路线（起止时间）</th>");
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append("<td rowspan='2'>点对点(周一至周五)</td>");
		sb.append("<td>舜耕校区(7:20)→圣井校区<br>圣井校区(12:20、17:40、21:20)→舜耕校区</td>");
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append("<td>燕山校区(7:20)→明水校区<br>明水校区(12:00、17:00)→燕山校区(18：00)→舜耕校区</td>");
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append("<td rowspan='2'>点对点(周六周日)</td>");
		sb.append("<td>舜耕校区(7:20、12:50)→圣井校区<br>圣井校区(12:20、17:40)→舜耕校区</td>");
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append("<td>周六：<br>燕山校区(7:20、13:00)→明水校区<br>明水校区(12:00、17:40)→燕山校区<br>周日：<br>燕山校区(7:20、12:30)→明水校区<br>明水校区(12:00、17:00)→燕山校区</td>");
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append("<td>小循环<br>(周一至周五)</td>");
		sb.append("<td>燕山校区←→舜耕校区 两校区对发<br>7:50、9:50、13:30、15:10、17:30<br>舜耕校区(6:50)→燕山校区</td>");
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append("<td rowspan='2'>大循环<br>(周一至周五)</td>");
		sb.append("<td>舜耕校区(8:45)→燕山校区(9:00)→圣井校区(9:40)→明水校区(10:00)<br>明水校区(10:50)→圣井校区(11:10)→燕山校区(12:00)→舜耕校区(12:25)</td>");
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append("<td>舜耕校区(12:00/12:45) → 燕山校区(12:15/13:00)→圣井校区(12:50/13:40)→明水校区(13:15/14:00)；<br>明水校区(15:10)→圣井校区(15:40)→燕山校区(16:20)→舜耕校区(16:45)</td>");
		sb.append("</tr>");
		sb.append("</table>");
		webView.loadDataWithBaseURL(null, sb.toString(), "text/html", "utf-8",
				null);// 加载数据

	}

}
