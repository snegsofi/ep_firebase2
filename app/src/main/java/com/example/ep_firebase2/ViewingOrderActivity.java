package com.example.ep_firebase2;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ViewingOrderActivity extends AppCompatActivity {

    private static final String TAG = "EmailPassword";
    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]
    private FirebaseFirestore db;

    List<Order> orderList;

    Button logout_Button;
    RecyclerView rvFoods;
    CheckBox todayOrder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewing_order);

        rvFoods = (RecyclerView) findViewById(R.id.order_recyclerView);
        todayOrder=findViewById(R.id.todayOrder_CheckBox);
        logout_Button=findViewById(R.id.logout2_Button);
        orderList=new ArrayList<>();



        boolean sorted=todayOrder.isChecked();

        todayOrder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    sortedData();
                }
                else{
                    readData();
                }
            }
        });


        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]

        db = FirebaseFirestore.getInstance();
        orderList=readData();

        //orderList=readData();
        logout_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mAuth.signOut();
                Intent intent=new Intent(ViewingOrderActivity.this,SignInActivity.class);
                startActivity(intent);
            }
        });



    }


    private List<Order> readData(){
        List<Order> order=new ArrayList<>();
        db.collection("Orders")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                order.add(new Order(document.get("IdUser").toString(),
                                        document.get("Product").toString(),
                                        document.get("Number").toString(),
                                        document.get("Adress").toString(),
                                        document.get("Phone").toString(),
                                        document.get("Date").toString(),
                                        Integer.parseInt(document.get("Price").toString())));

                                Log.d(TAG, order.get(0).getAddress());
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Log.d(TAG, document.getId() + " => " + document.get("IdUser").toString());
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }

                        List<Order> orderList=sortedOrder(order);

                        OrderAdapter adapter= new OrderAdapter(ViewingOrderActivity.this, orderList);
                        // Прикрепрепляем адаптер к recyclerView
                        rvFoods.setAdapter(adapter);
                        // размещение элементов
                        rvFoods.setLayoutManager(new LinearLayoutManager(ViewingOrderActivity.this,LinearLayoutManager.VERTICAL,false));
                    }
                });
        return order;
    }


    private List<Order> sortedData(){
        List<Order> order=new ArrayList<>();
        db.collection("Orders")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String timeStamp = new SimpleDateFormat("dd.MM.yyyy").format(Calendar.getInstance().getTime());
                                String time="";
                                try {
                                    Date thedate = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH).parse(document.get("Date").toString());
                                    time = new SimpleDateFormat("dd.MM.yyyy").format(thedate);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                if(time.equals(timeStamp)){
                                    order.add(new Order(document.get("IdUser").toString(),
                                            document.get("Product").toString(),
                                            document.get("Number").toString(),
                                            document.get("Adress").toString(),
                                            document.get("Phone").toString(),
                                            document.get("Date").toString(),
                                            Integer.parseInt(document.get("Price").toString())));
                                }
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }


                        //Map<Integer,Date> orderMap=new HashMap<>();
                        //for(int i=0;i<order.size();i++){
                        //    try {
                        //        Date thedate = new SimpleDateFormat("dd.MM.yyyy hh:mm", Locale.ENGLISH).parse(order.get(i).getTimeDelivery());
                        //        orderMap.put(i,thedate);
//
                        //    } catch (ParseException e) {
                        //        e.printStackTrace();
                        //    }
                        //}
//
                        //LinkedHashMap<Integer,Date> sortedOrder=orderMap.entrySet().stream()
                        //        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                        //        .collect(LinkedHashMap::new,
                        //                (m, c) -> m.put(c.getKey(), c.getValue()),
                        //                LinkedHashMap::putAll);
//

                        //List<Order> orderList=new ArrayList<>();
                        //for (Map.Entry<Integer, Date> entry : sortedOrder.entrySet()) {
                        //    orderList.add(order.get(entry.getKey()));
                        //}

                        List<Order> orderList=sortedOrder(order);
                        OrderAdapter adapter= new OrderAdapter(ViewingOrderActivity.this, orderList);
                        // Прикрепрепляем адаптер к recyclerView
                        rvFoods.setAdapter(adapter);
                        // размещение элементов
                        rvFoods.setLayoutManager(new LinearLayoutManager(ViewingOrderActivity.this,LinearLayoutManager.VERTICAL,false));
                    }
                });
        return order;
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public List<Order> sortedOrder(List<Order> list){
        Map<Integer,Date> orderMap=new HashMap<>();
        for(int i=0;i<list.size();i++){
            try {
                Date thedate = new SimpleDateFormat("dd.MM.yyyy hh:mm", Locale.ENGLISH).parse(list.get(i).getTimeDelivery());
                orderMap.put(i,thedate);

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        // Comparator.reverseOrder()
        LinkedHashMap<Integer,Date> sortedOrder=orderMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(LinkedHashMap::new,
                        (m, c) -> m.put(c.getKey(), c.getValue()),
                        LinkedHashMap::putAll);


        List<Order> orderList=new ArrayList<>();
        for (Map.Entry<Integer, Date> entry : sortedOrder.entrySet()) {
            orderList.add(list.get(entry.getKey()));
        }

        return orderList;

    }
}
