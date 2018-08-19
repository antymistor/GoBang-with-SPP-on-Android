package com.example.nihao;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.content.Intent;
import android.widget.SeekBar;
import android.widget.TextView;

public class SecondActivity extends AppCompatActivity {
    static int h;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        final TextView PE2 = (TextView) findViewById(R.id.PE2);
        final Button ClickButton2 = (Button) findViewById(R.id.ClickButton2);

        ClickButton2.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                h=(h+1)%2;

                if(h==1) {
                    PE2.setText("我很好");
                }
                else{
                    PE2.setText("我不好");
                }

                /*Intent intent = new Intent();
                intent.setClass(SecondActivity.this,MainActivity.class);
                startActivity(intent);
                finish();*/
                Intent intent = new Intent();
                intent.setClass(SecondActivity.this,MainActivity.class);
                //Intent intent=getIntent();
               //SecondActivity.this.setResult(0,intent);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                //SecondActivity.this.finish();


            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
