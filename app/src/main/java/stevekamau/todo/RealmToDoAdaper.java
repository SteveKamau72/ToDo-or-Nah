package stevekamau.todo;

import android.content.Context;

import io.realm.RealmResults;

/**
 * Created by steve on 9/6/17.
 */

public class RealmToDoAdaper extends RealmModelAdapter<ToDoItem> {

    public RealmToDoAdaper(Context context, RealmResults<ToDoItem> realmResults, boolean automaticUpdate) {

        super(context, realmResults, automaticUpdate);
    }
}
