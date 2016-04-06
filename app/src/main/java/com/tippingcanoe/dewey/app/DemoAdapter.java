package com.tippingcanoe.dewey.app;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class DemoAdapter extends RecyclerView.Adapter<DemoAdapter.DeweyViewHolder> {
	protected List<String> sentances;

	public DemoAdapter () {
		sentances = getRandomSentances();
	}

	@Override
	public DeweyViewHolder onCreateViewHolder ( ViewGroup parent, int viewType ) {
		View deweyCell = LayoutInflater.from(parent.getContext()).inflate(R.layout.dewey_cell, parent, false);

		//TextView deweyCell = new TextView(parent.getContext());
		//deweyCell.setLayoutParams(new RecyclerView.LayoutParams(40, ViewGroup.LayoutParams.MATCH_PARENT));
		//deweyCell.setGravity(Gravity.CENTER_HORIZONTAL);
		//deweyCell.setPadding(0, 10, 0, 10);

		return new DeweyViewHolder(deweyCell);
	}

	@Override
	public void onBindViewHolder ( DeweyViewHolder holder, int position ) {
		holder.label.setText(position + "\n" + sentances.get(position));
	}

	@Override
	public int getItemCount () {
		return 10000;
	}

	protected List<String> getRandomSentances () {
		ArrayList<String> words = new ArrayList<>(Arrays.asList("lorem", "ipsum", "dolor", "sit", "amet", "consectetur", "adipiscing", "elit", "curabitur", "vel", "hendrerit", "libero", "eleifend", "blandit", "nunc", "ornare", "odio", "ut", "orci", "gravida", "imperdiet", "nullam", "purus", "lacinia", "a", "pretium", "quis", "congue", "praesent", "sagittis", "laoreet", "auctor", "mauris", "non", "velit", "eros", "dictum", "proin", "accumsan", "sapien", "nec", "massa", "volutpat", "venenatis", "sed", "eu", "molestie", "lacus", "quisque", "porttitor", "ligula", "dui", "mollis", "tempus", "at", "magna", "vestibulum", "turpis", "ac", "diam", "tincidunt", "id", "condimentum", "enim", "sodales", "in", "hac", "habitasse", "platea", "dictumst", "aenean", "neque", "fusce", "augue", "leo", "eget", "semper", "mattis", "tortor", "scelerisque", "nulla", "interdum", "tellus", "malesuada", "rhoncus", "porta", "sem", "aliquet", "et", "nam", "suspendisse", "potenti", "vivamus", "luctus", "fringilla", "erat", "donec", "justo", "vehicula", "ultricies", "varius", "ante", "primis", "faucibus", "ultrices", "posuere", "cubilia", "curae", "etiam", "cursus", "aliquam", "quam", "dapibus", "nisl", "feugiat", "egestas", "class", "aptent", "taciti", "sociosqu", "ad", "litora", "torquent", "per", "conubia", "nostra", "inceptos", "himenaeos", "phasellus", "nibh", "pulvinar", "vitae", "urna", "iaculis", "lobortis", "nisi", "viverra", "arcu", "morbi", "pellentesque", "metus", "commodo", "ut", "facilisis", "felis", "tristique", "ullamcorper", "placerat", "aenean", "convallis", "sollicitudin", "integer", "rutrum", "duis", "est", "etiam", "bibendum", "donec", "pharetra", "vulputate", "maecenas", "mi", "fermentum", "consequat", "suscipit", "aliquam", "habitant", "senectus", "netus", "fames", "quisque", "euismod", "curabitur", "lectus", "elementum", "tempor", "risus", "cras"));
		List<String> sentances = new ArrayList<>();

		Random random = new Random();
		final int LOW = 1;
		final int HIGH = 1;

		for (int i = 0; i <= getItemCount(); i++) {
			String sentance = "";

			for (int j = 0; j <= (HIGH - LOW == 0 ? 0 : random.nextInt(HIGH - LOW) + LOW); j++) {
				sentance += words.get(random.nextInt(words.size() - 1)) + " ";
			}

			sentances.add(sentance);
		}

		return sentances;
	}

	protected final static class DeweyViewHolder extends RecyclerView.ViewHolder {
		TextView label;

		public DeweyViewHolder ( View itemView ) {
			super(itemView);
			label = (TextView) itemView;
		}
	}
}
