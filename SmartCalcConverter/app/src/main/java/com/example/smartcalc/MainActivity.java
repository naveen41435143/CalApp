package com.example.smartcalc;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartcalc.databinding.ActivityMainBinding;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {
	private ActivityMainBinding binding;
	private SectionsPagerAdapter sectionsPagerAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		binding = ActivityMainBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());

		sectionsPagerAdapter = new SectionsPagerAdapter(this);
		binding.viewPager.setAdapter(sectionsPagerAdapter);
		binding.viewPager.setOffscreenPageLimit(2);

		new TabLayoutMediator(binding.tabLayout, binding.viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
			@Override
			public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
				if (position == 0) {
					tab.setText(R.string.tab_calculator);
					tab.setIcon(R.drawable.ic_calculator);
				} else {
					tab.setText(R.string.tab_converter);
					tab.setIcon(R.drawable.ic_converter);
				}
			}
		}).attach();
	}
}