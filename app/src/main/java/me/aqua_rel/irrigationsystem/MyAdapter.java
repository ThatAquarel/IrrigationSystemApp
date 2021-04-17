package me.aqua_rel.irrigationsystem;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    String[] s1;
    Context context;
    Job job;
    ItemTouchHelper itemTouchHelper;

    public MyAdapter(Context _context, String[] _s1, Job _job) {
        this.context = _context;
        this.s1 = _s1;
        this.job = _job;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.my_row, parent, false);

        return new MyViewHolder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        holder.title.setText(s1[position]);
        holder.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                //Log.i("debug", String.valueOf(i) + " " + position);
                job.setJob(position, i * 5);
                holder.description.setText(i * 5 + " Min");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        View.OnTouchListener onTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (itemTouchHelper == null) {
                        return false;
                    }

                    itemTouchHelper.startDrag(holder);
                    return true;
                }
                return false;
            }
        };

        holder.title.setOnTouchListener(onTouchListener);
        holder.description.setOnTouchListener(onTouchListener);
    }

    @Override
    public int getItemCount() {
        return s1.length;
    }

    public void setItemTouchHelper(ItemTouchHelper itemTouchHelper_) {
        this.itemTouchHelper = itemTouchHelper_;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title, description;
        SeekBar seekBar;
        ConstraintLayout constraintLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            seekBar = itemView.findViewById(R.id.seekBar);
            constraintLayout = itemView.findViewById(R.id.row_constraint);
        }
    }
}
