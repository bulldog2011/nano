package com.leansoft.nano.sample;

import com.ebay.service.finding.ShoppingServiceClient;
import com.leansoft.nano.ws.ServiceCallback;

import ebay.apis.eblbasecomponents.AckCodeType;
import ebay.apis.eblbasecomponents.ErrorType;
import ebay.apis.eblbasecomponents.FindPopularItemsRequestType;
import ebay.apis.eblbasecomponents.FindPopularItemsResponseType;
import ebay.apis.eblbasecomponents.SimpleItemType;
import ebay.apis.eblbasecomponents.client.ShoppingInterface_XMLClient;

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
				ShoppingInterface_XMLClient client = ShoppingServiceClient.getSharedClient();
				client.setDebug(true); // enable xml message logging
			
				// Build request object
				FindPopularItemsRequestType request = new FindPopularItemsRequestType();
				String keywords = ((EditText)findViewById(R.id.keyword_input)).getText().toString();
				request.queryKeywords = keywords;
				// only need one item for demo
				request.maxEntries = 1;
				
				client.findPopularItems(request, new ServiceCallback<FindPopularItemsResponseType>() {

					@Override
					public void onSuccess(
							FindPopularItemsResponseType responseObject) {

						if (AckCodeType.SUCCESS == responseObject.ack) {
							if (responseObject.itemArray.item.size() > 0) {
								// show the title of first found item
								SimpleItemType item = responseObject.itemArray.item.get(0);
								Toast.makeText(MainActivity.this, item.title, Toast.LENGTH_LONG).show();
							} else { // no result
								Toast.makeText(MainActivity.this, "No result", Toast.LENGTH_LONG).show();
							}
						} else { // response resident error
							ErrorType error = responseObject.errors.get(0);
							Toast.makeText(MainActivity.this, error.shortMessage, Toast.LENGTH_LONG).show();
						}
					}

					@Override
					public void onFailure(Throwable error, String errorMessage) { // http or parsing error
						if (errorMessage != null) {
							Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_LONG).show();
						} else {
							Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
						}
					}
					
				});
			}
			
		});
	}


}
