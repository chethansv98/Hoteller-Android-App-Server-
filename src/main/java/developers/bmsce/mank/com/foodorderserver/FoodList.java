package developers.bmsce.mank.com.foodorderserver;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import developers.bmsce.mank.com.foodorderserver.Common.Common;
import developers.bmsce.mank.com.foodorderserver.Interface.ItemClickListener;
import developers.bmsce.mank.com.foodorderserver.Models.Category;
import developers.bmsce.mank.com.foodorderserver.Models.Food;
import developers.bmsce.mank.com.foodorderserver.R;
import developers.bmsce.mank.com.foodorderserver.ViewHolder.FoodViewHolder;
import developers.bmsce.mank.com.foodorderserver.ViewHolder.MenuViewHolder;

public class FoodList extends AppCompatActivity {

    EditText editName,edtDescription,edtPrice,edtDiscount;
    Button btnselect, btnuplaod;
    Food newfood;
    Uri saveUrl;
    private final int PICK_IMAGE_REQUEST = 71;

    FirebaseDatabase db;
    DatabaseReference foodlist;
    FirebaseStorage storage;
    StorageReference storageReference;

    FloatingActionButton fab;

    RelativeLayout roottLayout;


    RecyclerView recycler_view;
    RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<Food,FoodViewHolder> adpter;

    String categoryId="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);


        roottLayout = findViewById(R.id.root_layout);
        //Auth
        db = FirebaseDatabase.getInstance();
        foodlist = db.getReference("Foods");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        recycler_view = findViewById(R.id.recycler_food);
        recycler_view.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recycler_view.setLayoutManager(layoutManager);
        
        fab = findViewById(R.id.fabfl);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showAddFoodDialog();
                
            }
        });
        if(getIntent() != null){
            categoryId = getIntent().getStringExtra("CategoryId");

        }
        if (!categoryId.isEmpty() && categoryId != null) {
            loadListFood(categoryId);
        }

    }

    private void showAddFoodDialog() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(FoodList.this);
        alertDialog.setTitle("Add New Food");
        alertDialog.setMessage("Please Fill Complete Information");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_new_food_layout, null);


        editName = add_menu_layout.findViewById(R.id.editName);
        edtDescription = add_menu_layout.findViewById(R.id.editDescription);
        edtPrice = add_menu_layout.findViewById(R.id.editPrice);
        edtDiscount = add_menu_layout.findViewById(R.id.editDiscount);

        btnselect = add_menu_layout.findViewById(R.id.btnselect);
        btnuplaod = add_menu_layout.findViewById(R.id.btnupload);

        btnselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                chooseImage();
            }
        });

        btnuplaod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });


        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                if(newfood  !=null){

                    foodlist.push().setValue(newfood);
                    Snackbar.make(roottLayout,"New Food"+newfood.getName()+"was addded",Snackbar.LENGTH_SHORT).show();

                }
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

    private void loadListFood(String categoryId) {

        adpter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(Food.class,
                R.layout.food_item,
                FoodViewHolder.class,
                foodlist.orderByChild("menuId").equalTo(categoryId)) {
            @Override
            protected void populateViewHolder(FoodViewHolder viewHolder, Food model, int position) {


                viewHolder.food_name.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(viewHolder.food_image);
                final Food local = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                        Toast.makeText(FoodList.this,""+local.getName(),Toast.LENGTH_SHORT).show();



//                        Intent foodlist = new Intent(FoodList.this, FoodDetail.class);
//                        foodlist.putExtra("FoodId", adpter.getRef(position).getKey());
//                        startActivity(foodlist);


                    }
                });

            }
        };
adpter.notifyDataSetChanged();
        recycler_view.setAdapter(adpter);
    }
    private void uploadImage() {


        if (saveUrl != null) {

            final ProgressDialog progressDialog = new ProgressDialog(FoodList.this);
            progressDialog.setMessage("Uploading........");
            progressDialog.show();


            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("images/"+imageName);
            imageFolder.putFile(saveUrl)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                            progressDialog.dismiss();
                            Toast.makeText(FoodList.this,"Uplaod Succesfull",Toast.LENGTH_SHORT).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    newfood = new Food();
                                    newfood.setName(editName.getText().toString());
                                    newfood.setDescription(edtDescription.getText().toString());
                                    newfood.setPrice(edtPrice.getText().toString());
                                    newfood.setDiscount(edtDiscount.getText().toString());
                                    newfood.setMenuId(categoryId);
                                    newfood.setImage(uri.toString());

                                }
                            });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            progressDialog.dismiss();
                            Toast.makeText(FoodList.this,e.getMessage(),Toast.LENGTH_SHORT).show();

                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Uplaoded...."+progress+"%");

                        }
                    });
        }
    }
    private void chooseImage() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select picture of food"),PICK_IMAGE_REQUEST);


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data.getData() != null && data != null) {

            saveUrl = data.getData();
            btnselect.setText("Image Selected");

        }
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {


        if(item.getTitle().equals(Common.UPDATE))
        {

            showUpdateFoodDialog(adpter.getRef(item.getOrder()).getKey(), adpter.getItem(item.getOrder()));
        } else if (item.getTitle().equals(Common.DELETE)) {


            deleteFood(adpter.getRef(item.getOrder()).getKey());
        }

        return super.onContextItemSelected(item);
    }

    private void deleteFood(String key) {

        foodlist.child(key).removeValue();
    }

    private void showUpdateFoodDialog(final String key, final Food item) {

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(FoodList.this);
        alertDialog.setTitle("Edit Food");
        alertDialog.setMessage("Please Fill Complete Information");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_new_food_layout, null);


        editName = add_menu_layout.findViewById(R.id.editName);
        edtDescription = add_menu_layout.findViewById(R.id.editDescription);
        edtPrice = add_menu_layout.findViewById(R.id.editPrice);
        edtDiscount = add_menu_layout.findViewById(R.id.editDiscount);

        editName.setText(item.getName());
        edtDiscount.setText(item.getDiscount());
        edtDescription.setText(item.getDescription());
        edtPrice.setText(item.getPrice());


        btnselect = add_menu_layout.findViewById(R.id.btnselect);
        btnuplaod = add_menu_layout.findViewById(R.id.btnupload);

        btnselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                chooseImage();
            }
        });

        btnuplaod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeImage(item);
            }
        });


        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                if(newfood  !=null){

                    item.setName(editName.getText().toString());
                    item.setDescription(edtDescription.getText().toString());
                    item.setDiscount(edtDiscount.getText().toString());
                    item.setPrice(edtPrice.getText().toString());


                    foodlist.child(key).setValue(item);
                    Snackbar.make(roottLayout,"Food "+item.getName()+"was addded",Snackbar.LENGTH_SHORT).show();

                }
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

    private void ChangeImage(final Food item) {


        if (saveUrl != null) {

            final ProgressDialog progressDialog = new ProgressDialog(FoodList.this);
            progressDialog.setMessage("Uploading........");
            progressDialog.show();


            String imageName = UUID.randomUUID().toString();
            final StorageReference imageFolder = storageReference.child("images/"+imageName);
            imageFolder.putFile(saveUrl)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                            progressDialog.dismiss();
                            Toast.makeText(FoodList.this,"Uplaod Succesfull",Toast.LENGTH_SHORT).show();
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    item.setImage(uri.toString());

                                }
                            });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            progressDialog.dismiss();
                            Toast.makeText(FoodList.this,e.getMessage(),Toast.LENGTH_SHORT).show();

                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Uplaoded...."+progress+"%");

                        }
                    });
        }
    }


}
