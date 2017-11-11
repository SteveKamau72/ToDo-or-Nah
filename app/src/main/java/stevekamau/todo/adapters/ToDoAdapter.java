package stevekamau.todo.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import stevekamau.todo.R;
import stevekamau.todo.models.ToDoItem;
import stevekamau.todo.utils.TimeDateUtils;

/**
 * Created by steve on 9/6/17.
 */

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ToDoViewHolder> {

    final Context context;
    private LayoutInflater inflater;
    private List<ToDoItem> results;
    SimpleDateFormat simpleDateFormat;
    public ToDoAdapter(Context context, List<ToDoItem> results) {
        this.context = context;
        this.results = results;
        simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
    }

    @Override
    public ToDoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // inflate a new card view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.todo_item, parent, false);
        return new ToDoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ToDoViewHolder viewHolder, final int position) {
        // get the item
        final ToDoItem toDoItem = results.get(position);
        // cast the generic view holder to our specific one
        final ToDoViewHolder holder = (ToDoViewHolder) viewHolder;

        // set the title and the snippet
        holder.textTitle.setText(toDoItem.getTitle());

        if (toDoItem.getStatus().equals("inactive")) {
            holder.textTitle.setTextColor(context.getResources().getColor(R.color.dark_grey));
            strikeThroughText(holder.textTitle);
            holder.checkBox.setChecked(true);
        } else {
            holder.textTitle.setTextColor(context.getResources().getColor(R.color.black));
            unStrikeThroughText(holder.textTitle);
            holder.checkBox.setChecked(false);
        }
        //reminder icon
        if (toDoItem.getReminder().equalsIgnoreCase("1")) {
            holder.reminderIcon.setVisibility(View.VISIBLE);
            holder.reminderUpArrow.setVisibility(View.VISIBLE);
            holder.linearLayoutReminderSet.setVisibility(View.VISIBLE);
            String reminderDate = TimeDateUtils.formatDate(toDoItem.getCreatedAt(),
                    "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd");
            String reminderTime = TimeDateUtils.formatDate(toDoItem.getCreatedAt(),
                    "yyyy/MM/dd HH:mm:ss", "HH:mm:ss");
            holder.textSetTime.setText(TimeDateUtils.formatIntoAmPm(reminderTime) + " "
                    + TimeDateUtils.formatDate(reminderDate, "yyyy/MM/dd", "EE dd, MMM"));
            //check if date is past
            try {
                if (simpleDateFormat.parse(toDoItem.getCreatedAt()).before(new Date())) {
                    holder.reminderIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_reminder_past));
                    holder.reminderUpArrow.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_up_arrow_grey));
                    holder.linearLayoutReminderSet.setBackgroundResource(R.drawable.button_shape_grey);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            holder.reminderIcon.setVisibility(View.INVISIBLE);
            holder.reminderUpArrow.setVisibility(View.INVISIBLE);
            holder.linearLayoutReminderSet.setVisibility(View.GONE);
        }
        Log.e("s_track_1", "onBindViewHolder " + toDoItem.getReminder());
    }

    private void strikeThroughText(TextView textView) {
        textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
    }

    private void unStrikeThroughText(TextView textView) {
        textView.setPaintFlags(textView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    public static class ToDoViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.title)
        TextView textTitle;
        @BindView(R.id.time_set_text)
        TextView textSetTime;
        @BindView(R.id.checkbox)
        CheckBox checkBox;
        @BindView(R.id.reminder_icon)
        ImageView reminderIcon;
        @BindView(R.id.reminder_up_arrow)
        ImageView reminderUpArrow;
        @BindView(R.id.reminder_set)
        LinearLayout linearLayoutReminderSet;

        ToDoViewHolder(View itemView) {
            // standard view holder pattern with Butterknife view injection
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
