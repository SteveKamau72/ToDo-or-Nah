package stevekamau.todo;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by steve on 9/6/17.
 */

public class ToDoAdapter extends RealmRecyclerViewAdapter<ToDoItem> {

    final Context context;
    private Realm realm;
    private LayoutInflater inflater;

    public ToDoAdapter(Context context) {

        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // inflate a new card view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.todo_item, parent, false);
        return new ToDoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        realm = RealmController.getInstance().getRealm();

        // get the itme
        final ToDoItem toDoItem = getItem(position);
        // cast the generic view holder to our specific one
        final ToDoViewHolder holder = (ToDoViewHolder) viewHolder;

        // set the title and the snippet
        holder.textTitle.setText(toDoItem.getTitle());
        holder.checkBox.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        String isActive = "";
                        if (isChecked) {
                            holder.textTitle.setTextColor(context.getResources().getColor(R.color.dark_grey));
                            strikeThroughText(holder.textTitle);
                            isActive = "inactive";
                        } else {
                            holder.textTitle.setTextColor(context.getResources().getColor(R.color.black));
                            unStrikeThroughText(holder.textTitle);
                            isActive = "active";
                        }
                        RealmResults<ToDoItem> results = realm.where(ToDoItem.class).findAll();
                        realm.beginTransaction();
                        results.get(position).setTitle(toDoItem.getTitle());
                        results.get(position).setCreatedAt(toDoItem.getCreatedAt());
                        results.get(position).setStatus(isActive);
                        realm.copyToRealmOrUpdate(results);
                        realm.commitTransaction();

                    }
                }

        );
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
        if (getRealmAdapter() != null) {
            return getRealmAdapter().getCount();
        }
        return 0;
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
