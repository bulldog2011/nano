package com.leansoft.nano.demo;

import java.util.ArrayList;

import com.ebay.shopping.ShoppingServiceClient;
import com.ebay.util.eBayUtil;
import com.github.droidfu.widgets.WebImageView;
import com.leansoft.nano.custom.types.Duration;
import com.leansoft.nano.log.ALog;
import com.leansoft.nano.ws.SOAPServiceCallback;
import com.leansoft.nano.ws.XMLServiceCallback;

import com.ebay.shopping.api.AckCodeType;
import com.ebay.shopping.api.BuyerPaymentMethodCodeType;
import com.ebay.shopping.api.GetSingleItemRequestType;
import com.ebay.shopping.api.GetSingleItemResponseType;
import com.ebay.shopping.api.ListingTypeCodeType;
import com.ebay.shopping.api.SimpleItemType;
import com.ebay.shopping.api.client.ShoppingInterface_XMLClient;
import com.ebay.trading.TradingServiceClient;
import com.ebay.trading.api.AddToWatchListRequestType;
import com.ebay.trading.api.AddToWatchListResponseType;
import com.ebay.trading.api.client.EBayAPIInterface_SOAPClient;

import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;

public class DetailActivity extends Activity {
	
	private static final String TAG = DetailActivity.class
			.getSimpleName();
	
	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.item_detail);
		
		String itemId = getIntent().getStringExtra("ITEM_ID");
		this.getSingleItem(itemId);
	}
	
	// call Shopping GetSingleItem API
	private void getSingleItem(final String itemId) {
		
    	progressDialog = ProgressDialog.show(DetailActivity.this,
        		"Please wait...", "Retrieving data...", true, true);
		
		GetSingleItemRequestType request = new GetSingleItemRequestType();
		request.itemID = itemId;
		request.includeSelector = "Details,ShippingCosts";
		
		ShoppingInterface_XMLClient client = ShoppingServiceClient.getSharedClient();
		client.setDebug(true);
		
		client.getSingleItem(request, new XMLServiceCallback<GetSingleItemResponseType>() {

			@Override
			public void onSuccess(GetSingleItemResponseType responseObject) {
	    		if (progressDialog != null) {
	    			progressDialog.dismiss();
	    			progressDialog = null;
	    		}
	    		
	    		// need more error handling logic in real app
				if (responseObject.ack != AckCodeType.FAILURE) {
		            SimpleItemType item = responseObject.item;
		            
		            DetailActivity.this.updateUI(item);
		            
				} else { // response resident error
					String errorMessage = responseObject.errors.get(0).longMessage;
					ALog.e(TAG, errorMessage);
		            Toast.makeText(getApplicationContext(),
		            		errorMessage,
		                    Toast.LENGTH_LONG).show();
				}
				
			}

			@Override
			public void onFailure(Throwable error, String errorMessage) {
	    		if (progressDialog != null) {
	    			progressDialog.dismiss();
	    			progressDialog = null;
	    		}
				
	    		
				// handle HTTP or parsing error
				if (errorMessage != null) {
					ALog.e(TAG, errorMessage);
					Toast.makeText(DetailActivity.this, errorMessage, Toast.LENGTH_LONG).show();
				} else {
					ALog.e(TAG, error.getMessage(), error);
					Toast.makeText(DetailActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
				}
			}


			
		});
	}
	
	private void updateUI(final SimpleItemType item) {
		
		// item title
		TextView titleView = (TextView) findViewById(R.id.detail_title);
		titleView.setText(item.title);
		
		// item image
		WebImageView image = (WebImageView) findViewById(R.id.detail_image);
		if (item.pictureURL != null && item.pictureURL.size() > 0) {
			image.setImageUrl(item.pictureURL.get(0));
			image.loadImage();
		} else {
			image.setNoImageDrawable(R.drawable.placeholder);
		}
		image.setVisibility(View.VISIBLE);
		
		// item id
		TextView itemIdView = (TextView) findViewById(R.id.detail_itemid);
		itemIdView.setText(Html.fromHtml("<b>Item ID:</b>&nbsp;&nbsp;"
				+ item.itemID));
		
		// start time
		TextView startTimeView = (TextView) findViewById(R.id.detail_starttime);
		startTimeView.setText(Html.fromHtml("<b>Start Time:</b>&nbsp;&nbsp;"
				+ item.startTime.toLocaleString()));

		// end time
		TextView endTimeView = (TextView) findViewById(R.id.detail_endtime);
		endTimeView.setText(Html.fromHtml("<b>End Time:</b>&nbsp;&nbsp;"
				+ item.endTime.toLocaleString()));

		// condition
		TextView conditionView = (TextView) findViewById(R.id.detail_condition);
		String conditionDisplayName = "NA";
		if (item.conditionDisplayName != null) {
			conditionDisplayName = item.conditionDisplayName;
		}
		conditionView.setText(Html.fromHtml("<b>Condition:</b>&nbsp;&nbsp;"
				+ conditionDisplayName));
		
		// item price
		TextView priceView = (TextView) findViewById(R.id.detail_price);
		String currencyStr = (item.convertedCurrentPrice.currencyID == null ? ""
				: item.convertedCurrentPrice.currencyID.name());
		String Price = "";
		if (item.listingType == ListingTypeCodeType.FIXED_PRICE_ITEM
				|| item.listingType== ListingTypeCodeType.STORES_FIXED_PRICE) {
			Price = "Buy It Now";
		} else {
			Price = "Current Bid";
		}
		priceView.setText(Html.fromHtml("<b>"
				+ Price
				+ ":</b>&nbsp;&nbsp;"
				+ eBayUtil.formatCurrencyToString(
						item.convertedCurrentPrice.value, currencyStr)));
		
		// shipping cost
		String shippingCost = "NA";
		if (item.shippingCostSummary != null
				&& item.shippingCostSummary.shippingServiceCost != null) {
			currencyStr = (item.shippingCostSummary.shippingServiceCost.currencyID == null ? ""
					: item.shippingCostSummary.shippingServiceCost.currencyID
							.name());
			shippingCost = eBayUtil.formatCurrencyToString(
					item.shippingCostSummary.shippingServiceCost.value,
					currencyStr);
		}
		TextView shippingCostView = (TextView) findViewById(R.id.detail_shipping);
		shippingCostView.setText(Html
				.fromHtml("<b>Shipping Cost:</b>&nbsp;&nbsp;" + shippingCost));

		// item location
		TextView locationView = (TextView) findViewById(R.id.detail_location);
		locationView.setText(Html.fromHtml("<b>Location</b>&nbsp;&nbsp;"
				+ item.location));

		// listing type
		TextView listingTypeView = (TextView) findViewById(R.id.detail_listingtype);
		listingTypeView
				.setText(Html.fromHtml("<b>Listing Type:</b>&nbsp;&nbsp;"
						+ item.listingType));
		
		// time left
		TextView timeLeftView = (TextView) findViewById(R.id.detail_timeleft);
		Duration duration = item.timeLeft;
		if (duration.getDays() == 0 && duration.getHours() == 0
				&& duration.getMinutes() < 10) {
			timeLeftView.setTextColor(Color.RED);
		} else {
			timeLeftView.setTextColor(Color.BLACK);
		}

		timeLeftView.setText(Html.fromHtml("<b>Time Left:</b>&nbsp;&nbsp;"
				+ eBayUtil.formatDuration(item.timeLeft)));
		
		// payment method
		TextView paymentView = (TextView) findViewById(R.id.detail_payment);
		String payments = "";
		for (BuyerPaymentMethodCodeType payment : item.paymentMethods) {
			payments += "," + payment;
		}
		payments = payments.replaceFirst(",", "");
		paymentView.setText(Html.fromHtml("<b>Payment Method:</b>&nbsp;&nbsp;"
				+ payments));
		
		// view on eBay
		Button viewBtn = (Button) findViewById(R.id.btn_view);
		viewBtn.setOnClickListener(new ViewOneBayListener(
				item.viewItemURLForNaturalSearch));
		viewBtn.setVisibility(View.VISIBLE);
		
		// watch item
		final Button watchBtn = (Button) findViewById(R.id.btn_watch);
		watchBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				addToWatchList(item.itemID);
			}
			
		});
		watchBtn.setVisibility(View.VISIBLE);
	}

	// view item on eBay moile web
	private class ViewOneBayListener implements View.OnClickListener {
		
		private String itemUrl;
		
		public ViewOneBayListener(String itemUrl) {
			this.itemUrl = itemUrl;
		}
		
		@Override
		public void onClick(View arg0) {
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(itemUrl));
			startActivity(browserIntent);
		
		}
		
	}
	
	// call eBay Trading addToWatchList API
	private void addToWatchList(String itemId) {
    	progressDialog = ProgressDialog.show(DetailActivity.this,
        		"Please wait...", "Adding to watch...", true, true);
    	
    	AddToWatchListRequestType  request = new AddToWatchListRequestType ();
    	request.itemID = new ArrayList<String>();
    	request.itemID.add(itemId);
    	
    	EBayAPIInterface_SOAPClient client = TradingServiceClient.getSharedClient();
    	client.setDebug(true);
    	
    	client.addToWatchList(request, new SOAPServiceCallback<AddToWatchListResponseType>() {

			@Override
			public void onFailure(Throwable error, String errorMessage) {
			    if (progressDialog != null) {
			    	progressDialog.dismiss();
			        progressDialog = null;
			    }
				if (errorMessage != null) {
					Toast.makeText(DetailActivity.this, errorMessage, Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(DetailActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
				}
				
			}

			@Override
			public void onSuccess(AddToWatchListResponseType responseObject) {
			   if (progressDialog != null) {
				   progressDialog.dismiss();
			       progressDialog = null;
			   }

			   // need more error handling logic in real app
			   if (responseObject.ack != com.ebay.trading.api.AckCodeType.FAILURE) {
			    	Toast.makeText(DetailActivity.this,
			                       "Item was added to watch list successfully",
			        Toast.LENGTH_LONG).show();
			   } else {
			    	String errorMessage = responseObject.errors.get(0).longMessage;
			        ALog.e(TAG, errorMessage);
			        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
			   }
				
			}

			@Override
			public void onSOAPFault(Object soapFault) {
			   if (progressDialog != null) {
				   progressDialog.dismiss();
			       progressDialog = null;
			   }
				
			   com.leansoft.nano.soap11.Fault fault = (com.leansoft.nano.soap11.Fault)soapFault;
			   Toast.makeText(DetailActivity.this, fault.faultstring, Toast.LENGTH_LONG).show();	
				
			}
    		
    	});
    	
	}

}
