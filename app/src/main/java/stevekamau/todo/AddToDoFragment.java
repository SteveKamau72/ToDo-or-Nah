package stevekamau.todo;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddToDoFragment extends Fragment {
    @BindView(R.id.todo)
    EditText edTodo;
    private Realm realm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_to_do, container, false);
        ButterKnife.bind(this, view);
        //get realm instance
        this.realm = RealmController.with(this).getRealm();
        setViews();
        return view;
    }

    private void setViews() {
        edTodo.requestFocus();
        InputMethodManager imgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    @OnClick(R.id.fab)
    void addToDoItem() {
        if (TextUtils.isEmpty(edTodo.getText().toString())) {
            edTodo.setError("Come on, say something!");
        } else {
            edTodo.setError(null);
            saveToRealm();
            hideKeyboard(getActivity());
            ((MainActivity) getActivity()).closeFragments();
        }

    }

    private void saveToRealm() {
        ToDoItem todoItem = new ToDoItem();
        todoItem.setId((int) System.currentTimeMillis());
        todoItem.setTitle(edTodo.getText().toString());
        todoItem.setCreatedAt(((MainActivity) getActivity()).getTodayDate("yyyy/MM/dd HH:mm:ss"));
        todoItem.setStatus("active");

        // Persist your data easily
        realm.beginTransaction();
        realm.copyToRealm(todoItem);
        realm.commitTransaction();
        ((MainActivity) getActivity()).setTasksCount();
        Toasty.success(getActivity(), "Noted!", Toast.LENGTH_LONG, true).show();
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

}
