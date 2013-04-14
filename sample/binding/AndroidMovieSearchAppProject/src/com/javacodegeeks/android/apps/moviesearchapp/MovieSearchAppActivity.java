package com.javacodegeeks.android.apps.moviesearchapp;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.javacodegeeks.android.apps.moviesearchapp.model.Movie;
import com.javacodegeeks.android.apps.moviesearchapp.model.Person;
import com.javacodegeeks.android.apps.moviesearchapp.services.GenericSeeker;
import com.javacodegeeks.android.apps.moviesearchapp.services.MovieSeeker;
import com.javacodegeeks.android.apps.moviesearchapp.services.PersonSeeker;

public class MovieSearchAppActivity extends Activity {
	
	private static final String EMPTY_STRING = "";
	
	private EditText searchEditText;
	private RadioButton moviesSearchRadioButton;
	private RadioButton peopleSearchRadioButton;
	private RadioGroup searchRadioGroup;
	private TextView searchTypeTextView;
	private Button searchButton;
	
	private GenericSeeker<Movie> movieSeeker = new MovieSeeker();
	private  GenericSeeker<Person> personSeeker = new PersonSeeker();
	
	private ProgressDialog progressDialog;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        this.findAllViewsById();
        
        moviesSearchRadioButton.setOnClickListener(radioButtonListener);
        peopleSearchRadioButton.setOnClickListener(radioButtonListener);
        
        searchButton.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				String query = searchEditText.getText().toString();
				performSearch(query);
			}
		});
        
        searchEditText.setOnFocusChangeListener(new DftTextOnFocusListener(getString(R.string.search)));
        
        int id = searchRadioGroup.getCheckedRadioButtonId();
        RadioButton radioButton = (RadioButton) findViewById(id);
        searchTypeTextView.setText(radioButton.getText());
        
    }    
    
    private void findAllViewsById() {
    	searchEditText = (EditText) findViewById(R.id.search_edit_text);
    	moviesSearchRadioButton = (RadioButton) findViewById(R.id.movie_search_radio_button);
    	peopleSearchRadioButton = (RadioButton) findViewById(R.id.people_search_radio_button);
    	searchRadioGroup = (RadioGroup) findViewById(R.id.search_radio_group);
    	searchTypeTextView = (TextView) findViewById(R.id.search_type_text_view);
    	searchButton = (Button) findViewById(R.id.search_button);
	}
    
    public void longToast(CharSequence message) {
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}
    
    private OnClickListener radioButtonListener = new OnClickListener() {
        public void onClick(View v) {
            RadioButton radioButton = (RadioButton) v;
            searchTypeTextView.setText(radioButton.getText());
        }
    };
    
	private class DftTextOnFocusListener implements OnFocusChangeListener {
		
		private String defaultText;

		public DftTextOnFocusListener(String defaultText) {
			this.defaultText = defaultText;
		}

		public void onFocusChange(View v, boolean hasFocus) {
			if (v instanceof EditText) {
				EditText focusedEditText = (EditText) v;
				// handle obtaining focus
				if (hasFocus) {
					if (focusedEditText.getText().toString().equals(defaultText)) {
						focusedEditText.setText(EMPTY_STRING);
					}
				}
				// handle losing focus
				else {
					if (focusedEditText.getText().toString().equals(EMPTY_STRING)) {
						focusedEditText.setText(defaultText);
					}
				}
			}
		}
		
	}
	
    private void performSearch(String query) {
        
    	progressDialog = ProgressDialog.show(MovieSearchAppActivity.this,
        		"Please wait...", "Retrieving data...", true, true);
    	
        if (moviesSearchRadioButton.isChecked()) {
        	PerformMovieSearchTask task = new PerformMovieSearchTask();
        	task.execute(query);
        	progressDialog.setOnCancelListener(new CancelTaskOnCancelListener(task));
		}
		else if (peopleSearchRadioButton.isChecked()) {
			PerformPersonSearchTask task = new PerformPersonSearchTask();
        	task.execute(query);
        	progressDialog.setOnCancelListener(new CancelTaskOnCancelListener(task));
		}
        
    }
    
    private class CancelTaskOnCancelListener implements OnCancelListener {
    	private AsyncTask<?, ?, ?> task;
    	public CancelTaskOnCancelListener(AsyncTask<?, ?, ?> task) {
    		this.task = task;
    	}
    	@Override
		public void onCancel(DialogInterface dialog) {
    		if (task!=null) {
        		task.cancel(true);
        	}
		}
    }
	
	private class PerformMovieSearchTask extends AsyncTask<String, Void, List<Movie>> {

		@Override
		protected List<Movie> doInBackground(String... params) {
			String query = params[0];
			try {
				return movieSeeker.find(query, 10);
			} catch (Exception e) {
				throw new RuntimeException("failed to find movie", e);
			}
		}
		
		@Override
		protected void onPostExecute(final List<Movie> result) {			
			runOnUiThread(new Runnable() {
		    	@Override
		    	public void run() {
		    		if (progressDialog!=null) {
		    			progressDialog.dismiss();
		    			progressDialog = null;
		    		}
		    		Intent intent = new Intent(MovieSearchAppActivity.this, MoviesListActivity.class);
		    		intent.putExtra("movies", (ArrayList<Movie>)result);
		            startActivity(intent);
		    	}
		    });
		}
		
	}
	
	private class PerformPersonSearchTask extends AsyncTask<String, Void, List<Person>> {

		@Override
		protected List<Person> doInBackground(String... params) {
			String query = params[0];
			try {
				return personSeeker.find(query, 10);
			} catch (Exception e) {
				throw new RuntimeException("failed to find person", e);
			}
		}
		
		@Override
		protected void onPostExecute(final List<Person> result) {			
			runOnUiThread(new Runnable() {
		    	@Override
		    	public void run() {
		    		if (progressDialog!=null) {
		    			progressDialog.dismiss();
		    			progressDialog = null;
		    		}
		    		if (result!=null) {
						for (Person person : result) {
							longToast(person.name);
						}
					}
		    	}
		    });
		}
		
	}
    
}
