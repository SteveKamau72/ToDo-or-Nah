package stevekamau.todo;/**
 * Created by steve on 10/13/17.
 */

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.ParseException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

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

    private void setViews() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(parentActivity);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        allToDosList = toDoDB.getAllToDoItems();
        groupedDatesList = toDoDB.getAllToDosDates();
        sectionAdapter = new SectionedRecyclerViewAdapter();
        Log.e("grouped_dates", String.valueOf(groupedDatesList));

        for (int i = 0; i < groupedDatesList.size(); i++) {
            try {
                Log.e("grouped_todos", String.valueOf(toDoDB.getToDoItemGrouped(groupedDatesList.get(i))));
                toDosList = toDoDB.getToDoItemGrouped(groupedDatesList.get(i));
                HeaderRecyclerViewSection headerSection = new HeaderRecyclerViewSection(
                        TimeDateUtils.formatDate(groupedDatesList.get(i),
                                "yyyy/MM/dd", "EEEE dd, MMMM"), toDosList
                        , parentActivity);
                sectionAdapter.addSection(headerSection);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        recyclerView.setAdapter(sectionAdapter);
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