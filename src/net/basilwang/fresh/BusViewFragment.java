package net.basilwang.fresh;

import net.basilwang.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

public class BusViewFragment extends Fragment {

	private View mainView;
	private WebView webView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		mainView = inflater.inflate(R.layout.question_web_view, null);
		webView = (WebView) mainView.findViewById(R.id.webView1);
		initWebView();
		return mainView;
	}

	private void initWebView() {
		StringBuilder sb = new StringBuilder();// 创建一个字符串构建器，将要显示的HTML内容放置在该构建器中
		sb.append("<table border='1dp'>");
		sb.append("<tr>");
		sb.append("<th>车站</th>");
		sb.append("<th>换乘路线</th>");
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append("<td rowspan='4'>济南站</td>");
		sb.append("<td><font color='#C71585'>明水校区：</font>可乘公交车到燕山校区，再换乘校车前往。或在火车站广场东200米车站东街的【火车站】乘K51到【千佛山】下，原地换乘K301路到【章丘中学】下，马路对面换乘【章丘3路】到【经济学院】下</td>");
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append("<td><font color='#C71585'>圣井校区：</font>可乘公交车到燕山校区，再换乘校车前往。或在火车站广场东200米车站东街的【火车站】乘K51到【千佛山】下，原地换乘K301路到【山东财政学院东校】下</td>");
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append("<td><font color='#C71585'>燕山校区：</font>步行到【站前路南口】乘49路，或【天桥南】站乘K50路到【经济学院】下</td>");
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append("<td><font color='#C71585'>舜耕校区：</font>在出站口西南200米的【火车站】乘34路、43路到【舜玉小区】下</td>");
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append("<td rowspan='4'>济南东站</td>");
		sb.append("<td><font color='#C71585'>明水校区：</font>可乘公交车到燕山校区，再换乘校车前往。或在历黄路路西的【济南东站】乘31路到【山大路南口】下，原地换乘K301路到【章丘中学】下，马路对面换乘章丘3路到【经济学院】下</td>");
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append("<td><font color='#C71585'>圣井校区：</font>可乘公交车到燕山校区，再换乘校车前往。或在历黄路路西的【济南东站】乘31路到【山大路南口】下，原地换乘K301路到【山东财政学院东校】下</td>");
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append("<td><font color='#C71585'>燕山校区：</font>在站外路南的【济南东站】乘112路到【文化东路】下，原地换乘151路到【经济学院】下；或在历黄路路西的【济南东站】乘31路到【解放桥南】下，原地换乘151路到【经济学院】下</td>");
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append("<td><font color='#C71585'>舜耕校区：</font>在站外路北的【济南东站】乘33路到【河套庄】下，马路对面换乘66路到【舜玉小区】下</td>");
		sb.append("</tr>");
		sb.append("<td rowspan='4'>济南西站</td>");
		sb.append("<td><font color='#C71585'>明水校区：</font>乘K157路到【北小辛庄西街】下，步行到经十路路南站牌换乘K301路到【章丘中学】下，马路对面换乘【章丘3路】到【经济学院】下</td>");
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append("<td><font color='#C71585'>圣井校区：</font>可乘公交车到燕山校区，再换乘校车前往。或乘K157路到【北小辛庄西街】下，步行到经十路路南站牌换乘K301路到【山东财政学院东校】下</td>");
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append("<td><font color='#C71585'>燕山校区：</font>乘K157路到【大杨庄】下，原地换乘K56路到【经济学院】下</td>");
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append("<td><font color='#C71585'>舜耕校区：</font>乘K157路到【辛西路北口】下，到路口东侧路南儿童医院门口换乘42路到【舜玉小区】下</td>");
		sb.append("</tr>");
		sb.append("<td rowspan='4'>济南长途汽车总站</td>");
		sb.append("<td><font color='#C71585'>明水校区：</font>可乘公交车到燕山校区，再换乘校车前往。或在站外济泺路路西的【长途汽车站】乘K68路【千佛山】下，原地换乘K301路到【章丘中学】下，马路对面换乘【章丘3路】到【经济学院】下</td>");
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append("<td><font color='#C71585'>圣井校区：</font>可乘公交车到燕山校区，再换乘校车前往。或在站外济泺路路西的【长途汽车站】乘K68路【千佛山】下，原地换乘K301路到【山东财政学院东校】下</td>");
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append("<td><font color='#C71585'>燕山校区：</font>在站外济泺路路西的【长途汽车站】乘K50路到【经济学院】下</td>");
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append("<td><font color='#C71585'>舜耕校区：</font>在站外济泺路路西的【长途汽车站】乘K50路到【大观园】下，原地换乘43路到【舜玉小区】下</td>");
		sb.append("</tr>");
		sb.append("<td rowspan='4'>济南长途汽车东站</td>");
		sb.append("<td><font color='#C71585'>明水校区：</font>可乘公交车到燕山校区，再换乘校车前往。或在长途东站内换乘济章城际公交到【章丘客运站】下，站内换乘章丘3路到【经济学院】下。或在站外工业南路路南的【工业南路西口】乘116路到【田庄东】下，原地换乘K301路到【章丘中学】下，马路对面换乘【章丘3路】到【经济学院】下</td>");
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append("<td><font color='#C71585'>圣井校区：</font>可乘公交车到燕山校区，再换乘校车前往。或在站外工业南路路南的【工业南路西口】乘116路到【田庄东】下，原地换乘K301路到【山东财政学院东校】下</td>");
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append("<td><font color='#C71585'>燕山校区：</font>在站外二环东路路中央的BRT站台（走人行天桥进入）乘BRT-4号线（燕山立交方向）到【燕山立交桥】下，原地换乘49路到【经济学院】下(或向下走一站)</td>");
		sb.append("</tr>");
		sb.append("<tr>");
		sb.append("<td><font color='#C71585'>舜耕校区：</font>在站外工业南路路北的【工业南路西口】乘123路到【省杂技团】下，原地换乘48路到【舜玉小区】下；或在【工业南路西口】乘137路到【解放路】下，原地换乘48路到【舜玉小区】下</td>");
		sb.append("</tr>");
		sb.append("</table>");
		webView.loadDataWithBaseURL(null, sb.toString(), "text/html",
				"utf-8", null);// 加载数据

	}

}
