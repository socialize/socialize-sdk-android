/*
 * Copyright (c) 2012 Socialize Inc.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.socialize.ui.comment;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.PaintDrawable;
import com.socialize.ui.util.Colors;

/**
 * @author Jason Polites
 *
 */
public class CommentListItemBackgroundFactory  {
	
	private Drawable background;
	
	private int bgColor;
	private int topColor;
	private int bottomColor;
	
	private Colors colors;
	
	public void init() {
		bgColor = colors.getColor(Colors.LIST_ITEM_BG);
		topColor = colors.getColor(Colors.LIST_ITEM_TOP);
		bottomColor = colors.getColor(Colors.LIST_ITEM_BOTTOM);
		background = makeDefaultBackground();
	}
	
	public Drawable getBackground() {
		return background;
	}
	
	protected Drawable makeDefaultBackground() {
		PaintDrawable shadow = new PaintDrawable(bottomColor);
		PaintDrawable highlight = new PaintDrawable(topColor);
		PaintDrawable surface = new PaintDrawable(bgColor);
		LayerDrawable layers = new LayerDrawable(new Drawable[] {shadow, highlight, surface});
		
		layers.setLayerInset(0, 0, 0, 0, 0);
		layers.setLayerInset(1, 0, 0, 0, 1);
		layers.setLayerInset(2, 0, 1, 0, 1);
		
		return layers;
	}	
	
	// So we can mock
	protected GradientDrawable makeGradient(int bottom, int top) {
		return new GradientDrawable(
				GradientDrawable.Orientation.BOTTOM_TOP,
				new int[] { bottom, top });
	}

	public void setColors(Colors colors) {
		this.colors = colors;
	}		
}
