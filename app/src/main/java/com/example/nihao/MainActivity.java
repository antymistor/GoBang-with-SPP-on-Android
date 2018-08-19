package com.example.nihao;
import android.app.ActionBar;
import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.content.Intent;
import android.util.Log;
import android.view.MenuInflater;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.os.Bundle;
import android.content.IntentFilter;
import android.widget.EditText;
import android.widget.ListView;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.widget.ImageView;
import android.widget.Toast;
import android.app.PendingIntent;
import android.support.v4.app.NotificationCompat;
import android.app.NotificationManager;
import android.graphics.BitmapFactory;
import android.content.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.lang.String;
import android.bluetooth.*;
import android.view.MotionEvent;
public class MainActivity extends AppCompatActivity  {
    //public class MainActivity extends AppCompatActivity {

    private static final String TAG = "BluetoothChat";
    private static final boolean D = true;
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final int Blackside=0;
    public static final int Whiteside=1;
    public static final int ON=0;
    public  static final int OFF=1;
    public  static final int OVER=2;
    public  static final int WAIT=3;
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;
    private String mConnectedDeviceName = null;
    private ArrayAdapter<String> mConversationArrayAdapter;
    private StringBuffer mOutStringBuffer;
    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothChatService mChatService = null;
    private File localFile=null;

    private float X = 0;
    private float Y = 0;
    private int xp=0;
    private int yp=0;
    private TextView PE = null;
    private TextView PE2 = null;
    private Button ClickButton = null;
    private Button ClickButton2 = null;
    private Button ClickButton3 = null;
    private ImageView HB;
    private Bitmap baseBitmap;
    private Bitmap oldbmp;
    private Canvas canvas;
    private Paint paint;
    private Intent intent;
    private int SIDE=Blackside;
    private int STATE=WAIT;
    private int datap[][] = new int[19][19];
    private int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        PE = (TextView) findViewById(R.id.PE);
        PE2 = (TextView) findViewById(R.id.PE2);
        ClickButton = (Button) findViewById(R.id.ClickButton);
        ClickButton2 = (Button) findViewById(R.id.ClickButton2);
        ClickButton3 = (Button) findViewById(R.id.ClickButton3);
        HB = (ImageView) findViewById(R.id.HB);
        drawbase();
        //intent = new Intent();
        //intent.setClass(MainActivity.this, SecondActivity.class);
       // PE.setText("下一手为黑方第1手");
        ClickButton3.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("tel:110"));
                startActivity(intent);

            }
        });
        ClickButton2.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
        drawbase();
        datap=new int[19][19];
        STATE=WAIT;
        sendMessage("C");
            }
        });

        ClickButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent();
                intent.setClass(MainActivity.this, SecondActivity.class);
                startActivity(intent);*/
                if (i > 0) {
                    STATE=ON;
                    baseBitmap = Bitmap.createBitmap(oldbmp);
                    canvas = new Canvas(baseBitmap);
                    HB.setImageBitmap(baseBitmap);
                    i = i - 1;
                    datap[(int) ((X - 30) / 55)][(int) ((Y - 30) / 55)] = 0;
                    // HB.setImageBitmap(oldbmp);
                    if (i % 2 == 1) {
                        PE.setText("下一手为白方第" + (i + 1) / 2 + "手");
                    } else {
                        PE.setText("下一手为黑方第" + (i + 2) / 2 + "手");
                    }
                    ClickButton.setEnabled(false);
                    ClickButton.setText("当前不可悔");
                    sendMessage("H");
                }

                //getActionBar().setSubtitle("第"+i+"手");


            }
        });
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
        }

    }
    private void setupChat() {


        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothChatService(this, mHandler);

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
    }

    public void drawbase(){
        paint = new Paint();
        paint.setStrokeWidth(5);
        paint.setAntiAlias(true);
        paint.setAlpha(10);
        paint.setColor(Color.BLACK);
        paint.setTextSize(30);


        baseBitmap = Bitmap.createBitmap(1050,
                1050, Bitmap.Config.ARGB_4444);
        canvas = new Canvas(baseBitmap);

        canvas.drawColor(getResources().getColor(R.color.colorqipan));
        for (i = 1; i <= 19; i++) {
            canvas.drawLine(30 + (i - 1) * 55, 30, 30 + (i - 1) * 55, 1020, paint);
        }
        for (i = 1; i <= 19; i++) {
            canvas.drawLine(30, 30 + (i - 1) * 55, 1020, 30 + (i - 1) * 55, paint);
        }
        i = 0;
        paint.setStrokeWidth(15);
        canvas.drawLine(30, 23, 30, 1020 + 7, paint);
        canvas.drawLine(1020, 23, 1020, 1020 + 7, paint);
        canvas.drawLine(30, 30, 1020, 30, paint);
        canvas.drawLine(30, 1020, 1020, 1020, paint);
        canvas.drawCircle(525, 525, 10, paint);
        canvas.drawCircle(195, 195, 10, paint);
        canvas.drawCircle(855, 195, 10, paint);
        canvas.drawCircle(855, 525, 10, paint);
        canvas.drawCircle(855, 855, 10, paint);
        canvas.drawCircle(195, 525, 10, paint);
        canvas.drawCircle(195, 855, 10, paint);
        canvas.drawCircle(525, 195, 10, paint);
        canvas.drawCircle(525, 855, 10, paint);

        HB.setImageBitmap(baseBitmap);
    }

    @Override
    public void onStart() {
        super.onStart();

        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        } else {
            if (mChatService == null) setupChat();
        }
         localFile = new File(Environment.getExternalStorageDirectory(),"SAVEDMATCH");
        if (!localFile.exists()) {
            localFile.mkdir();
        }

    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                X = event.getX() - 15;
                Y = event.getY() - 618;
                if (X >= 20 && X < 1030 && Y >= 20 && Y <= 1030&&(STATE==ON||STATE==WAIT)) {
                    //i=i+1;
                    X = (Math.round((X - 30) / 55)) * 55 + 30;
                    Y = (Math.round((Y - 30) / 55)) * 55 + 30;
                    //PE.setText("" + X + "  " + Y);
                    if (datap[(int) ((X - 30) / 55)][(int) ((Y - 30) / 55)] == 0) {
                        if(i==0){SIDE=Blackside;PE2.setText("本局执黑");}
                        STATE=OFF;
                        i++;
                        /*if (i % 2 == 1) {
                            //if(Winornot(1,(int) ((X - 30) / 55),(int) ((Y - 30) / 55)){;}
                            datap[(int) ((X - 30) / 55)][(int) ((Y - 30) / 55)] = i + 1000;
                            paint.setColor(Color.BLACK);
                            if (Winornot(1, (int) ((X - 30) / 55), (int) ((Y - 30) / 55))) {
                                PE.setText("黑方胜");
                                //Toast.makeText(this, "黑方胜", Toast.LENGTH_SHORT).show();
                                STATE=OVER;
                            } else {
                                PE.setText("下一手为白方第" + (i + 1) / 2 + "手");
                            }
                        } else {
                            datap[(int) ((X - 30) / 55)][(int) ((Y - 30) / 55)] = i - 1000;
                            paint.setColor(Color.WHITE);
                            if (Winornot(0, (int) ((X - 30) / 55), (int) ((Y - 30) / 55))) {
                                PE.setText("白方胜");
                                STATE=OVER;
                               //Toast.makeText(this, "白方胜", Toast.LENGTH_SHORT).show();
                            } else {
                                PE.setText("下一手为黑方第" + (i + 2) / 2 + "手");
                            }
                        }*/
                        ClickButton.setEnabled(true);
                        ClickButton.setText("悔一步");
                        // PE.setText("" +i);
                        oldbmp = Bitmap.createBitmap(baseBitmap);
                        canvas.drawCircle(X, Y, 20, paint);
                        if (i % 2 == 1) {
                            paint.setColor(Color.WHITE);
                        } else {
                            paint.setColor(Color.BLACK);
                        }
                        paint.setStrokeWidth(1);
                        if (i < 10) {
                            canvas.drawText(Integer.toString(i), X - 7, Y + 10, paint);
                        } else {
                            if (i < 100) {
                                canvas.drawText(Integer.toString(i), X - 15, Y + 10, paint);
                            } else {
                                canvas.drawText(Integer.toString(i), X - 23, Y + 10, paint);
                            }
                        }
                        HB.setImageBitmap(baseBitmap);
                        sendMessage(Integer.toString((int) ((X - 30) / 55))+"P"+Integer.toString((int) ((Y - 30) / 55)));
                        if (i % 2 == 1) {
                            //if(Winornot(1,(int) ((X - 30) / 55),(int) ((Y - 30) / 55)){;}
                            datap[(int) ((X - 30) / 55)][(int) ((Y - 30) / 55)] = i + 1000;
                            paint.setColor(Color.BLACK);
                            if (Winornot(1, (int) ((X - 30) / 55), (int) ((Y - 30) / 55))) {
                                PE.setText("黑方胜");
                                //Toast.makeText(this, "黑方胜", Toast.LENGTH_SHORT).show();
                                STATE=OVER;
                            } else {
                                PE.setText("下一手为白方第" + (i + 1) / 2 + "手");
                            }
                        } else {
                            datap[(int) ((X - 30) / 55)][(int) ((Y - 30) / 55)] = i - 1000;
                            paint.setColor(Color.WHITE);
                            if (Winornot(0, (int) ((X - 30) / 55), (int) ((Y - 30) / 55))) {
                                PE.setText("白方胜");
                                STATE=OVER;
                               // Toast.makeText(this, "白方胜", Toast.LENGTH_SHORT).show();
                            } else {
                                PE.setText("下一手为黑方第" + (i + 2) / 2 + "手");
                            }
                        }
                    }
                    if(STATE==OVER) {
                       // localFile= new File(context.getFilesDir(),)
                        File finalImageFile = new File(localFile, System.currentTimeMillis() + ".jpg");
                        if (finalImageFile.exists()) {
                            finalImageFile.delete();
                        }
                        try {
                            finalImageFile.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        FileOutputStream fos = null;
                        try {
                            fos = new FileOutputStream(finalImageFile);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        baseBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                        try {
                            fos.flush();
                            fos.close();
                            Toast.makeText(this, "图片保存在："+ finalImageFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                            Uri uri = Uri.fromFile(finalImageFile);
                            intent.setData(uri);
                            sendBroadcast(intent);
                        } catch (IOException e) {
                            Toast.makeText(this, "shibai", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                }
                break;
        }
        return true;
    }

    public boolean Winornot(int p, int x, int y) {
        int c = 0;
        if (p == 1) {
            if ((x - 1) >= 0 && datap[x - 1][y] > 1000) {
                c++;
                if ((x - 2) >= 0 && datap[x - 2][y] > 1000) {
                    c++;
                    if ((x - 3) >= 0 && datap[x - 3][y] > 1000) {
                        c++;
                        if ((x - 4) >= 0 && datap[x - 4][y] > 1000) {
                            c++;
                        }
                    }
                }
            }
            if ((x + 1) <= 18 && datap[x + 1][y] > 1000) {
                c++;
                if ((x + 2) <= 18 && datap[x + 2][y] > 1000) {
                    c++;
                    if ((x + 3) <= 18 && datap[x + 3][y] > 1000) {
                        c++;
                        if ((x + 4) <= 18 && datap[x + 4][y] > 1000) {
                            c++;
                        }
                    }
                }
            }
            if (c > 3) {
                return true;
            }
            c = 0;
            if ((y - 1) >= 0 && datap[x][y - 1] > 1000) {
                c++;
                if ((y - 2) >= 0 && datap[x][y - 2] > 1000) {
                    c++;
                    if ((y - 3) >= 0 && datap[x][y - 3] > 1000) {
                        c++;
                        if ((y - 4) >= 0 && datap[x][y - 4] > 1000) {
                            c++;
                        }
                    }
                }
            }
            if ((y + 1) <= 18 && datap[x][y + 1] > 1000) {
                c++;
                if ((y + 2) <= 18 && datap[x][y + 2] > 1000) {
                    c++;
                    if ((y + 3) <= 18 && datap[x][y + 3] > 1000) {
                        c++;
                        if ((y + 4) <= 18 && datap[x][y + 4] > 1000) {
                            c++;
                        }
                    }
                }
            }
            if (c > 3) {
                return true;
            }
            c = 0;
            if ((y - 1) >= 0 && (x - 1) >= 0 && datap[x - 1][y - 1] > 1000) {
                c++;
                if ((x - 2) >= 0 && (y - 2) >= 0 && datap[x - 2][y - 2] > 1000) {
                    c++;
                    if ((x - 3) >= 0 && (y - 3) >= 0 && datap[x - 3][y - 3] > 1000) {
                        c++;
                        if ((x - 4) >= 0 && (y - 4) >= 0 && datap[x - 4][y - 4] > 1000) {
                            c++;
                        }
                    }
                }
            }
            if ((x + 1) <= 18 && (y + 1) <= 18 && datap[x + 1][y + 1] > 1000) {
                c++;
                if ((x + 2) <= 18 && (y + 2) <= 18 && datap[x + 2][y + 2] > 1000) {
                    c++;
                    if ((x + 3) <= 18 && (y + 3) <= 18 && datap[x + 3][y + 3] > 1000) {
                        c++;
                        if ((x + 4) <= 18 && (y + 4) <= 18 && datap[x + 4][y + 4] > 1000) {
                            c++;
                        }
                    }
                }
            }
            if (c > 3) {
                return true;
            }
            c = 0;
            if ((y + 1) <= 18 && (x - 1) >= 0 && datap[x - 1][y + 1] > 1000) {
                c++;
                if ((x - 2) >= 0 && (y + 2) <= 18 && datap[x - 2][y + 2] > 1000) {
                    c++;
                    if ((x - 3) >= 0 && (y + 3) <= 18 && datap[x - 3][y + 3] > 1000) {
                        c++;
                        if ((x - 4) >= 0 && (y + 4) <= 18 && datap[x - 4][y + 4] > 1000) {
                            c++;
                        }
                    }
                }
            }
            if ((x + 1) <= 18 && (y - 1) >= 0 && datap[x + 1][y - 1] > 1000) {
                c++;
                if ((x + 2) <= 18 && (y - 2) >= 0 && datap[x + 2][y - 2] > 1000) {
                    c++;
                    if ((x + 3) <= 18 && (y - 3) >= 0 && datap[x + 3][y - 3] > 1000) {
                        c++;
                        if ((x + 4) <= 18 && (y - 4) >= 0 && datap[x + 4][y - 4] > 1000) {
                            c++;
                        }
                    }
                }
            }
            if (c > 3) {
                return true;
            }
            return false;
        } else {
            if ((x - 1) >= 0 && datap[x - 1][y] < 0) {
                c++;
                if ((x - 2) >= 0 && datap[x - 2][y] < 0) {
                    c++;
                    if ((x - 3) >= 0 && datap[x - 3][y] < 0) {
                        c++;
                        if ((x - 4) >= 0 && datap[x - 4][y] < 0) {
                            c++;
                        }
                    }
                }
            }
            if ((x + 1) <= 18 && datap[x + 1][y] < 0) {
                c++;
                if ((x + 2) <= 18 && datap[x + 2][y] < 0) {
                    c++;
                    if ((x + 3) <= 18 && datap[x + 3][y] < 0) {
                        c++;
                        if ((x + 4) <= 18 && datap[x + 4][y] < 0) {
                            c++;
                        }
                    }
                }
            }
            if (c > 3) {
                return true;
            }
            c = 0;
            if ((y - 1) >= 0 && datap[x][y - 1] < 0) {
                c++;
                if ((y - 2) >= 0 && datap[x][y - 2] < 0) {
                    c++;
                    if ((y - 3) >= 0 && datap[x][y - 3] < 0) {
                        c++;
                        if ((y - 4) >= 0 && datap[x][y - 4] < 0) {
                            c++;
                        }
                    }
                }
            }
            if ((y + 1) <= 18 && datap[x][y + 1] < 0) {
                c++;
                if ((y + 2) <= 18 && datap[x][y + 2] < 0) {
                    c++;
                    if ((y + 3) <= 18 && datap[x][y + 3] < 0) {
                        c++;
                        if ((y + 4) <= 18 && datap[x][y + 4] < 0) {
                            c++;
                        }
                    }
                }
            }
            if (c > 3) {
                return true;
            }
            c = 0;
            if ((y - 1) >= 0 && (x - 1) >= 0 && datap[x - 1][y - 1] < 0) {
                c++;
                if ((x - 2) >= 0 && (y - 2) >= 0 && datap[x - 2][y - 2] < 0) {
                    c++;
                    if ((x - 3) >= 0 && (y - 3) >= 0 && datap[x - 3][y - 3] < 0) {
                        c++;
                        if ((x - 4) >= 0 && (y - 4) >= 0 && datap[x - 4][y - 4] < 0) {
                            c++;
                        }
                    }
                }
            }
            if ((x + 1) <= 18 && (y + 1) <= 18 && datap[x + 1][y + 1] < 0) {
                c++;
                if ((x + 2) <= 18 && (y + 2) <= 18 && datap[x + 2][y + 2] < 0) {
                    c++;
                    if ((x + 3) <= 18 && (y + 3) <= 18 && datap[x + 3][y + 3] < 0) {
                        c++;
                        if ((x + 4) <= 18 && (y + 4) <= 18 && datap[x + 4][y + 4] < 0) {
                            c++;
                        }
                    }
                }
            }
            if (c > 3) {
                return true;
            }
            c = 0;
            if ((y + 1) <= 18 && (x - 1) >= 0 && datap[x - 1][y + 1] < 0) {
                c++;
                if ((x - 2) >= 0 && (y + 2) <= 18 && datap[x - 2][y + 2] < 0) {
                    c++;
                    if ((x - 3) >= 0 && (y + 3) <= 18 && datap[x - 3][y + 3] < 0) {
                        c++;
                        if ((x - 4) >= 0 && (y + 4) <= 18 && datap[x - 4][y + 4] < 0) {
                            c++;
                        }
                    }
                }
            }
            if ((x + 1) <= 18 && (y - 1) >= 0 && datap[x + 1][y - 1] < 0) {
                c++;
                if ((x + 2) <= 18 && (y - 2) >= 0 && datap[x + 2][y - 2] < 0) {
                    c++;
                    if ((x + 3) <= 18 && (y - 3) >= 0 && datap[x + 3][y - 3] < 0) {
                        c++;
                        if ((x + 4) <= 18 && (y - 4) >= 0 && datap[x + 4][y - 4] < 0) {
                            c++;
                        }
                    }
                }
            }
            if (c > 3) {
                return true;
            }
            return false;
        }
    }



    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            //setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
                            PE2.setText("已连接");
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            //setStatus(R.string.title_connecting);
                            PE2.setText(R.string.title_connecting);
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:
                            //setStatus(R.string.title_not_connected);
                            PE2.setText(R.string.title_not_connected);
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    //byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    //String writeMessage = new String(writeBuf);
                    //mConversationArrayAdapter.add("Me:  " + writeMessage);
                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    //PE.setText(readMessage);
                    if(readMessage.equals("H")&&i!=0){
                        STATE=OFF;
                        baseBitmap = Bitmap.createBitmap(oldbmp);
                        canvas = new Canvas(baseBitmap);
                        HB.setImageBitmap(baseBitmap);
                        i = i - 1;
                        datap[xp][yp] = 0;
                        // HB.setImageBitmap(oldbmp);
                        if (i % 2 == 1) {
                            PE.setText("下一手为白方第" + (i + 1) / 2 + "手");
                        } else {
                            PE.setText("下一手为黑方第" + (i + 2) / 2 + "手");
                        }
                        ClickButton.setEnabled(false);
                        }
                    else{if(readMessage.equals("C")){drawbase();STATE=WAIT;datap=new int[19][19];}
                         else {
                        if (!readMessage.equals("H")) {
                            String[] s = readMessage.split("P");
                            PE.setText(s[0] + s[1]);
                            xp = Integer.parseInt(s[0]);
                            yp = Integer.parseInt(s[1]);
                            if (xp < 19 && yp < 19) {
                                if (i == 0) {
                                    SIDE = Whiteside;
                                    PE2.setText("本局执白");
                                }
                                i++;
                                STATE = ON;
                                ClickButton.setEnabled(false);
                                ClickButton.setText("当前不可悔");
                                // PE.setText("" +i);
                                if (i % 2 == 1) {
                                    //if(Winornot(1,(int) ((X - 30) / 55),(int) ((Y - 30) / 55)){;}
                                    datap[xp][yp] = i + 1000;
                                    paint.setColor(Color.BLACK);
                                    if (Winornot(1, xp, yp)) {
                                        PE.setText("黑方胜");
                                        STATE = OVER;
                                        //Toast.makeText(this, "黑方胜", Toast.LENGTH_SHORT).show();
                                    } else {
                                        PE.setText("下一手为白方第" + (i + 1) / 2 + "手");
                                    }
                                } else {
                                    datap[xp][yp] = i - 1000;
                                    paint.setColor(Color.WHITE);
                                    if (Winornot(0, xp, yp)) {
                                        PE.setText("白方胜");
                                        STATE = OVER;
                                        //Toast.makeText(this, "白方胜", Toast.LENGTH_SHORT).show();
                                    } else {
                                        PE.setText("下一手为黑方第" + (i + 2) / 2 + "手");
                                    }
                                }
                                oldbmp = Bitmap.createBitmap(baseBitmap);
                                xp = xp * 55 + 30;
                                yp = yp * 55 + 30;
                                canvas.drawCircle(xp, yp, 20, paint);
                                if (SIDE == Whiteside) {
                                    paint.setColor(Color.WHITE);
                                } else {
                                    paint.setColor(Color.BLACK);
                                }
                                paint.setStrokeWidth(1);
                                if (i < 10) {
                                    canvas.drawText(Integer.toString(i), xp - 7, yp + 10, paint);
                                } else {
                                    if (i < 100) {
                                        canvas.drawText(Integer.toString(i), xp - 15, yp + 10, paint);
                                    } else {
                                        canvas.drawText(Integer.toString(i), xp - 23, yp + 10, paint);
                                    }
                                }
                                xp = (int) ((xp - 30) / 55);
                                yp = (int) ((yp - 30) / 55);
                                HB.setImageBitmap(baseBitmap);
                            }
                            if(STATE==OVER) {
                                File finalImageFile = new File(localFile, System.currentTimeMillis() + ".jpg");
                                if (finalImageFile.exists()) {
                                    finalImageFile.delete();
                                }
                                try {
                                    finalImageFile.createNewFile();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                FileOutputStream fos = null;
                                try {
                                    fos = new FileOutputStream(finalImageFile);
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                                baseBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                                try {
                                    fos.flush();
                                    fos.close();
                                    Toast.makeText(getApplicationContext(), "图片保存在："+ finalImageFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                                    //Toast.makeText(this, "图片保存在：",Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                                    Uri uri = Uri.fromFile(finalImageFile);
                                    intent.setData(uri);
                                    sendBroadcast(intent);
                                } catch (IOException e) {
                                    Toast.makeText(getApplicationContext(), "shibai", Toast.LENGTH_SHORT).show();
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    }
                    //mConversationArrayAdapter.add(mConnectedDeviceName+":  " + readMessage);
                    break;
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(getApplicationContext(), "Connected to "
                            + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private void connectDevice(Intent data, boolean secure) {
        // Get the device MAC address
        String address = data.getExtras()
                .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        mChatService.connect(device, secure);
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(D) Log.d(TAG, "onActivityResult " + resultCode);
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, true);
                }
                break;
            case REQUEST_CONNECT_DEVICE_INSECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, false);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    setupChat();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }
    private void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mChatService.write(send);

            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return true;
    }
    private void ensureDiscoverable() {
        if (mBluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent serverIntent = null;
        switch (item.getItemId()) {
            case R.id.secure_connect_scan:
                // Launch the DeviceListActivity to see devices and do scan
                serverIntent = new Intent(this, DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
                return true;
            case R.id.insecure_connect_scan:
                // Launch the DeviceListActivity to see devices and do scan
                serverIntent = new Intent(this, DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_INSECURE);
                return true;
            case R.id.discoverable:
                // Ensure this device is discoverable by others
                ensureDiscoverable();
                return true;
        }
        return false;
    }
}
