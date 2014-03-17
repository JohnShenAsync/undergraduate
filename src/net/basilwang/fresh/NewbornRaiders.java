package net.basilwang.fresh;

import net.basilwang.R;
import net.basilwang.StaticAttachmentActivity;
import net.basilwang.map.SchoolMapFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class NewbornRaiders extends Fragment implements OnClickListener {

	private View view;
	private View schoolbus, school, trip, question;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.newborn_raiders, null);
		prepare();
		return view;
	}

	public void prepare() {
		schoolbus = (View) view.findViewById(R.id.lay_schoolbus);
		schoolbus.setOnClickListener(this);
		school = (View) view.findViewById(R.id.lay_school);
		school.setOnClickListener(this);
		trip = (View) view.findViewById(R.id.lay_trip);
		trip.setOnClickListener(this);
		question = (View) view.findViewById(R.id.lay_question);
		question.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Fragment content = null;
		Log.v("result", "hello");
		switch (v.getId()) {
		case R.id.lay_schoolbus:
			content = new SchoolBusFragment();
			break;
		case R.id.lay_school:
			content = new SchoolMapFragment();
			break;
		case R.id.lay_trip:
			content = new BusViewFragment();
			break;
		case R.id.lay_question:
			content = new FreshQuestion();
			break;
		}
		if (content != null)
			switchFragment(content);
	}

	private void switchFragment(Fragment fragment) {
		if (getActivity() == null)
			return;
		if (getActivity() instanceof StaticAttachmentActivity) {
			StaticAttachmentActivity fca = (StaticAttachmentActivity) getActivity();
			fca.switchContent(fragment, 1);
		}
	}
}
