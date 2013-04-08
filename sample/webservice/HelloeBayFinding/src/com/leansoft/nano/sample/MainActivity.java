package com.leansoft.nano.sample;

import com.ebay.marketplace.search.v1.services.AckValue;
import com.ebay.marketplace.search.v1.services.ErrorData;
import com.ebay.marketplace.search.v1.services.FindItemsByKeywordsRequest;
import com.ebay.marketplace.search.v1.services.FindItemsByKeywordsResponse;
import com.ebay.marketplace.search.v1.services.PaginationInput;
import com.ebay.marketplace.search.v1.services.SearchItem;
import com.ebay.marketplace.search.v1.services.client.FindingServicePortType_SOAPClient;
import com.ebay.service.finding.FindingServiceClient;
import com.leansoft.nano.soap12.Reasontext;
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
				FindingServicePortType_SOAPClient client = FindingServiceClient.getSharedClient();
				client.setDebug(true); // enable soap message logging
				
				// Build request
				FindItemsByKeywordsRequest request = new FindItemsByKeywordsRequest();
				String keywords = ((EditText)findViewById(R.id.keyword_input)).getText().toString();
				request.keywords = keywords;
				PaginationInput pi = new PaginationInput();
				pi.pageNumber = 1;
				pi.entriesPerPage = 1;
				request.paginationInput = pi;
				
				// Make API call and register callbacks
				client.findItemsByKeywords(request, new SOAPServiceCallback<FindItemsByKeywordsResponse>() {

					@Override
					public void onSuccess(
							FindItemsByKeywordsResponse responseObject) {
						
						if (AckValue.SUCCESS == responseObject.ack) {
							if (responseObject.searchResult != null && responseObject.searchResult.count > 0) {
								// show the title of the first found item
								SearchItem item = responseObject.searchResult.item.get(0);
								Toast.makeText(MainActivity.this, item.title, Toast.LENGTH_LONG).show();
							} else { // no result
								Toast.makeText(MainActivity.this, "No result", Toast.LENGTH_LONG).show();
							}
						} else { // response resident error
							ErrorData errorData = responseObject.errorMessage.error.get(0);
							Toast.makeText(MainActivity.this, errorData.message, Toast.LENGTH_LONG).show();
						}
					}

					@Override
					public void onFailure(Throwable error, String errorMessage) { // http or parsing error
						Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_LONG).show();
					}

					@Override
					public void onSOAPFault(Object soapFault) { // soap fault
						com.leansoft.nano.soap12.Fault fault = (com.leansoft.nano.soap12.Fault)soapFault;
						Reasontext reasonText = fault.reason.text.get(0);
						Toast.makeText(MainActivity.this, reasonText.value, Toast.LENGTH_LONG).show();
					}
					
				});
			}
			
		});
	}
}
