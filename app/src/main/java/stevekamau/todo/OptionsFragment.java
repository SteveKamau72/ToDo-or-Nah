package stevekamau.todo;/**
 * Created by steve on 10/16/17.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codemybrainsout.ratingdialog.RatingDialog;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class OptionsFragment extends Fragment {
    private static final String TAG = OptionsFragment.class.getSimpleName();
    Activity parentActivity;

    public OptionsFragment() {
    }

    public static OptionsFragment newInstance() {
        OptionsFragment fragment = new OptionsFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_options, container, false);
        ButterKnife.bind(this, rootView);

        setViews();
        return rootView;
    }

    private void setViews() {
    }

    @OnClick(R.id.cancel)
    void close() {
        ((MainActivity) getActivity()).closeFragments();
    }

    @OnClick(R.id.settings_layout)
    void settings() {
        //((MainActivity) getActivity()).createFragments(new SettingsFragment);
    }

    @OnClick(R.id.feedback_layout)
    void feedBack() {
        startActivity(Intent.createChooser(IntentUtils.sendEmail("stevekamau72@gmail.com",
                "Feedback on ToDo or Nah app", ""), "Send via email"));
    }

    @OnClick(R.id.share_layout)
    void share() {
        startActivity(Intent.createChooser(IntentUtils.shareContent("Simple todo app to keep you on track of your day, download ", "ToDo-or-Nah for Android"), "Share..."));
    }

    @OnClick(R.id.rate_layout)
    void rate() {
        final RatingDialog ratingDialog = new RatingDialog.Builder(getActivity())
                .icon(getResources().getDrawable(R.drawable.launcher))
                .title("We're awesome, yes? Rate us")
                .titleTextColor(R.color.black)
                .negativeButtonText("Not Now")
                .negativeButtonTextColor(R.color.grey_200)
                .threshold(3)
                .formTitle("Submit Feedback")
                .formHint("Tell us where we can improve")
                .formSubmitText("Submit")
                .formCancelText("Cancel")
                .ratingBarColor(R.color.accent)
                .positiveButtonBackgroundColor(R.drawable.button_selector_positive)
                .negativeButtonBackgroundColor(R.drawable.button_selector_negative)
                .onRatingChanged(new RatingDialog.RatingDialogListener() {
                    @Override
                    public void onRatingSelected(float rating, boolean thresholdCleared) {
                        //startActivity(Intent.createChooser(IntentUtils
                    }
                })
                .onRatingBarFormSumbit(new RatingDialog.RatingDialogFormListener() {
                    @Override
                    public void onFormSubmitted(String feedback) {
                        startActivity(Intent.createChooser(IntentUtils.sendEmail("steveKamau72@gmail.com", "FeedBack on ToDo-or-Nah App for Android", feedback), "Email us"));
                    }
                }).build();

        ratingDialog.show();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated: hit");
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume: hit");
        super.onResume();
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