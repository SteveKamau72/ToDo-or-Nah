package stevekamau.todo;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;

/**
 * Created by steve on 10/13/17.
 */

public class HeaderRecyclerViewSection extends StatelessSection {
    private static final String TAG = HeaderRecyclerViewSection.class.getSimpleName();
    private String title;
    private List<ToDoItem> list;
    private Context context;
    ToDoDB toDoDB;

    public HeaderRecyclerViewSection(String title, List<ToDoItem> list, Context context) {
        super(R.layout.all_todos_header, R.layout.todo_item);
        this.title = title;
        this.list = list;
        this.context = context;
        toDoDB = new ToDoDB(context);
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
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ItemViewHolder iHolder = (ItemViewHolder) holder;
        final ToDoItem toDoItem = list.get(position);
        iHolder.tvTitle.setText(toDoItem.getTitle());
        if (toDoItem.getStatus().equals("inactive")) {
            iHolder.tvTitle.setTextColor(context.getResources().getColor(R.color.dark_grey));
            strikeThroughText(iHolder.tvTitle);
            iHolder.checkBox.setChecked(true);
        } else {
            iHolder.tvTitle.setTextColor(context.getResources().getColor(R.color.black));
            unStrikeThroughText(iHolder.tvTitle);
            iHolder.checkBox.setChecked(false);
        }
        iHolder.rowLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeTaskStatus(position);
                changeTaskView(iHolder.tvTitle, iHolder.checkBox);
            }
        });

    }

    private void changeTaskView(TextView textTitle, CheckBox checkBox) {
        if (checkBox.isChecked()) {
            textTitle.setTextColor(context.getResources().getColor(R.color.dark_grey));
            unStrikeThroughText(textTitle);
            checkBox.setChecked(false);
        } else {
            textTitle.setTextColor(context.getResources().getColor(R.color.black));
            strikeThroughText(textTitle);
            checkBox.setChecked(true);
        }
    }

    private void changeTaskStatus(int position) {
        String status = list.get(position).getStatus();
        if (status.equals("active")) {
            status = "inactive";
        } else {
            status = "active";
        }
        toDoDB.setToDoAsDone(status, list.get(position).getId());
    }

    private void strikeThroughText(TextView textView) {
        textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
    }

    private void unStrikeThroughText(TextView textView) {
        textView.setPaintFlags(textView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
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
