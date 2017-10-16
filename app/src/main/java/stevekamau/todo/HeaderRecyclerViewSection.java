package stevekamau.todo;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;

/**
 * Created by steve on 10/13/17.
 */

public class HeaderRecyclerViewSection extends StatelessSection {
    private static final String TAG = HeaderRecyclerViewSection.class.getSimpleName();
    private String title;
    private List<ToDoItem> list;

    public HeaderRecyclerViewSection(String title, List<ToDoItem> list) {
        super(R.layout.all_todos_header, R.layout.todo_item);
        this.title = title;
        this.list = list;
    }

    @Override
    public int getContentItemsTotal() {
        return list.size();
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        ItemViewHolder iHolder = (ItemViewHolder) holder;
        iHolder.tvTitle.setText(list.get(position).getTitle());
    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new HeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        HeaderViewHolder hHolder = (HeaderViewHolder) holder;
        hHolder.headerTitle.setText(title);
    }
}
