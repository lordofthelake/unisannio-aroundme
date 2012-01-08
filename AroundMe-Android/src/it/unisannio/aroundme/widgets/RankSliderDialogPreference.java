package it.unisannio.aroundme.widgets;

import it.unisannio.aroundme.R;
import it.unisannio.aroundme.Setup;
import it.unisannio.aroundme.widgets.SliderView.OnChangeListener;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

public class RankSliderDialogPreference extends DialogPreference {

	private SliderView slider;
	private float value = Setup.FILTERS_DEFAULT_RANK;
	
	public RankSliderDialogPreference(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public RankSliderDialogPreference(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		
		
	}
	
	@Override
	protected void onPrepareDialogBuilder(Builder builder) {
		super.onPrepareDialogBuilder(builder);
		builder.setPositiveButton(R.string.dialog_ok, this);
		builder.setNegativeButton(R.string.dialog_cancel, this);
	}
	
	@Override
	protected View onCreateDialogView() {
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		slider = (SliderView) inflater.inflate(R.layout.slider_view_rank, null);
		return slider;
	}
	
	@Override
	protected void onBindDialogView(View view) {
		super.onBindDialogView(view);
		slider.setConvertedValue(value);
	}

	@Override
	protected void onSetInitialValue(boolean restorePersistedValue,
			Object defaultValue) {

		super.onSetInitialValue(restorePersistedValue, defaultValue);
		if (restorePersistedValue && shouldPersist()) 
		      value = getPersistedFloat(Setup.FILTERS_DEFAULT_RANK);
		
	}
	
	@Override
	public void onClick(DialogInterface dialog, int which) {
		super.onClick(dialog, which);
		if(which == Dialog.BUTTON_POSITIVE) {
			value = slider.getConvertedValue();
			callChangeListener(value);
			
			if(shouldPersist())
				persistFloat(value);
		}
		
	}
}
