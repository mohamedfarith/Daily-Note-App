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

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.MyViewHolder> {
   private ArrayList<NotesDatabaseList> noteDbList;
   private Context mContext;
   private TextView txtnotesItem;
   private TextView txtCurrentTime;
   NoteDb deleteRowInDb;
   SQLiteDatabase deleteRowInDatabase;
    LongPressActionListener longPressListener;
   private static final String TAG = NotesAdapter.class.getSimpleName();
    NotesAdapter(Context mContext, ArrayList<NotesDatabaseList> txt){
        this.mContext  = mContext;
        this.noteDbList= txt;
    }
    @NonNull
    @Override
    public NotesAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_view,parent,false);
        initView(view);
        return (new MyViewHolder(view));

    }
    private void initView(View view){
        txtnotesItem = view.findViewById(R.id.txt_notes);
        txtCurrentTime = view.findViewById(R.id.current_time);
    }

    @Override
    public void onBindViewHolder(@NonNull NotesAdapter.MyViewHolder holder, int position) {
    txtnotesItem.setText(noteDbList.get(position).getNotes());
    txtCurrentTime.setText(noteDbList.get(position).getDate());
    }

    @Override
    public int getItemCount() {
        return noteDbList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        public MyViewHolder(@NonNull final View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick: "+noteDbList.get(getAdapterPosition()));
                    Intent intent = new Intent(mContext,NoteActivity.class);
                    Log.d(TAG, "onClick: adapter postition"+getAdapterPosition());
                    intent.putExtra("notes",noteDbList.get(getAdapterPosition()).getNotes());
                    String value = String.valueOf(getAdapterPosition()+1);
                    intent.putExtra("position",value);
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
                            deleteRowInDb.deleteRowInDb(deleteRowInDatabase,getAdapterPosition());
                            longPressListener = (LongPressActionListener) mContext;
                            longPressListener.updateRecyclerView(getAdapterPosition());
                        }
                    });
                    deleteAlertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });deleteAlertDialog.create().show();
                }
            });
        }
    }

}

