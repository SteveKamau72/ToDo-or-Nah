package stevekamau.todo.activity;/**
 * Created by steve on 10/18/17.
 */

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.zagum.switchicon.SwitchIconView;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import stevekamau.todo.R;
import stevekamau.todo.models.ToDoItem;
import stevekamau.todo.utils.AlarmReceiver;
import stevekamau.todo.utils.TimeDateUtils;
import stevekamau.todo.utils.ToDoDB;
import stevekamau.todo.utils.Toasty;

public class EditToDoFragment extends Fragment implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {
    private static final String TAG = EditToDoFragment.class.getSimpleName();
    @BindView(R.id.todo)
    EditText edTodo;
    @BindView(R.id.switchIconView)
    SwitchIconView reminderSwitchIcon;
    @BindView(R.id.reminder_time)
    TextView tvReminderTime;
    @BindView(R.id.date)
    TextView tvDate;
    ToDoDB toDoDB;
    Activity parentActivity;
    String selectedDate, selectedTime;
    Boolean reminder = false;
    ArrayList<ToDoItem> toDoItemArrayList = new ArrayList<>();

    public EditToDoFragment() {
    }

    public static EditToDoFragment newInstance() {
        EditToDoFragment fragment = new EditToDoFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: hit");
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: hit");
        View rootView = inflater.inflate(R.layout.fragment_edit_todo, container, false);
        ButterKnife.bind(this, rootView);
        toDoDB = new ToDoDB(getActivity());
        setViews();
        return rootView;
    }

    private void setViews() {
        toDoItemArrayList = toDoDB.getToDoItemById(((MainActivity) getActivity()).getSelectedToDoItemId());
        selectedDate = TimeDateUtils.formatDate(toDoItemArrayList.get(0).getCreatedAt(),
                "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd");
        selectedTime = TimeDateUtils.formatDate(toDoItemArrayList.get(0).getCreatedAt(),
                "yyyy/MM/dd HH:mm:ss", "HH:mm:ss");
        tvDate.setText(TimeDateUtils.formatDate(selectedDate, "yyyy/MM/dd", "EE dd, MMM"));
        edTodo.setText(toDoItemArrayList.get(0).getTitle());
        edTodo.requestFocus();


        if (toDoItemArrayList.get(0).getReminder().equals("1")) {
            reminder = true;
            reminderSwitchIcon.setIconEnabled(true);
            tvReminderTime.setText(TimeDateUtils.formatIntoAmPm(selectedTime));
        } else {
            reminder = false;
            reminderSwitchIcon.setIconEnabled(false);
        }
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    @OnClick(R.id.fab)
    void addToDoItem() {
        if (TextUtils.isEmpty(edTodo.getText().toString())) {
            edTodo.setError("Come on, say something!");
        } else {
            edTodo.setError(null);
            editDB();
            hideKeyboard(getActivity());
            ((MainActivity) getActivity()).closeFragments();
        }

    }

    private void editDB() {
        toDoDB.updateToDoItem(String.valueOf(toDoItemArrayList.get(0).getId()), edTodo.getText().toString(),
                selectedDate + " " + selectedTime, "active", reminder);
        ((MainActivity) getActivity()).updateTodaysTodos();
        ((MainActivity) getActivity()).setAdapter();
        if (reminder) {
            setReminderAlarm(selectedDate + " " + selectedTime);
        }
        Toasty.success(getActivity(), "Noted!", Toast.LENGTH_LONG, true).show();
    }

    private void setReminderAlarm(String dateTime) {
        Log.e("time_set", selectedDate + " " + selectedTime);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
        Date date = null;
        try {
            date = (Date) formatter.parse(dateTime);
            Intent intentAlarm = new Intent(parentActivity, AlarmReceiver.class);
            intentAlarm.putExtra("todo", edTodo.getText().toString());
            // create the object
            AlarmManager alarmManager = (AlarmManager) parentActivity.getSystemService(Context.ALARM_SERVICE);
            final int broadcast_id = 0;
            //set the alarm for particular time
            alarmManager.set(AlarmManager.RTC_WAKEUP, date.getTime(), PendingIntent.getBroadcast(parentActivity, broadcast_id, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    @OnClick(R.id.cancel)
    void close() {
        hideKeyboard(getActivity());
        ((MainActivity) getActivity()).closeFragments();
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

    @OnClick(R.id.date_layout)
    void selectDate() {
        //((MainActivity) getActivity()).createFragments((new DateFragment()));
        showDatePickerDialog();
    }

    @OnClick(R.id.reminder_layout)
    void addReminder() {
        if (reminderSwitchIcon.isIconEnabled()) {
            reminderSwitchIcon.setIconEnabled(false);
            reminder = false;
        } else {
            reminderSwitchIcon.setIconEnabled(true);
            showTimePickerDialog();
            reminder = true;
        }

    }

    private void showDatePickerDialog() {
        hideKeyboard(parentActivity);
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                EditToDoFragment.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.show(parentActivity.getFragmentManager(), "Datepickerdialog");
    }

    private void showTimePickerDialog() {
        hideKeyboard(parentActivity);
        Calendar now = Calendar.getInstance();
        TimePickerDialog tpd = TimePickerDialog.newInstance(
                EditToDoFragment.this,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                false
        );
        tpd.show(parentActivity.getFragmentManager(), "Timepickerdialog");
    }

    @Override
    public void onResume() {
        edTodo.setFocusable(true);

        super.onResume();
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        String hourString = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
        String minuteString = minute < 10 ? "0" + minute : "" + minute;
        String secondString = second < 10 ? "0" + second : "" + second;
        Log.e("time_string", hourString + ":" + minuteString + ":" + secondString);
        selectedTime = hourString + ":" + minuteString + ":" + secondString;
        tvReminderTime.setText(TimeDateUtils.formatIntoAmPm(selectedTime));
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        selectedDate = year + "/" + (++monthOfYear) + "/" + dayOfMonth;
        tvDate.setText(TimeDateUtils.formatDate(selectedDate, "yyyy/MM/dd", "EE dd, MMM"));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated: hit");
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void onPause() {
        Log.d(TAG, "onPause: hit");
        super.onPause();
    }


    @Override
    public void onDestroyView() {
        Log.d(TAG, "onDestroyView: hit");
        super.onDestroyView();
    }


    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: hit");
        super.onDestroy();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        parentActivity = activity;
    }
} 