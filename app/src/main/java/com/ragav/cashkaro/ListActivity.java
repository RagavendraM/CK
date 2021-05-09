package com.ragav.cashkaro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.ragav.cashkaro.DatabaseUtils.Model;
import com.ragav.cashkaro.DatabaseUtils.ModelDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ListActivity extends AppCompatActivity {

//    ModelDatabase database;
    Adapter eventAdapter;
    RecyclerView recyclerview;
    private DataViewModel viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_appbar);

        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Long Press to Delete", Snackbar.LENGTH_LONG);
        snackbar.show();

        recyclerview = findViewById(R.id.recycler_list);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        recyclerview.setHasFixedSize(true);
        eventAdapter = new Adapter(getApplicationContext());
        recyclerview.setAdapter(eventAdapter);

        viewModel = ViewModelProviders.of(this).get(DataViewModel.class);
        viewModel.getAllData().observe(this,(data)-> eventAdapter.setData(data));
    }


    public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
        Context context;
        List<Model> data;
        Date date;
        boolean isEnabled;
        boolean isAllSelected;
        List<Model> selectedList = new ArrayList<>();

        public Adapter(Context context) {
            this.context = context;
            data = new ArrayList<>();
        }
        public void setData(List<Model> newData){
           if(data!=null){
               data.clear();
               data.addAll(newData);
               notifyDataSetChanged();
           }
           else{
               data = newData;
           }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_list, parent, false));
        }


        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            date = new Date(Long.parseLong(data.get(position).getTimeShared()));
            holder.eventText.setText(data.get(position).getTitle());
            //holder.timeAndDateText.setText((date.toString()).split("GMT")[0]);
            holder.eventApp.setText(data.get(position).getApp());

            holder.itemView.setOnLongClickListener(view -> {
                   if(!isEnabled){
                       ActionMode.Callback callback = new ActionMode.Callback() {
                           @Override
                           public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                               MenuInflater menuInflater = actionMode.getMenuInflater();
                               menuInflater.inflate(R.menu.menu,menu);
                               return true;
                           }

                           @Override
                           public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                              isEnabled = true;
                              ClickItem(holder);
                               return false;
                           }

                           @Override
                           public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                               int id = menuItem.getItemId();
                               switch (id){
                                   case R.id.menu_delete:
                                       for(Model model :selectedList){
                                          viewModel.deleteData(model);
                                       }
                                       if(data.size()==0){
                                           Toast.makeText(getApplicationContext(),"All Data Removed",Toast.LENGTH_LONG).show();
                                       }
                                       actionMode.finish();
                                       break;
                                   case R.id.menu_select_all:
                                      if(data.size()==selectedList.size()){
                                          isAllSelected = false;
                                          selectedList.clear();
                                      }
                                      else {
                                          isAllSelected = true;
                                          selectedList.clear();
                                          selectedList.addAll(data);
                                      }
                                       notifyDataSetChanged();
                                       break;
                               }
                               return false;
                           }

                           @Override
                           public void onDestroyActionMode(ActionMode actionMode) {
                                    isEnabled = false;
                                    isAllSelected = false;
                                    selectedList.clear();
                                    notifyDataSetChanged();
                           }
                       };
                       ((AppCompatActivity) view.getContext()).startActionMode(callback);
                   }
                   else {
                       ClickItem(holder);
                   }
                return false;
            });


            if(isAllSelected){
                holder.checkBox.setVisibility(View.VISIBLE);
                //holder.itemView.setBackgroundColor(getResources().getColor(R.color.teal_700));
            }
            else{
                holder.checkBox.setVisibility(View.GONE);
                //holder.itemView.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            }

//
        }

        private void ClickItem(ViewHolder holder) {
            Model model = data.get(holder.getAdapterPosition());
            if(holder.checkBox.getVisibility() == View.GONE){
                holder.checkBox.setVisibility(View.VISIBLE);
                holder.itemView.setBackgroundColor(getResources().getColor(R.color.teal_700));
                selectedList.add(model);
            }
            else {
                holder.checkBox.setVisibility(View.GONE);
                holder.itemView.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                selectedList.remove(model);
            }
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private TextView eventText,eventApp;
            private TextView timeAndDateText;
            private ImageView checkBox;
            private LinearLayout ll;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                eventText = itemView.findViewById(R.id.txt_title);
                //timeAndDateText =  itemView.findViewById(R.id.txt_date);
                eventApp = itemView.findViewById(R.id.txt_app);
                checkBox = itemView.findViewById(R.id.checkbox);
                ll = itemView.findViewById(R.id.ll_bg);
            }
        }
    }
}