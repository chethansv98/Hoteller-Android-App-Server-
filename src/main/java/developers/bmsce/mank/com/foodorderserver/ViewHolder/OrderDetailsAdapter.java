package developers.bmsce.mank.com.foodorderserver.ViewHolder;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import developers.bmsce.mank.com.foodorderserver.Models.Order;
import developers.bmsce.mank.com.foodorderserver.R;

class myViewHolder extends RecyclerView.ViewHolder{


    public TextView pname,pquantity,pprice,pdiscount;

    public myViewHolder(View itemView) {

        super(itemView);

        pname = itemView.findViewById(R.id.product_name);
        pquantity = itemView.findViewById(R.id.product_quantity);
        pprice = itemView.findViewById(R.id.product_price);
        pdiscount = itemView.findViewById(R.id.product_discount);
    }
}

public class OrderDetailsAdapter extends RecyclerView.Adapter<myViewHolder> {


    List<Order>  myOrders;

    public OrderDetailsAdapter(List<Order> myOrders) {
        this.myOrders = myOrders;
    }

    @NonNull
    @Override


    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_detail_layout, parent, false);
        return new myViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {

        Order myOrder = myOrders.get(position);

        holder.pname.setText(String.format("Name : %s",myOrder.getProductName()));
        holder.pquantity.setText(String.format("Quantity : %s",myOrder.getQuantity()));
        holder.pprice.setText(String.format("Price : %s",myOrder.getPrice()));
        holder.pdiscount.setText(String.format("Discount : %s",myOrder.getDiscount()));


    }

    @Override
    public int getItemCount() {
        return myOrders.size();
    }
}
