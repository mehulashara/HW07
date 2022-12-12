package edu.uncc.hw07;

import java.util.ArrayList;

public class Forum {

    String forum_title,forum_description, forum_user_id, created_at, created_by, forum_id;
    ArrayList<String> likes;

    public Forum() {
    }

    public Forum(String forum_title, String forum_description, String forum_user_id, String created_at, String created_by, String forum_id, ArrayList<String> likes) {
        this.forum_title = forum_title;
        this.forum_description = forum_description;
        this.forum_user_id = forum_user_id;
        this.created_at = created_at;
        this.created_by = created_by;
        this.forum_id = forum_id;
        this.likes = likes;
    }

    public String getForum_title() {
        return forum_title;
    }

    public String getForum_description() {
        return forum_description;
    }

    public String getForum_user_id() {
        return forum_user_id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getCreated_by() {
        return created_by;
    }

    public String getForum_id() {
        return forum_id;
    }

    public ArrayList<String> getLikes() {
        return likes;
    }
}
