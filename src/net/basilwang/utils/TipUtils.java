package net.basilwang.utils;

import net.basilwang.listener.ShowTipListener;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import net.basilwang.R;

public class TipUtils {

	public static void showTipIfNecessary(Context context, int tipResId,
			final ShowTipListener tipListener) {
		final Dialog dialog = new Dialog(context, R.style.Dialog_Fullscreen);
		dialog.setContentView(R.layout.tip_view);
		ImageView iv = (ImageView) dialog.findViewById(R.id.tip_imageview);
		iv.setBackgroundResource(tipResId);
		iv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				tipListener.dismissTip();
			}
		});
		dialog.show();

	}
}
