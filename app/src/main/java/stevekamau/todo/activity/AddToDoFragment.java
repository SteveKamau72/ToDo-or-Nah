package stevekamau.todo.activity;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
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
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import stevekamau.todo.R;
import stevekamau.todo.utils.AlarmReceiver;
import stevekamau.todo.utils.TimeDateUtils;
import stevekamau.todo.utils.ToDoDB;
import stevekamau.todo.utils.Toasty;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddToDoFragment extends Fragment implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_to_do, container, false);
        ButterKnife.bind(this, view);
        toDoDB = new ToDoDB(getActivity());
        setViews();
        return view;
    }

    private void setViews() {
        selectedDate = TimeDateUtils.getTodayDate("yyyy/MM/dd");
        selectedTime = TimeDateUtils.getTodayDate("HH:mm:ss");
        edTodo.requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    @OnClick(R.id.fab)
    void addToDoItem() {
        if (TextUtils.isEmpty(edTodo.getText().toString())) {
            edTodo.setError("Come on, say something!");
        } else {
            edTodo.setError(null);
            saveToDB();
            hideKeyboard(getActivity());
            ((MainActivity) getActivity()).closeFragments();
        }

    }

    private void saveToDB() {
        toDoDB.addTodo(edTodo.getText().toString(),
                selectedDate + " " + selectedTime, "active", reminder);
        ((MainActivity) getActivity()).updateTodaysTodos();
        ((MainActivity) getActivity()).setAdapter();
        if (reminder) {
            setReminderAlarm(selectedDate + " " + selectedTime);
        }
        setEveningNotificationForSelectedDate(selectedDate);
        Toasty.success(getActivity(), "Noted!", Toast.LENGTH_LONG, true).show();
    }


    private void setReminderAlarm(String dateTime) {
        Log.e("time_set", selectedDate + " " + selectedTime);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
        try {
            Date date = (Date) formatter.parse(dateTime);
            Intent intentAlarm = new Intent(parentActivity, AlarmReceiver.class);
            intentAlarm.putExtra("title", getString(R.string.app_name));
            intentAlarm.putExtra("message", edTodo.getText().toString());
            // create the object
            AlarmManager alarmManager = (AlarmManager) parentActivity.getSystemService(Context.ALARM_SERVICE);
            final int broadcast_id = (int) date.getTime();
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    parentActivity, broadcast_id, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);
            //set the alarm for particular time
            alarmManager.set(AlarmManager.RTC_WAKEUP, date.getTime(), pendingIntent);
            Log.e("broadcast_id", "" + broadcast_id);
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    private void setEveningNotificationForSelectedDate(String selectedDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
        try {
            Date date = (Date) formatter.parse(selectedDate + " 20:00:00");
            Intent intentAlarm = new Intent(parentActivity, AlarmReceiver.class);
            intentAlarm.putExtra("title", getString(R.string.done_with_day_title));
            intentAlarm.putExtra("message", "Tick off done todos");
            // create the object
            AlarmManager alarmManager = (AlarmManager) parentActivity.getSystemService(Context.ALARM_SERVICE);
            final int broadcast_id = (int) date.getTime();
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    parentActivity, broadcast_id, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);
            //set the alarm for particular time
            alarmManager.set(AlarmManager.RTC_WAKEUP, date.getTime(), pendingIntent);
            Log.e("broadcast_id", "" + broadcast_id);
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
                AddToDoFragment.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.setMinDate(now);
        dpd.show(parentActivity.getFragmentManager(), "Datepickerdialog");
    }

    private void showTimePickerDialog() {
        hideKeyboard(parentActivity);
        Calendar now = Calendar.getInstance();
        TimePickerDialog tpd = TimePickerDialog.newInstance(
                AddToDoFragment.this,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                false
        );
        if (tvDate.getText().toString().equalsIgnoreCase("today")) {
            tpd.setMinTime(now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), now.get(Calendar.SECOND));
        }
        tpd.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                reminderSwitchIcon.setIconEnabled(false);
                reminder = false;
            }
        });
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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        parentActivity = activity;
    }


}
