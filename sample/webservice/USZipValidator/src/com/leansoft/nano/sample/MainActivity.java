package com.leansoft.nano.sample;

import com.leansoft.nano.soap11.Fault;
import com.leansoft.nano.ws.SOAPServiceCallback;
import com.webservicemart.service.USZipValidatorServiceClient;
import com.webservicemart.ws.ValidateZip;
import com.webservicemart.ws.ValidateZipResponse;
import com.webservicemart.ws.client.USZipSoap_SOAPClient;

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
		
		Button validateButton = (Button) this.findViewById(R.id.validateButton);
		
		validateButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// Get shared client
				USZipSoap_SOAPClient client = USZipValidatorServiceClient.getSharedClient();
				client.setDebug(true);
				
				// Build request
				ValidateZip request = new ValidateZip();
				String zipText = ((EditText)findViewById(R.id.zipText)).getText().toString();
				request.zipCode = zipText;
				
				// make API call and register callbacks
				client.validateZip(request, new SOAPServiceCallback<ValidateZipResponse>() {

					@Override
					public void onSuccess(ValidateZipResponse responseObject) { // success
						Toast.makeText(MainActivity.this, responseObject.validateZipResult, Toast.LENGTH_LONG).show();
					}

					@Override
					public void onFailure(Throwable error, String errorMessage) { // http or parsing error
						Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_LONG).show();
						
					}

					@Override
					public void onSOAPFault(Object soapFault) { // soap fault
						Fault fault = (Fault)soapFault;
						Toast.makeText(MainActivity.this, fault.faultstring, Toast.LENGTH_LONG).show();
						
					}
					
				});
			}
			
		});
	}

}
