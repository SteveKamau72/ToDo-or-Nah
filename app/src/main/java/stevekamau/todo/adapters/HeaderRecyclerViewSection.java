package stevekamau.todo.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;
import stevekamau.todo.R;
import stevekamau.todo.activity.AllToDOsFragement;
import stevekamau.todo.activity.MainActivity;
import stevekamau.todo.models.ToDoItem;
import stevekamau.todo.utils.TimeDateUtils;
import stevekamau.todo.utils.ToDoDB;

/**
 * Created by steve on 10/13/17.
 */

public class HeaderRecyclerViewSection extends StatelessSection {
    private static final String TAG = HeaderRecyclerViewSection.class.getSimpleName();
    private String title;
    private List<ToDoItem> list;
    private Context context;
    ToDoDB toDoDB;
    AllToDOsFragement allToDOsFragement;
    SimpleDateFormat simpleDateFormat;

    public HeaderRecyclerViewSection(String title, List<ToDoItem> list, Context context, AllToDOsFragement allToDOsFragement) {
        super(R.layout.all_todos_header, R.layout.todo_item);
        this.title = title;
        this.list = list;
        this.context = context;
        toDoDB = new ToDoDB(context);
        this.allToDOsFragement = allToDOsFragement;
        simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
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
            iHolder.linearLayoutReminderSet.setBackgroundResource(R.drawable.button_shape_grey);
        } else {
            iHolder.tvTitle.setTextColor(context.getResources().getColor(R.color.black));
            unStrikeThroughText(iHolder.tvTitle);
            iHolder.checkBox.setChecked(false);
            iHolder.linearLayoutReminderSet.setBackgroundResource(R.drawable.button_shape_mauvre);
        }
        iHolder.rowLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeTaskStatus(position);
                changeTaskView(iHolder.tvTitle, iHolder.checkBox);
                ((MainActivity) context).setViews();
            }
        });

        iHolder.rowLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ((MainActivity) context).callBottomSheet(toDoItem);
                allToDOsFragement.setViews();
                return true;
            }
        });

        //reminder icon
        if (toDoItem.getReminder().equalsIgnoreCase("1")) {
            iHolder.reminderIcon.setVisibility(View.VISIBLE);
            iHolder.linearLayoutReminderSet.setVisibility(View.VISIBLE);
            String reminderDate = TimeDateUtils.formatDate(toDoItem.getCreatedAt(),
                    "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd");
            String reminderTime = TimeDateUtils.formatDate(toDoItem.getCreatedAt(),
                    "yyyy/MM/dd HH:mm:ss", "HH:mm:ss");
            iHolder.textSetTime.setText(TimeDateUtils.formatIntoAmPm(reminderTime) + " "
                    + TimeDateUtils.formatDate(reminderDate, "yyyy/MM/dd", "EE dd, MMM"));
            //check if date is past
            try {
                if (simpleDateFormat.parse(toDoItem.getCreatedAt()).before(new Date())) {
                    iHolder.reminderIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_reminder_past));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            iHolder.reminderIcon.setVisibility(View.INVISIBLE);
            iHolder.linearLayoutReminderSet.setVisibility(View.GONE);
        }

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
