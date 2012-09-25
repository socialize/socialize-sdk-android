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
package com.socialize.demo.snippets;

import com.socialize.ConfigUtils;
import com.socialize.SocializeTools;
import android.app.Activity;


/**
 * @author Jason Polites
 *
 */
public class ConfigSnippets extends Activity {

public void enableDiagnosticLogging() {
// begin-snippet-0

// Enable diagnostic logging
// This will clear any previous log entries.
ConfigUtils.getConfig(this).setDiagnosticLoggingEnabled(this, true);

// .. some time later...

// Disable diagnostic logging
// Previous log entries will be retained.
ConfigUtils.getConfig(this).setDiagnosticLoggingEnabled(this, false);
	
// end-snippet-0
}

public void sendLogs() {
// begin-snippet-1
// Send logs via email
SocializeTools.sendExternalLogs(this);
// end-snippet-1
}

public void clearLogs() {
// begin-snippet-2
// Send logs via email
SocializeTools.deleteExternalLogs(this);
// end-snippet-2
}

	
}
