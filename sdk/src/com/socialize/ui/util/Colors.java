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
package com.socialize.ui.util;

import android.graphics.Color;
import com.socialize.log.SocializeLogger;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Jason Polites
 */
public class Colors {

	public static final String TITLE = "TITLE";
	public static final String BODY = "BODY";
	public static final String HEADER = "HEADER";
	
	public static final String COMMENT_TITLE = "COMMENT_TITLE";
	public static final String COMMENT_BODY = "COMMENT_BODY";
	
	public static final String BUTTON_BOTTOM = "BUTTON_BOTTOM";
	public static final String SOCIALIZE_BLUE = "SOCIALIZE_BLUE";
	
	public static final String BUTTON_TOP_STROKE = "BUTTON_TOP_STROKE";
	public static final String BUTTON_BOTTOM_STROKE = "BUTTON_BOTTOM_STROKE";
	
	public static final String BUTTON_TOP = "BUTTON_TOP";
	public static final String TEXT_BG = "TEXT_BG";
	public static final String TEXT_STROKE = "TEXT_STROKE";
	public static final String LIST_ITEM_BG = "LIST_ITEM_BG";
	public static final String APP_BG = "APP_BG";
	public static final String ACTIVITY_BG = "ACTIVITY_BG";
	public static final String LIST_ITEM_TOP = "LIST_ITEM_TOP";
	public static final String LIST_ITEM_BOTTOM = "LIST_ITEM_BOTTOM";
	public static final String STANDARD_BACKGROUND_COLOR = "STANDARD_BACKGROUND_COLOR";
	public static final String LOADING_ITEM_BG = "LOADING_ITEM_BG";
	
	public static final String ANON_CELL_TITLE = "ANON_CELL_TITLE";
	public static final String ANON_CELL_TEXT = "ANON_CELL_TEXT";
	
	public static final String AUTH_PANEL_TOP = "AUTH_PANEL_TOP";
	public static final String AUTH_PANEL_BOTTOM = "AUTH_PANEL_BOTTOM";
	public static final String AUTH_PANEL_CANCEL_TEXT = "AUTH_PANEL_CANCEL_TEXT";
	
	public static final String COMMENT_ENTRY_TOP = "COMMENT_ENTRY_TOP";
	public static final String COMMENT_ENTRY_BOTTOM = "COMMENT_ENTRY_BOTTOM";
	
	public static final String AUTH_REQUEST_DIALOG_TOP = "AUTH_REQUEST_DIALOG_TOP";
	public static final String AUTH_REQUEST_DIALOG_BOTTOM = "AUTH_REQUEST_DIALOG_BOTTOM";
	
	public static final String ACTION_BAR_SLIDER_CONTENT = "ACTION_BAR_SLIDER_CONTENT";
	
	public static final String CHECKBOX_BORDER_BOTTOM = "CHECKBOX_BORDER_BOTTOM";
	public static final String CHECKBOX_BORDER_TOP = "CHECKBOX_BORDER_TOP";
	public static final String CLICKABLE_CELL_STROKE = "CLICKABLE_CELL_STROKE";
	
	public static final String BUTTON_DISABLED_BOTTOM = "BUTTON_DISABLED_BOTTOM";
	public static final String BUTTON_DISABLED_TOP = "BUTTON_DISABLED_TOP";
	public static final String BUTTON_DISABLED_TEXT = "BUTTON_DISABLED_TEXT";
	public static final String BUTTON_DISABLED_STROKE = "BUTTON_DISABLED_STROKE";
	public static final String BUTTON_DISABLED_BACKGROUND = "BUTTON_DISABLED_BACKGROUND";
	
	private Map<String, String> colorHexCodes;
	private Map<String, Integer> colors;
	
	private static Map<String, Integer> parsedColors = new HashMap<String, Integer>();
	
	public void init() {
		colors = new TreeMap<String, Integer>();
		
		if(colorHexCodes != null) {
			Set<Entry<String, String>> entries = colorHexCodes.entrySet();
			
			for (Entry<String, String> entry : entries) {
				String hex = entry.getValue().trim().toLowerCase();
				if(!hex.startsWith("#")) {
					hex = "#" + hex;
					colorHexCodes.put(entry.getKey(), hex);
				}
				try {
					int color = Color.parseColor(entry.getValue());
					colors.put(entry.getKey(), color);
				}
				catch (Exception e) {
					SocializeLogger.e(e.getMessage(), e);
				}
			}
		}
	}
	
	public String getHexColor(String name) {
		return colorHexCodes.get(name);
	}
	
	public int getColor(String name) {
		Integer i = colors.get(name);
		if(i == null) {
			return 0;
		}
		return i;
	}

	public void setColorHexCodes(Map<String, String> colorHexCodes) {
		this.colorHexCodes = colorHexCodes;
	}
	
	public static int parseColor(String colorHex) {
		Integer color = parsedColors.get(colorHex);
		if(color != null) {
			return color;
		}
		else {
			int icolor = Color.parseColor(colorHex);
			
			parsedColors.put(colorHex, icolor);
			
			return icolor;
		}
	}
}
