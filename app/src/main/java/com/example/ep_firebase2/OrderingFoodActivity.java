package com.example.ep_firebase2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.net.Inet4Address;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class OrderingFoodActivity extends AppCompatActivity {

    List<Food> foodList;
    ExtendedFloatingActionButton order_Button;
    BottomSheetDialog dialog;
    Button logOut_Button;
    Button address_Button;
    Dialog addressDialog;
    Map<String, List<Food>> orderList;
    TextView listTextView, priceTextView, deliveryTimeTextView, addressTextView, countTextView, phoneTextView;
    Button payButton;
    String email, id;


    private static final String TAG = "EmailPassword";
    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]
    private FirebaseFirestore db;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ordering_food_screen);

        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        Log.d("Qwe---------",email);

        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]

        db = FirebaseFirestore.getInstance();


        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        dialog=new BottomSheetDialog(this);
        createDialog();

        RecyclerView rvFoods = (RecyclerView) findViewById(R.id.recyclerView);
        order_Button=findViewById(R.id.extended_fab);
        logOut_Button=findViewById(R.id.logout_Button);
        address_Button=findViewById(R.id.address_Button);
        foodList=new ArrayList<>();
        orderList=new HashMap<>();
        loadFoodList();

        addressDialog=new Dialog(OrderingFoodActivity.this);

        // определяем слушателя нажатия элемента в списке
        FoodAdapter.OnFoodClickListener foodClickListener = new FoodAdapter.OnFoodClickListener() {
            @Override
            public void onFoodClick(Food food, int position) {
                int price=0;
                if(!order_Button.getText().toString().isEmpty()){
                    String str = order_Button.getText().toString().substring(0,order_Button.getText().toString().length()-1);
                    price=Integer.parseInt(str);
                }
                price+=food.getPrice();
                order_Button.setText(Integer.toString(price)+"₽");

                List<Food> myFoods=new ArrayList<>();
                myFoods.add(new Food(food.getPrice(),1));
                orderList.put(food.getName(),myFoods);
            }
        };


        // заполнение Bottom Sheets данными, просмотр чека
        order_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String list="";
                for (Map.Entry<String, List<Food>> entry : orderList.entrySet()) {
                    list+=entry.getKey()+"\t\t"+entry.getValue().get(0).getPrice()+" ₽ x "+entry.getValue().get(0).getCount();
                    list+="\n";
                }
                listTextView.setText(list);
                priceTextView.setText(order_Button.getText());
                setTime();
                addressTextView.setText(address_Button.getText());
                dialog.show();
                setPhone();

                payButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(OrderingFoodActivity.this, "Заказ создан",
                                Toast.LENGTH_LONG).show();

                        int price=0;
                        if(!priceTextView.getText().toString().isEmpty()){
                            String str = order_Button.getText().toString().substring(0,order_Button.getText().toString().length()-1);
                            price=Integer.parseInt(str);
                        }

                        addUser(id,
                                listTextView.getText().toString(),
                                "1",
                                addressTextView.getText().toString(),
                                phoneTextView.getText().toString(),
                                deliveryTimeTextView.getText().toString(),
                                price);
                    }
                });
            }
        });
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);


        // выход из учетной записи
        logOut_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mAuth.signOut();
                Intent intent=new Intent(OrderingFoodActivity.this,SignInActivity.class);
                startActivity(intent);
            }
        });

        // указание адреса доставки
        address_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] result = {""};
                AlertDialog.Builder b = new AlertDialog.Builder(OrderingFoodActivity.this);
                b.setTitle("Please enter a address");
                final EditText input = new EditText(OrderingFoodActivity.this);
                b.setView(input);
                b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        result[0] = input.getText().toString();
                        address_Button.setText(result[0]);
                    }
                });
                b.setNegativeButton("CANCEL", null);
                b.show();

                //showAddressDialog();
            }
        });

        // Создание адаптера
        FoodAdapter adapter = new FoodAdapter(this, foodList, foodClickListener);
        // Прикрепрепляем адаптер к recyclerView
        rvFoods.setAdapter(adapter);
        // размещение элементов
        rvFoods.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
    }


    // список меню
    public void loadFoodList(){
        foodList.add(new Food("Pizza", 885));
        foodList.add(new Food("Ice cream", 245));
        foodList.add(new Food("Pasta", 428));
        foodList.add(new Food("Oreo", 102));
        foodList.add(new Food("Tea", 312));
    }

    // создание Bottom Sheets
    private void createDialog(){
        View view =getLayoutInflater().inflate(R.layout.buttom_sheets_layout,null,false);

        listTextView=view.findViewById(R.id.order_TextView);
        priceTextView=view.findViewById(R.id.price_TextView);
        deliveryTimeTextView=view.findViewById(R.id.timeDelivery_TextView);
        addressTextView=view.findViewById(R.id.address_TextView);
        payButton=view.findViewById(R.id.pay_Button);
        phoneTextView=view.findViewById(R.id.phone_TextView);
        dialog.setContentView(view);
    }


    // диалоговое окно с указанием адреса доставки
    private void showAddressDialog(){
        addressDialog.setContentView(R.layout.adress_dialog_layout);
        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        addressDialog.setCancelable(true);

        Button addAddressButton=addressDialog.findViewById(R.id.confirmAddress_Button);
        EditText addressEditText=addAddressButton.findViewById(R.id.addressEditText);

        addAddressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //address_Button.setText(addressEditText.getText().toString());
                //Toast.makeText(OrderingFoodActivity.this, addressEditText.getText().toString(),
                //      Toast.LENGTH_LONG).show();
                addressDialog.cancel();

            }
        });

        addressDialog.show();
    }


    // указание времени доставки
    SimpleDateFormat timeFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
    // метод устанавливает время на кнопках
    private void setTime() {
        long currentTime = System.currentTimeMillis();
        deliveryTimeTextView.setText(timeFormat.format(currentTime + timeToMillis(1,30))); // текущее время + 1 ч. 30 мин.
    }

    // метод переводит часы и минуты в милисекунды
    private long timeToMillis(int hour, int minute){
        return TimeUnit.HOURS.toMillis(hour) + TimeUnit.MINUTES.toMillis(minute);
    }

    private void addUser(String id, String product,String number,String address, String phone,String time, Integer price){
        // Create a new user with a first and last name
        Map<String, Object> order = new HashMap<>();
        order.put("IdUser", id);
        order.put("Product", product);
        order.put("Number", number);
        order.put("Adress", address);
        order.put("Phone",phone);
        order.put("Date",time);
        order.put("Price",price);

        // Add a new document with a generated ID
        db.collection("Orders")
                .add(order)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }


    private void setPhone(){
        db.collection("UsersClient")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(document.get("Email").toString().equals(email)){
                                    phoneTextView.setText(document.get("Phone").toString());
                                    addressTextView.setText(document.get("Adress").toString());
                                    id=document.get("Id").toString();
                                }
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }
}
