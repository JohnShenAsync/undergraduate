package net.basilwang;

import net.basilwang.config.SAXParse;
import net.basilwang.dao.CurriculumService;
import net.basilwang.entity.Curriculum;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class EditCurriculumActivity extends Activity {
	private final String TAG = "editcurriculumactivity";
	private CurriculumService curriculumService;
	private Curriculum curriculum;
	private String[] severityArray;
	private Spinner spinnerSeverity;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_curriculum);
		setTitle(SAXParse.getTAConfiguration().getSelectedCollege().getName());
		curriculumService = new CurriculumService(this);
		severityArray = getResources().getStringArray(R.array.severity);
		Button btnCancel = (Button) findViewById(R.id.btnCancel);
		Button btnSave = (Button) findViewById(R.id.btnSave);
		Bundle bundle = getIntent().getExtras();
		long id = bundle.getLong("g");
		Log.i(TAG, String.valueOf(id));
		curriculum = curriculumService.getCurriculumById((int) id);
		EditText txtCurriculumName = (EditText) findViewById(R.id.txtCurriculumName);
		txtCurriculumName.setText(curriculum.getName());
		spinnerSeverity = (Spinner) findViewById(R.id.spinnerSeverity);
		spinnerSeverity.setSelection(curriculum.getSeverity());
		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				EditCurriculumActivity.this.finish();
			}

		});
		btnSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int severity = spinnerSeverity.getSelectedItemPosition();
				curriculum.setSeverity(severity);
				curriculumService.update(curriculum);
				EditCurriculumActivity.this.finish();
			}

		});
		// spinnerSeverity.setOnItemSelectedListener(new
		// OnItemSelectedListener(){
		//
		// @Override
		// public void onItemSelected(AdapterView<?> parent, View view,
		// int position, long id) {
		// //
		//
		// }
		//
		// @Override
		// public void onNothingSelected(AdapterView<?> parent) {
		// //
		//
		// }
		//
		// });
	}
}
