package com.example.smartcalc.converter;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartcalc.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ConverterFragment extends Fragment {
	private LinearLayout categoryContainer;
	private EditText etInput;
	private Spinner spinnerFrom;
	private RecyclerView rvResults;
	private ConversionResultsAdapter adapter;

	private ConverterRegistry registry;
	private String currentCategoryKey;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_converter, container, false);
		categoryContainer = view.findViewById(R.id.categoryContainer);
		etInput = view.findViewById(R.id.etInput);
		spinnerFrom = view.findViewById(R.id.spinnerFrom);
		rvResults = view.findViewById(R.id.rvResults);

		rvResults.setLayoutManager(new LinearLayoutManager(getContext()));
		adapter = new ConversionResultsAdapter();
		rvResults.setAdapter(adapter);

		registry = new ConverterRegistry();
		setupCategories();
		return view;
	}

	private void setupCategories() {
		Map<String, UnitCategory> categories = registry.getCategories();
		for (String key : categories.keySet()) {
			android.widget.TextView chip = new android.widget.TextView(getContext());
			chip.setText(key);
			chip.setPadding(24, 12, 24, 12);
			chip.setBackgroundResource(android.R.drawable.btn_default_small);
			chip.setTextColor(getResources().getColor(R.color.colorOnSurface));
			chip.setOnClickListener(v -> selectCategory(key));
			categoryContainer.addView(chip);
		}
		if (!categories.isEmpty()) selectCategory(categories.keySet().iterator().next());
	}

	private void selectCategory(String key) {
		currentCategoryKey = key;
		UnitCategory category = registry.getCategories().get(key);
		List<String> unitNames = new ArrayList<>(category.units.keySet());
		ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, unitNames);
		spinnerFrom.setAdapter(spinnerAdapter);
		spinnerFrom.setSelection(0);

		// Adjust input type per category
		if (category.isSpecialRadix) {
			// Allow alphanumeric uppercase for hex; disable suggestions
			etInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
		} else {
			etInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
		}

		spinnerFrom.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
			@Override public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) { recalc(); }
			@Override public void onNothingSelected(android.widget.AdapterView<?> parent) {}
		});
		etInput.addTextChangedListener(new TextWatcher() {
			@Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			@Override public void onTextChanged(CharSequence s, int start, int before, int count) { recalc(); }
			@Override public void afterTextChanged(Editable s) {}
		});
		recalc();
	}

	private void recalc() {
		if (currentCategoryKey == null) return;
		UnitCategory category = registry.getCategories().get(currentCategoryKey);
		if (category == null) return;
		String unitName = (String) spinnerFrom.getSelectedItem();
		if (unitName == null) return;

		String text = etInput.getText().toString();
		double input = 0.0;
		try { input = Double.parseDouble(text); } catch (Exception ignored) {}

		List<ConversionResult> results = new ArrayList<>();

		if (category.isSpecialTemperature) {
			double[] arr = category.convertTemperature(unitName, input);
			String[] names = new String[] { "Celsius (°C)", "Fahrenheit (°F)", "Kelvin (K)" };
			for (int i = 0; i < names.length; i++) results.add(new ConversionResult(names[i], arr[i]));
		} else if (category.isSpecialRadix) {
			// Parse input according to selected base
			int base = unitName.startsWith("Binary") ? 2 : unitName.startsWith("Octal") ? 8 : unitName.startsWith("Hex") ? 16 : 10;
			try {
				long v = Long.parseLong(text.trim(), base);
				results.add(new ConversionResult("Binary", Long.toBinaryString(v).toUpperCase(Locale.getDefault())));
				results.add(new ConversionResult("Octal", Long.toOctalString(v).toUpperCase(Locale.getDefault())));
				results.add(new ConversionResult("Decimal", Long.toString(v)));
				results.add(new ConversionResult("Hex", Long.toHexString(v).toUpperCase(Locale.getDefault())));
			} catch (Exception e) {
				results.add(new ConversionResult("", ""));
			}
		} else if (category.isSpecialBmi) {
			String categoryText;
			if (input < 18.5) categoryText = "Underweight";
			else if (input < 25) categoryText = "Normal";
			else if (input < 30) categoryText = "Overweight";
			else categoryText = "Obese";
			results.add(new ConversionResult("BMI", input));
			results.add(new ConversionResult("Category", categoryText));
		} else if (category.isSpecialGst) {
			for (Map.Entry<String, Double> e : category.units.entrySet()) {
				String k = e.getKey();
				double factor = e.getValue();
				results.add(new ConversionResult(k, input * factor));
			}
		} else if (category.isSpecialDiscount) {
			for (Map.Entry<String, Double> e : category.units.entrySet()) {
				String k = e.getKey();
				double factor = e.getValue();
				results.add(new ConversionResult(k, input * factor));
			}
		} else if (category.isSpecialFinance) {
			for (Map.Entry<String, Double> e : category.units.entrySet()) {
				String k = e.getKey();
				double factor = e.getValue();
				results.add(new ConversionResult(k, input * factor));
			}
		} else {
			double inBase = input * category.units.get(unitName);
			for (Map.Entry<String, Double> e : category.units.entrySet()) {
				String target = e.getKey();
				double factor = e.getValue();
				double outVal = inBase / factor;
				results.add(new ConversionResult(target, outVal));
			}
		}
		adapter.submit(results);
	}
}