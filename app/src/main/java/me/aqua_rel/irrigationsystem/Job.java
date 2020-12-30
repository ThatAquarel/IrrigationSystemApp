package me.aqua_rel.irrigationsystem;

import android.annotation.SuppressLint;
import android.util.Log;
import android.widget.TextView;

public class Job {
    private int frontyard = 30;
    private int backyard1 = 30;
    private int backyard2 = 30;

    private MainActivity mainActivity;

    public Job(MainActivity _mainActivity){
        this.mainActivity = _mainActivity;
    }

    @SuppressLint("SetTextI18n")
    public void setJob(int job, int time) {
        switch (job) {
            case 0:
                this.frontyard = time;
                break;
            case 1:
                this.backyard1 = time;
                break;
            case 2:
                this.backyard2 = time;
                break;
        }
        //Log.i("info", frontyard + " " + backyard1 + " " + backyard2);
        TextView textView = mainActivity.findViewById(R.id.startText2);
        int total = getFrontyard() + getBackyard1() + getBackyard2();
        textView.setText("Total " + total + " Minutes");
    }

    public int getFrontyard() {
        return frontyard;
    }

    public int getBackyard1() {
        return backyard1;
    }

    public int getBackyard2() {
        return backyard2;
    }
}
