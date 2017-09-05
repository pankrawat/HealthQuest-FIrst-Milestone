package com.tupelo.wellness;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.pubnub.api.Callback;
import com.pubnub.api.PnGcmMessage;
import com.pubnub.api.PnMessage;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;
import com.tupelo.wellness.activity.TabActivity;
import com.tupelo.wellness.adt.ChatMessage;
import com.tupelo.wellness.callbacks.BasicCallback;
import com.tupelo.wellness.database.DbAdapter;
import com.tupelo.wellness.helper.AppTheme;
import com.tupelo.wellness.helper.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import github.ankushsachdeva.emojicon.EmojiconGridView;
import github.ankushsachdeva.emojicon.EmojiconsPopup;
import github.ankushsachdeva.emojicon.emoji.Emojicon;


/**
 * Main Activity is where all the magic happens. To keep this demo simple I did not use fragment
 * views, simply a ListView that is populated by a custom adapter, ChatAdapter. If you want to
 * make this chat app your own, go to http://www.pubnub.com/get-started/ and replace the Pub/Sub
 * keys found in Constants.java, then be sure to enable Storage & Playback, Presence, and Push.
 * For all features to work, you will also need to Register for GCM Messaging and update your
 * sender ID as well.
 * Sample data to test from console:
 * {"type":"groupMessage","data":{"chatUser":"Dev","chatMsg":"Hello World!","chatTime":1436642192966}}
 */
public class MainActivity extends AppCompatActivity {
    String TAG = MainActivity.class.getName();
    private Pubnub mPubNub;
    private Button mChannelView;
    private EditText mMessageET;
    private MenuItem mHereNow;
    private ListView mListView;
    private ChatAdapter mChatAdapter;
    private SharedPreferences mSharedPrefs;

    private String username;
    private String channel = "CHALLENGEYOURSELF2";

    private GoogleCloudMessaging gcm;
    private String gcmRegId;
    Toolbar toolbar;

    /*   @Override
       public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

           View v = inflater.inflate(R.layout.activity_main, container, false);
   */
    @Override
    public void onCreate(Bundle savedInstanceState) {
     /*requestWindowFeature(Window.FEATURE_NO_TITLE);*/
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String statusBarColor = AppTheme.getInstance().colorPrimaryDark;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(statusBarColor));
        }

        toolbar = (Toolbar) this.findViewById(R.id.tabanim_toolbar);
        toolbar.setBackgroundColor(Color.parseColor(AppTheme.getInstance().colorPrimary));
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Intent intent = getIntent();
        String channelname;
        String channel = intent.getStringExtra("channel");
        String channelheader = intent.getStringExtra("channelname");
        this.channel = channel;





        SharedPreferences pref = MainActivity.this.getSharedPreferences("MyProfile", 0); // 0 - for private mode
        channelname= pref.getString("userName", "username");

        Log.e(TAG,"the channel name is " + channelname);


        this.username = channelname;
        TextView name=(TextView)findViewById(R.id.channelname);
        name.setTextColor(Color.WHITE);
        name.setBackgroundColor(Color.parseColor(AppTheme.getInstance().colorPrimary));
        name.setText(channelheader);

        String EMAIL_PATTERN = "([^.@\\s]+)(\\.[^.@\\s]+)*@([^.@\\s]+\\.)+([^.@\\s]+)";
        DbAdapter dbAdapter = DbAdapter.getInstance(MainActivity.this.getApplicationContext());
        Cursor cursor = dbAdapter.fetchQuery(DbAdapter.TABLE_NAME_TUPELO);
        String userName = "", avatarUrl = "";
        if (cursor.getCount() > 0) {
            userName = cursor.getString(2);
            avatarUrl = cursor.getString(4);
        }

        Log.e(TAG, "user name is " + userName);
        if (userName.contains("@"))
            username = userName.substring(0, userName.indexOf("@"));
        Log.e(TAG,"username global is " + username);
    /*    if (validUsername(username))
            return;*/

        SharedPreferences sp = MainActivity.this.getSharedPreferences(Constants.CHAT_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(Constants.CHAT_USERNAME, username);
        edit.apply();




        final ImageView emojiButton = (ImageView) findViewById(R.id.emojiButton);

        final View rootView = findViewById(R.id.root_view);

        final EmojiconsPopup popup = new EmojiconsPopup(rootView, this);


        //Will automatically set size according to the soft keyboard size
        popup.setSizeForSoftKeyboard();

        //If the emoji popup is dismissed, change emojiButton to smiley icon
        popup.setOnDismissListener(new PopupWindow.OnDismissListener() {



            @Override
            public void onDismiss() {
                changeEmojiKeyboardIcon(emojiButton, R.mipmap.ic_msg_panel_smiles);
            }
        });

        //If the text keyboard closes, also dismiss the emoji popup
        popup.setOnSoftKeyboardOpenCloseListener(new EmojiconsPopup.OnSoftKeyboardOpenCloseListener() {

            @Override
            public void onKeyboardOpen(int keyBoardHeight) {

            }

            @Override
            public void onKeyboardClose() {
                if(popup.isShowing())
                    popup.dismiss();
            }
        });

        //On emoji clicked, add it to edittext
        popup.setOnEmojiconClickedListener(new EmojiconGridView.OnEmojiconClickedListener() {

            @Override
            public void onEmojiconClicked(Emojicon emojicon) {
                if (mMessageET == null || emojicon == null) {
                    return;
                }

                int start = mMessageET.getSelectionStart();
                int end = mMessageET.getSelectionEnd();
                if (start < 0) {
                    mMessageET.append(emojicon.getEmoji());
                } else {
                    mMessageET.getText().replace(Math.min(start, end),
                            Math.max(start, end), emojicon.getEmoji(), 0,
                            emojicon.getEmoji().length());
                }
            }
        });

        //On backspace clicked, emulate the KEYCODE_DEL key event
        popup.setOnEmojiconBackspaceClickedListener(new EmojiconsPopup.OnEmojiconBackspaceClickedListener() {

            @Override
            public void onEmojiconBackspaceClicked(View v) {
                KeyEvent event = new KeyEvent(
                        0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
                mMessageET.dispatchKeyEvent(event);
            }
        });



        emojiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!popup.isShowing()){

                    //If keyboard is visible, simply show the emoji popup
                    if(popup.isKeyBoardOpen()){
                        popup.showAtBottom();
                        changeEmojiKeyboardIcon(emojiButton, R.mipmap.ic_msg_panel_smiles);
                    }

                    //else, open the text keyboard first and immediately after that show the emoji popup
                    else{
                        mMessageET.setFocusableInTouchMode(true);
                        mMessageET.requestFocus();
                        popup.showAtBottomPending();
                        final InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.showSoftInput(mMessageET, InputMethodManager.SHOW_IMPLICIT);
                        changeEmojiKeyboardIcon(emojiButton, R.mipmap.ic_msg_panel_smiles);
                    }
                }

                //If popup is showing, simply dismiss it to show the undelying text keyboard
                else{
                    popup.dismiss();
                }
            }
        });

        ImageView sendChatButton = (ImageView) findViewById(R.id.sendchatbutton);
        /*sendChatButton.setBackgroundColor(Color.parseColor(AppTheme.getInstance().colorPrimary));*/
        sendChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = mMessageET.getText().toString();
                if (message.equals("")) return;
                mMessageET.setText("");
                ChatMessage chatMsg = new ChatMessage(username, message, System.currentTimeMillis());
                try {
                    JSONObject json = new JSONObject();
                    json.put(Constants.JSON_USER, chatMsg.getUsername());
                    json.put(Constants.JSON_MSG, chatMsg.getMessage());
                    json.put(Constants.JSON_TIME, chatMsg.getTimeStamp());
                    publish(Constants.JSON_GROUP, json);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mChatAdapter.addMessage(chatMsg);
            }
        });


        mSharedPrefs = MainActivity.this.getSharedPreferences(Constants.CHAT_PREFS, Context.MODE_PRIVATE);
        if (!mSharedPrefs.contains(Constants.CHAT_USERNAME)) {
      /*      Intent toLogin = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(toLogin);*/
            Log.e(TAG, "user name not found");
        }

        Bundle extras = MainActivity.this.getIntent().getExtras();
        if (extras != null) {
            Log.d("Main-bundle", extras.toString() + " Has Chat: " + extras.getString(Constants.CHAT_ROOM));
            if (extras.containsKey(Constants.CHAT_ROOM))
                this.channel = extras.getString(Constants.CHAT_ROOM);
        }

        this.username = mSharedPrefs.getString(Constants.CHAT_USERNAME, "Anonymous");
        Log.e(TAG,"the user name 2 is " + userName);
        this.mListView = (ListView) findViewById(R.id.list);
        this.mChatAdapter = new ChatAdapter(MainActivity.this, new ArrayList<ChatMessage>());
        this.mChatAdapter.userPresence(this.username, "join"); // Set user to online. Status changes handled in presence
        setupAutoScroll();
        this.mListView.setAdapter(mChatAdapter);
        setupListView();

        this.mMessageET = (EditText) findViewById(R.id.message_et);
        //this.mChannelView = (Button) findViewById(R.id.channel_bar);
        //this.mChannelView.setText(this.channel);

        initPubNub();
       /* return v;*/
    }










    private void changeEmojiKeyboardIcon(ImageView iconToBeChanged, int drawableResourceId){
        iconToBeChanged.setImageResource(drawableResourceId);
    }








    private boolean validUsername(String username) {
        if (username.length() == 0) {
            //mUsername.setError("Username cannot be empty.");
            return false;
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
/*    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }*/

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main_chat, menu);
//        this.mHereNow = menu.findItem(R.id.action_here_now_chat);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        switch(id){
//            case R.id.action_here_now:
//                hereNow(true);
//                return true;
//            case R.id.action_sign_out:
//                signOut();
//                return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    /**
     * Might want to unsubscribe from PubNub here and create background service to listen while
     * app is not in foreground.
     * PubNub will stop subscribing when screen is turned off for this demo, messages will be loaded
     * when app is opened through a call to history.
     * The best practice would be creating a background service in onStop to handle messages.
     */
    @Override
    public void onStop() {
        super.onStop();
        if (this.mPubNub != null)
            this.mPubNub.unsubscribeAll();
    }

    /**
     * Instantiate PubNub object if it is null. Subscribe to channel and pull old messages via
     *   history.
     */
/*    @Override
    public void onRestart() {
        super.onRestart();
        if (this.mPubNub==null){
            initPubNub();
        } else {
            subscribeWithPresence();
            history();
        }
    }*/

    /**
     * I remove the PubNub object in onDestroy since turning the screen off triggers onStop and
     *   I wanted PubNub to receive messages while the screen is off.
     *
     *//*
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }*/

    /**
     * Instantiate PubNub object with username as UUID
     * Then subscribe to the current channel with presence.
     * Finally, populate the listview with past messages from history
     */
    private void initPubNub() {
        this.mPubNub = new Pubnub(Constants.PUBLISH_KEY, Constants.SUBSCRIBE_KEY);
        this.mPubNub.setUUID(this.username);
        subscribeWithPresence();
        history();
        gcmRegister();
    }

    /**
     * Use PubNub to send any sort of data
     *
     * @param type The type of the data, used to differentiate groupMessage from directMessage
     * @param data The payload of the publish
     */
    public void publish(String type, JSONObject data) {
        JSONObject json = new JSONObject();
        try {
            json.put("type", type);
            json.put("data", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.mPubNub.publish(this.channel, json, new BasicCallback());
    }

    /**
     * Update here now number, uses a call to the pubnub hereNow function.
     *
     * @param displayUsers If true, display a modal of users in room.
     */
    public void hereNow(final boolean displayUsers) {
        this.mPubNub.hereNow(this.channel, new Callback() {
            @Override
            public void successCallback(String channel, Object response) {
                try {
                    JSONObject json = (JSONObject) response;
                    final int occ = json.getInt("occupancy");
                    final JSONArray hereNowJSON = json.getJSONArray("uuids");
                    Log.d("JSON_RESP", "Here Now: " + json.toString());
                    final Set<String> usersOnline = new HashSet<String>();
                    usersOnline.add(username);
                    for (int i = 0; i < hereNowJSON.length(); i++) {
                        usersOnline.add(hereNowJSON.getString(i));
                    }
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mHereNow != null)
                                mHereNow.setTitle(String.valueOf(occ));
                            mChatAdapter.setOnlineNow(usersOnline);
                            if (displayUsers)
                                alertHereNow(usersOnline);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Called at login time, sets meta-data of users' log-in times using the PubNub State API.
     * Information is retrieved in getStateLogin
     */
    public void setStateLogin() {
        Callback callback = new Callback() {
            @Override
            public void successCallback(String channel, Object response) {
                Log.d("PUBNUB", "State: " + response.toString());
            }
        };
        try {
            JSONObject state = new JSONObject();
            state.put(Constants.STATE_LOGIN, System.currentTimeMillis());
            this.mPubNub.setState(this.channel, this.mPubNub.getUUID(), state, callback);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get state information. Information is deleted when user unsubscribes from channel
     * so display a user not online message if there is no UUID data attached to the
     * channel's state
     *
     * @param user
     */
    public void getStateLogin(final String user) {
        Callback callback = new Callback() {
            @Override
            public void successCallback(String channel, Object response) {
                if (!(response instanceof JSONObject)) return; // Ignore if not JSON
                try {
                    JSONObject state = (JSONObject) response;
                    final boolean online = state.has(Constants.STATE_LOGIN);
                    final long loginTime = online ? state.getLong(Constants.STATE_LOGIN) : 0;

                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!online)
                                Toast.makeText(MainActivity.this, user + " is not online.", Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(MainActivity.this, user + " logged in since " + ChatAdapter.formatTimeStamp(loginTime), Toast.LENGTH_SHORT).show();

                        }
                    });

                    Log.d("PUBNUB", "State: " + response.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        this.mPubNub.getState(this.channel, user, callback);
    }

    /**
     * Subscribe to channel, when subscribe connection is established, in connectCallback, subscribe
     * to presence, set login time with setStateLogin and update hereNow information.
     * When a message is received, in successCallback, get the ChatMessage information from the
     * received JSONObject and finally put it into the listview's ChatAdapter.
     * Chat adapter calls notifyDatasetChanged() which updates UI, meaning must run on UI thread.
     */
    public void subscribeWithPresence() {
        Callback subscribeCallback = new Callback() {
            @Override
            public void successCallback(String channel, Object message) {
                if (message instanceof JSONObject) {
                    try {
                        JSONObject jsonObj = (JSONObject) message;
                        JSONObject json = jsonObj.getJSONObject("data");
                        String name = json.getString(Constants.JSON_USER);
                        String msg = json.getString(Constants.JSON_MSG);
                        long time = json.getLong(Constants.JSON_TIME);
                        if (name.equals(mPubNub.getUUID())) return; // Ignore own messages
                        final ChatMessage chatMsg = new ChatMessage(name, msg, time);
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mChatAdapter.addMessage(chatMsg);
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.d("PUBNUB", "Channel: " + channel + " Msg: " + message.toString());
            }

            @Override
            public void connectCallback(String channel, Object message) {
                Log.d("Subscribe", "Connected! " + message.toString());
                hereNow(false);
                setStateLogin();
            }
        };
        try {
            mPubNub.subscribe(this.channel, subscribeCallback);
            presenceSubscribe();
        } catch (PubnubException e) {
            e.printStackTrace();
        }
    }

    /**
     * Subscribe to presence. When user join or leave are detected, update the hereNow number
     * as well as add/remove current user from the chat adapter's userPresence array.
     * This array is used to see what users are currently online and display a green dot next
     * to users who are online.
     */

    public void presenceSubscribe() {
        Callback callback = new Callback() {
            @Override
            public void successCallback(String channel, Object response) {
                Log.i("PN-pres", "Pres: " + response.toString() + " class: " + response.getClass().toString());
                if (response instanceof JSONObject) {
                    JSONObject json = (JSONObject) response;
                    Log.d("PN-main", "Presence: " + json.toString());
                    try {
                        final int occ = json.getInt("occupancy");
                        final String user = json.getString("uuid");
                        final String action = json.getString("action");
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mChatAdapter.userPresence(user, action);
                                if (mHereNow != null) {
                                    mHereNow.setTitle(String.valueOf(occ));
                                }
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void errorCallback(String channel, PubnubError error) {
                Log.d("Presence", "Error: " + error.toString());
            }
        };
        try {
            this.mPubNub.presence(this.channel, callback);
        } catch (PubnubException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get last 100 messages sent on current channel from history.
     */
    public void history() {
        this.mPubNub.history(this.channel, 100, false, new Callback() {
            @Override
            public void successCallback(String channel, final Object message) {
                try {
                    JSONArray json = (JSONArray) message;
                    Log.d("History", json.toString());
                    final JSONArray messages = json.getJSONArray(0);
                    final List<ChatMessage> chatMsgs = new ArrayList<ChatMessage>();
                    for (int i = 0; i < messages.length(); i++) {
                        try {
                            if (!messages.getJSONObject(i).has("data")) continue;
                            JSONObject jsonMsg = messages.getJSONObject(i).getJSONObject("data");
                            String name = jsonMsg.getString(Constants.JSON_USER);
                            String msg = jsonMsg.getString(Constants.JSON_MSG);
                            long time = jsonMsg.getLong(Constants.JSON_TIME);
                            ChatMessage chatMsg = new ChatMessage(name, msg, time);
                            chatMsgs.add(chatMsg);
                            Log.e(TAG,"the message i get is " + msg +" the user is "+ name);
                        } catch (JSONException e) { // Handle errors silently
                            e.printStackTrace();
                        }
                    }

                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Toast.makeText(MainActivity.this, "RUNNIN", Toast.LENGTH_SHORT).show();
                            mChatAdapter.setMessages(chatMsgs);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void errorCallback(String channel, PubnubError error) {
                Log.d("History", error.toString());
            }
        });
    }

    /**
     * Log out, remove username from SharedPreferences, unsubscribe from PubNub, and send user back
     * to the LoginActivity
     */
    public void signOut() {
        this.mPubNub.unsubscribeAll();
        Intent intent = new Intent(MainActivity.this, TabActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("oldUsername", this.username);
        startActivity(intent);
    }

    /**
     * Setup the listview to scroll to bottom anytime it receives a message.
     */
    private void setupAutoScroll() {
        this.mChatAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                mListView.setSelection(mChatAdapter.getCount() - 1);
                // mListView.smoothScrollToPosition(mChatAdapter.getCount()-1);
            }
        });
    }

    /**
     * On message click, display the last time the user logged in.
     */
    private void setupListView() {
        this.mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ChatMessage chatMsg = mChatAdapter.getItem(position);
                sendNotification(chatMsg.getUsername());
            }
        });
    }

    /**
     * Publish message to current channel.
     *
     * @param view The 'SEND' Button which is clicked to trigger a sendMessage call.
     */
    public void sendMessage(View view) {

    }

    /**
     * Create an alert dialog with a list of users who are here now.
     * When a user's name is clicked, get their state information and display it with Toast.
     *
     * @param userSet
     */
    private void alertHereNow(Set<String> userSet) {
        List<String> users = new ArrayList<String>(userSet);
        LayoutInflater li = LayoutInflater.from(MainActivity.this);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("Here Now");
        alertDialog.setNegativeButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        final ArrayAdapter<String> hnAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, users);
        alertDialog.setAdapter(hnAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String user = hnAdapter.getItem(which);
                getStateLogin(user);
            }
        });
        alertDialog.show();
    }

    /**
     * Create an alert dialog with a text view to enter a new channel to join. If the channel is
     * not empty, unsubscribe from the current channel and join the new one.
     * Then, get messages from history and update the channelView which displays current channel.
     *
     * @param view
     */
    public void changeChannel(View view) {
        LayoutInflater li = LayoutInflater.from(MainActivity.this);
        View promptsView = li.inflate(R.layout.channel_change, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);
        userInput.setText(this.channel);                       // Set text to current ID
        userInput.setSelection(userInput.getText().length());  // Move cursor to end

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String newChannel = userInput.getText().toString();
                                if (newChannel.equals("")) return;

                                mPubNub.unsubscribe(channel);
                                mChatAdapter.clearMessages();
                                channel = newChannel;
                                mChannelView.setText(channel);
                                subscribeWithPresence();
                                history();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    /**
     * GCM Functionality.
     * In order to use GCM Push notifications you need an API key and a Sender ID.
     * Get your key and ID at - https://developers.google.com/cloud-messaging/
     */

    private void gcmRegister() {
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(MainActivity.this);
            try {
                gcmRegId = getRegistrationId();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (gcmRegId.isEmpty()) {
                registerInBackground();
            } else {
                //Toast.makeText(this, "Registration ID already exists: " + gcmRegId, Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.e("GCM-register", "No valid Google Play Services APK found.");
        }
    }

    private void gcmUnregister() {
        new UnregisterTask().execute();
    }

    private void removeRegistrationId() {
        SharedPreferences prefs = MainActivity.this.getSharedPreferences(Constants.CHAT_PREFS, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(Constants.GCM_REG_ID);
        editor.apply();
    }

    public void sendNotification(String toUser) {
        PnGcmMessage gcmMessage = new PnGcmMessage();
        JSONObject json = new JSONObject();
        try {
            json.put(Constants.GCM_POKE_FROM, this.username);
            json.put(Constants.GCM_CHAT_ROOM, this.channel);
            gcmMessage.setData(json);

            PnMessage message = new PnMessage(
                    this.mPubNub,
                    toUser,
                    new BasicCallback(),
                    gcmMessage);
            message.put("pn_debug", true); // Subscribe to yourchannel-pndebug on console for reports
            message.publish();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (PubnubException e) {
            e.printStackTrace();
        }
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(MainActivity.this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, MainActivity.this, Constants.PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.e("GCM-check", "This device is not supported.");
                MainActivity.this.finish();
            }
            return false;
        }
        return true;
    }

    private void registerInBackground() {
        new RegisterTask().execute();
    }

    private void storeRegistrationId(String regId) {
        SharedPreferences prefs = MainActivity.this.getSharedPreferences(Constants.CHAT_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.GCM_REG_ID, regId);
        editor.apply();
    }


    private String getRegistrationId() {
        SharedPreferences prefs = MainActivity.this.getSharedPreferences(Constants.CHAT_PREFS, Context.MODE_PRIVATE);
        return prefs.getString(Constants.GCM_REG_ID, "");
    }

    private void sendRegistrationId(String regId) {
        this.mPubNub.enablePushNotificationsOnChannel(this.username, regId, new BasicCallback());
    }

    private class RegisterTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            String msg = "";
            try {
                if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(MainActivity.this);
                }
                gcmRegId = gcm.register(Constants.GCM_SENDER_ID);
                msg = "Device registered, registration ID: " + gcmRegId;

                sendRegistrationId(gcmRegId);

                storeRegistrationId(gcmRegId);
                Log.i("GCM-register", msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return msg;
        }
    }

    private class UnregisterTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(MainActivity.this);
                }

                // Unregister from GCM
                gcm.unregister();

                // Remove Registration ID from memory
                removeRegistrationId();

                // Disable Push Notification
                mPubNub.disablePushNotificationsOnChannel(username, gcmRegId);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }


}
