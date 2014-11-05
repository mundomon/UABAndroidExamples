package org.uab.android.contentprovider.sqlite;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

public class CoursesNewFormActivty extends Activity {
	
	AutoCompleteTextView	classesAutoCompleteTextView;
	EditText				numberOfCreditsEditText;
	TextView				defaultHourTextView;
	Button					setHourButton;
	
	Button					saveButton;
	
	Map<Integer, Boolean> checkBoxStateMap = new HashMap<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Get a reference to the UI element
		classesAutoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.classAutoComplete);
		
		numberOfCreditsEditText = (EditText) findViewById(R.id.creditsEditText);
		
		// Set an ArrayAdapter containing pre-define classes names
		ArrayAdapter<CharSequence> classesAdapter = ArrayAdapter.
				createFromResource(this, R.array.classes, R.layout.list_item);
		classesAutoCompleteTextView.setAdapter(classesAdapter);
		
		setHourButton = (Button) findViewById(R.id.setHourButton);
		
		// Set an OnClickListener for the set hour Button
		setHourButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				// Get an instance of a Calendar to set the current time
				Calendar c = Calendar.getInstance();
				int hourOfDay = c.get(Calendar.HOUR_OF_DAY);
				int minute = c.get(Calendar.MINUTE);
				
				// Create a new dialog that displays a TimePicker
				TimePickerDialog timePickerDialog = new TimePickerDialog(
						CoursesNewFormActivty.this, 
						timeSetListener, 
						hourOfDay, 
						minute, 
						true);
				timePickerDialog.show();
			}
		});
		
		defaultHourTextView = (TextView) findViewById(R.id.defaultHourLabel);
		
		saveButton = (Button) findViewById(R.id.saveButton);
		saveButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				// Get the values from the form's inputs
				String courseName = classesAutoCompleteTextView.getText().toString();
				int numberOfCredits = Integer.parseInt(numberOfCreditsEditText.getText().toString());
				String checkBoxState = checkBoxStateMap.keySet().toString();
				String hour = defaultHourTextView.getText().toString();
				
				// Open the database for writing
				ContentResolver contentResolver = getContentResolver();
				
				// Create the ContentValues for the data to be saved 
				ContentValues values = new ContentValues();
				values.put(CoursesProvider.COURSE_NAME, courseName);
				values.put(CoursesProvider.COURSE_CREDITS, numberOfCredits);
				values.put(CoursesProvider.COURSE_DAYS, checkBoxState);
				values.put(CoursesProvider.COURSE_HOUR, hour);
				
				// Save the data through the ContentProvider
				contentResolver.insert(CoursesProvider.CONTENT_URI, values);
				
				// Clear the form's inputs
				clearForm();
				
				// Set the result for the calling Activity
				setResult(RESULT_OK);
				finish();
			}
		});
	}
	
	// Resets the form's input Views
	private void clearForm() {
		
		classesAutoCompleteTextView.setText("");
		numberOfCreditsEditText.setText("");
		
		for ( int checkBoxId : checkBoxStateMap.keySet() ) {
			
			CheckBox cb = (CheckBox) findViewById(checkBoxId);
			cb.setChecked(false);
		}
		
		defaultHourTextView.setText(R.string.default_hour_label);
	}
	
	// Called when the user clicks on one of the CheckBoxes, changing it's state
	public void onCheckBoxClicked(View v) {
		
		// Save the ID of the CheckBox jointly with it's current state
		CheckBox cb = (CheckBox) v;
		checkBoxStateMap.put(cb.getId(), cb.isChecked());
		
	}
	
	// Implementation of the OnTimeSetListener interface
	OnTimeSetListener timeSetListener = new OnTimeSetListener() {
		
		// This method is called when the user click done on the TimePicker
		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			
			// Set the hour and minute on the corresponding TextView
			defaultHourTextView.setText(hourOfDay + ":" + minute);
		}
	};
}
