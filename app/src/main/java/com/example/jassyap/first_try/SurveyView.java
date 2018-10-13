package com.example.jassyap.first_try;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import static com.google.android.gms.common.internal.safeparcel.SafeParcelable.NULL;

public class SurveyView extends AppCompatActivity {

    private static final String TAG = NULL ;
    private TextView surveyTitle;
    private LinearLayout qnalayout;
    private View view;

    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_view);

        qnalayout = findViewById(R.id.qna_layout);
        surveyTitle = findViewById(R.id.surveyTitle);

        //database reference pointing to root of database
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();


        DatabaseReference questionnaireRef = myRef.child("questionnaire");
        final DatabaseReference qQuestionnaireRef = questionnaireRef.child("q1");

        //let's see if we manage to fetch all data under q1 node
        //can be commented
        qQuestionnaireRef.addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> contactChildren = dataSnapshot.getChildren();
                for(DataSnapshot ds1 : contactChildren){
                    Log.d(TAG, "Key is: " + ds1.getKey());
                    Log.d(TAG, "Value is: " + ds1.getValue());
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //generate new question and answer layouts for each QnA


        //fetch survey's data to be put into
        qQuestionnaireRef.addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {

                //set the survey of title
                surveyTitle.setText(dataSnapshot.child("title").getValue(String.class));

                Iterable<DataSnapshot> contactChildren = dataSnapshot.getChildren();
                for(DataSnapshot ds1 : contactChildren){
                    //to check if we manage to fetch values
                    //Log.d(TAG, "Key is: " + ds1.getKey());
                    //Log.d(TAG, "Value is: " + ds1.getValue());



                    //check for regex, just pick questions only, other infos (i.e. creator, title) are not loaded into
                    if (ds1.getKey().matches("q[0-9]*-[0-9]*")) {

                        view = LayoutInflater.from(qnalayout.getContext()).inflate(R.layout.qna_view, null);
                        qnalayout.addView(view);

                        //set the questions
                        TextView question = view.findViewById(R.id.question);
                        question.setText(ds1.getValue(String.class));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
