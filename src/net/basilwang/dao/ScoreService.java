package net.basilwang.dao;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.basilwang.config.SAXParse;
import net.basilwang.config.ScoreConfig;
import net.basilwang.config.ScoreConfigInTD;
import net.basilwang.entity.Score;
import android.content.Context;
import android.database.Cursor;

public class ScoreService implements IDAOService {
	private DAOHelper daoHelper;
	private String[] indexs;

	public ScoreService(Context context) {
		this.daoHelper = new DAOHelper(context);
	}

	public void save(Score score) {

		String sql = "INSERT INTO Scores (coursename,coursecode,coursetype,coursebelongto,scorelevel,scorepoint,score,secondmajorflag,secondscore,thirdscore,department,memo,isthirdscore,myid,semesterid) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		Object[] bindArgs = { score.getCourseName(), score.getCourseCode(),
				score.getCourseType(), score.getCourseBelongTo(),
				score.getScoreLevel(), score.getScorePoint(), score.getScore(),
				score.getSecondMajorFlag(), score.getSecondScore(),
				score.getThirdScore(), score.getDepartment(), score.getMemo(),
				score.getIsthirdscore(), score.getMyid(),
				getSemesterId(score.getSemesterName()) };
		daoHelper.insert(sql, bindArgs);

	}

	public void delete(int accountId, String semesterName) {
		String sql = "delete from scores where myid=? and semesterid = ?";
		Object[] bindArgs = { accountId, getSemesterId(semesterName) };
		daoHelper.delete(sql, bindArgs);
	}

	public Cursor getScores(int myid) {
		String sql = "select _id,coursename,coursecode,coursetype,coursebelongto,"
				+ "scorelevel,scorepoint,score,secondmajorflag,secondscore,thirdscore,department,memo,isthirdscore,myid from scores where myid=?";

		String[] bindArgs = { String.valueOf(myid) };
		return daoHelper.query(sql, bindArgs);
	}

	public String[] getOrInitScoreConfigInTDIndexs(
			List<ScoreConfigInTD> scoreConfigInTDS) {
		int size = getScoreConfigInTDHaveIndex(scoreConfigInTDS).size() + 1;
		indexs = new String[size];
		for (ScoreConfigInTD scoreConfigInTD : scoreConfigInTDS) {
			if (scoreConfigInTD.getVisible() == null
					|| scoreConfigInTD.getVisible().equals("false")) {
				continue;
			}
			indexs[Integer.valueOf(scoreConfigInTD.getIndex())] = scoreConfigInTD
					.getDbfield();
		}
		return indexs;
	}

	public List<ScoreConfigInTD> getScoreConfigInTDHaveIndex(
			List<ScoreConfigInTD> scoreConfigInTDS) {
		List<ScoreConfigInTD> scoreConfigInTdIndexs = new LinkedList<ScoreConfigInTD>();
		for (ScoreConfigInTD scoreConfigInTD : scoreConfigInTDS) {
			if (scoreConfigInTD.getVisible() == null
					|| scoreConfigInTD.getVisible().equals("false")) {
				continue;
			}
			scoreConfigInTdIndexs.add(scoreConfigInTD);
		}
		return scoreConfigInTdIndexs;
	}

	private String constructSQL() {
		List<ScoreConfigInTD> scoreConfigInTDS = getScoreConfig()
				.getScoreConfigintds();
		getOrInitScoreConfigInTDIndexs(scoreConfigInTDS);
		String sql = "SELECT _id";
		for (int i = 1; i < indexs.length; i++) {
			sql += "," + indexs[i].toLowerCase();
		}
		sql += " FROM scores WHERE myid=? AND semesterid = ?";
		return sql;
	}

	private String getSemesterId(String semesterName) {
		semesterName = semesterName.replace('–', '-');
		String sql = "SELECT _id FROM semesters WHERE semestername =? ";
		String[] bindArgs = { semesterName };
		Cursor result = daoHelper.query(sql, bindArgs);
		if (result.moveToNext()) {
			return result.getString(result.getColumnIndex("_id"));
		}
		daoHelper.closeDB();
		return "0";
	}

	public List<Score> getScores(String semesterName, int myid) {
		String sql = constructSQL();
		String[] bindArgs = { String.valueOf(myid), getSemesterId(semesterName) };
		Cursor result = daoHelper.query(sql, bindArgs);
		List<Score> list = new ArrayList<Score>();
		while (result.moveToNext()) {
			Score score = new Score();
			for (int i = 1; i < indexs.length; i++) {
				String value = result.getString(i);
				score.setter(indexs[i], value);
			}
			list.add(score);
		}
		daoHelper.closeDB();
		return list;
	}

	private ScoreConfig getScoreConfig() {
		return SAXParse.getTAConfiguration().getSelectedCollege()
				.getScoreConfig();
	}

	public void formatAndSaveScoreById(String semesterName, String scoreStr,
			int accountId) {
		// 2012-07-10 basilwang delete the related scores of the seleced
		// semester value
		if (scoreStr == null) {
			return;
		}
		delete(accountId, semesterName);
		ScoreConfig scoreConfig = getScoreConfig();
		Pattern trPattern = Pattern.compile(scoreConfig.getTr());
		Matcher matcher;
		matcher = trPattern.matcher(scoreStr);
		Matcher tdMatcher = null;
		matcher.find();
		while (matcher.find()) {
			String tr = matcher.group();
			Pattern tdPattern = Pattern.compile(scoreConfig.getTd());
			tdMatcher = tdPattern.matcher(tr);
			Score score = new Score(semesterName);
			score.setMyid(accountId);
			List<ScoreConfigInTD> scoreConfigIntds = scoreConfig
					.getScoreConfigintds();
			for (ScoreConfigInTD scoreConfigIntd : scoreConfigIntds) {
				tdMatcher.find();
				if (scoreConfigIntd.getDbfield() == null) {
					continue;
				}
				score.setter(scoreConfigIntd.getDbfield(), tdMatcher.group(1));
			}
			save(score);
		}
	}

	public void deleteAccount() {
		String sql = "DELETE  FROM scores";
		daoHelper.delete(sql);
	}

}
