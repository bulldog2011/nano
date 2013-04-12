package com.leansoft.nano.demo;

import java.util.ArrayList;
import java.util.List;

import com.ebay.finding.FindingServiceClient;
import com.ebay.util.eBayUtil;
import com.ebay.finding.api.AckValue;
import com.ebay.finding.api.Amount;
import com.ebay.finding.api.FindItemsByKeywordsRequest;
import com.ebay.finding.api.FindItemsByKeywordsResponse;
import com.ebay.finding.api.ItemFilter;
import com.ebay.finding.api.ItemFilterType;
import com.ebay.finding.api.PaginationInput;
import com.ebay.finding.api.SearchItem;
import com.ebay.finding.api.SearchResult;
import com.ebay.finding.api.client.FindingServicePortType_SOAPClient;
import com.github.droidfu.widgets.WebImageView;
import com.leansoft.nano.custom.types.Duration;
import com.leansoft.nano.log.ALog;
import com.leansoft.nano.soap12.Reasontext;
import com.leansoft.nano.ws.SOAPServiceCallback;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

public class FindingActivity extends ListActivity implements OnScrollListener {
	
	private static final String TAG = FindingActivity.class
			.getSimpleName();

	
	static final int DEFAULT_ENTRIES_PER_PAGE = 7;
	
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
				findItemsByKeywords(1, DEFAULT_ENTRIES_PER_PAGE, true);
			}
		});
		
	}


	// asynchronously trigger findItemsByKeywords call
	private void findItemsByKeywords(int pageNum, int entriesPerPage, final boolean newSearch) {
		
    	progressDialog = ProgressDialog.show(FindingActivity.this,
        		"Please wait...", "Retrieving data...", true, true);
		
		if (newSearch) {
			// reset
			lastItem = 0;
			totalItemCount = 0;
		}
		
		// build a request object
		FindItemsByKeywordsRequest request = new FindItemsByKeywordsRequest();
		request.keywords = searchKeywords;
		PaginationInput pi = new PaginationInput();
        pi.pageNumber = pageNum;
        pi.entriesPerPage = entriesPerPage;
        request.paginationInput = pi;
        
		// show fixed price and auction item only
		ItemFilter itemFilter = new ItemFilter();
		itemFilter.name = ItemFilterType.LISTING_TYPE;
		itemFilter.value = new ArrayList<String>();
		itemFilter.value.add("FixedPrice");
		itemFilter.value.add("Auction");
		request.itemFilter = new ArrayList<ItemFilter>();
		request.itemFilter.add(itemFilter);
		
		// Get shared client
		FindingServicePortType_SOAPClient client = FindingServiceClient.getSharedClient();
		client.setDebug(true);
		
		// make API call and register callbacks
		client.findItemsByKeywords(request, new SOAPServiceCallback<FindItemsByKeywordsResponse>() {

			@Override
			public void onSuccess(FindItemsByKeywordsResponse responseObject) {
	    		if (progressDialog != null) {
    			    progressDialog.dismiss();
    			    progressDialog = null;
    		    }
				
				if (responseObject.ack == AckValue.SUCCESS) { // handle sucessful response
		            SearchResult searchResult = responseObject.searchResult;
		            if (newSearch) {
			            List<SearchItem> items = (searchResult.item != null) ? searchResult.item : new ArrayList<SearchItem>();
						ItemAdapter itemAdapter = new ItemAdapter(FindingActivity.this, R.layout.row, items);
			            FindingActivity.this.setListAdapter(itemAdapter);
		            } else {
		            	if (searchResult.item != null) {
		            		ItemAdapter itemAdapter = (ItemAdapter) FindingActivity.this.getListAdapter();
		            		itemAdapter.items.addAll(searchResult.item);
		            		itemAdapter.notifyDataSetChanged();
		            	}
		            }
	            
				} else { // handle response resident error
					String errorMessage = responseObject.errorMessage.error.get(0).message;
					ALog.e(TAG, errorMessage);
		            Toast.makeText(getApplicationContext(),
		            		errorMessage,
		                    Toast.LENGTH_LONG).show();
				}
			}

			@Override
			public void onFailure(Throwable error, String errorMessage) { // http or parsing error
	    		if (progressDialog != null) {
    			    progressDialog.dismiss();
    			    progressDialog = null;
    		    }
				
				Toast.makeText(FindingActivity.this, errorMessage, Toast.LENGTH_LONG).show();
				
			}

			@Override
			public void onSOAPFault(Object soapFault) { // soap fault
	    		if (progressDialog != null) {
    			    progressDialog.dismiss();
    			    progressDialog = null;
    		    }
			
			    com.leansoft.nano.soap12.Fault fault = (com.leansoft.nano.soap12.Fault)soapFault;
				Reasontext reasonText = fault.reason.text.get(0);
				
				ALog.e(TAG, reasonText.value);
				
				Toast.makeText(FindingActivity.this, reasonText.value, Toast.LENGTH_LONG).show();
				
			}
			
		});
	}
	
	private class ItemAdapter extends ArrayAdapter<SearchItem> {

		private List<SearchItem> items;

		public ItemAdapter(Context context, int textViewResourceId,
				List<SearchItem> items) {
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

			final SearchItem item = items.get(position);
			if (item != null) {
				TextView title = (TextView) v.findViewById(R.id.title);
				TextView price = (TextView) v.findViewById(R.id.price);
				TextView bidType = (TextView) v.findViewById(R.id.bids);
				TextView timeleft = (TextView) v.findViewById(R.id.timeleft);

				if (title != null) {
					title.setText(item.title);
				}

				Amount convertedCurrentPrice = item.sellingStatus.convertedCurrentPrice;
				price.setText(eBayUtil.formatCurrencyToString(
						convertedCurrentPrice.value,
						convertedCurrentPrice.currencyId));

				
				String listingType = item.listingInfo.listingType;
				if (listingType.equalsIgnoreCase("FixedPrice")
						|| listingType.equalsIgnoreCase("StoreInventory")) {
					bidType.setBackgroundResource(R.drawable.bin_clear);
					bidType.setText("");
				} else {
					bidType.setBackgroundResource(R.drawable.light_blue_pixel);
					bidType.setText(item.sellingStatus.bidCount + " bids");
				}

				Duration duration = item.sellingStatus.timeLeft;
				if (duration.getDays() == 0 && duration.getHours() == 0
						&& duration.getMinutes() < 10) {
					timeleft.setTextColor(Color.RED);
				} else {
					timeleft.setTextColor(Color.BLACK);
				}

				timeleft.setText(eBayUtil.formatDuration(duration));

				WebImageView image = (WebImageView) v
						.findViewById(R.id.gallery_icon);
				if (image != null) {
					if (item.galleryURL != null) {
						image.setImageUrl(item.galleryURL);
						image.loadImage();
					} else {
						image.setNoImageDrawable(R.drawable.placeholder);
					}
				}
				
				// once clicked, navigate to item details page
				v.setOnClickListener(new OnItemClickListener(item.itemId, v.getContext()));
			}

			return v;
		}
	}
	
	private class OnItemClickListener implements OnClickListener {
		private String mItemId;
		private Context mCxt;

		OnItemClickListener(String itemId, Context cxt) {
			mItemId = itemId;
			mCxt = cxt;
		}

		@Override
		public void onClick(View arg0) {
			ALog.d(TAG, "onItemClick at item " + mItemId);
			Intent intent = new Intent(mCxt, DetailActivity.class);
			intent.putExtra("ITEM_ID", mItemId);
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
				findItemsByKeywords(nextPage, DEFAULT_ENTRIES_PER_PAGE, false);
				Toast.makeText(this, "Loading more ...",
						Toast.LENGTH_SHORT).show();
			}
			
		}
		
	}

}
