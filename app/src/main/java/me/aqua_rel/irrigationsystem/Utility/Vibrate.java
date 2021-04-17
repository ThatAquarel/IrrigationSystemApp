package me.aqua_rel.irrigationsystem.Utility;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

import me.aqua_rel.irrigationsystem.MainActivity;

public class Vibrate {
    private final MainActivity mainActivity;

    public Vibrate(MainActivity _mainActivity) {
        this.mainActivity = _mainActivity;
    }

    public void click() {
        if (Build.VERSION.SDK_INT >= 26) {
            Vibrator vibrator = (Vibrator) this.mainActivity.getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(VibrationEffect.createOneShot(20, 255));
        }
    }
}
