package com.titfer.Activties.Insert;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.titfer.Models.AlbumModel;
import com.titfer.Models.CommentModel;
import com.titfer.Models.ReportModel;
import com.titfer.R;
import com.titfer.app.AppContoller;
import com.titfer.app.ConnStatesCallBack;
import com.titfer.app.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class WriteReportActivity extends AppCompatActivity implements com.titfer.app.ConnStatesCallBack.FirebaseConnectionState {

    FirebaseDatabase database ;


    @BindView(R.id.title_input)
    EditText title_input ;


    @BindView(R.id.done)
    View done ;

    @BindView(R.id.cancel)
    View cancel ;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_comment);
        ButterKnife.bind(this) ;
        if (savedInstanceState == null)

        database = FirebaseDatabase.getInstance(Constants.Ref);
        title_input.setHint("write your problem");

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/CaviarDreamsBold.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );


    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @OnClick(R.id.cancel)
    void cancel(){
        onBackPressed();

    }
    @BindView(R.id.progressView)
    View progrssView ;
    @BindView(R.id.container)
    View container ;


    private void showloading(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        container.setVisibility(show ? View.GONE : View.VISIBLE);
        container.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                container.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        progrssView.setVisibility(show ? View.VISIBLE : View.GONE);
        progrssView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                progrssView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    public void onBackPressed() {
        supportFinishAfterTransition();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }



    @OnClick(R.id.done)
    void done(){
        if (TextUtils.isEmpty(title_input.getText().toString())){
            title_input.setError("please write a problem ");
            return;
        }
        showloading(true);
        DatabaseReference commentsRef =  database.getReference("Reports");
        String key = commentsRef.push().getKey() ;
        ReportModel reportModel = new   ReportModel(title_input.getText().toString(),
                        AppContoller.getInstance().getPrefManager().getUser().getId()
        );
        title_input.setText("");

        current_valueEventListener = true ;
        commentsRef.child(key).setValue(reportModel).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                onBackPressed();
            }
        });



    }


    @Override
    public void onStart() {
        super.onStart();
        ConnStatesCallBack.register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        ConnStatesCallBack.unregiter();

    }

    @Override
    public void connected() {

    }

    boolean current_valueEventListener = false ;

    com.titfer.app.ConnStatesCallBack ConnStatesCallBack = new ConnStatesCallBack() ;
    @Override
    public void disConnected() {
        if (current_valueEventListener) {
            current_valueEventListener = false ;
            FirebaseDatabase.getInstance().purgeOutstandingWrites();
            showloading(false);
            Toast.makeText(getApplicationContext() , getString(R.string.no_conn) , Toast.LENGTH_SHORT).show() ;
        }
    }




}
