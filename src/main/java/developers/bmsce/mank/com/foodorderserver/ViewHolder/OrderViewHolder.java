package developers.bmsce.mank.com.foodorderserver.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import developers.bmsce.mank.com.foodorderserver.Common.Common;
import developers.bmsce.mank.com.foodorderserver.Interface.ItemClickListener;
import developers.bmsce.mank.com.foodorderserver.R;


public class OrderViewHolder extends RecyclerView.ViewHolder {



   public TextView txtOrderId, txtOrderStatus,txtOrderphone,txtOrderAddress,txtOderDate;
//     private ItemClickListener itemClickListener;
     public Button btnEdit,btnDelete,btnDirection,btnDetail;


    public OrderViewHolder(View itemView) {
        super(itemView);

        txtOrderId = itemView.findViewById(R.id.order_id);
        txtOrderStatus = itemView.findViewById(R.id.order_status);
        txtOrderphone = itemView.findViewById(R.id.order_phone);
        txtOrderAddress = itemView.findViewById(R.id.order_address);
        txtOderDate = itemView.findViewById(R.id.order_date);

        btnEdit = itemView.findViewById(R.id.btnedit);
        btnDelete = itemView.findViewById(R.id.btndelete);
        btnDirection = itemView.findViewById(R.id.btnDirection);
        btnDetail = itemView.findViewById(R.id.btnDetail);
//
//        itemView.setOnCreateContextMenuListener(this);
//        itemView.setOnClickListener(this);


    }

//    public void setItemClickListener(ItemClickListener itemClickListener) {
//        this.itemClickListener = itemClickListener;
//    }

//    @Override
//    public void onClick(View v) {
//        itemClickListener.onClick(v,getAdapterPosition(),false);
//
//    }

//    @Override
//    public void onCreateContextMenu(ContextMenu contextMenu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//
//        contextMenu.setHeaderTitle("Select the Action");
//
//
//        contextMenu.add(0, 0, getAdapterPosition(), Common.UPDATE);
//        contextMenu.add(0, 1, getAdapterPosition(), Common.DELETE);
//
//
//    }
}
