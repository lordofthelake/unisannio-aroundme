package it.unisannio.aroundme.adapters;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

/**
 * Adapter per {@link ViewPager}, che mantiene in memoria un array di {@code View} da usare come pagine per il widget.
 * 
 * @author Michele Piccirillo <michele.piccirillo@gmail.com>
 */
public class ArrayPagerAdapter extends PagerAdapter {
	private View[] views;
	
	/**
	 * Crea un nuovo adapter.
	 * 
	 * @param views array di {@code View} da usare come pagine 
	 */
	public ArrayPagerAdapter(View... views) {
		this.views = views;
	}
	
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		View page = views[position];
		container.addView(page);
		return page;
	}
	
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}
	
	
	@Override
	public int getCount() {
		return views.length;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}
	
	@Override
	public CharSequence getPageTitle(int position) {
		return views[position].getTag().toString(); 
	}

}
