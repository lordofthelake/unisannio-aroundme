package it.unisannio.aroundme.widgets;

import java.text.MessageFormat;

import it.unisannio.aroundme.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

/**
 * 
 * @author @author Michele Piccirillo <michele.piccirillo@gmail.com>
 *
 */
public class SliderView extends LinearLayout implements OnSeekBarChangeListener {
	public static interface OnChangeListener {
		void onSliderChanged(SliderView view);
	}
	
	private int minValue = 0;
	private int maxValue = 0;
	
	private int multiplier = 1;
	private float conversion = 1.0f;
	private String format = "{0}";
	
	private SeekBar seekBar;
	private TextView textView;
	
	private OnChangeListener listener;

	public SliderView(Context context) {
		super(context);
		initialize(null);
	}

	public SliderView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize(attrs);
	}

	public SliderView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initialize(attrs);
	}
	
	
	
	private void initialize(AttributeSet attrs) {
		LayoutInflater inflater = (LayoutInflater)   getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.slider_view, this);
		
		this.seekBar = (SeekBar) findViewById(R.id.slider_view_seekbar);
		this.textView = (TextView) findViewById(R.id.slider_view_text);
		seekBar.setOnSeekBarChangeListener(this);
		
		
		if(attrs != null) {
			TypedArray a = getContext().obtainStyledAttributes(attrs,R.styleable.SliderView);
			
			multiplier = a.getInt(R.styleable.SliderView_multiplier, 1);
			conversion = a.getFloat(R.styleable.SliderView_conversion, 1.0f);
			
			format = a.getString(R.styleable.SliderView_format);
			
			if(format == null)
				format = "{0}";
			
			minValue = a.getInt(R.styleable.SliderView_minValue, 0);
			setMaxValue(a.getInt(R.styleable.SliderView_maxValue, 100));
			setValue(a.getInt(R.styleable.SliderView_value, 0));
			
			a.recycle();
		}
	}
	
	private void updateText() {
		textView.setText(MessageFormat.format(format, getValue(), getMultipliedValue(), getConvertedValue()));
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		updateText();
			
		if(listener != null)
			listener.onSliderChanged(this);
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {	
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
	}

	public int getValue() {
		return seekBar.getProgress() + minValue;
	}
	
	public void setValue(int value) {
		seekBar.setProgress(value - minValue);
		updateText();
	}
	
	public int getMultipliedValue() {
		return getValue() * multiplier;
	}
	
	public void setMultipliedValue(int value) {
		setValue(value / multiplier);
	}
	
	public float getConvertedValue() {
		return getMultipliedValue() * conversion;
	}
	
	public void setConvertedValue(float value) {
		setMultipliedValue(Math.round(value / conversion));
	}
	
	public int getMaxValue() {
		return maxValue;
	}
	
	public void setMaxValue(int maxValue) {
		this.maxValue = maxValue;
		seekBar.setMax(maxValue - minValue);
	}
	
	public int getMinValue() {
		return minValue;
	}
	
	public void setMinValue(int minValue) {
		int value = getValue();
		this.minValue = minValue;
		setValue(value);
	}
	
	public String getFormat() {
		return format;
	}
	
	public void setFormat(String format) {
		this.format = format;
	}
	
	public void setOnChangeListener(OnChangeListener listener) {
		this.listener = listener;
	}
}
