package stevekamau.todo;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.day_today)
    TextView tvDayToday;
    @BindView(R.id.month)
    TextView tvMonth;
    @BindView(R.id.tasks_no)
    TextView tvTaskNo;
    Realm realm;
    ToDoAdapter todoAdapter;
    FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        //get realm instance
        this.realm = RealmController.with(this).getRealm();
        setViews();
    }

    private void setViews() {
        tvDayToday.setText(getTodayDate("EEEE") + " " + getTodayDate("dd"));
        tvMonth.setText(getTodayDate("MMMM"));
        setupRecycler();

        if (RealmController.with(this).getToDoItems().size() > 0) {

        } else {
            //no items
        }

        // refresh the realm instance
        RealmController.with(this).refresh();
        // get all persisted objects
        // create the helper adapter and notify data set changes
        // changes will be reflected automatically
        setRealmAdapter(RealmController.with(this).getToDoItems());
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

        // create an empty adapter and add it to the recycler view
        todoAdapter = new ToDoAdapter(this);
        recyclerView.setAdapter(todoAdapter);
    }

    public void setRealmAdapter(RealmResults<ToDoItem> realmResults) {
        RealmModelAdapter realmModelAdapter = new RealmModelAdapter(this.getApplicationContext(), realmResults, true);
        // Set the data and tell the RecyclerView to draw
        todoAdapter.setRealmAdapter(realmModelAdapter);
        todoAdapter.notifyDataSetChanged();
        setTasksCount();
    }

    public void setTasksCount() {
        tvTaskNo.setText(RealmController.with(this).getToDoItems().size() + " tasks");
    }

    private void createFragments(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
        fragmentTransaction.replace(R.id.container_body, fragment);
        fragmentTransaction.commit();
    }

    public String getTodayDate(String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date());
    }

    public void closeFragments() {
        createFragments(new EmptyFragment());
    }
}
