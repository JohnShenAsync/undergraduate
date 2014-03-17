package net.basilwang;

import net.basilwang.utils.PreferenceUtils;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

public class SplashActivity extends Activity {
	private String tokenContent;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 去掉窗口标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 全屏显示
		getWindow().setFlags(
				WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

		setContentView(R.layout.splash);
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				jump();
			}
		}, 2000);// 2000为间隔的时间
	}
	public String getToken(){
		tokenContent=PreferenceUtils.getPreferToken(this);
		return tokenContent;
	}
	public void jump(){
		Intent mainIntent =new Intent();
		if(getToken()==null){
			mainIntent.setClass(this, LoginActivity.class);
		}else{
			mainIntent.setClass(this, StaticAttachmentActivity.class);
		}
		SplashActivity.this.startActivity(mainIntent);
		SplashActivity.this.finish();
	}

}