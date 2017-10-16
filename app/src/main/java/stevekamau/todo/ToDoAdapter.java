package stevekamau.todo;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

/**
 * Created by steve on 9/6/17.
 */

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ToDoViewHolder> {

    final Context context;
    private LayoutInflater inflater;
    private List<ToDoItem> results;

    public ToDoAdapter(Context context, List<ToDoItem> results) {
        this.context = context;
        this.results = results;
    }

    @Override
    public ToDoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // inflate a new card view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.todo_item, parent, false);
        return new ToDoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ToDoViewHolder viewHolder, final int position) {
        Log.e("s_track_1", "onBindViewHolder");
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

        public TextView textTitle;
        public CheckBox checkBox;

        public ToDoViewHolder(View itemView) {
            // standard view holder pattern with Butterknife view injection
            super(itemView);
            textTitle = (TextView) itemView.findViewById(R.id.title);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkbox);
        }
    }
}
