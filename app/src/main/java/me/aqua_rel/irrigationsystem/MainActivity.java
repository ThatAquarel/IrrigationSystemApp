package me.aqua_rel.irrigationsystem;

import android.annotation.SuppressLint;
import android.os.Bundle;
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

import java.util.Arrays;
import java.util.Collections;

import me.aqua_rel.irrigationsystem.Utility.UdpClient;
import me.aqua_rel.irrigationsystem.Utility.Vibrate;

public class MainActivity extends AppCompatActivity {
    String[] s1;
    int multiplyFactor = 60;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Job job = new Job(this);
        final Vibrate vibrate = new Vibrate(this);

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
                int positionDragged = dragged.getAdapterPosition();
                int positionTarget = target.getAdapterPosition();

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
                vibrate.click();
                createAlert(getResources().getString(R.string.start_job));

                final StringBuilder message = new StringBuilder();
                for (String string : s1) {
                    switch (string) {
                        case "Front Yard":
                            message.append(string).append("=").append(job.getFrontyard() * multiplyFactor).append(",");
                            break;
                        case "Back Yard Pool":
                            message.append(string).append("=").append(job.getBackyard1() * multiplyFactor).append(",");
                            break;
                        case "Back Yard Shed":
                            message.append(string).append("=").append(job.getBackyard2() * multiplyFactor).append(",");
                            break;
                    }
                }

                new Thread(new Runnable() {
                    public void run() {
                        UdpClient udpClient = new UdpClient();
                        try {
                            udpClient.request(getResources().getString(R.string.ip), 6969, message.toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

                int total = job.getFrontyard() + job.getBackyard1() + job.getBackyard2();
                TextView textView = findViewById(R.id.stopText2);
                textView.setText(total + " Minute Job");
            }
        });

        ConstraintLayout stop = findViewById(R.id.stop);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vibrate.click();
                createAlert(getResources().getString(R.string.stop_job));

                new Thread(new Runnable() {
                    public void run() {
                        try {
                            UdpClient udpClient = new UdpClient();
                            udpClient.request(getResources().getString(R.string.ip), 6969, "Front Yard=0,Back Yard Pool=0,Back Yard Shed=0,");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

                TextView textView = findViewById(R.id.stopText2);
                textView.setText(getResources().getText(R.string.null_label));
            }
        });
    }

    public void createAlert(String text) {
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
}
