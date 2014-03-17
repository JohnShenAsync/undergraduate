package net.basilwang;

import java.util.ArrayList;
import java.util.List;
import net.basilwang.dao.AccountService;
import net.basilwang.sever.FeedBack;
import net.basilwang.sever.MessageContent;
import net.basilwang.sever.MessageService;
import net.basilwang.utils.PreferenceUtils;
import br.com.dina.ui.widget.UITableView;
import br.com.dina.ui.widget.UITableView.ClickListener;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MessageFragment extends Fragment {

	View messageView;
	UITableView messageTable;
	List<MessageContent> dbMessages = new ArrayList<MessageContent>();
	List<MessageContent> dbMessages0 = new ArrayList<MessageContent>();
	List<MessageContent> dbMessages1 = new ArrayList<MessageContent>();
	List<MessageContent> dbMessages2 = new ArrayList<MessageContent>();
	MessageService messageService;
	int maxId;
	int images[] = { R.drawable.read0, R.drawable.read1, R.drawable.read2 };

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		messageView = inflater.inflate(R.layout.messages_frament, container,
				false);
		messageTable = (UITableView) messageView
				.findViewById(R.id.message_fragment);
		messageService = new MessageService(this.getActivity());
		maxId = messageService.selectMaxId();
		int messageId[] = messageService.selectAllMessageId();
		for (int i = 0; i < messageId.length; i++) {
			feedBack(messageId[i]);
		}
		requestMessages();
		createMessages();
		return messageView;
	}

	public void feedBack(int messageId) {
		String token=PreferenceUtils.getPreferToken(this.getActivity());
		FeedBack back = new FeedBack();
		String url = "http://www.ruguozhai.me/api/message/ReadMessage";
		back.execute(url,token,messageId);
	}

	// 获取本地数据库中的信息
	public void requestMessages() {
		MessageService messageService = new MessageService(this.getActivity());
		dbMessages0 = messageService.getMessageOfRead(0);
		addMessages(dbMessages0);
		dbMessages2 = messageService.getMessageOfRead(2);
		addMessages(dbMessages2);
		dbMessages1 = messageService.getMessageOfRead(1);
		addMessages(dbMessages1);
	}

	public void addMessages(List<MessageContent> list) {
		for (int i = 0; i < list.size(); i++) {
			dbMessages.add(list.get(i));
		}
	}

	public void createMessages() {
		messageTable.clear();
		CustomClickListener listener = new CustomClickListener();// 点击删除消息
		messageTable.setClickListener(listener);
		try {
			if (dbMessages.size() == 0||IsNull()) {
				messageTable.addBasicItem("没有新消息");
				messageTable.setClickable(false);
			} else {
				createMessageList();
				messageTable.setClickable(true);
			}
		} catch (Exception e) {

		}
		messageTable.commit();
	}

	public boolean IsNull(){
		for(int i=0;i<dbMessages.size();i++){
			if(!dbMessages.get(i).getContent().equals(""))
				return false;
		}
		return true;
	}
	public void createMessageList() {
		for (MessageContent m : dbMessages) {
			if(m.getContent().equals(""))
				continue;
			messageTable.addBasicItem(images[m.getIsRead()], m.getContent(), m
					.getCreateTime().substring(0, 10));
		}
	}

	private class CustomClickListener implements ClickListener {

		@Override
		public void onClick(int index) {
			try {
				String summery = dbMessages.get(index).getContent();
				dialogMessage(summery, index);
			} catch (Exception e) {

			}
		}
	}

	public void deleteMessage(int index) {
		if (dbMessages.get(index).getMessageId() == maxId) {
			messageService.updateMessage(dbMessages.get(index));
		} else {
			messageService.deleteMessage(dbMessages.get(index));
		}
		dbMessages.remove(index);
	}

	public void IsRead(int index,int mark) {
		dbMessages.get(index).setIsRead(mark);
		messageService.updateRead(mark, dbMessages.get(index));
	}

	public void dialogMessage(String message, final int index) {
		AlertDialog.Builder builder = new Builder(this.getActivity());
		builder.setMessage(message);
		builder.setTitle("消息内容");
		builder.setPositiveButton("删除", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				deleteMessage(index);
				createMessages();
				dialog.dismiss();
			}

		});
		builder.setNegativeButton("已读", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				IsRead(index,1);
				createMessages();
				dialog.dismiss();
				
			}
		});
		builder.setNeutralButton("标记", new OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				IsRead(index,2);
				createMessages();
				dialog.dismiss();
			}
			
		});

		builder.create().show();
	}
}
