package com.example.smartcalc.converter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ConversionResultsAdapter extends RecyclerView.Adapter<ConversionResultsAdapter.VH> {
	private final List<ConversionResult> items = new ArrayList<>();

	public void submit(List<ConversionResult> newItems) {
		items.clear();
		items.addAll(newItems);
		notifyDataSetChanged();
	}

	@NonNull
	@Override
	public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
		return new VH(v);
	}

	@Override
	public void onBindViewHolder(@NonNull VH holder, int position) {
		ConversionResult item = items.get(position);
		holder.title.setText(item.unitName);
		if (item.display != null) {
			holder.subtitle.setText(item.display);
		} else {
			holder.subtitle.setText(String.format(Locale.getDefault(), "%.10g", item.value));
		}
	}

	@Override
	public int getItemCount() { return items.size(); }

	static class VH extends RecyclerView.ViewHolder {
		final TextView title;
		final TextView subtitle;
		VH(@NonNull View itemView) {
			super(itemView);
			title = itemView.findViewById(android.R.id.text1);
			subtitle = itemView.findViewById(android.R.id.text2);
		}
	}
}