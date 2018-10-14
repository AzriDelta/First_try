package com.example.jassyap.first_try;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
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
    private Button submit;
    private int number_of_questions = 0;
    private DatabaseReference myRef, questionnaireRef, qQuestionnaireRef, respondRef;

    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_view);
        qnalayout = findViewById(R.id.qna_layout);
        surveyTitle = findViewById(R.id.surveyTitle);
        submit = findViewById(R.id.submit);



        //database reference pointing to root of database
        myRef = FirebaseDatabase.getInstance().getReference();
        //point to questionnaire node
        questionnaireRef = myRef.child("questionnaire");
        //point to a certain survey
        //in this demo, points to 'q1'
        qQuestionnaireRef = questionnaireRef.child("q1");

        //let's see if we manage to fetch all data under q1 node
        //can be commented
/*        qQuestionnaireRef.addValueEventListener(new ValueEventListener() {
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
        });*/



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
                        Log.d(TAG, "ID for qna_view layout: " + view.getId());
                        qnalayout.addView(view);



                        TextView question = view.findViewById(R.id.question);
                        TextView answer = view.findViewById(R.id.answer);

                        //generate new ID for question and answer textbar
                        question.setId(question.getId() + (number_of_questions+1));
                        answer.setId(answer.getId() + (number_of_questions+1));

                        //load the question from firebase into question Textbar
                        question.setText(ds1.getValue(String.class));

                        Log.d(TAG, "ID for question text bar: " + question.getId());

                        number_of_questions++;
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //point to 'respond->q1->r-q1' node under root node
        respondRef = myRef.child("respond").child("q1").child("r-q1");

        //push all answers back to 'r-q1' node
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Number of questions: " + number_of_questions);

                //access each qna_view.xml that has been inflated, one by one
                for (int i=1; i<=number_of_questions; i++) {

                     //= view.findViewById(R.id.question);
                     //= view.findViewById(R.id.answer);

                    //LayoutInflater li=getLayoutInflater();
                    //vw=li.inflate(R.layout.frag, null);
                    //btn = (Button)vw.findViewById(R.id.btn);

                    //LinearLayout linlay = findViewById(R.id.qna_layout);
                    //setContentView(R.layout.qna_view);

                    TextView question = findViewById((R.id.question) + i);
                    TextView answer = findViewById((R.id.answer) + i);

                    //ensure that we got the correct question
                    Log.d(TAG, "Question: " + question.getText().toString());
                    Log.d(TAG, "Answer: " + answer.getText().toString());

                    respondRef.child(question.getText().toString()).setValue(answer.getText().toString());
                }
            }
        });
    }
}
