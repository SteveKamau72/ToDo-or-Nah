package stevekamau.todo.adapters;

/**
 * Created by steve on 10/13/17.
 */

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import stevekamau.todo.R;

public class ItemViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.title)
    TextView tvTitle;
    @BindView(R.id.time_set_text)
    TextView textSetTime;
    @BindView(R.id.checkbox)
    CheckBox checkBox;
    @BindView(R.id.layout_row)
    LinearLayout rowLayout;
    @BindView(R.id.reminder_icon)
    ImageView reminderIcon;
    @BindView(R.id.reminder_up_arrow)
    ImageView reminderUpArrow;
    @BindView(R.id.reminder_set)
    LinearLayout linearLayoutReminderSet;

    public ItemViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}