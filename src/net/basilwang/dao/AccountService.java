package net.basilwang.dao;

import java.util.ArrayList;
import java.util.List;

import net.basilwang.entity.Account;
import android.content.Context;
import android.database.Cursor;

public class AccountService implements IDAOService {
	private DAOHelper daoHelper;

	public AccountService(Context context) {
		this.daoHelper = new DAOHelper(context);
	}

	public int save(Account account) {
		String sql = "INSERT INTO accounts (name,userno,password,url,ishttps) VALUES (?, ?,?,?,?)";
		Object[] bindArgs = { account.getName(), account.getUserno(),
				account.getPassword(), account.getUrl(), account.getIshttps(), };
		daoHelper.insert(sql, bindArgs);
		return 0;
	}

	public void update(Account account) {
		String sql = "update accounts set name=?,userno=?,password=?,url=?,ishttps=? where _id=? ";
		Object[] bindArgs = { account.getName(), account.getUserno(),
				account.getPassword(), account.getUrl(), account.getIshttps(),
				account.getId() };
		daoHelper.update(sql, bindArgs);
	}

	public List<Account> getAccounts() {
		String sql = "select _id,name,userno,password,url,ishttps from accounts ";
		Cursor result = daoHelper.query(sql, null);
		List<Account> list = new ArrayList<Account>();
		while (result.moveToNext()) {
			Account account = new Account();
			account.setId(result.getInt(result.getColumnIndex("_id")));
			account.setName(result.getString(result.getColumnIndex("name")));
			account.setUserno(result.getString(result.getColumnIndex("userno")));
			account.setUrl(result.getString(result.getColumnIndex("url")));
			account.setPassword(result.getString(result
					.getColumnIndex("password")));

			list.add(account);
		}
		daoHelper.closeDB();
		return list;
	}

	public int getAccountCount(){
		String sql="select count(userno) from accounts";
		Cursor result=daoHelper.query(sql, null);
		int i=0;
		while(result.moveToNext()){
			i=result.getInt(0);
		}
		daoHelper.closeDB();
		return i;
	}
	public Account getAccountByName(String name) {
		Account account = new Account();
		String sql = "select _id,name,userno,password,url,ishttps from accounts where name=? ";
		String[] bindArgs = { name };
		Cursor result = daoHelper.query(sql, bindArgs);
		if (result.moveToNext()) {
			account.setId(result.getInt(result.getColumnIndex("_id")));
			account.setName(result.getString(result.getColumnIndex("name")));
			account.setUserno(result.getString(result.getColumnIndex("userno")));
			account.setUrl(result.getString(result.getColumnIndex("url")));
			account.setPassword(result.getString(result
					.getColumnIndex("password")));

		}
		daoHelper.closeDB();
		return account;
	}

	public Account getAccountById(int accountId) {
		Account account = new Account();
		String sql = "select _id,name,userno,password,url,ishttps from accounts where _id=? ";
		String[] bindArgs = { String.valueOf(accountId) };
		Cursor result = daoHelper.query(sql, bindArgs);
		if (result.moveToNext()) {
			account.setId(result.getInt(result.getColumnIndex("_id")));
			account.setName(result.getString(result.getColumnIndex("name")));
			account.setUserno(result.getString(result.getColumnIndex("userno")));
			account.setUrl(result.getString(result.getColumnIndex("url")));
			account.setPassword(result.getString(result
					.getColumnIndex("password")));

		}
		daoHelper.closeDB();
		return account;
	}

	@Override
	public void deleteAccount() {
		String sql = "DELETE  FROM accounts";
		daoHelper.delete(sql);
	}
}
