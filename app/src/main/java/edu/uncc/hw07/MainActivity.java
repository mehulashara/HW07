package edu.uncc.hw07;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements LoginFragment.LoginListener, SignUpFragment.SignUpListener, ForumsFragment.ForumsListener, CreateForumFragment.CreateForumsListner {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.rootView, new LoginFragment())
                    .commit();
        } else {
            login();
        }
    }

    @Override
    public void createNewAccount() {
        getSupportFragmentManager().beginTransaction().replace(R.id.rootView, new SignUpFragment())
                .commit();
    }

    @Override
    public void login() {
        gotoForumsFragment();
        //getSupportFragmentManager().beginTransaction().replace(R.id.rootView, new ForumsFragment(), "TAG").commit();
    }

    @Override
    public void gotoForumsFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.rootView, new ForumsFragment()).addToBackStack(null).commit();
    }

    @Override
    public void buttonNewForum() {
        getSupportFragmentManager().beginTransaction().replace(R.id.rootView, new CreateForumFragment()).addToBackStack(null).commit();
    }

    @Override
    public void logout() {
        FirebaseAuth.getInstance().signOut();
        getSupportFragmentManager().beginTransaction().replace(R.id.rootView, new LoginFragment()).commit();
    }

    @Override
    public void gotoForumFragment(String forum_title, String created_by, String forum_description, String forum_id) {
        getSupportFragmentManager().beginTransaction().replace(R.id.rootView, ForumFragment.newInstance(forum_title, created_by, forum_description, forum_id)).addToBackStack(null).commit();
    }

    @Override
    public void buttonCancel() {
        getSupportFragmentManager().popBackStack();
    }
}