package com.ragav.cashkaro;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.snackbar.Snackbar;
import com.ragav.cashkaro.DatabaseUtils.Model;
import com.ragav.cashkaro.NetworkUtils.ApiClient;
import com.ragav.cashkaro.NetworkUtils.ApiInterface;
import com.ragav.cashkaro.Broadcast.SharedAppReceiver;
import com.ragav.cashkaro.WorkManager.NotificationHandler;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity implements SharedAppReceiver.Listener {
    RecyclerView recyclerViewMain;
    List<DataModel> dataList;
    ContentAdapter contentAdapter;
    DataViewModel viewModel;
    SharedAppReceiver receiver;

    String[] params = new String[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NotificationHandler.oneTimeRequest();
        //createNotification(); //Sample Notification

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.custom_appbar);

        receiver = new SharedAppReceiver();
        receiver.setData(MainActivity.this);

        viewModel = ViewModelProviders.of(this).get(DataViewModel.class);
        dataList = new ArrayList<>();


        //RecyclerView
        recyclerViewMain = findViewById(R.id.recycler);
        recyclerViewMain.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMain.setHasFixedSize(true);
        contentAdapter = new ContentAdapter(this, dataList);
        recyclerViewMain.setAdapter(contentAdapter);

        //Network Connection
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        //Retrofit
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<List<DataModel>> call = apiService.getData();

        if (activeNetwork!=null) {
            call.enqueue(new Callback<List<DataModel>>() {

                @Override
                public void onResponse(Call<List<DataModel>> call, Response<List<DataModel>> response) {
                    dataList = response.body();
                    contentAdapter.setDataList(dataList);
                }

                @Override
                public void onFailure(Call<List<DataModel>> call, Throwable t) {
                    Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Please Try Again!!!", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }


            });
        } else {
            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "No Internet!!!", Snackbar.LENGTH_LONG);
            snackbar.show();
        }

        findViewById(R.id.btn_go).setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, ListActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void receivedData(String data) {
        Model model = new Model();
        model.setTitle(params[0]);
        model.setImgUrl(params[1]);
        model.setApp(data);
        model.setTimeShared("" + System.currentTimeMillis());
        viewModel.saveData(model);
    }

    public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.MyviewHolder> {

        Context context;
        List<DataModel> dataList;

        public ContentAdapter(Context context, List<DataModel> dataList) {
            this.context = context;
            this.dataList = dataList;
        }

        public void setDataList(List<DataModel> dataList) {
            this.dataList = dataList;
            notifyDataSetChanged();
        }

        @Override
        public ContentAdapter.MyviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.recyclerview_item, parent, false);
            return new MyviewHolder(view);
        }

        @Override
        public void onBindViewHolder(ContentAdapter.MyviewHolder holder, int position) {
            holder.tvMovieName.setText(dataList.get(position).getTitle());
            Glide.with(context).load(dataList.get(position).getImageUrl()).apply(RequestOptions.centerCropTransform()).into(holder.image);

            Intent intent = new Intent(getApplicationContext(), SharedAppReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 5, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            holder.share.setOnClickListener(view -> {
                params[0] = dataList.get(position).getTitle();
                params[1] = dataList.get(position).getImageUrl();
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, params.toString());
                sendIntent.setType("text/plain");
                startActivityForResult(Intent.createChooser(sendIntent, "Share ..", pendingIntent.getIntentSender()), 1);
            });

        }


        @Override
        public int getItemCount() {
            if (dataList != null) {
                return dataList.size();
            }
            return 0;

        }

        public class MyviewHolder extends RecyclerView.ViewHolder {
            TextView tvMovieName;
            ImageView image;
            ImageView share;

            public MyviewHolder(View itemView) {
                super(itemView);
                tvMovieName = itemView.findViewById(R.id.title);
                image = itemView.findViewById(R.id.image);
                share = itemView.findViewById(R.id.share);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1) {
            //DB operations
//            Model model = new Model();
//            model.setTitle(params[0]);
//            model.setImgUrl(params[1]);
//            model.setApp("share");
//            model.setTimeShared("" + System.currentTimeMillis());
//            viewModel.saveData(model);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void createNotification(){

        //intent to open our activity
        Intent intent = new Intent(getApplicationContext(), ListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),0,intent,0);

        Bitmap bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.a);
        //notifications
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(),"TASK")
                .setSmallIcon(R.drawable.ic_share)
                .setContentTitle("Event Reminder")
                .setContentText("Good Morning..Please check out latest updates")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap))
                .setLargeIcon(bitmap)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        //show notification
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH ;
            NotificationChannel notificationChannel = new NotificationChannel("TASK" , "CASH" , importance) ;
            assert notificationManagerCompat != null;
            notificationManagerCompat.createNotificationChannel(notificationChannel) ;
        }
        notificationManagerCompat.notify(1,builder.build());
    }

    @Override
    protected void onDestroy() {
        receiver.setData(null);
        super.onDestroy();
    }
}