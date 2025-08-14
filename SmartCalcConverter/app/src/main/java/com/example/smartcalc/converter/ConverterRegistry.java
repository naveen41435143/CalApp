package com.example.smartcalc.converter;

import java.util.LinkedHashMap;
import java.util.Map;

public class ConverterRegistry {
	private final Map<String, UnitCategory> categories = new LinkedHashMap<>();

	public ConverterRegistry() {
		addLength();
		addMass();
		addArea();
		addTime();
		addData();
		addSpeed();
		addTemperature();
		addVolume();
		addCurrency();
		addBmi();
		addGst();
		addDiscount();
		addFinance();
		addDate();
		addNumeralSystem();
	}

	public Map<String, UnitCategory> getCategories() { return categories; }

	private void addLength() {
		UnitCategory c = new UnitCategory("Length");
		c.units.put("meter (m)", 1.0);
		c.units.put("kilometer (km)", 1000.0);
		c.units.put("centimeter (cm)", 0.01);
		c.units.put("millimeter (mm)", 0.001);
		c.units.put("mile (mi)", 1609.344);
		c.units.put("yard (yd)", 0.9144);
		c.units.put("foot (ft)", 0.3048);
		c.units.put("inch (in)", 0.0254);
		categories.put(c.name, c);
	}

	private void addMass() {
		UnitCategory c = new UnitCategory("Mass");
		c.units.put("kilogram (kg)", 1.0);
		c.units.put("gram (g)", 0.001);
		c.units.put("milligram (mg)", 1e-6);
		c.units.put("pound (lb)", 0.45359237);
		c.units.put("ounce (oz)", 0.028349523125);
		categories.put(c.name, c);
	}

	private void addArea() {
		UnitCategory c = new UnitCategory("Area");
		c.units.put("square meter (m²)", 1.0);
		c.units.put("square kilometer (km²)", 1_000_000.0);
		c.units.put("square centimeter (cm²)", 0.0001);
		c.units.put("square millimeter (mm²)", 1e-6);
		c.units.put("hectare (ha)", 10_000.0);
		c.units.put("acre (ac)", 4046.8564224);
		categories.put(c.name, c);
	}

	private void addTime() {
		UnitCategory c = new UnitCategory("Time");
		c.units.put("second (s)", 1.0);
		c.units.put("minute (min)", 60.0);
		c.units.put("hour (h)", 3600.0);
		c.units.put("day (d)", 86400.0);
		c.units.put("week (wk)", 604800.0);
		c.units.put("year (yr)", 31557600.0);
		categories.put(c.name, c);
	}

	private void addData() {
		UnitCategory c = new UnitCategory("Data");
		c.units.put("bit (b)", 1.0);
		c.units.put("byte (B)", 8.0);
		c.units.put("kilobyte (KB)", 8.0 * 1024);
		c.units.put("megabyte (MB)", 8.0 * 1024 * 1024);
		c.units.put("gigabyte (GB)", 8.0 * 1024 * 1024 * 1024);
		c.units.put("terabyte (TB)", 8.0 * 1024 * 1024 * 1024 * 1024);
		categories.put(c.name, c);
	}

	private void addSpeed() {
		UnitCategory c = new UnitCategory("Speed");
		c.units.put("m/s", 1.0);
		c.units.put("km/h", 1000.0 / 3600.0);
		c.units.put("mph", 1609.344 / 3600.0);
		c.units.put("knot", 1852.0 / 3600.0);
		categories.put(c.name, c);
	}

	private void addTemperature() {
		UnitCategory c = new UnitCategory("Temperature");
		// For temperature, factors are not linear scale only. Use pseudo-base Celsius in UnitCategory.convert
		c.units.put("Celsius (°C)", 1.0);
		c.units.put("Fahrenheit (°F)", 1.0);
		c.units.put("Kelvin (K)", 1.0);
		c.isSpecialTemperature = true;
		categories.put(c.name, c);
	}

	private void addVolume() {
		UnitCategory c = new UnitCategory("Volume");
		c.units.put("liter (L)", 1.0);
		c.units.put("milliliter (mL)", 0.001);
		c.units.put("cubic meter (m³)", 1000.0);
		c.units.put("gallon (US)", 3.785411784);
		c.units.put("quart (US)", 0.946352946);
		c.units.put("pint (US)", 0.473176473);
		c.units.put("cup (US)", 0.2365882365);
		c.units.put("fluid ounce (US)", 0.0295735295625);
		categories.put(c.name, c);
	}

	private void addCurrency() {
		UnitCategory c = new UnitCategory("Currency");
		// Static placeholders; for production use live rates
		c.units.put("USD", 1.0);
		c.units.put("EUR", 1.1);
		c.units.put("INR", 0.012);
		c.units.put("JPY", 0.0065);
		categories.put(c.name, c);
	}

	private void addBmi() {
		UnitCategory c = new UnitCategory("BMI");
		// Not a unit conversion; expose example calculation: interpret input as BMI and convert to category buckets
		c.units.put("BMI", 1.0);
		c.isSpecialBmi = true;
		categories.put(c.name, c);
	}

	private void addGst() {
		UnitCategory c = new UnitCategory("GST");
		c.units.put("Net Amount", 1.0);
		c.units.put("Gross @5%", 1.05);
		c.units.put("Gross @12%", 1.12);
		c.units.put("Gross @18%", 1.18);
		c.units.put("Gross @28%", 1.28);
		c.isSpecialGst = true;
		categories.put(c.name, c);
	}

	private void addDiscount() {
		UnitCategory c = new UnitCategory("Discount");
		c.units.put("Price", 1.0);
		c.units.put("10% off", 0.90);
		c.units.put("20% off", 0.80);
		c.units.put("30% off", 0.70);
		c.units.put("50% off", 0.50);
		c.isSpecialDiscount = true;
		categories.put(c.name, c);
	}

	private void addFinance() {
		UnitCategory c = new UnitCategory("Finance");
		c.units.put("Principal", 1.0);
		c.units.put("Simple Interest @10%/yr (1yr)", 1.10);
		c.units.put("Compound Monthly @10%/yr (1yr)", Math.pow(1.10, 1.0));
		c.isSpecialFinance = true;
		categories.put(c.name, c);
	}

	private void addDate() {
		UnitCategory c = new UnitCategory("Date");
		c.units.put("Days", 1.0);
		c.units.put("Weeks", 7.0);
		c.units.put("Months (30d)", 30.0);
		c.units.put("Years (365d)", 365.0);
		categories.put(c.name, c);
	}

	private void addNumeralSystem() {
		UnitCategory c = new UnitCategory("Numeral System");
		c.units.put("Binary", 1.0);
		c.units.put("Octal", 1.0);
		c.units.put("Decimal", 1.0);
		c.units.put("Hex", 1.0);
		c.isSpecialRadix = true;
		categories.put(c.name, c);
	}
}