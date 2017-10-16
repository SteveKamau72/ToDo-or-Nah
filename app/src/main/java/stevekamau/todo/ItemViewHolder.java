package stevekamau.todo;

/**
 * Created by steve on 10/13/17.
 */

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class ItemViewHolder extends RecyclerView.ViewHolder {
    public TextView tvTitle;

    public ItemViewHolder(View itemView) {
        super(itemView);
        tvTitle = (TextView) itemView.findViewById(R.id.title);
    }
}