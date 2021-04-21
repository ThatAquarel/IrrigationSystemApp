package me.aqua_rel.irrigationsystem;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;

import me.aqua_rel.irrigationsystem.Utility.TcpClient;

public class MainActivity extends AppCompatActivity {
    String[] s1;
    String response;
    int multiplyFactor = 60;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Job job = new Job(this);

        s1 = getResources().getStringArray(R.array.zones);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        final MyAdapter myAdapter = new MyAdapter(this, s1, job);
        recyclerView.setAdapter(myAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });

        final ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, 0) {
            @Override
            public boolean isLongPressDragEnabled() {
                return false;
            }

            @Override
            public boolean isItemViewSwipeEnabled() {
                return false;
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder dragged, @NonNull RecyclerView.ViewHolder target) {
                int positionDragged = dragged.getAbsoluteAdapterPosition();
                int positionTarget = target.getAbsoluteAdapterPosition();

                Collections.swap(Arrays.asList(s1), positionDragged, positionTarget);

                myAdapter.notifyItemMoved(positionDragged, positionTarget);

                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerView);
        myAdapter.setItemTouchHelper(itemTouchHelper);

        final CheckBox checkBox = findViewById(R.id.checkBox);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkBox.isChecked()) {
                    multiplyFactor = 1;
                } else {
                    multiplyFactor = 60;
                }
            }
        });

        ConstraintLayout start = findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                createAlert(getResources().getString(R.string.start_job));

                StringBuilder stringBuilder = new StringBuilder();
                for (String string : s1) {
                    String tag = job.nameToTag(string);
                    stringBuilder.append(tag).append("=").append(job.getJob(tag) * multiplyFactor).append(",");
                }
                String string = stringBuilder.toString();
                Log.i("Stop job", string);
                callTcp(string);

                TextView stopText = findViewById(R.id.stopText2);
                stopText.setText(job.getTotal() + " Minute Job");
            }
        });

        ConstraintLayout stop = findViewById(R.id.stop);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAlert(getResources().getString(R.string.stop_job));

                StringBuilder stringBuilder = new StringBuilder();
                for (String string : s1) {
                    String tag = job.nameToTag(string);
                    stringBuilder.append(tag).append("=").append("0,");
                }
                String string = stringBuilder.toString();
                Log.i("Stop job", string);
                callTcp(string);

                TextView stopText = findViewById(R.id.stopText2);
                stopText.setText(getResources().getText(R.string.null_label));
            }
        });
    }

    private void createAlert(String text) {
        final AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("Irrigation System")
                .setMessage(text)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        alertDialog.dismiss();
                    }
                },
                500
        );
    }

    private void callTcp(final String text) {
        new Thread(new NetworkThread(this, new NetworkCallback() {
            @Override
            public void callback() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView responseText = findViewById(R.id.response);
                        responseText.setText(response.replace(" ", "\n"));
                    }
                });
            }
        }, text)).start();
    }

    private interface NetworkCallback {
        void callback();
    }

    private static class NetworkThread implements Runnable {
        NetworkCallback callback;
        MainActivity mainActivity;
        String text;

        public NetworkThread(MainActivity mainActivity, NetworkCallback callback, String text) {
            this.callback = callback;
            this.mainActivity = mainActivity;
            this.text = text;
        }

        public void run() {
            TcpClient tcpClient = new TcpClient(mainActivity.getResources().getString(R.string.host), 6969);
            try {
                mainActivity.response = tcpClient.request(text);

            } catch (Exception e) {
                e.printStackTrace();
            }
            this.callback.callback();
        }
    }
}
