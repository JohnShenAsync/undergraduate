package net.basilwang.sever;

import java.util.ArrayList;
import java.util.List;
import net.basilwang.dao.DAOHelper;
import net.basilwang.dao.IDAOService;
import android.content.Context;
import android.database.Cursor;

public class MessageService implements IDAOService {

	private DAOHelper daoHelper;

	public MessageService(Context context) {
		this.daoHelper = new DAOHelper(context);
	}

	public void save(MessageContent message) {
		String sql = "INSERT INTO messages(content,messageId,Id,createTime,IsRead) VALUES (?,?,?,?,?)";
		Object[] bindArgs = { message.getContent(), message.getMessageId(),
				message.getId(), message.getCreateTime(), message.getIsRead() };
		daoHelper.insert(sql, bindArgs);
	}

	public List<MessageContent> getAllMessages() {
		String sql = "select * from messages";
		Cursor result = daoHelper.query(sql, null);
		List<MessageContent> list = new ArrayList<MessageContent>();
		while (result.moveToNext()) {
			MessageContent message = new MessageContent();
			message.setContent(result.getString(0));
			message.setMessageId(result.getInt(1));
			message.setId(result.getInt(2));
			message.setCreateTime(result.getString(3));
			message.setIsRead(result.getInt(4));
			list.add(message);
		}
		daoHelper.closeDB();
		return list;
	}

	public List<MessageContent> getMessageOfRead(int i) {
		String sql = "select * from messages where isRead=?";
		String sa[] = { String.valueOf(i) };
		Cursor result = daoHelper.query(sql, sa);
		List<MessageContent> list = new ArrayList<MessageContent>();
		while (result.moveToNext()) {
			MessageContent message = new MessageContent();
			message.setContent(result.getString(0));
			message.setMessageId(result.getInt(1));
			message.setId(result.getInt(2));
			message.setCreateTime(result.getString(3));
			message.setIsRead(result.getInt(4));
			list.add(message);
		}
		daoHelper.closeDB();
		return list;
	}

	public int[] selectAllMessageId() {
		String sql = "select messageId from messages";
		Cursor result = daoHelper.query(sql, null);
		int[] ids = new int[result.getCount()];
		for (int i = 0; result.moveToNext(); i++) {
			ids[i] = result.getInt(0);
		}
		daoHelper.closeDB();
		return ids;
	}

	public int selectMaxId() {
		String sql = "select max(messageId) from messages";
		Cursor result = daoHelper.query(sql, null);
		int maxId = 0;
		while (result.moveToNext()) {
			maxId = result.getInt(0);
		}
		daoHelper.closeDB();
		return maxId;
	}

	public void deleteMessage(MessageContent message) {
		String sql = "delete from messages where messageid=?";
		Object[] bindArgs = { message.getMessageId() };
		daoHelper.delete(sql, bindArgs);
	}

	public void updateMessage(MessageContent message) {
		String sql = "UPDATE messages SET content = ?" + "WHERE messageid=?";
		Object[] bindArgs = { "", message.getMessageId() };
		daoHelper.update(sql, bindArgs);
	}

	public void updateRead(int i, MessageContent message) {
		Object[] bindArgs = { String.valueOf(i), message.getMessageId() };
		String sql = "UPDATE messages SET IsRead = ?" + "WHERE messageid=?";
		daoHelper.update(sql, bindArgs);
	}

	@Override
	public void deleteAccount() {
		String sql = "DELETE  FROM curriculum";
		daoHelper.delete(sql);
	}

}
