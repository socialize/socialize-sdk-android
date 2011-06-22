package com.socialize.sample;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ListAdapter extends ArrayAdapter<ListItem> {

	private Context context;
	private List<ListItem> objects;
	
	public ListAdapter(Context context, int textViewResourceId, List<ListItem> objects) {
		super(context, textViewResourceId, objects);
		this.context = context;
		this.objects = objects;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;

		ViewHolder holder = null;

		if (row == null) {
			holder = new ViewHolder();
			LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = vi.inflate(R.layout.list_row, null);
			TextView text = (TextView) row.findViewById(R.id.row);
			holder.setText(text);
			row.setTag(holder);
		}
		else {
			holder = (ViewHolder) row.getTag();
		}

		ListItem data = objects.get(position);

		if(data != null) {
			holder.getText().setText(data.getName());
		}

		return row;
	}
}
