package stevekamau.todo.activity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.codemybrainsout.ratingdialog.RatingDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;
import stevekamau.todo.R;
import stevekamau.todo.adapters.ToDoAdapter;
import stevekamau.todo.models.ToDoItem;
import stevekamau.todo.utils.AlarmReceiver;
import stevekamau.todo.utils.IntentUtils;
import stevekamau.todo.utils.RecyclerItemClickListener;
import stevekamau.todo.utils.TimeDateUtils;
import stevekamau.todo.utils.ToDoDB;
import stevekamau.todo.utils.Toasty;

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
    @BindView(R.id.show_history)
    LinearLayout linearLayoutShowHistory;
    @BindView(R.id.bottom_sheet)
    View viewBottomSheet;
    ToDoAdapter todoAdapter;
    FragmentTransaction fragmentTransaction;
    List<ToDoItem> activeTodoItemsList = new ArrayList<>();
    ToDoDB toDoDB;
    int todo_id;
    private BottomSheetBehavior bottomSheetBehavior;
    SharedPreferences sharedPreferences;
    private PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        toDoDB = new ToDoDB(this);
        setViews();
        setUpMorningDailyNotification(7, "morning");
        sharedPreferences = getSharedPreferences("general_settings", MODE_PRIVATE);
    }


    public void setViews() {
        activeTodoItemsList.clear();
        tvDayToday.setText(TimeDateUtils.getTodayDate("EEEE") + " " + TimeDateUtils.getTodayDate("dd"));
        tvMonth.setText(TimeDateUtils.getTodayDate("MMMM"));
        activeTodoItemsList = toDoDB.getTodayToDoItems("active");
        setTasksCount();
        setupRecycler();
        setAdapter();
        checkAvailabilityOfTodos();

        bottomSheetBehavior = BottomSheetBehavior.from(viewBottomSheet);
        bottomSheetBehavior.setHideable(true);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

    }

    private void checkAvailabilityOfTodos() {
        if (activeTodoItemsList.size() > 0) {
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
                        ToDoItem todoItem = activeTodoItemsList.get(position);
                        callBottomSheet(todoItem);
                    }
                })
        );
    }

    private void changeTaskView(View view) {
        TextView textTitle = view.findViewById(R.id.title);
        CheckBox checkBox = view.findViewById(R.id.checkbox);
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

    public void updateTodaysTodos() {
        activeTodoItemsList = toDoDB.getTodayToDoItems("active");
        checkAvailabilityOfTodos();
    }

    public void setAdapter() {
        Log.e("size_items", "-" + activeTodoItemsList.size());
        // create an empty adapter and add it to the recycler view
        todoAdapter = new ToDoAdapter(this, activeTodoItemsList);
        recyclerView.setAdapter(todoAdapter);
    }

    public void setTasksCount() {
        tvTaskNo.setText(activeTodoItemsList.size() + " tasks");
    }

    public void createFragments(Fragment fragment) {
        linearLayoutShowHistory.setVisibility(View.INVISIBLE);
        hideBottomSheet();
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

    @OnClick(R.id.delete_layout)
    void deleteToDO() {
        //remove alarm
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
        try {
            Intent intentAlarm = new Intent(this, AlarmReceiver.class);
            Date date = (Date) formatter.parse(toDoDB.getToDoItemById(String.valueOf(todo_id)).get(0).
                    getCreatedAt());
            final int broadcast_id = (int) date.getTime();
            Log.e("broadcast_id_", "" + broadcast_id);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    getApplicationContext(), broadcast_id, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager aManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            aManager.cancel(pendingIntent);

            //delete from db
            toDoDB.deleteToDo(String.valueOf(todo_id));
            Toasty.success(this, "Deleted!", Toast.LENGTH_LONG, true).show();
            hideBottomSheet();
            updateTodaysTodos();
            setAdapter();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.edit_layout)
    void editToDo() {
        createFragments((new EditToDoFragment()));
    }

    @OnTouch(R.id.main_content)
    boolean mainLayoutTouch() {
        hideBottomSheet();
        return false;
    }

    private void hideBottomSheet() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
    }

    @Override
    public void onBackPressed() {
        //setAdapter();
        int fragments = getSupportFragmentManager().getBackStackEntryCount();
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        } else {
            if (fragments == 0) {
                finish();
            } else {
                super.onBackPressed();
                linearLayoutShowHistory.setVisibility(View.VISIBLE);
            }
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

        if (sharedPreferences.getBoolean("new_update", false)) {
            showUpdateDialog();
        }
        ratingDialog();
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

    private void showUpdateDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("New Update Available!");
        alertDialog.setMessage("Upgrade to get the latest version of " + getString(R.string.app_name));
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "UPDATE",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        startActivity(Intent.createChooser(IntentUtils.openPlayStore(getApplicationContext(), false), ""));
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("new_update", false);
                        editor.apply();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    public String getSelectedToDoItemId() {
        return String.valueOf(todo_id);
    }

    public void callBottomSheet(ToDoItem toDoItem) {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        todo_id = toDoItem.getId();
    }

    private void ratingDialog() {
        final RatingDialog ratingDialog = new RatingDialog.Builder(MainActivity.this)
                .icon(getResources().getDrawable(R.drawable.launcher))
                .title("We're awesome, yes? Rate us")
                .titleTextColor(R.color.black)
                .threshold(5)
                .session(5)
                .formTitle("Submit Feedback")
                .formHint("Tell us where we can improve")
                .formSubmitText("Submit")
                .formCancelText("Cancel")
                .ratingBarColor(R.color.colorPrimary)
                .positiveButtonTextColor(R.color.white)
                .positiveButtonBackgroundColor(R.drawable.button_selector_positive)
                .negativeButtonBackgroundColor(R.drawable.button_selector_negative)
                .onRatingChanged(new RatingDialog.RatingDialogListener() {
                    @Override
                    public void onRatingSelected(float rating, boolean thresholdCleared) {

                    }
                })
                .onRatingBarFormSumbit(new RatingDialog.RatingDialogFormListener() {
                    @Override
                    public void onFormSubmitted(String feedback) {
                        startActivity(Intent.createChooser(IntentUtils.sendEmail("steveKamau72@gmail.com", "FeedBack on ToDo-or-Nah for Android", feedback), "Email us"));
                    }
                }).build();

        ratingDialog.show();
    }

    private void setUpMorningDailyNotification(int hour, String mode) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        long timeToAlarm = calendar.getTimeInMillis();
        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            timeToAlarm += (24 * 60 * 60 * 1000);
        }
        Intent intent1 = new Intent(MainActivity.this, AlarmReceiver.class);
        intent1.putExtra("title", getString(R.string.morning_welcome_title));
        intent1.putExtra("message", "Tap to add something to do today");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                MainActivity.this, 0, intent1,
                PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) MainActivity.this
                .getSystemService(MainActivity.this.ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP, timeToAlarm,
                AlarmManager.INTERVAL_DAY, pendingIntent);
    }
}
