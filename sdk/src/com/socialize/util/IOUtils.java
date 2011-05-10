package com.socialize.util;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.LinkedList;
import java.util.List;

public final class IOUtils {
	
	public static final String read(File file) throws IOException {

		InputStream in = null;

		try {
			in = new FileInputStream(file);
			return read(in, "UTF-8");
		}
		finally {
			if (in != null) {
				in.close();
			}
		}
	}
	
	public static final String[] readLines(File file) throws IOException {

		InputStream in = null;

		try {
			in = new FileInputStream(file);
			return readLines(in, "UTF-8");
		}
		finally {
			if (in != null) {
				in.close();
			}
		}
	}

	public static final String read(InputStream in) throws IOException {
		return read(in, "UTF-8");
	}

	public static final String read(InputStream is, String charset) throws IOException {
		if (is != null) {
			StringBuilder sb = new StringBuilder();
			String line;

			BufferedReader reader = new BufferedReader(new InputStreamReader(is, charset));
			
			while ((line = reader.readLine()) != null) {
				sb.append(line).append("\n");
			}
			
			return sb.toString();
		}
		else {
			return "";
		}
	}
	
	public static final String[] readLines(InputStream in) throws IOException {
		return readLines(in, "UTF-8");
	}

	public static final String[] readLines(InputStream is, String charset) throws IOException {

		String[] strLines = null;

		if (is != null) {
			List<String> lines = new LinkedList<String>();
			String line;

			BufferedReader reader = new BufferedReader(new InputStreamReader(is, charset));
			
			while ((line = reader.readLine()) != null) {
				lines.add(line + "\n");
			}

			return lines.toArray(new String[lines.size()]);
		}
		return strLines;
	}
	
	
	public static final long pipe(InputStream in, OutputStream out, int bufferSize) throws IOException {
		
		int read;
		long total = 0L;
		byte[] buffer = new byte[bufferSize];
		
		while((read = in.read(buffer)) >= 0) {
			
			total+=read;
			out.write(buffer, 0, read);
		}
		
		out.flush();
		
		return total;
	}
	
	public static final long pipeChannels(InputStream in, FileOutputStream out, int bufferSize) throws IOException {
		
		int read;
		long total = 0L;
		byte[] buffer = new byte[bufferSize];
		
		FileChannel channel = out.getChannel();
		
		ByteBuffer bb = ByteBuffer.allocate(bufferSize);
		
		while((read = in.read(buffer)) >= 0) {
			
			total+=read;
			
			bb.put(buffer, 0, read);
			
			channel.write(bb);
			channel.force(true);
			
			bb.clear();
		}
		
		channel.close();
		
		return total;
	}
	
	
}
