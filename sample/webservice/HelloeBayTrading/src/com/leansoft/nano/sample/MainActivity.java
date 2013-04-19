package com.leansoft.nano.sample;

import java.text.SimpleDateFormat;

import com.ebay.api.trading.GeteBayOfficialTimeRequestType;
import com.ebay.api.trading.GeteBayOfficialTimeResponseType;
import com.ebay.api.trading.client.EBayAPIInterface_SOAPClient;
import com.ebay.service.trading.TradingServiceClient;
import com.leansoft.nano.ws.SOAPServiceCallback;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Button geteBayOfficialTimeButton = (Button) this.findViewById(R.id.geteBayOfficialTimeButton);
		
		geteBayOfficialTimeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Get shared client
				EBayAPIInterface_SOAPClient client = TradingServiceClient.getSharedClient();
				client.setDebug(true); // enable soap message logging
				
				// Build request
				GeteBayOfficialTimeRequestType request = new GeteBayOfficialTimeRequestType();
				
				// make API call and register callbacks
				client.geteBayOfficialTime(request, new SOAPServiceCallback<GeteBayOfficialTimeResponseType>() {

					@Override
					public void onSuccess( // success
							GeteBayOfficialTimeResponseType responseObject) {
						SimpleDateFormat dateFormat =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						String officialTimeString = dateFormat.format(responseObject.timestamp);
						Toast.makeText(MainActivity.this, officialTimeString, Toast.LENGTH_LONG).show();
					}

					@Override
					public void onFailure(Throwable error, String errorMessage) { // http or parsing error
						if (errorMessage != null) {
							Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_LONG).show();
						} else {
							Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
						}
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
