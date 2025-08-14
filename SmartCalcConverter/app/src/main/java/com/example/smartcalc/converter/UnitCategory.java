package com.example.smartcalc.converter;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class UnitCategory {
	public final String name;
	public final Map<String, Double> units = new LinkedHashMap<>();
	public boolean isSpecialTemperature = false;
	public boolean isSpecialBmi = false;
	public boolean isSpecialGst = false;
	public boolean isSpecialDiscount = false;
	public boolean isSpecialFinance = false;
	public boolean isSpecialRadix = false;

	public UnitCategory(String name) {
		this.name = name;
	}

	public double[] convertTemperature(String fromName, double value) {
		// Convert to Celsius as base
		double celsius;
		if (fromName.startsWith("Celsius")) celsius = value;
		else if (fromName.startsWith("Fahrenheit")) celsius = (value - 32) * 5.0 / 9.0;
		else celsius = value - 273.15; // Kelvin
		return new double[] { celsius, celsius * 9.0/5.0 + 32, celsius + 273.15 };
	}

	public String convertRadixToAll(String fromName, String text) {
		int base = fromName.startsWith("Binary") ? 2 : fromName.startsWith("Octal") ? 8 : fromName.startsWith("Hex") ? 16 : 10;
		try {
			long v = Long.parseLong(text.trim(), base);
			return String.format(Locale.getDefault(), "bin:%s | oct:%s | dec:%d | hex:%s",
				Long.toBinaryString(v), Long.toOctalString(v), v, Long.toHexString(v).toUpperCase(Locale.getDefault()));
		} catch (Exception e) {
			return "";
		}
	}
}