package com.example.myworkapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class CollectAdapter extends ArrayAdapter<CollectItem> {
    public CollectAdapter(Context context, List<CollectItem> items) {
        super(context, 0, items);
    }

    @Override
   public View getView(int position, View convertView, ViewGroup parent) {
        CollectItem item = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.list_item, parent, false);
        }

        TextView titleView = convertView.findViewById(R.id.item_title);
        TextView contentView = convertView.findViewById(R.id.item_content);

        titleView.setText(item.getTitle());
        contentView.setText(item.getContent());

        return convertView;
    }
}
