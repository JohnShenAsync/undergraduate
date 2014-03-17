package net.basilwang.fresh;

import net.basilwang.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FreshQuestion extends Fragment {

	@SuppressWarnings("unused")
	private QuestionDetail militaryQuestion;
	private View mainView;
	TextView military, mingshui, shengjing, yanshan, shungeng, schoolbus, bus;
	CharSequence questionMilitary, questionMingshui, questionShengjing,
			questionYanshan, questionShungeng;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mainView = inflater.inflate(R.layout.fresh_question, null);
		questionMilitary = getResources().getText(R.string.question_military);
		questionMingshui = getResources().getText(R.string.question_mingshui);
		questionShengjing = getResources().getText(R.string.question_shengjing);
		questionYanshan = getResources().getText(R.string.question_yanshan);
		questionShungeng = getResources().getText(R.string.question_shungeng);
		military = (TextView) mainView.findViewById(R.id.q_military);
		mingshui = (TextView) mainView.findViewById(R.id.q_mingshui);
		shengjing = (TextView) mainView.findViewById(R.id.q_shengjing);
		yanshan = (TextView) mainView.findViewById(R.id.q_yanshan);
		shungeng = (TextView) mainView.findViewById(R.id.q_shungeng);
		prepare();
		return mainView;
	}

	public void prepare() {
		militaryQuestion = new QuestionDetail(
				(View) mainView.findViewById(R.id.military), military,
				questionMilitary);
		militaryQuestion = new QuestionDetail(
				(View) mainView.findViewById(R.id.mingshui), mingshui,
				questionMingshui);
		militaryQuestion = new QuestionDetail(
				(View) mainView.findViewById(R.id.shengjing), shengjing,
				questionShengjing);
		militaryQuestion = new QuestionDetail(
				(View) mainView.findViewById(R.id.yanshan), yanshan,
				questionYanshan);
		militaryQuestion = new QuestionDetail(
				(View) mainView.findViewById(R.id.shungeng), shungeng,
				questionShungeng);
	}
}
