package com.leansoft.nano.sample;

import com.leansoft.nano.soap11.Fault;
import com.leansoft.nano.ws.SOAPServiceCallback;

import net.webservicex.BarCodeData;
import net.webservicex.BarcodeOption;
import net.webservicex.BarcodeType;
import net.webservicex.CheckSumMethod;
import net.webservicex.GenerateBarCode;
import net.webservicex.GenerateBarCodeResponse;
import net.webservicex.ImageFormats;
import net.webservicex.ShowTextPosition;
import net.webservicex.client.BarCodeSoap_SOAPClient;
import net.webservicex.service.BarCodeServiceClient;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Button encodeButton = (Button) this.findViewById(R.id.encodeButton);
		
		encodeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// get shared client
				BarCodeSoap_SOAPClient client = BarCodeServiceClient.getSharedClient();
				client.setDebug(true); // enable soap message logging
				
				// build request
				BarCodeData barCodeData = new BarCodeData();
				barCodeData.height = 125;
				barCodeData.width = 225;
				barCodeData.angle = 0;
				barCodeData.ratio = 5;
				barCodeData.module = 0;
				barCodeData.left = 25;
				barCodeData.top = 0;
				barCodeData.checkSum = false;
				barCodeData.fontName = "Arial";
				barCodeData.barColor = "Black";
				barCodeData.bgColor = "White";
				barCodeData.fontSize = 10.0f;
				barCodeData.barcodeOption = BarcodeOption.BOTH;
				barCodeData.barcodeType = BarcodeType.CODE_2_5_INTERLEAVED;
				barCodeData.checkSumMethod = CheckSumMethod.NONE;
				barCodeData.showTextPosition = ShowTextPosition.BOTTOM_CENTER;
				barCodeData.barCodeImageFormat = ImageFormats.PNG;
				
				GenerateBarCode request = new GenerateBarCode();
				request.barCodeParam = barCodeData;
				request.barCodeText = ((EditText)findViewById(R.id.barCodeText)).getText().toString();
				
				// make API call with registered callbacks
				client.generateBarCode(request, new SOAPServiceCallback<GenerateBarCodeResponse>() {

					@Override
					public void onSuccess(GenerateBarCodeResponse responseObject) { // success
						ImageView barCodeImage = (ImageView)findViewById(R.id.barCodeImage);
						byte[] imageData = responseObject.generateBarCodeResult;
						Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
						barCodeImage.setImageBitmap(bitmap);
					}
					
					@Override
					public void onFailure(Throwable error, String errorMessage) {// http or parsing error
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
