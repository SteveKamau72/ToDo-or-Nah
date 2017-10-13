package stevekamau.todo;/**
 * Created by steve on 9/26/17.
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DoneTasksFragment extends Fragment {
    private static final String TAG = DoneTasksFragment.class.getSimpleName();
    Activity parentActivity;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    ToDoDB toDoDB;
    ToDoAdapter todoAdapter;

    public DoneTasksFragment() {
    }

    public static DoneTasksFragment newInstance() {
        DoneTasksFragment fragment = new DoneTasksFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_done_tasks, container, false);
        ButterKnife.bind(this, rootView);
        toDoDB = new ToDoDB(parentActivity);
        setViews();
        return rootView;
    }

    private void setViews() {
        setupRecycler();
        setAdapter();
    }

    private void setupRecycler() {
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager since the cards are vertically scrollable
        final LinearLayoutManager layoutManager = new LinearLayoutManager(parentActivity);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
    }

    public void setAdapter() {
        // create an empty adapter and add it to the recycler view
        todoAdapter = new ToDoAdapter(parentActivity, toDoDB.getToDoItems("inactive"));
        recyclerView.setAdapter(todoAdapter);
        todoAdapter.notifyDataSetChanged();
    }

    @OnClick(R.id.cancel)
    void close() {
        ((MainActivity) getActivity()).closeFragments();
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