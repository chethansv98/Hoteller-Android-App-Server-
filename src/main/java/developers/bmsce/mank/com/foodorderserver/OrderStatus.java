package developers.bmsce.mank.com.foodorderserver;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;

import developers.bmsce.mank.com.foodorderserver.Common.Common;
import developers.bmsce.mank.com.foodorderserver.Interface.ItemClickListener;
import developers.bmsce.mank.com.foodorderserver.Models.MyResponse;
import developers.bmsce.mank.com.foodorderserver.Models.Notification;
import developers.bmsce.mank.com.foodorderserver.Models.Order;
import developers.bmsce.mank.com.foodorderserver.Models.Request;
import developers.bmsce.mank.com.foodorderserver.Models.Sender;
import developers.bmsce.mank.com.foodorderserver.Models.Token;
import developers.bmsce.mank.com.foodorderserver.Remote.APIService;
import developers.bmsce.mank.com.foodorderserver.ViewHolder.OrderViewHolder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderStatus extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference requests;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<Request,OrderViewHolder> adapter;

    MaterialSpinner spinner;
    APIService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);




        //Auth
        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");




        recyclerView = findViewById(R.id.listOrders);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        loadOrders();
    }

    private void loadOrders() {

        adapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(
                Request.class,
                R.layout.order_layout,
                OrderViewHolder.class,
                requests
        ) {
            @Override
            protected void populateViewHolder(OrderViewHolder viewHolder, final Request model, final int position) {


                viewHolder.txtOrderId.setText(adapter.getRef(position).getKey());
                viewHolder.txtOrderStatus.setText(Common.converCodeToStatus(model.getStatus()));
                viewHolder.txtOrderphone.setText(model.getPhone());
                viewHolder.txtOrderAddress.setText(model.getAddress());
                viewHolder.txtOderDate.setText(Common.getdate(Long.parseLong(adapter.getRef(position).getKey())));

                viewHolder.btnEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        showUpdateFoodDialog(adapter.getRef(position).getKey(), adapter.getItem(position));

                    }
                });

                viewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        deleteFood(adapter.getRef(position).getKey());


                    }
                });

                viewHolder.btnDetail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent orderDetail = new Intent(OrderStatus.this, OrderDetail.class);
                        Common.currentRequest = model;
                        orderDetail.putExtra("OrderId", adapter.getRef(position).getKey());
                        startActivity(orderDetail);

                    }
                });
                viewHolder.btnDirection.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent inten = new Intent(OrderStatus.this, TrackOrder.class);
                        Common.currentRequest = model;
                        startActivity(inten);
                    }
                });


            }
        };
        recyclerView.setAdapter(adapter);
    }


//    @Override
//    public boolean onContextItemSelected(MenuItem item) {
//
//
//        if(item.getTitle().equals(Common.UPDATE))
//        {
//
//            showUpdateFoodDialog(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
//        } else if (item.getTitle().equals(Common.DELETE)) {
//
//
//           deleteFood(adapter.getRef(item.getOrder()).getKey());
//        }
//
//        return super.onContextItemSelected(item);
//    }



    private void deleteFood(String key) {

        requests.child(key).removeValue();
        adapter.notifyDataSetChanged();
    }

    private void showUpdateFoodDialog(String key, final Request item) {

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(OrderStatus.this);
        alertDialog.setTitle("Update Order");
        alertDialog.setMessage("Choose Status of Order");

        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.update_order_layout, null);

        spinner = view.findViewById(R.id.statusSpinner);
        spinner.setItems("Placed","On my Way","Shipped");


        alertDialog.setView(view);

        final String localKey = key;

        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                item.setStatus(String.valueOf(spinner.getSelectedIndex()));
                requests.child(localKey).setValue(item);

                sendOrderStatusToUser(localKey,item);
                adapter.notifyDataSetChanged();


            }
        });
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.show();





    }

    private void sendOrderStatusToUser(final String key,Request item) {

        DatabaseReference tokens = database.getReference("Tokens");
        tokens.orderByKey().equalTo(key)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot postDataSnapshot1 : dataSnapshot.getChildren()) {

                            Token token = postDataSnapshot1.getValue(Token.class);
                            Notification notification = new Notification("Lakshmi Resturent","your order "+key+" was updated");
                            Sender content = new Sender(token.getToken(), notification);
                            mService.sendNotification(content)
                                    .enqueue(new Callback<MyResponse>() {
                                        @Override
                                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {

                                            if(response.code() == 200) {
                                                if (response.body().success == 1) {

                                                    Toast.makeText(OrderStatus.this, "Order was updated", Toast.LENGTH_SHORT).show();

                                                } else {

                                                    Log.e("notification", "onResponse: "+key+"failed" );
                                                    Toast.makeText(OrderStatus.this, "Order was updated but failed", Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                        }

                                        @Override
                                        public void onFailure(Call<MyResponse> call, Throwable t) {

                                            Log.e("ERROR", "onFailure: "+t.getMessage() );

                                        }
                                    });


                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


}
