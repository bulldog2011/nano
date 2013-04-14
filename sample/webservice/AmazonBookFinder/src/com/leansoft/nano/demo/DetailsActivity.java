package com.leansoft.nano.demo;

import java.math.BigInteger;
import java.util.ArrayList;

import com.amazon.service.ecommerce.AWSECommerceClient;
import com.amazon.webservices.awsecommerceservice._2011_08_01.Cart;
import com.amazon.webservices.awsecommerceservice._2011_08_01.CartCreate;
import com.amazon.webservices.awsecommerceservice._2011_08_01.CartCreateRequest;
import com.amazon.webservices.awsecommerceservice._2011_08_01.CartCreateResponse;
import com.amazon.webservices.awsecommerceservice._2011_08_01.Errors;
import com.amazon.webservices.awsecommerceservice._2011_08_01.Item;
import com.amazon.webservices.awsecommerceservice._2011_08_01.Offer;
import com.amazon.webservices.awsecommerceservice._2011_08_01.OfferListing;
import com.amazon.webservices.awsecommerceservice._2011_08_01.Offers;
import com.amazon.webservices.awsecommerceservice._2011_08_01.cartcreaterequest.Items;
import com.amazon.webservices.awsecommerceservice._2011_08_01.client.AWSECommerceServicePortType_SOAPClient;
import com.github.droidfu.widgets.WebImageView;
import com.leansoft.nano.log.ALog;
import com.leansoft.nano.ws.SOAPServiceCallback;

import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;

public class DetailsActivity extends Activity {
	
	private static final String TAG = DetailsActivity.class
			.getSimpleName();
	
	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.item_detail);
		
		Item item = (Item) getIntent().getSerializableExtra("ITEM");
		this.updateUI(item);
	}
	
	private void updateUI(Item item) {
		// item title
		TextView titleView = (TextView) findViewById(R.id.detail_title);
		titleView.setText(item.itemAttributes.title);
		
		// list price
		TextView priceView = (TextView) findViewById(R.id.detail_price);
		if (item.itemAttributes.listPrice != null) {
			priceView.setText(Html.fromHtml("<b>List Price : </b>&nbsp;&nbsp;" + item.itemAttributes.listPrice.formattedPrice));
			priceView.setTextColor(Color.BLUE);
		}
		
		// author
		TextView authorView = (TextView) findViewById(R.id.detail_author);
		authorView.setText(Html.fromHtml("<b>Author : </b>&nbsp;&nbsp;" + item.itemAttributes.author.get(0)));
		
		// edition
		TextView bindingView = (TextView) findViewById(R.id.detail_binding);
		bindingView.setText(Html.fromHtml("<b>Binding : </b>&nbsp;&nbsp;" + item.itemAttributes.binding));
		
		// edition
		TextView editionView = (TextView) findViewById(R.id.detail_edition);
		editionView.setText(Html.fromHtml("<b>Edition : </b>&nbsp;&nbsp;" + item.itemAttributes.edition));
		
		// publisher
		TextView publisherView = (TextView) findViewById(R.id.detail_publisher);
		publisherView.setText(Html.fromHtml("<b>Publisher : </b>&nbsp;&nbsp;" + item.itemAttributes.publisher));
		
		// pubdate
		TextView pubdateView = (TextView) findViewById(R.id.detail_pubdate);
		pubdateView.setText(Html.fromHtml("<b>Publish Date : </b>&nbsp;&nbsp;" + item.itemAttributes.publicationDate));
		
		// item image
		WebImageView image = (WebImageView) findViewById(R.id.detail_image);
		if (item.mediumImage != null && item.mediumImage.url != null) {
			image.setImageUrl(item.mediumImage.url);
			image.loadImage();
		} else {
			image.setNoImageDrawable(R.drawable.placeholder);
		}
		image.setVisibility(View.VISIBLE);
		
		// add item to shopping cart
		final Button addBtn = (Button) findViewById(R.id.btn_add);
		addBtn.setOnClickListener(new AddToCartListener(item));
		addBtn.setVisibility(View.VISIBLE);
	}
	
	private class AddToCartListener implements OnClickListener {
		private Item item;
		
		public AddToCartListener(Item item) {
			this.item = item;
		}

		@Override
		public void onClick(View v) {
			addToCart(item);
		}
	}
	
	public void addToCart(Item item) {
		if (!this.checkAvailibility(item)) {
			Toast.makeText(DetailsActivity.this, "Can't add to cart, item not available", Toast.LENGTH_LONG).show();
			return;
		}
		
    	progressDialog = ProgressDialog.show(DetailsActivity.this,
        		"Please wait...", "Retrieving data...", true, true);
    	
		// Get shared client
		AWSECommerceServicePortType_SOAPClient client = AWSECommerceClient.getSharedClient();
		client.setDebug(true);
		
		// create request
		CartCreate request = new CartCreate();
		request.associateTag = "tag";
		
		CartCreateRequest cartCreateRequest = new CartCreateRequest();
		cartCreateRequest.items = new Items();
		cartCreateRequest.items.item = new ArrayList<com.amazon.webservices.awsecommerceservice._2011_08_01.cartcreaterequest.items.Item>();
		com.amazon.webservices.awsecommerceservice._2011_08_01.cartcreaterequest.items.Item cartCreateItem = 
				    new com.amazon.webservices.awsecommerceservice._2011_08_01.cartcreaterequest.items.Item();
		cartCreateItem.asin = item.asin;
		cartCreateItem.quantity = BigInteger.valueOf(1);
		cartCreateRequest.items.item.add(cartCreateItem);
		
		request.shared = cartCreateRequest;
		request.shared.responseGroup = new ArrayList<String>();
		request.shared.responseGroup.add("Cart");
		
	    // authenticate the request
	    // see : http://docs.aws.amazon.com/AWSECommerceService/latest/DG/NotUsingWSSecurity.html
		AWSECommerceClient.authenticateRequest("CartCreate");
		// make API call with registered callbacks
		client.cartCreate(request, new SOAPServiceCallback<CartCreateResponse>() {

			@Override
			public void onSuccess(CartCreateResponse responseObject) {
	    		if (progressDialog != null) {
    			    progressDialog.dismiss();
    			    progressDialog = null;
    		    }
	    		
	    		Cart cart = responseObject.cart.get(0);
	    		if (cart != null) {
	    			if ("True".equals(cart.request.isValid)) {
	    				
	    				// Purchase on Amazon
	    				Button purchaseBtn = (Button) findViewById(R.id.btn_purchase);
	    				purchaseBtn.setOnClickListener(new PurchaseOnAmazonListener(
	    						cart.purchaseURL));
	    				purchaseBtn.setVisibility(View.VISIBLE);
	    				
	    				Toast.makeText(DetailsActivity.this, "Item was added to your shopping cart successfully", Toast.LENGTH_LONG).show();
	    				
	    			} else {
	    				Toast.makeText(DetailsActivity.this, "Invalid request", Toast.LENGTH_LONG).show();
	    			}
	    		} else { // response resident error
					if (responseObject.operationRequest != null && responseObject.operationRequest.errors != null) {
						Errors errors = responseObject.operationRequest.errors;
						if (errors.error != null && errors.error.size() > 0) {
							com.amazon.webservices.awsecommerceservice._2011_08_01.errors.Error error = errors.error.get(0);
							Toast.makeText(DetailsActivity.this, error.message, Toast.LENGTH_LONG).show();
						} else {
							Toast.makeText(DetailsActivity.this, "No result", Toast.LENGTH_LONG).show();
						}
					} else {
						Toast.makeText(DetailsActivity.this, "No result", Toast.LENGTH_LONG).show();
					}
	    		}
				
			}

			@Override
			public void onFailure(Throwable error, String errorMessage) { // http or parse error
	    		if (progressDialog != null) {
    			    progressDialog.dismiss();
    			    progressDialog = null;
    		    }
				
				ALog.e(TAG, errorMessage);
				Toast.makeText(DetailsActivity.this, errorMessage, Toast.LENGTH_LONG).show();
			}

			@Override
			public void onSOAPFault(Object soapFault) { // soap fault
	    		if (progressDialog != null) {
    			    progressDialog.dismiss();
    			    progressDialog = null;
    		    }
				
				com.leansoft.nano.soap11.Fault fault = (com.leansoft.nano.soap11.Fault)soapFault;
				
				ALog.e(TAG, fault.faultstring);
				
				Toast.makeText(DetailsActivity.this, fault.faultstring, Toast.LENGTH_LONG).show();	
			}
			
		});
	}
	
	public boolean checkAvailibility(Item item) {
		Offers offers = item.offers;
		if (offers.totalOffers.longValue() > 0) {
			Offer offer = offers.offer.get(0);
			OfferListing offerListing = offer.offerListing.get(0);
			if (offerListing.availability != null) {
				return true;
			}
		}
		
		return false;
	}
	
	// Purchase on Amazon Mobile Web
	private class PurchaseOnAmazonListener implements View.OnClickListener {
		
		private String purchaseUrl;
		
		public PurchaseOnAmazonListener(String purchaseUrl) {
			this.purchaseUrl = purchaseUrl;
		}
		
		@Override
		public void onClick(View arg0) {
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(purchaseUrl));
			startActivity(browserIntent);
		
		}
		
	}

}
