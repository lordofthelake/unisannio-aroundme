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
