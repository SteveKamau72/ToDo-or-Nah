package stevekamau.todo;

import android.app.Activity;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
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
    @BindView(R.id.emptyList)
    LinearLayout emptyList;
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
        tvDayToday.setText(TimeDateUtils.getTodayDate("EEEE") + " " + TimeDateUtils.getTodayDate("dd"));
        tvMonth.setText(TimeDateUtils.getTodayDate("MMMM"));
        activeTodoItemsList = toDoDB.getTodayToDoItems("active");
        setTasksCount();
        if (activeTodoItemsList.size() > 0) {
            setupRecycler();
            setAdapter();
            emptyList.setVisibility(View.GONE);
        } else {
            emptyList.setVisibility(View.VISIBLE);
        }
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
        toDoDB.setToDoAsDone(status, activeTodoItemsList.get(position).getId());
    }

    private void strikeThroughText(TextView textView) {
        textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
    }

    private void unStrikeThroughText(TextView textView) {
        textView.setPaintFlags(textView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
    }

    public void setAdapter() {
        activeTodoItemsList = toDoDB.getTodayToDoItems("active");
        // create an empty adapter and add it to the recycler view
        todoAdapter = new ToDoAdapter(this, activeTodoItemsList);
        recyclerView.setAdapter(todoAdapter);
        //todoAdapter.notifyDataSetChanged();
    }

    public void setTasksCount() {
        tvTaskNo.setText(activeTodoItemsList.size() + " tasks");
    }

    public void createFragments(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
        fragmentTransaction.replace(R.id.container_body, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void closeFragments() {
        onBackPressed();
    }

    @OnClick(R.id.show_history)
    void showHistory() {
        createFragments((new AllToDOsFragement()));
    }

    @OnClick(R.id.date_layout)
    void selectDate() {
        createFragments((new AllToDOsFragement()));
    }

    @OnClick(R.id.options)
    void displayOptions() {
        createFragments((new OptionsFragment()));
    }

    @Override
    public void onBackPressed() {
        setAdapter();
        int fragments = getSupportFragmentManager().getBackStackEntryCount();
        if (fragments == 0) {
            finish();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onResume() {
        hideKeyboard(this);
        super.onResume();
    }

    @Override
    public void onStart() {
        hideKeyboard(this);
        super.onStart();
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
