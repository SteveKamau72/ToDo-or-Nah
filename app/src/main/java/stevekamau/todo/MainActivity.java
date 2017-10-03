package stevekamau.todo;

import android.graphics.Paint;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.day_today)
    TextView tvDayToday;
    @BindView(R.id.month)
    TextView tvMonth;
    @BindView(R.id.tasks_no)
    TextView tvTaskNo;
    ToDoAdapter todoAdapter;
    FragmentTransaction fragmentTransaction;
    List<ToDoItem> activeTodoItemsList = new ArrayList<>();
    ToDoDB toDoDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        toDoDB = new ToDoDB(this);
        setViews();
    }

    private void setViews() {
        tvDayToday.setText(getTodayDate("EEEE") + " " + getTodayDate("dd"));
        tvMonth.setText(getTodayDate("MMMM"));
        setupRecycler();
        setAdapter();
    }

    @OnClick(R.id.fab)
    void addToDoItem() {
        createFragments(new AddToDoFragment());
    }

    private void setupRecycler() {
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager since the cards are vertically scrollable
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // changeTaskStatus(position);
                        changeTaskView(view);
                        changeTaskStatus(position);

                        //setAdapter();
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );
    }

    private void changeTaskView(View view) {
        TextView textTitle = (TextView) view.findViewById(R.id.title);
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
        if (checkBox.isChecked()) {
            textTitle.setTextColor(getResources().getColor(R.color.dark_grey));
            unStrikeThroughText(textTitle);
            checkBox.setChecked(false);
        } else {
            textTitle.setTextColor(getResources().getColor(R.color.black));
            strikeThroughText(textTitle);
            checkBox.setChecked(true);
        }
    }

    private void changeTaskStatus(int position) {
        String status = activeTodoItemsList.get(position).getStatus();
        if (status.equals("active")) {
            status = "inactive";
        } else {
            status = "active";
        }
        toDoDB.setToDoAsDone(status, activeTodoItemsList.get(position).getTitle());
    }

    private void strikeThroughText(TextView textView) {
        textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
    }

    private void unStrikeThroughText(TextView textView) {
        textView.setPaintFlags(textView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
    }

    public void setAdapter() {
        activeTodoItemsList = toDoDB.getToDoItems("active");
        // create an empty adapter and add it to the recycler view
        todoAdapter = new ToDoAdapter(this, activeTodoItemsList);
        recyclerView.setAdapter(todoAdapter);
        //todoAdapter.notifyDataSetChanged();
        setTasksCount();
    }

    public void setTasksCount() {
        tvTaskNo.setText(activeTodoItemsList.size() + " tasks");
    }

    private void createFragments(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
        fragmentTransaction.replace(R.id.container_body, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public String getTodayDate(String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date());
    }

    public void closeFragments() {
        createFragments(new EmptyFragment());
    }

    @OnClick(R.id.show_history)
    void showHistory() {
        createFragments(new DoneTasksFragment());
    }

    @Override
    public void onBackPressed() {

        int fragments = getSupportFragmentManager().getBackStackEntryCount();
        if (fragments == 0) {
            finish();
        } else {
            super.onBackPressed();
        }
    }

}
