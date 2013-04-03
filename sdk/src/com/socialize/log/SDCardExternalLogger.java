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
package com.socialize.log;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import com.socialize.log.SocializeLogger.LogLevel;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Jason Polites
 * 
 */
public class SDCardExternalLogger implements ExternalLogger {

	private static final String TAG = SDCardExternalLogger.class.getSimpleName();

	private static final String LOG_FILE_NAME = "socialize.log";

	private PrintWriter writer;
	
	private boolean canWrite = true;
	
	@Override
	public boolean canWrite() {
		return canWrite;
	}

	@Override
	public void init(Context context) {
		canWrite = context.getPackageManager().checkPermission("android.permission.WRITE_EXTERNAL_STORAGE", context.getPackageName()) == PackageManager.PERMISSION_GRANTED;
		if(canWrite) {
			createWriter(context, LOG_FILE_NAME);
		}
	}

	@Override
	public void destroy() {
		if(writer != null) {
			writer.flush();
			writer.close();
			writer = null;
		}
	}

	@Override
	public void log(LogLevel level, long time, String tag, String message) {
		if( writer != null ) {
	        writer.print( String.valueOf(time) );
	        writer.print( " " );
	        writer.print( level.toString() );
	        writer.print( " " );
	        writer.print( tag );
	        writer.print( " " );
	        writer.print( Thread.currentThread().getName() );
	        writer.print( " - " );
	        writer.println( message );
	    }
	}

	@Override
	public final void log(LogLevel level, long time, String tag, String message, Throwable error) {
		if(error != null) {
			StringBuilder builder = new StringBuilder();
			builder.append(message).append(": ").append(stackTraceToString(error));
			log(level, time, TAG, builder.toString());
		}
		log(level, time, tag, message);
	}
	
	public static Set<Uri> getExternalLogFilePaths(Context context) {
		File dir = getLogFilePath(context);

		Set<Uri> files = new HashSet<Uri>();

		if(dir.exists()) {
			File[] list = dir.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String filename) {
					return filename.endsWith(".log");
				}
			});

			if(list != null) {
				for (File file : list) {
					files.add(Uri.fromFile(file));
				}
			}
		}

		return files;
	}

	public static void clearExternalLogFiles(Context context) {
		File dir = getLogFilePath(context);
		if(dir.exists()) {
			File[] list = dir.listFiles(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String filename) {
					return filename.endsWith(".log");
				}
			});
			
			if(list != null) {
				for (File file : list) {
					if(!file.delete()) {
						file.deleteOnExit();
					}
				}
			}
		}
	}

	private static File getLogFilePath(Context context) {
		return new File(Environment.getExternalStorageDirectory(), "socialize-" + context.getPackageName());
	}

	private PrintWriter createWriter(Context context, String logFilename) {
		try {
			destroy();

			String state = Environment.getExternalStorageState();
			if (state.equals(Environment.MEDIA_MOUNTED)) {
				File dir = getLogFilePath(context);
				if (!dir.mkdirs()) {
					Log.w(TAG, "Could not create log directory: " + dir.getAbsolutePath());
				}
				File log = new File(dir, logFilename);
				if (log.exists()) {
					log.delete();
				}
				Log.i(TAG, "Opening " + log.getAbsolutePath());
				writer = new PrintWriter(new FileWriter(log), true);
				return writer;
			}
			else {
				Log.w(TAG, "Could not create log file because external storage state was " + state);
			}
		}
		catch (IOException ioe) {
			Log.e(TAG, "Failed while opening the log file.", ioe);
		}

		return null;
	}

	String stackTraceToString(Throwable error) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PrintWriter pw = new PrintWriter(out);
		error.printStackTrace(pw);
		try {
			pw.flush();
			return new String(out.toByteArray(), "utf-8");
		}
		catch (Exception ignore) {
			error.printStackTrace();
			ignore.printStackTrace();
			return "";
		}
	}

}
