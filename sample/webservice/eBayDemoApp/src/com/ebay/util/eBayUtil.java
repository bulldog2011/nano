package com.ebay.util;

import com.leansoft.nano.custom.types.Duration;
import com.leansoft.nano.log.ALog;

public class eBayUtil {
	
	private static final String TAG = eBayUtil.class
			.getSimpleName();
	
	public static String formatCurrencyToString(double amountValue,
			String currencyStr) {
		String formattedText = null;
		try {

			// add currency symbol
			if (("" + amountValue).contains(".")) {
				if (currencyStr == null || currencyStr.equals("USD")) {
					formattedText = String.format("$%.2f",
							amountValue);
				} else {
					formattedText = String.format(
							"%.2f %S", amountValue, currencyStr);
				}
			} else {
				formattedText = String.format("%f %S",
						amountValue, currencyStr);
			}
		} catch (Exception ex) {
			ALog.e(TAG, "formatCurrencyError", ex);
		}
		return (formattedText.toString());
	}
	
	public static String formatDuration(Duration duration) {
		StringBuffer formattedText = new StringBuffer();
		boolean leading = false;
		if (duration.isNegative()) {
			formattedText.append("- ");
		}
		if (duration.getYears() > 0) {
			leading = true;
			formattedText.append(duration.getYears() + "y ");
		}
		if (leading || duration.getMonths() > 0) {
			leading = true;
			formattedText.append(duration.getMonths() + "M ");
		}
		if (leading || duration.getDays() > 0) {
			leading = true;
			formattedText.append(duration.getDays() + "d ");
		}
		if (leading || duration.getHours() > 0) {
			leading = true;
			formattedText.append(duration.getHours() + "H ");
		}
		if (leading || duration.getMinutes() > 0) {
			leading = true;
			formattedText.append(duration.getMinutes() + "m ");
		}
		if (leading || duration.getSeconds() > 0) {
			formattedText.append((int) duration.getSeconds() + "s");
		}

		if (formattedText.length() == 0) {
			return "Ended";
		}

		return formattedText.toString();
	}

}
