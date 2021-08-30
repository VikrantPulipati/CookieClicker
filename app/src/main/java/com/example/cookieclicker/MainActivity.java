package com.example.cookieclicker;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Activity activity = this;
    ConstraintLayout layout;

    ImageView cookie;
    TextView cookieCount;
    TextView cookiesPerClick;
    TextView cookiesPerSecond;
    public int cookies = 0;
    final ScaleAnimation clickAnim = new ScaleAnimation(.9f,1.1f,.9f,1.1f,
            Animation.RELATIVE_TO_SELF,.5f,Animation.RELATIVE_TO_SELF,.5f);

    ImageView grandma;
    TextView grandmaDetails;
    ImageView cursor;
    TextView cursorDetails;
    public int cookiesPerClickCount = 1;
    public int cookiesPerSecondCount = 0;

    Toolbar toolbar;

    SharedPreferences sharedPref;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        cookies = sharedPref.getInt("cookies", 0);
        cookiesPerClickCount = sharedPref.getInt("cookiesPerClick", 1);
        cookiesPerSecondCount = sharedPref.getInt("cookiesPerSecond", 0);

        toolbar = (Toolbar)findViewById(R.id.id_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(Color.parseColor("#FF6200EE"));
        toolbar.setTitleTextColor(Color.WHITE);

        layout = findViewById(R.id.id_layout);

        clickAnim.setDuration(100);

        cookie = findViewById(R.id.id_cookie);
        cookie.setImageResource(R.drawable.cookie9);
        cookie.setOnClickListener(v -> {
            try {
                newCookie();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            cookie.startAnimation(clickAnim);
        });

        cookieCount = findViewById(R.id.id_cookieCount);
        cookiesPerSecond = findViewById(R.id.id_cookiesPerSecond);
        cookiesPerSecond.setText(cookiesPerSecondCount + " Cookies Per Second");
        cookiesPerClick = findViewById(R.id.id_cookiesPerClick);
        cookiesPerClick.setText(cookiesPerClickCount + " Cookies Per Click");

        cursor = findViewById(R.id.id_cursor);
        cursor.setOnClickListener(v -> {
            cursor.startAnimation(clickAnim);
            cookiesPerClickCount++;
            addCookies(-10);
            cookiesPerClick.setText(cookiesPerClickCount + " Cookies Per Click");
        });
        cursor.setImageResource(R.drawable.cursor);
        cursorDetails = findViewById(R.id.id_cursorDetails);
        if (cookies >= 10) {
            cursor.setVisibility(View.VISIBLE);
            cursorDetails.setVisibility(View.VISIBLE);
        }

        grandma = findViewById(R.id.id_grandma);
        grandma.setOnClickListener(v -> {
            grandma.startAnimation(clickAnim);
            cookiesPerSecondCount++;
            addCookies(-30);
            cookiesPerSecond.setText(cookiesPerSecondCount + " Cookies Per Second");
        });
        grandma.setImageResource(R.drawable.grandma);
        grandmaDetails = findViewById(R.id.id_grandmaDetails);
        if (cookies >= 30) {
            grandma.setVisibility(View.VISIBLE);
            grandmaDetails.setVisibility(View.VISIBLE);
        }

        Thread passiveIncome = new Thread("Passive Income") {
            public void run () {
                while (true) {
                    runOnUiThread(new Runnable () {
                        @Override
                        public void run() {
                            addCookies(cookiesPerSecondCount);
                        }
                    });
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        passiveIncome.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        switch (item.getItemId()) {
            case R.id.id_clear:
                addCookies(0-cookies);
                cookiesPerClickCount = 1;
                cookiesPerClick.setText(cookiesPerClickCount + " Cookies Per Click");
                cookiesPerSecondCount = 0;
                cookiesPerSecond.setText(cookiesPerSecondCount + " Cookies Per Second");
                return true;

            case R.id.id_save:
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt("cookies", cookies);
                editor.putInt("cookiesPerSecond", cookiesPerSecondCount);
                editor.putInt("cookiesPerClick", cookiesPerClickCount);
                editor.apply();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void addCookies (int count) {
        cookies += count;
        cookieCount.setText(cookies + " cookies");

        TranslateAnimation enterAnim = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, -1f, Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f);
        enterAnim.setDuration(1000);
        if (cookies >= 10 && (cursor.getVisibility() == View.INVISIBLE && cursorDetails.getVisibility() == View.INVISIBLE)) {
            cursor.setVisibility(View.VISIBLE);
            cursorDetails.setVisibility(View.VISIBLE);
            cursor.setClickable(true);
            cursor.startAnimation(enterAnim);
            cursorDetails.startAnimation(enterAnim);
        }
        if (cookies >= 30 && (grandma.getVisibility() == View.INVISIBLE && grandmaDetails.getVisibility() == View.INVISIBLE)) {
            grandma.setVisibility(View.VISIBLE);
            grandmaDetails.setVisibility(View.VISIBLE);
            grandma.setClickable(true);
            grandma.startAnimation(enterAnim);
            grandmaDetails.startAnimation(enterAnim);
        }
        TranslateAnimation exitAnim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_PARENT, -1f,
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f);
        exitAnim.setDuration(1000);
        if (cookies < 10 && (cursor.getVisibility() == View.VISIBLE && cursorDetails.getVisibility() == View.VISIBLE)) {
            cursor.startAnimation(exitAnim);
            cursorDetails.startAnimation(exitAnim);
            cursor.setClickable(false);
            cursor.setVisibility(View.INVISIBLE);
            cursorDetails.setVisibility(View.INVISIBLE);
        }
        if (cookies < 30 && (grandma.getVisibility() == View.VISIBLE && grandmaDetails.getVisibility() == View.VISIBLE)) {
            grandma.startAnimation(exitAnim);
            grandmaDetails.startAnimation(exitAnim);
            grandma.setClickable(false);
            grandma.setVisibility(View.INVISIBLE);
            grandmaDetails.setVisibility(View.INVISIBLE);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void newCookie () throws InterruptedException {
        addCookies(cookiesPerClickCount);

        TextView textViewCode = new TextView(activity);
        textViewCode.setId(View.generateViewId());
        textViewCode.setText("+" + cookiesPerClickCount);

        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT);
        textViewCode.setLayoutParams(params);

        layout.addView(textViewCode);

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(layout);

        constraintSet.connect(textViewCode.getId(), ConstraintSet.TOP, layout.getId(), ConstraintSet.TOP);
        constraintSet.connect(textViewCode.getId(), ConstraintSet.BOTTOM, layout.getId(), ConstraintSet.BOTTOM);
        constraintSet.connect(textViewCode.getId(), ConstraintSet.LEFT, layout.getId(), ConstraintSet.LEFT);
        constraintSet.connect(textViewCode.getId(), ConstraintSet.RIGHT, layout.getId(), ConstraintSet.RIGHT);

        //finish...
        constraintSet.applyTo(layout);

        Thread plusOne = new Thread ("Plus One") {
            public void run () {
                float randomOffsetHor = (float) (Math.random()*.5-.25);
                TranslateAnimation anim = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, randomOffsetHor, Animation.RELATIVE_TO_PARENT, randomOffsetHor,
                        Animation.RELATIVE_TO_PARENT, 0f, Animation.RELATIVE_TO_PARENT, -.5f);
                anim.setDuration(1000);

                textViewCode.startAnimation(anim);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable () {
                    @Override
                    public void run() {
                        layout.removeView(textViewCode);
                    }
                });
            }
        };

        plusOne.start();

        Log.d("TAG1", "Number of Views: " + layout.getChildCount());
    }
}