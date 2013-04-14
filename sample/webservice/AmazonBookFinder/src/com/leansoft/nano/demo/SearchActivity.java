package com.leansoft.nano.demo;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import com.amazon.service.ecommerce.AWSECommerceClient;
import com.amazon.webservices.awsecommerceservice._2011_08_01.Errors;
import com.amazon.webservices.awsecommerceservice._2011_08_01.Item;
import com.amazon.webservices.awsecommerceservice._2011_08_01.ItemSearch;
import com.amazon.webservices.awsecommerceservice._2011_08_01.ItemSearchRequest;
import com.amazon.webservices.awsecommerceservice._2011_08_01.ItemSearchResponse;
import com.amazon.webservices.awsecommerceservice._2011_08_01.Items;
import com.amazon.webservices.awsecommerceservice._2011_08_01.client.AWSECommerceServicePortType_SOAPClient;
import com.github.droidfu.widgets.WebImageView;
import com.leansoft.nano.log.ALog;
import com.leansoft.nano.ws.SOAPServiceCallback;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;

public class SearchActivity extends ListActivity implements OnScrollListener {
	
	private static final String TAG = SearchActivity.class
			.getSimpleName();

	
	static final int DEFAULT_ENTRIES_PER_PAGE = 10;
	
	private int lastItem = 0;
	private int totalItemCount = 0;
	
	private String searchKeywords;
	
	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		this.getListView().setOnScrollListener(this);
		
		Button searchButton = (Button) findViewById(R.id.btn_search);
		searchButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String keywords = ((EditText) findViewById(R.id.edit_input))
						.getText().toString();
				// validation
				if (keywords == null | keywords.length() == 0) {
		            Toast.makeText(getApplicationContext(),
		                    "please enter keyword first!",
		                    Toast.LENGTH_LONG).show();
		            return;
				}
				
				searchKeywords = keywords;
				
				// make a search
				searchItemsByKeywords(1, DEFAULT_ENTRIES_PER_PAGE, true);
			}
		});
	}
	
	// asynchronously trigger ItemSearch call
	private void searchItemsByKeywords(int pageNum, int entriesPerPage, final boolean newSearch) {
    	progressDialog = ProgressDialog.show(SearchActivity.this,
        		"Please wait...", "Retrieving data...", true, true);
		
		if (newSearch) {
			// reset
			lastItem = 0;
			totalItemCount = 0;
		}
		
		// Get shared client
		AWSECommerceServicePortType_SOAPClient client = AWSECommerceClient.getSharedClient();
		client.setDebug(true);
		
		// Build request
		ItemSearch request = new ItemSearch();
		request.associateTag = "tag"; // seems any tag is ok
		request.shared = new ItemSearchRequest();
		request.shared.searchIndex = "Books";
		request.shared.responseGroup = new ArrayList<String>();
		request.shared.responseGroup.add("Images");
		request.shared.responseGroup.add("ItemAttributes");
		request.shared.responseGroup.add("Offers");
		request.shared.keywords = searchKeywords;
		request.shared.itemPage = BigInteger.valueOf(pageNum);
		
		// authenticate the request
        // http://docs.aws.amazon.com/AWSECommerceService/latest/DG/NotUsingWSSecurity.html
		AWSECommerceClient.authenticateRequest("ItemSearch");
		
		// make API call
		client.itemSearch(request, new SOAPServiceCallback<ItemSearchResponse>() {

			@Override
			public void onSuccess(ItemSearchResponse responseObject) { // handle successful response
	    		if (progressDialog != null) {
    			    progressDialog.dismiss();
    			    progressDialog = null;
    		    }
				
	    		// success handling logic
	    		if (responseObject.items != null && responseObject.items.size() > 0) {
	    			Items items = responseObject.items.get(0);
	    			if (items.item != null && items.item.size() > 0) {
		    			if (newSearch) {
							ItemAdapter itemAdapter = new ItemAdapter(SearchActivity.this, R.layout.row, items.item);
							SearchActivity.this.setListAdapter(itemAdapter);
		    			} else {
		            		ItemAdapter itemAdapter = (ItemAdapter) SearchActivity.this.getListAdapter();
		            		itemAdapter.items.addAll(items.item);
		            		itemAdapter.notifyDataSetChanged();
		    			}
	    			} else {
						Toast.makeText(SearchActivity.this, "No result", Toast.LENGTH_LONG).show();
	    			}
	    		} else  { // response resident error
					if (responseObject.operationRequest != null && responseObject.operationRequest.errors != null) {
						Errors errors = responseObject.operationRequest.errors;
						if (errors.error != null && errors.error.size() > 0) {
							com.amazon.webservices.awsecommerceservice._2011_08_01.errors.Error error = errors.error.get(0);
							Toast.makeText(SearchActivity.this, error.message, Toast.LENGTH_LONG).show();
						} else {
							Toast.makeText(SearchActivity.this, "No result", Toast.LENGTH_LONG).show();
						}
					} else {
						Toast.makeText(SearchActivity.this, "No result", Toast.LENGTH_LONG).show();
					}
	    		}
			}

			@Override
			public void onFailure(Throwable error, String errorMessage) { // HTTP or parsing error
	    		if (progressDialog != null) {
    			    progressDialog.dismiss();
    			    progressDialog = null;
    		    }
				
				ALog.e(TAG, errorMessage);
				Toast.makeText(SearchActivity.this, errorMessage, Toast.LENGTH_LONG).show();
			}

			@Override
			public void onSOAPFault(Object soapFault) { // soap fault
				
	    		if (progressDialog != null) {
    			    progressDialog.dismiss();
    			    progressDialog = null;
    		    }
				
				com.leansoft.nano.soap11.Fault fault = (com.leansoft.nano.soap11.Fault)soapFault;
				
				ALog.e(TAG, fault.faultstring);
				
				Toast.makeText(SearchActivity.this, fault.faultstring, Toast.LENGTH_LONG).show();	
				
			}
			
		});
		
	}
	
	private class ItemAdapter extends ArrayAdapter<Item> {

		private List<Item> items;

		public ItemAdapter(Context context, int textViewResourceId,
				List<Item> items) {
			super(context, textViewResourceId, items);
			this.items = items;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.row, null);
			}

			final Item item = items.get(position);
			if (item != null) {
				TextView title = (TextView) v.findViewById(R.id.title);

				if (title != null) {
					title.setText(item.itemAttributes.title);
				}

				WebImageView image = (WebImageView) v
						.findViewById(R.id.gallery_icon);
				if (image != null) {
					if (item.smallImage.url != null) {
						image.setImageUrl(item.smallImage.url);
						image.loadImage();
					} else {
						image.setNoImageDrawable(R.drawable.placeholder);
					}
				}
				
				// once clicked, navigate to item details page
				v.setOnClickListener(new OnItemClickListener(item, v.getContext()));
			}

			return v;
		}
	}
	
	private class OnItemClickListener implements OnClickListener {
		private Item item;
		private Context mCxt;

		OnItemClickListener(Item item, Context cxt) {
			this.item = item;
			mCxt = cxt;
		}

		@Override
		public void onClick(View arg0) {
			ALog.d(TAG, "onItemClick at item " + item.itemAttributes.title);
			Intent intent = new Intent(mCxt, DetailsActivity.class);
			intent.putExtra("ITEM", item);
			startActivity(intent);
		}
	}
	
	// for dynamic pagination
	@Override
	public void onScroll(AbsListView view, int firstVisible, int visibleCount,
			int totalCount) {

		lastItem = firstVisible + visibleCount;
		totalItemCount = totalCount;
	}

	// for dynamic pagination
	@Override
	public void onScrollStateChanged(AbsListView arg0, int scrollState) {
		if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
			
			boolean loadMore = lastItem >= totalItemCount;
			
			if (loadMore) {
				int nextPage = totalItemCount / DEFAULT_ENTRIES_PER_PAGE + 1;
				searchItemsByKeywords(nextPage, DEFAULT_ENTRIES_PER_PAGE, false);
				Toast.makeText(this, "Loading more ...",
						Toast.LENGTH_SHORT).show();
			}
			
		}
		
	}

}
