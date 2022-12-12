package edu.uncc.hw07;

public class ForumComment {

    String created_by, comment_uid, comment_description, comment_datetime, forum_id, comment_id;

    public ForumComment() {
    }

    public ForumComment(String created_by, String comment_uid, String comment_description, String comment_datetime, String forum_id, String comment_id) {
        this.created_by = created_by;
        this.comment_uid = comment_uid;
        this.comment_description = comment_description;
        this.comment_datetime = comment_datetime;
        this.forum_id = forum_id;
        this.comment_id = comment_id;
    }

    public String getCreated_by() {
        return created_by;
    }

    public String getComment_uid() {
        return comment_uid;
    }

    public String getComment_description() {
        return comment_description;
    }

    public String getComment_datetime() {
        return comment_datetime;
    }

    public String getForum_id() {
        return forum_id;
    }

    public String getComment_id() {
        return comment_id;
    }
}
