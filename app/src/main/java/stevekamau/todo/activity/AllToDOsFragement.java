package stevekamau.todo.activity;/**
 * Created by steve on 10/13/17.
 */

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.text.ParseException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;
import stevekamau.todo.R;
import stevekamau.todo.adapters.HeaderRecyclerViewSection;
import stevekamau.todo.models.ToDoItem;
import stevekamau.todo.utils.TimeDateUtils;
import stevekamau.todo.utils.ToDoDB;
import stevekamau.todo.utils.Toasty;

public class AllToDOsFragement extends Fragment {
    private static final String TAG = AllToDOsFragement.class.getSimpleName();
    Activity parentActivity;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    ToDoDB toDoDB;
    private SectionedRecyclerViewAdapter sectionAdapter;
    ArrayList<String> groupedDatesList = new ArrayList<>();
    ArrayList<ToDoItem> toDosList = new ArrayList<>();
    ArrayList<ToDoItem> allToDosList = new ArrayList<>();
    @BindView(R.id.emptyList)
    LinearLayout emptyList;

    public AllToDOsFragement() {
    }

    public static AllToDOsFragement newInstance() {
        AllToDOsFragement fragment = new AllToDOsFragement();
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
        View rootView = inflater.inflate(R.layout.fragment_all_to_dos, container, false);
        ButterKnife.bind(this, rootView);
        toDoDB = new ToDoDB(parentActivity);
        setViews();
        return rootView;
    }

    public void setViews() {
        groupedDatesList.clear();
        toDosList.clear();
        allToDosList.clear();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(parentActivity);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        allToDosList = toDoDB.getAllToDoItems();
        groupedDatesList = toDoDB.getAllToDosDates();
        checkAvailabilityOfTodos();
        sectionAdapter = new SectionedRecyclerViewAdapter();
        Log.e("grouped_dates", String.valueOf(groupedDatesList));

        for (int i = 0; i < groupedDatesList.size(); i++) {
            try {
                Log.e("grouped_todos", String.valueOf(toDoDB.getToDoItemGrouped(groupedDatesList.get(i))));
                toDosList = toDoDB.getToDoItemGrouped(groupedDatesList.get(i));
                HeaderRecyclerViewSection headerSection = new HeaderRecyclerViewSection(
                        TimeDateUtils.formatDate(groupedDatesList.get(i),
                                "yyyy/MM/dd", "EEEE dd, MMMM"), toDosList
                        , parentActivity, AllToDOsFragement.this);
                sectionAdapter.addSection(headerSection);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        recyclerView.setAdapter(sectionAdapter);
    }

    private void checkAvailabilityOfTodos() {
        if (allToDosList.size() > 0) {
            emptyList.setVisibility(View.GONE);
        } else {
            emptyList.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated: hit");
        super.onActivityCreated(savedInstanceState);
    }

    @OnClick(R.id.cancel)
    void close() {
        parentActivity.onBackPressed();
    }

    @OnClick(R.id.delete)
    void deleteAll() {
        if (toDoDB.getAllToDoItems().size() > 0) {
            AlertDialog alertDialog = new AlertDialog.Builder(parentActivity).create();
            alertDialog.setTitle("Whooaaa!");
            alertDialog.setMessage("Delete all ToDo's?");
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            toDoDB.deleteAllTodos();
                            ((MainActivity) parentActivity).setViews();
                        }
                    });
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NAH",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        } else {
            Toasty.info(getActivity(), "Nothing to delete here!", Toast.LENGTH_LONG, true).show();
        }
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