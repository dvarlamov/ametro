package org.ametro.activity;

import org.ametro.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class PaymentDetailsDialog extends Activity implements OnClickListener, TextWatcher{

	public static final String EXTRA_AMOUNT = "AMOUNT";
	public static final String EXTRA_CURRENCY = "CURRENCY";
	public static final String EXTRA_ALLOW_DECIMAL_AMOUNT = "ALLOW_DECIMAL";
	
	public static final String EXTRA_CONTEXT = "CONTEXT";
	public static final String EXTRA_CURRENCY_NAMES = "CURRENCY_NAMES";
	public static final String EXTRA_CURRENCY_CODES = "CURRENCY_CODES";
	
	private String[] mCurrencyNames;
	private String[] mCurrencyCodes;
	
	private String mCurrencyCode;
	
	public boolean mAllowDecimalAmount;
	private float mAmount;
	private String mDialogContext;
	
	private Spinner mCurrencySpinner;
	private EditText mAmountEditText;
	
	private Button mOkButton;
	private Button mCancelButton;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.donate_dialog);
		mCurrencySpinner = (Spinner)findViewById(R.id.currencies);
		mOkButton = (Button)findViewById(R.id.btn_ok);
		mCancelButton = (Button)findViewById(R.id.btn_cancel);
		mAmountEditText = (EditText)findViewById(R.id.amount);
		
		mOkButton.setOnClickListener(this);
		mCancelButton.setOnClickListener(this);
		
		prepareData();
		bindData();
	}

	protected void onResume() {
		mAmountEditText.addTextChangedListener(this);
		afterTextChanged(mAmountEditText.getEditableText());
		super.onResume();
	}
	
	protected void onPause() {
		mAmountEditText.removeTextChangedListener(this);
		super.onPause();
	}
	
	private void prepareData() {
		Intent data = getIntent();
		mCurrencyNames = data.getStringArrayExtra(EXTRA_CURRENCY_NAMES);
		mCurrencyCodes = data.getStringArrayExtra(EXTRA_CURRENCY_CODES);
		
		mCurrencyCode = data.getStringExtra(EXTRA_CURRENCY);
		
		mAllowDecimalAmount = data.getBooleanExtra(EXTRA_ALLOW_DECIMAL_AMOUNT, false);
		mAmount = data.getFloatExtra(EXTRA_AMOUNT, 0.0f);
		mDialogContext = data.getStringExtra(EXTRA_CONTEXT);
		
		if(mCurrencyCode == null){
			mCurrencyCode = "";
		}
		
		if(mCurrencyNames == null){
			mCurrencyNames = new String[]{ mCurrencyCode };
			mCurrencyCodes = new String[]{ mCurrencyCode };
		}
	}

	private void bindData() {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mCurrencyNames );
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mCurrencySpinner.setAdapter(adapter);
				
		int pos = 0;
		int len = mCurrencyCodes.length;
		for(int i=0; i<len; i++){
			if(mCurrencyCodes[i].equals(mCurrencyCode)){
				pos = i;
				break;
			}
		}
		mCurrencySpinner.setSelection(pos);
		mCurrencySpinner.setEnabled(mCurrencyNames.length > 1);
		
		int inputType = InputType.TYPE_CLASS_NUMBER;
		if(mAllowDecimalAmount){
			inputType |= InputType.TYPE_NUMBER_FLAG_DECIMAL;
		}
		mAmountEditText.setSelectAllOnFocus(true);
		mAmountEditText.setInputType(inputType);
		if(mAmount>0){
		if(mAllowDecimalAmount){
			mAmountEditText.setText(Float.toString(mAmount));
		}else{
			mAmountEditText.setText(Integer.toString((int)mAmount));
		}
		}else{
			mAmountEditText.setText("");
		}
	}

	public void onClick(View v) {
		if(v == mCancelButton){
			setResult(RESULT_CANCELED);
			finish();
		}else if(v == mOkButton){
			String amountString = mAmountEditText.getText().toString();
			if(amountString!=null && amountString.length()!=0){
				float amount = Float.parseFloat( amountString );
				if(amount>0){
					Intent data = new Intent();
					data.putExtra(EXTRA_CONTEXT, mDialogContext);
					data.putExtra(EXTRA_CURRENCY, mCurrencyCodes[ mCurrencySpinner.getSelectedItemPosition()] );
					data.putExtra(EXTRA_AMOUNT, amount);
					setResult(RESULT_OK, data);
					finish();
				}
			}
		}
	}

	public void afterTextChanged(Editable s) {
		String amountString = s.toString();
		if(amountString==null || amountString.length()==0){
			amountString = "0";
		}	
		float amount = Float.parseFloat( amountString );
		mOkButton.setEnabled(amount>0);
	}

	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		// TODO Auto-generated method stub
		
	}

	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
		
	}
}
