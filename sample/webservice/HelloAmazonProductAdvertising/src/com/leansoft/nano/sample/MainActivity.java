package com.leansoft.nano.sample;

import java.util.ArrayList;

import com.amazon.service.ecommerce.AWSECommerceClient;
import com.amazon.webservices.awsecommerceservice._2011_08_01.Errors;
import com.amazon.webservices.awsecommerceservice._2011_08_01.Item;
import com.amazon.webservices.awsecommerceservice._2011_08_01.ItemSearch;
import com.amazon.webservices.awsecommerceservice._2011_08_01.ItemSearchRequest;
import com.amazon.webservices.awsecommerceservice._2011_08_01.ItemSearchResponse;
import com.amazon.webservices.awsecommerceservice._2011_08_01.Items;
import com.amazon.webservices.awsecommerceservice._2011_08_01.client.AWSECommerceServicePortType_SOAPClient;
import com.leansoft.nano.ws.SOAPServiceCallback;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Button searchButton = (Button) this.findViewById(R.id.search_button);
		
		searchButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// Get shared client
				AWSECommerceServicePortType_SOAPClient client = AWSECommerceClient.getSharedClient();
				client.setDebug(true);
				
				// Build request
				ItemSearch request = new ItemSearch();
				request.associateTag = "teg"; // seems any tag is ok
				request.shared = new ItemSearchRequest();
				request.shared.searchIndex = "Books";
				request.shared.responseGroup = new ArrayList<String>();
				request.shared.responseGroup.add("Images");
				request.shared.responseGroup.add("Small");
				
				ItemSearchRequest itemSearchRequest = new ItemSearchRequest();
				itemSearchRequest.title = ((EditText)findViewById(R.id.keyword_input)).getText().toString();
				request.request = new ArrayList<ItemSearchRequest>();
				request.request.add(itemSearchRequest);
				
				// authenticate the request
		        // http://docs.aws.amazon.com/AWSECommerceService/latest/DG/NotUsingWSSecurity.html
				AWSECommerceClient.authenticateRequest("ItemSearch");
				// Make API call and register callbacks
				client.itemSearch(request, new SOAPServiceCallback<ItemSearchResponse>() {

					@Override
					public void onSuccess(ItemSearchResponse responseObject) {
						// success handling logic
						if (responseObject.items != null && responseObject.items.size() > 0) {
							Items items = responseObject.items.get(0);
							if (items.item != null && items.item.size() > 0) {
								Item item = items.item.get(0);
								Toast.makeText(MainActivity.this, item.itemAttributes.title, Toast.LENGTH_LONG).show();
							} else {
								Toast.makeText(MainActivity.this, "No result", Toast.LENGTH_LONG).show();
							}
							
						} else {
							if (responseObject.operationRequest != null && responseObject.operationRequest.errors != null) {
								Errors errors = responseObject.operationRequest.errors;
								if (errors.error != null && errors.error.size() > 0) {
									com.amazon.webservices.awsecommerceservice._2011_08_01.errors.Error error = errors.error.get(0);
									Toast.makeText(MainActivity.this, error.message, Toast.LENGTH_LONG).show();
								} else {
									Toast.makeText(MainActivity.this, "No result", Toast.LENGTH_LONG).show();
								}
							} else {
								Toast.makeText(MainActivity.this, "No result", Toast.LENGTH_LONG).show();
							}
						}
						
					}

					@Override
					public void onFailure(Throwable error, String errorMessage) { // http or parsing error
						Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_LONG).show();
					}

					@Override
					public void onSOAPFault(Object soapFault) { // soap fault
						com.leansoft.nano.soap11.Fault fault = (com.leansoft.nano.soap11.Fault)soapFault;
						Toast.makeText(MainActivity.this, fault.faultstring, Toast.LENGTH_LONG).show();	
					}
					
				});
			}
			
		});
	}


}
