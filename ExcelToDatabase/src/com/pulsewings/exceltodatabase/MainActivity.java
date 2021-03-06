package com.pulsewings.exceltodatabase;

import java.io.InputStream;

import jxl.Sheet;
import jxl.Workbook;
import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

	private NotesDbAdapter dbAdapter;

	private static final String TAG = "NotesDbAdapter";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Log.d(TAG, "DatabaseTest :: onCreate()");
		this.dbAdapter = new NotesDbAdapter(this);

		copyExcelDataToDatabase();

		Button bt = (Button) findViewById(R.id.addButton);
		bt.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				String title = "러키";
				String body = "해피";

				dbAdapter.open();
				dbAdapter.createNote(title, body);
				dbAdapter.close();

				TextView tv = (TextView) findViewById(R.id.message);
				tv.setText(title + ", " + body + "를 추가하였습니다.");
			}
		});

		Button bt1 = (Button) findViewById(R.id.loadButton);
		bt1.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				dbAdapter.open();
				Cursor result = dbAdapter.fetchAllNotes();
				result.moveToFirst();
				String resultStr = "";
				while (!result.isAfterLast()) {
					String title = result.getString(1);
					String body = result.getString(2);
					resultStr += title + ", " + body + "\n";
					result.moveToNext();
				}

				TextView tv = (TextView) findViewById(R.id.message);
				tv.setText(resultStr);
				result.close();
				dbAdapter.close();
			}
		});
	}

	private void copyExcelDataToDatabase() {
		Log.w("ExcelToDatabase", "copyExcelDataToDatabase()");

		Workbook workbook = null;
		Sheet sheet = null;

		try {
			InputStream is = getBaseContext().getResources().getAssets().open("notes.xlsx");
			workbook = Workbook.getWorkbook(is);

			if (workbook != null) {
				sheet = workbook.getSheet(0);

				if (sheet != null) {

					int nMaxColumn = 2;
					int nRowStartIndex = 0;
					int nRowEndIndex = sheet.getColumn(nMaxColumn - 1).length - 1;
					int nColumnStartIndex = 0;
					int nColumnEndIndex = sheet.getRow(2).length - 1;

					dbAdapter.open();
					for (int nRow = nRowStartIndex; nRow <= nRowEndIndex; nRow++) {
						String title = sheet.getCell(nColumnStartIndex, nRow).getContents();
						String body = sheet.getCell(nColumnStartIndex + 1, nRow).getContents();
						dbAdapter.createNote(title, body);
					}
					dbAdapter.close();
				} else {
					System.out.println("Sheet is null!!");
				}
			} else {
				System.out.println("WorkBook is null!!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (workbook != null) {
				workbook.close();
			}
		}
	}
}


