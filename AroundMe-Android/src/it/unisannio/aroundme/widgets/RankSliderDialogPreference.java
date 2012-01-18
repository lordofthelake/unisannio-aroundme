/* AroundMe - Social Network mobile basato sulla geolocalizzazione
 * Copyright (C) 2012 AroundMe Working Group
 *   
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package it.unisannio.aroundme.widgets;

import it.unisannio.aroundme.R;
import it.unisannio.aroundme.Setup;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Tipo di preferenza utilizzato per modificare la compatibilit&agrave; minima richiesta dei filtri.
 * 
 * Viene utilizzato uno {@link SliderView} opportunamente configurato come widget di configurazione.
 * 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 */
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
