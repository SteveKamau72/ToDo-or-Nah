package stevekamau.todo;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by steve on 9/6/17.
 */

public class ToDoItem extends RealmObject {

    private int id;
    //@PrimaryKey
    private String title;
    private String createdAt;
    private String status;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
