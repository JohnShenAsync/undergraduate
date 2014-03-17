package net.basilwang;

import static net.basilwang.dao.Preferences.CURRICULUM_DOWNLOAD_TIP;
import static net.basilwang.dao.Preferences.CURRICULUM_TIP_SHOW;
import static net.basilwang.dao.Preferences.SCHOOLMAP_TIP_SHOW;
import static net.basilwang.dao.Preferences.SCORE_TIP_SHOW;
import static net.basilwang.dao.Preferences.SOCRE_DOWNLOAD_TIP;
import static net.basilwang.dao.Preferences.WEEK_VIEW_TIP_SHOW;

import java.lang.reflect.Constructor;
import java.util.HashMap;

import net.basilwang.dao.AccountService;
import net.basilwang.dao.CurriculumService;
import net.basilwang.dao.IDAOService;
import net.basilwang.dao.Preferences;
import net.basilwang.dao.ScoreService;
import net.basilwang.dao.SemesterService;
import net.basilwang.fresh.NewbornRaiders;
import net.basilwang.utils.PreferenceUtils;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.sharesdk.framework.AbstractWeibo;
import cn.sharesdk.framework.WeiboActionListener;
import cn.sharesdk.onekeyshare.ShareAllGird;

public class SliderMenuFragment extends ListFragment implements Callback,
		OnClickListener, WeiboActionListener {

	public static final int EXIT_APPLICATION = 0x0001;
	private Handler handler;
	private ImageView img_icon_top;
	private View menuView;
	int messageNum = 0;
	int isUnusual = 0;
	private int[] messages = { 0, R.drawable.message1, R.drawable.message2,
			R.drawable.message3, R.drawable.message4, R.drawable.message5,
			R.drawable.message6 };

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		menuView = inflater.inflate(R.layout.menu_list, null);
		prepare();
		return menuView;
	}

	public void prepare() {
		img_icon_top = (ImageView) menuView.findViewById(R.id.img_icon_top);
		img_icon_top.setOnClickListener(this);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		adapter();
	}

	public void adapter() {
		String[] menuNames = { "课程表", "查成绩", "下课表", "校园攻略", "消息中心", "分  享",
				"设  置", "注  销" };
		SampleAdapter adapter = new SampleAdapter(this.getActivity());
		for (int i = 0; i < menuNames.length; i++) {
			adapter.add(new SampleItem(menuNames[i], getIconResc(i),
					getMessage_IconResc(i)));
			setListAdapter(adapter);
		}
	}

	// 异步获取信息结束后，刷新新消息的数量
	public void refresh(int i) {
		messageNum = i;
		adapter();
	}

	public void isUnusual(int i) {
		PreferenceUtils.modifyIntValueInPreferences(this.getActivity(),
				net.basilwang.dao.Preferences.Unusual, i);
		isUnusual = PreferenceUtils.getPreferenceUnusual(this.getActivity());
	}

	private int getIconResc(int position) {
		// int[] iconResc = { R.drawable.menu_curriculm,
		// R.drawable.menu_mygrade,
		// R.drawable.menu_downloadc, R.drawable.menu_strategy,
		// R.drawable.menu_message, R.drawable.menu_shared,
		// R.drawable.menu_set};
		int[] iconResc = { R.drawable.menu_curriculmn, R.drawable.menu_grade,
				R.drawable.menu_download, R.drawable.menu_newstudent,
				R.drawable.menu_messages, R.drawable.menu_share,
				R.drawable.menu_setting, R.drawable.menu_logoff };
		return iconResc[position];

	}

	private int getMessage_IconResc(int position) {
		int[] iconResc = { 0, 0, 0, 0, messages[messageNum], 0, 0, 0 };
		return iconResc[position];

	}

	private class SampleItem {
		public String tag;
		public int iconRes;
		public int messageRes;

		public SampleItem(String tag, int iconRes, int messageRes) {
			this.tag = tag;
			this.iconRes = iconRes;
			this.messageRes = messageRes;
		}
	}

	public class SampleAdapter extends ArrayAdapter<SampleItem> {

		public SampleAdapter(Context context) {
			super(context, 0);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(
						R.layout.row, null);
			}
			ImageView icon = (ImageView) convertView
					.findViewById(R.id.row_icon);
			icon.setImageResource(getItem(position).iconRes);
			TextView title = (TextView) convertView
					.findViewById(R.id.row_title);
			title.setText(getItem(position).tag);
			ImageView message_icon = (ImageView) convertView
					.findViewById(R.id.new_message_icon);
			message_icon.setImageResource(getItem(position).messageRes);

			return convertView;
		}

	}

	@Override
	public void onListItemClick(ListView lv, View v, int position, long id) {
		Log.v("result", "messageFragment" + this.messageNum);
		Fragment newContent = null;
		switch (position) {
		case 0:
			newContent = new CurriculumViewPagerFragment();
			break;
		case 1:
			if (isUnusual == 0) {
				newContent = new ScoreFragment();
			} else {
				Toast.makeText(this.getActivity(), "教务系统异常，暂不支持查成绩",
						Toast.LENGTH_SHORT).show();
			}
			break;
		case 2:
			if (isUnusual == 0) {
				newContent = new DownloadCurriculumFragment();
			} else {
				Toast.makeText(this.getActivity(), "教务系统异常，暂不支持下课表",
						Toast.LENGTH_SHORT).show();
			}
			break;
		case 3:
			newContent = new NewbornRaiders();
			break;
		case 4:
			newContent = new MessageFragment();
			messageNum = 0;
			adapter();
			break;
		case 5:
			showGrid(false);
			break;
		case 6:
			newContent = new PreferenceFragmentPlugin();
			break;
		case 7:
//			exit();
			showAlertDialogForLogOff();
			break;
		}
		if (newContent != null)
			switchFragment(newContent);
	}

	private void showGrid(boolean silent) {
		Intent i = new Intent(getActivity(), ShareAllGird.class);
		// 分享时Notification的图�
		i.putExtra("notif_icon", R.drawable.ic_launcher);
		// 分享时Notification的标�
		i.putExtra("notif_title", getActivity().getString(R.string.app_name));

		// address是接收人地址，仅在信息和邮件使用，否则可以不提供
		i.putExtra("address", "12345678901");
		// title标题，在印象笔记、邮箱、信息、微信（包括好友和朋友圈）、人人网和QQ空间使用，否则可以不提供
		i.putExtra("title", getActivity().getString(R.string.share));
		// titleUrl是标题的网络链接，仅在人人网和QQ空间使用，否则可以不提供
		i.putExtra("titleUrl", "http://sharesdk.cn");
		// text是分享文本，所有平台都需要这个字�
		i.putExtra("text", getActivity().getString(R.string.share_content));
		// imagePath是本地的图片路径，除Linked-In外的所有平台都支持这个字段
		// i.putExtra("imagePath", MainActivity.TEST_IMAGE);
		// imageUrl是图片的网络路径，新浪微博、人人网、QQ空间和Linked-In支持此字�
		// i.putExtra("imageUrl",
		// "http://img.appgo.cn/imgs/sharesdk/content/2013/06/13/1371120300254.jpg");
		// url仅在微信（包括好友和朋友圈）中使用，否则可以不提�
		i.putExtra("url", "http://sharesdk.cn");
		// thumbPath是缩略图的本地路径，仅在微信（包括好友和朋友圈）中使用，否则可以不提�
		// i.putExtra("thumbPath", MainActivity.TEST_IMAGE);
		// appPath是待分享应用程序的本地路劲，仅在微信（包括好友和朋友圈）中使用，否则可以不提�
		// i.putExtra("appPath", MainActivity.TEST_IMAGE);
		// comment是我对这条分享的评论，仅在人人网和QQ空间使用，否则可以不提供
		i.putExtra("comment", getActivity().getString(R.string.share));
		// site是分享此内容的网站名称，仅在QQ空间使用，否则可以不提供
		i.putExtra("site", getActivity().getString(R.string.app_name));
		// siteUrl是分享此内容的网站地址，仅在QQ空间使用，否则可以不提供
		i.putExtra("siteUrl", "http://sharesdk.cn");

		// foursquare分享时的地方�
		i.putExtra("venueName", "Southeast in China");
		// foursquare分享时的地方描述
		i.putExtra("venueDescription", "This is a beautiful place!");
		// foursquare分享时的地方纬度
		i.putExtra("latitude", 36.644009419436394f);
		// foursquare分享时的地方经度
		i.putExtra("longitude", 117.0709615945816f);
		// 是否直接分享
		i.putExtra("silent", silent);
		// 设置自定义的外部回调
		i.putExtra("callback", OneKeyShareCallback.class.getName());
		getActivity().startActivity(i);
	}

	// the meat of switching the above fragment
	private void switchFragment(Fragment fragment) {
		if (getActivity() == null)
			return;
		if (getActivity() instanceof StaticAttachmentActivity) {
			StaticAttachmentActivity fca = (StaticAttachmentActivity) getActivity();
			fca.switchContent(fragment, 0);
		}
	}

	public void exit() {
		Intent mIntent = new Intent();
		mIntent.setClass(this.getActivity(), StaticAttachmentActivity.class);
		// 这里设置flag还是比较重要�
		mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		// 发出退出程序指�
		mIntent.putExtra("flag", EXIT_APPLICATION);
		startActivity(mIntent);
	}
	
	private void showAlertDialogForLogOff() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				getActivity());
		alertDialogBuilder.setTitle(R.string.logoff_title);
		alertDialogBuilder.setMessage(R.string.logoff_tips);
		alertDialogBuilder.setPositiveButton("Yes",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						new LogOff().deleteAccount();
						StaticAttachmentActivity.instance.finish();
						startActivity(new Intent(getActivity(),LoginActivity.class));
					}
				});
		alertDialogBuilder.setNegativeButton("No",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});

		alertDialogBuilder.create();
		alertDialogBuilder.show();
	}

	public void onComplete(AbstractWeibo weibo, int action,
			HashMap<String, Object> res) {
		Message msg = new Message();
		msg.arg1 = 1;
		msg.arg2 = action;
		msg.obj = weibo;
		handler.sendMessage(msg);
	}

	public void onCancel(AbstractWeibo weibo, int action) {
		Message msg = new Message();
		msg.arg1 = 3;
		msg.arg2 = action;
		msg.obj = weibo;
		handler.sendMessage(msg);
	}

	public void onError(AbstractWeibo weibo, int action, Throwable t) {
		t.printStackTrace();

		Message msg = new Message();
		msg.arg1 = 2;
		msg.arg2 = action;
		msg.obj = weibo;
		handler.sendMessage(msg);
	}

	/** 处理操作结果 */
	public boolean handleMessage(Message msg) {
		AbstractWeibo weibo = (AbstractWeibo) msg.obj;
		String text = StaticAttachmentActivity.actionToString(msg.arg2);
		switch (msg.arg1) {
		case 1: { // 成功
			text = weibo.getName() + " completed at " + text;
		}
			break;
		case 2: { // 失败
			text = weibo.getName() + " caught error at " + text;
		}
			break;
		case 3: { // 取消
			text = weibo.getName() + " canceled at " + text;
		}
			break;
		}

		Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
		return false;
	}

	@Override
	public void onClick(View v) {
		Log.v("click", "click");
	}
	
	private class LogOff {

		public void deleteAccount() {
			Class<?>[] daoServices = { AccountService.class,
					CurriculumService.class, SemesterService.class,
					ScoreService.class };
			for (int i = 0; i < daoServices.length; i++) {
				IDAOService daoService = getInstance(daoServices[i]);
				daoService.deleteAccount();
			}
			clearPreferAndSaveTipPrefer();
		}

		private void clearPreference() {
			SharedPreferences.Editor ed = Preferences.getEditor(getActivity());
			ed.clear();
			ed.commit();
		}

		public void clearPreferAndSaveTipPrefer() {
			String[] tips = { SCORE_TIP_SHOW, CURRICULUM_TIP_SHOW,
					WEEK_VIEW_TIP_SHOW, SOCRE_DOWNLOAD_TIP,
					CURRICULUM_DOWNLOAD_TIP, SCHOOLMAP_TIP_SHOW };
			int[] tipValues = new int[tips.length];
			for (int i = 0; i < tips.length; i++) {
				tipValues[i] = PreferenceManager.getDefaultSharedPreferences(
						getActivity()).getInt(tips[i], 0);
			}
			clearPreference();
			// save tips value in prefer
			for (int i = 0; i < tips.length; i++) {
				PreferenceUtils.modifyIntValueInPreferences(getActivity(),
						tips[i], tipValues[i]);
			}
		}
	}
	
	public IDAOService getInstance(Class<?> object) {
		Constructor<?> constructor = null;
		IDAOService instance = null;
		try {
			constructor = object.getConstructor(Context.class);
			instance = (IDAOService) constructor.newInstance(getActivity());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return instance;
	}

}
