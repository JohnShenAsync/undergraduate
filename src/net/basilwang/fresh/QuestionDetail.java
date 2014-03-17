package net.basilwang.fresh;

import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class QuestionDetail implements OnClickListener {

	private View view;
	private TextView textView;
	private CharSequence c;
	private boolean isOpen;

	public QuestionDetail(View v, TextView text, CharSequence c) {
		this.view = v;
		this.textView = text;
		this.c = c;
		isOpen = false;
		textView.setMaxLines(0);
		view.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (this.isOpen == false) {
			this.textView.setMaxLines(200);
			this.textView.setText(Html.fromHtml(c.toString()));
			this.textView.setMovementMethod(LinkMovementMethod.getInstance());
			this.isOpen = true;
		} else {
			this.textView.setMaxLines(0);
			this.isOpen = false;
		}
	}

}
