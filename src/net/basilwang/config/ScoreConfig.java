package net.basilwang.config;

import java.util.ArrayList;
import java.util.List;

public class ScoreConfig {
	public String tr;
	public String td;
	public List<ScoreConfigInTD> scoreConfigintds;

	public ScoreConfig() {
		scoreConfigintd = new ScoreConfigInTD();
		scoreConfigintds = new ArrayList<ScoreConfigInTD>();
	}

	// private
	private ScoreConfigInTD scoreConfigintd;

	private enum tagName {
		tr, td, scoreconfigintd
	};

	public void setProperty(String nodeName, String content) {
		try {
			switch (tagName.valueOf(nodeName)) {
			case tr:
				setTr(content);
				break;
			case td:
				setTd(content);
				break;
			case scoreconfigintd:
				scoreConfigintd = new ScoreConfigInTD();
				break;
			}
		} catch (IllegalArgumentException e) {
			scoreConfigintd.setProperty(nodeName, content);
			return;
		}
	}

	public void addListNode(String nodeName) {
		if (nodeName == "scoreconfigintd") {
			scoreConfigintds.add(scoreConfigintd);
			scoreConfigintd = new ScoreConfigInTD();
		}
	}

	public String getTr() {
		return tr;
	}

	public void setTr(String tr) {
		this.tr = tr;
	}

	public String getTd() {
		return td;
	}

	public void setTd(String td) {
		this.td = td;
	}

	public List<ScoreConfigInTD> getScoreConfigintds() {
		return scoreConfigintds;
	}
}
