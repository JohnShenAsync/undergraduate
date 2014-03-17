package net.basilwang;

import net.basilwang.listener.ActionModeListener;

import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class AddActionMode implements ActionMode.Callback {
	private String[] titles;
	private ActionModeListener actionModeListener;

	public AddActionMode(String[] titles, ActionModeListener listener) {
		this.titles = titles;
		this.actionModeListener = listener;
	}
 
	@Override
	public boolean onActionItemClicked(ActionMode arg0, MenuItem menu) {
		actionModeListener.onActionItemClickedListener(menu.getTitle()
				.toString());
		return true;
	}

	@Override
	public boolean onCreateActionMode(ActionMode arg0, Menu menu) {
		for (int i = 0; i < titles.length; i++) {
			menu.add(titles[i]).setShowAsAction(
					MenuItem.SHOW_AS_ACTION_ALWAYS
							| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		}
		return true;
	}

	@Override
	public void onDestroyActionMode(ActionMode mode) {
		actionModeListener.finishActionMode();
	}

	@Override
	public boolean onPrepareActionMode(ActionMode arg0, Menu arg1) {
		return false;
	}

}
