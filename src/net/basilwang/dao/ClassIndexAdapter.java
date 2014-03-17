package net.basilwang.dao;

import java.util.List;

import net.basilwang.config.ClassIndex;
import net.basilwang.config.SAXParse;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ClassIndexAdapter extends BaseAdapter {
	private List<ClassIndex> classIndexs;
	private Context context;

	public ClassIndexAdapter(Context context) {
		this.classIndexs = SAXParse.getTAConfiguration().getSelectedCollege()
				.getCurriculumConfig().getClassindexs();
		this.context = context;
	}

	@Override
	public int getCount() {
		return classIndexs.size();
	}

	@Override
	public Object getItem(int position) {
		return classIndexs.get(position).getName();
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		TextView text = new TextView(context);
		text.setText(classIndexs.get(position).getName());
		text.setHeight(parent.getHeight() / classIndexs.size() - 1);
		return text;
	}

}
