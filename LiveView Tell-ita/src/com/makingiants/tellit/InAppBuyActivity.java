package com.makingiants.tellit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.vending.billing.IabHelper;
import com.android.vending.billing.IabHelper.OnIabSetupFinishedListener;
import com.android.vending.billing.IabResult;
import com.android.vending.billing.Purchase;

public class InAppBuyActivity extends Activity implements
		IabHelper.OnIabPurchaseFinishedListener, OnIabSetupFinishedListener {

	public final static String EXTRA_KEY_SKU = "com.makingiants.tellit.in_app_type";

	private IabHelper mHelper;
	private String sku;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_donation_thanks);

		Bundle extras = this.getIntent().getExtras();

		if (extras == null) {
			finish();
		}

		try {

			sku = extras.getString(EXTRA_KEY_SKU);

			// compute your public key and store it in base64EncodedPublicKey
			mHelper = new IabHelper(this, getString(R.string.in_app_key));

			mHelper.startSetup(this);

		} catch (IllegalStateException e) {
			Toast.makeText(this, getString(R.string.in_app_message_error),
					Toast.LENGTH_LONG).show();
			finish();
		} catch (Exception e) {
			Toast.makeText(this, getString(R.string.in_app_message_error),
					Toast.LENGTH_LONG).show();
			finish();
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != RESULT_CANCELED) {

			mHelper.handleActivityResult(requestCode, resultCode, data);

		}
	}

	@Override
	public void onIabSetupFinished(IabResult result) {

		if (result.isFailure()) {
			Log.e("onIabSetupFinished", "Problem setting up In-app Billing: "
					+ result);
			finish();
		}

		// Send the request
		mHelper.launchPurchaseFlow(this, sku, 10001, this, "inapp:"
				+ getPackageName() + ":android.test.purchased");

	}

	@Override
	public void onIabPurchaseFinished(IabResult result, Purchase purchase) {

		// TODO Auto-generated method stub
		if (result.isFailure()) {
			Log.e("OnIabPurchaseFinishedListener Failure", result.toString());
			finish();
		}

		if (purchase.getSku().equals(sku)) {
			mHelper.consumeAsync(purchase, null);
			finish();
		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (mHelper != null) {
			mHelper.dispose();
		}
		mHelper = null;
	}

}
