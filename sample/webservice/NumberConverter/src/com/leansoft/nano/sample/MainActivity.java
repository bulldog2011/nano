package com.leansoft.nano.sample;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.dataaccess.service.NumberConversionServiceClient;
import com.dataaccess.webservicesserver.NumberToDollars;
import com.dataaccess.webservicesserver.NumberToDollarsResponse;
import com.dataaccess.webservicesserver.NumberToWords;
import com.dataaccess.webservicesserver.NumberToWordsResponse;
import com.dataaccess.webservicesserver.client.NumberConversionSoapType_SOAPClient;
import com.leansoft.nano.soap11.Fault;
import com.leansoft.nano.ws.SOAPServiceCallback;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.app.Activity;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Button numberToWordsButton = (Button) this.findViewById(R.id.numToWordsButton);
		
		numberToWordsButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// Get shared client
				NumberConversionSoapType_SOAPClient client = NumberConversionServiceClient.getSharedClient();
				client.setDebug(true); // enable soap message logging
				
				// build request
				NumberToWords request = new NumberToWords();
				try {
					String number = ((EditText)findViewById(R.id.numerInputText)).getText().toString();
					request.ubiNum = new BigInteger(number);
				} catch (NumberFormatException ex) {
					Toast.makeText(MainActivity.this, "Invalid integer number", Toast.LENGTH_LONG).show();
					return;
				}
				
				client.numberToWords(request, new SOAPServiceCallback<NumberToWordsResponse>() {

					@Override
					public void onSuccess(NumberToWordsResponse responseObject) { // success
						Toast.makeText(MainActivity.this, responseObject.numberToWordsResult, Toast.LENGTH_LONG).show();
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
		
		Button numberToDollarsButton = (Button) this.findViewById(R.id.numToDollarsButton);
		
		numberToDollarsButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// Get shared client
				NumberConversionSoapType_SOAPClient client = NumberConversionServiceClient.getSharedClient();
				client.setDebug(true); // enable soap message logging
				
				// build request
				NumberToDollars request = new NumberToDollars();
				try {
					String number = ((EditText)findViewById(R.id.numerInputText)).getText().toString();
					request.dNum = new BigDecimal(number);
				} catch (NumberFormatException ex) {
					Toast.makeText(MainActivity.this, "Invalid decimal number", Toast.LENGTH_LONG).show();
					return;
				}
				
				client.numberToDollars(request, new SOAPServiceCallback<NumberToDollarsResponse>() {

					@Override
					public void onSuccess(NumberToDollarsResponse responseObject) { // success
						Toast.makeText(MainActivity.this, responseObject.numberToDollarsResult, Toast.LENGTH_LONG).show();
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
