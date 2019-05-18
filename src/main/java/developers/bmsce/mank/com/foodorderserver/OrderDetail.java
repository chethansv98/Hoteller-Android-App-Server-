package developers.bmsce.mank.com.foodorderserver;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import developers.bmsce.mank.com.foodorderserver.Common.Common;
import developers.bmsce.mank.com.foodorderserver.ViewHolder.OrderDetailsAdapter;

public class OrderDetail extends AppCompatActivity {

    TextView order_id, order_phone, order_total, order_address, order_comment;
    String order_id_value = "";
    RecyclerView lsFoods;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        order_id = findViewById(R.id.order_id);
        order_phone = findViewById(R.id.order_phone);
        order_total = findViewById(R.id.order_total);
        order_address = findViewById(R.id.order_address);
        order_comment = findViewById(R.id.order_comment);

        lsFoods = findViewById(R.id.lstFoods);
        lsFoods.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        lsFoods.setLayoutManager(layoutManager);


        if (getIntent() != null) {

            order_id_value = getIntent().getStringExtra("OrderId");
        }

        order_id.setText(order_id_value);
        order_phone.setText(Common.currentRequest.getPhone());
        order_total.setText(Common.currentRequest.getTotal());
        order_address.setText(Common.currentRequest.getAddress());
        order_comment.setText(Common.currentRequest.getComment());

        OrderDetailsAdapter adapter = new OrderDetailsAdapter(Common.currentRequest.getFoods());

        adapter.notifyDataSetChanged();
        lsFoods.setAdapter(adapter);

    }

}

