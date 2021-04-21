package me.aqua_rel.irrigationsystem;

import android.annotation.SuppressLint;
import android.widget.TextView;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class Job {
    private final String[] tags = {"front", "back_pool", "back_shed"};

    private final HashMap<String, String> namesAndTags = new HashMap<>();
    private final LinkedHashMap<String, Integer> timers = new LinkedHashMap<>();

    private final MainActivity mainActivity;

    public Job(MainActivity _mainActivity) {
        String[] names = {"Front Yard", "Back Yard Pool", "Back Yard Shed"};

        this.mainActivity = _mainActivity;

        for (int i = 0; i < 3; i++) {
            namesAndTags.put(names[i], tags[i]);
            timers.put(tags[i], 30);
        }
    }

    @SuppressLint("SetTextI18n")
    public void setJob(int job, int time) {
        timers.put(tags[job], time);

        TextView textView = mainActivity.findViewById(R.id.startText2);
        textView.setText("Total " + getTotal() + " Minutes");
    }

    public int getJob(String name) {
        int job = Arrays.asList(tags).indexOf(name);

        String key = tags[job];

        Integer time = timers.get(key);
        if (time == null) {
            time = 0;
        }

        return time;
    }

    public String getTotal(){
        return String.valueOf(getJob(tags[0]) + getJob(tags[1]) + getJob(tags[2]));
    }

    public String nameToTag(String name) {
        return namesAndTags.get(name);
    }
}
