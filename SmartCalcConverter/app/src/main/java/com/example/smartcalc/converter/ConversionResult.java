package com.example.smartcalc.converter;

public class ConversionResult {
	public final String unitName;
	public final double value;
	public final String display;

	public ConversionResult(String unitName, double value) {
		this.unitName = unitName;
		this.value = value;
		this.display = null;
	}

	public ConversionResult(String unitName, String displayText) {
		this.unitName = unitName;
		this.value = Double.NaN;
		this.display = displayText;
	}
}