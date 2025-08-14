package com.example.smartcalc;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.smartcalc.calculator.CalculatorFragment;
import com.example.smartcalc.converter.ConverterFragment;

public class SectionsPagerAdapter extends FragmentStateAdapter {
	public SectionsPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
		super(fragmentActivity);
	}

	@NonNull
	@Override
	public Fragment createFragment(int position) {
		if (position == 0) {
			return new CalculatorFragment();
		}
		return new ConverterFragment();
	}

	@Override
	public int getItemCount() {
		return 2;
	}
}