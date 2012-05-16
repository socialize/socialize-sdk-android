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
package com.socialize.android.ioc;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import android.content.Context;


/**
 * 
 * @author Jason Polites
 *
 */
public class BeanMappingParser {
	
	public static final String DEFAULT_FILENAME = "android-beans.xml";
	
	private ParserUtils utils = null;
	private ParserHandlerFactory parserHandlerFactory;
	private SAXParserFactory saxParserFactory;
	
	public BeanMappingParser() {
		super();
	}
	
	public BeanMappingParser(ParserHandlerFactory parserHandlerFactory, SAXParserFactory saxParserFactory) {
		this();
		this.parserHandlerFactory = parserHandlerFactory;
		this.saxParserFactory = saxParserFactory;
	}

	public BeanMapping parse(Context context) throws IOException {
		return parse(context, DEFAULT_FILENAME);
	}
	
	public BeanMapping parse(Context context, String filename) throws IOException {
		InputStream in = null;
		
		if(filename == null) {
			filename = DEFAULT_FILENAME;
		}
		
		try {
			in = findFile(context, filename);
			if(in != null) {
				return parse(context, in);
			}
			else {
				throw new FileNotFoundException("Failed to locate bean config [" +
						filename +
						"] in either classpath or asset folder");
			}
			
		}
		finally {
			if(in != null) {
				in.close();
			}
		}
	}
	
	private InputStream findFile(Context context, String filename) {
		InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);
		if (in == null) {
			try {
				in = context.getAssets().open(filename);
			}
			catch (IOException ignore) {}
		}
		return in;
	}
	
	public BeanMapping parse(Context context, InputStream...streams) {
		BeanMapping mapping = null;
		try {
			SAXParser parser = getNewSAXParser();
			
			if(streams.length > 1) {
				BeanMapping secondary = null;
				for (InputStream in : streams) {
					
					if(in != null) {
						BeanMappingParserHandler handler = getNewHandler();
						
						parser.parse(in, handler);
						
						if(mapping == null) {
							mapping = handler.getBeanMapping();
						}
						else {
							secondary = handler.getBeanMapping();
							mapping.merge(secondary);
						}
					}
					else {
						Logger.w("BeanMappingParser", "Bean mapping config stream was null, skipping...");
					}
				}
			}
			else {
				if(streams[0] != null) {
					BeanMappingParserHandler handler = getNewHandler();
					parser.parse(streams[0], handler);
					mapping = handler.getBeanMapping();	
				}
				else {
					Logger.e("BeanMappingParser", "Bean mapping config stream was null");
				}
			}
			
			if(mapping != null) {
				// Check for extends
				Collection<BeanRef> beanRefs = mapping.getBeanRefs();
				
				if(beanRefs != null) {
					for (BeanRef beanRef : beanRefs) {
						if(beanRef.getExtendsBean() != null && beanRef.getExtendsBean().trim().length() > 0) {
							
							// Copy props without overwriting
							BeanRef extended = mapping.getBeanRef(beanRef.getExtendsBean());
							
							if(extended != null) {
								if(utils == null) utils = new ParserUtils();
								utils.merge(extended, beanRef);
							}
							else {
								Logger.w("BeanMappingParser", "No such bean [" +
										beanRef.getExtendsBean() +
										"] found in extends reference for bean [" +
										beanRef.getName() +
										"]");
							}
							
						}
					}	
				}
			}
			else {
				Logger.e("BeanMappingParser", "Unable to parse mapping. No config files found!");
			}
	
		}
		catch (Exception e) {
			Logger.e("BeanMappingParser", "IOC Parse error", e);
		}
		
		return mapping;
	}
	
	private SAXParser getNewSAXParser() throws ParserConfigurationException, SAXException {
		if(saxParserFactory == null) {
			saxParserFactory = SAXParserFactory.newInstance();
		}
		return saxParserFactory.newSAXParser();
	}
	
	private BeanMappingParserHandler getNewHandler() {
		if(parserHandlerFactory != null) {
			return parserHandlerFactory.newInstance();
		}
		return new BeanMappingParserHandler();
	}
}
