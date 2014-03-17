/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.basilwang.widget;

import net.basilwang.dao.CurriculumService;
import android.content.Context;
import android.database.Cursor;

/**
 * Loader for {@link TaskWidget}.
 *
 * This loader not only loads the messages, but also:
 * - The number of accounts.
 * - The message count shown in the widget header.
 *   It's currently just the same as the message count, but this will be updated to the unread
 *   counts for inboxes.
 */
class CurriculumWidgetLoader extends SimpleCursorLoader {

	//private TASQLiteOpenHelper mHelper;
	private String semesterValue;
	private int day;
	private int accountId;
	private CurriculumService curriculumService;
	public CurriculumWidgetLoader(Context context,String semesterValue,
			int day, int accountId) {
		super(context);
		//mHelper = helper;
		this.semesterValue=semesterValue;
		this.day=day;
		this.accountId=accountId;
		curriculumService=new CurriculumService(context);
	}

	@Override
	public Cursor loadInBackground() {
		Cursor cursor = null;

		cursor=curriculumService.getCurriculumCursorByDay(semesterValue,day,accountId);
		
		
		if (cursor != null) {
			cursor.getCount();
		}
		
		return cursor;
	}
    
}
