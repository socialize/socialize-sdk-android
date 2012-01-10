package com.socialize.android.ioc;

public class ImportRef {

	private String name;
	private String source;
	private boolean dependentOnly = false;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public boolean isDependentOnly() {
		return dependentOnly;
	}
	public void setDependentOnly(boolean dependentOnly) {
		this.dependentOnly = dependentOnly;
	}
}
