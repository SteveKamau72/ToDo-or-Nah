package stevekamau.todo;

/**
 * Created by steve on 10/13/17.
 */

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ItemViewHolder extends RecyclerView.ViewHolder {
    public TextView tvTitle;
    public CheckBox checkBox;
    public LinearLayout rowLayout;

    public ItemViewHolder(View itemView) {
        super(itemView);
        tvTitle = (TextView) itemView.findViewById(R.id.title);
        checkBox = (CheckBox) itemView.findViewById(R.id.checkbox);
        rowLayout = (LinearLayout) itemView.findViewById(R.id.layout_row);
    }
}