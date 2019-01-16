package com.example.farith.dailynotes;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.farith.dailynotes.ModelClass.NotesDatabaseList;

import org.w3c.dom.Text;

import java.security.PrivateKey;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.MyViewHolder> {
    private ArrayList<NotesDatabaseList> noteDbList;
    private Context mContext;
    //   private TextView txtnotesItem;
//   private TextView txtCurrentTime;
    NoteDb deleteRowInDb;
    SQLiteDatabase deleteRowInDatabase;
    LongPressActionListener longPressListener;
    private static final String TAG = NotesAdapter.class.getSimpleName();

    NotesAdapter(Context mContext, ArrayList<NotesDatabaseList> txt) {
        this.mContext = mContext;
        this.noteDbList = txt;
    }

    @NonNull
    @Override
    public NotesAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_view, parent, false);
        //  initView(view);
        return (new MyViewHolder(view));

    }

    @Override
    public void onBindViewHolder(@NonNull NotesAdapter.MyViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: " + noteDbList.get(position).getNotes());
        String noteValue = noteDbList.get(holder.getAdapterPosition()).getNotes();
        holder.txtnotesItem.setText(noteValue);
        holder.txtCurrentTime.setText(getCurrentTime(noteDbList.get(holder.getAdapterPosition()).getDate()));
    }

    @Override
    public int getItemCount() {
        return noteDbList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtnotesItem;
        TextView txtCurrentTime;

        public MyViewHolder(@NonNull final View itemView) {
            super(itemView);
            txtnotesItem = itemView.findViewById(R.id.txt_notes);
            txtCurrentTime = itemView.findViewById(R.id.current_time);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick: " + noteDbList.get(getAdapterPosition()));
                    Intent intent = new Intent(mContext, NoteActivity.class);
                    Log.d(TAG, "onClick: adapter postition" + getAdapterPosition());
                    intent.putExtra("notes", noteDbList.get(getAdapterPosition()).getNotes());
                    String value = String.valueOf(getAdapterPosition() + 1);
                    intent.putExtra("position", value);
                    intent.putExtra("previousTime", noteDbList.get(getAdapterPosition()).getDate());
                    intent.putExtra("noti", noteDbList.get(getAdapterPosition()).getNotificationID());
                    intent.putExtra("reminder_time", noteDbList.get(getAdapterPosition()).getReminder());
                    mContext.startActivity(intent);

                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    createDialog();
                    return true;
                }

                private void createDialog() {
                    final AlertDialog.Builder deleteAlertDialog = new AlertDialog.Builder(mContext);
                    deleteAlertDialog.setTitle("CONFIRM DELETE").setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteRowInDb = new NoteDb(mContext);
                            deleteRowInDb.deleteRowInDb(deleteRowInDatabase, getAdapterPosition(), noteDbList.get(getAdapterPosition()).getNotes());
                            longPressListener = (LongPressActionListener) mContext;
                            longPressListener.updateRecyclerView(getAdapterPosition(), noteDbList.get(getAdapterPosition()).getNotificationID());
                        }
                    });
                    deleteAlertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    deleteAlertDialog.create().show();
                }
            });
        }
    }

    //The date is formatted from milliseconds to Simple date format
    private String getCurrentTime(String dbStoredDate) {
        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, h:mm a");
        long dbDate = Long.parseLong(dbStoredDate);
        Date date = new Date(dbDate);
        String formattedDate = df.format(date);
        Log.d(TAG, "getCurrentTime: date " + date);
        return formattedDate;
    }

}

