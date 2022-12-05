package com.example.ep_firebase2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OrderAdapter extends
        RecyclerView.Adapter<OrderAdapter.ViewHolder>{

    // возвращает объект ViewHolder, который будет хранить данные по одному объекту Order
    @Override
    public OrderAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // надуваем данными из XML-файла объекты View
        // 1 параметр - идентификатор ресурса разметки
        // 2 параметр - корневой компонент
        // 3 параметр - нужно ли привязывать надутые объекты к корневому элементы
        View contactView = inflater.inflate(R.layout.order_recycler_view, parent, false);

        // возвращаем новый элемент ViewHolder
        OrderAdapter.ViewHolder viewHolder = new OrderAdapter.ViewHolder(contactView);
        return viewHolder;
    }


    // выполняет привязку объекта ViewHolder к объекту Order по определенной позиции
    // заполняет элементы данными через holder
    @Override
    public void onBindViewHolder(OrderAdapter.ViewHolder holder, int position) {
        // Получаем данные из определенной позиции
        Order Order = mOrders.get(position);

        // Установка значений элементам
        TextView textView = holder.idTextView;
        textView.setText(Order.getId());
        TextView textView1 = holder.productTextView;
        textView1.setText(Order.getProduct());
        TextView textView2 = holder.numberTextView;
        textView2.setText(Order.getNumber());
        TextView textView3 = holder.addressTextView;
        textView3.setText(Order.getAddress());
        TextView textView4 = holder.phoneTextView;
        textView4.setText(Order.getPhone());
        TextView textView5 = holder.timeDeliveryTextView;
        textView5.setText(Order.getTimeDelivery());
        TextView textView6 = holder.priceTextView;
        textView6.setText(Integer.toString(Order.getPrice()));

    }


    // возвращает количество объектов в списке
    @Override
    public int getItemCount() {
        return mOrders.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        // описывает и предоставляет доступ ко всем представлениям в каждой строке элемента
        public TextView idTextView;
        public TextView productTextView;
        public TextView numberTextView;
        public TextView addressTextView;
        public TextView phoneTextView;
        public TextView timeDeliveryTextView;
        public TextView priceTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            idTextView = (TextView) itemView.findViewById(R.id.idUser_TextView);
            productTextView = (TextView) itemView.findViewById(R.id.product_TextView);
            numberTextView=(TextView) itemView.findViewById(R.id.number_TextView);
            addressTextView = (TextView) itemView.findViewById(R.id.address_TextView);
            phoneTextView = (TextView) itemView.findViewById(R.id.phone_TextView);
            timeDeliveryTextView=(TextView) itemView.findViewById(R.id.timeDelivery_TextView);
            priceTextView=(TextView) itemView.findViewById((R.id.price_TextView));
        }
    }

    private final LayoutInflater inflater;
    private final List<Order> mOrders;

    OrderAdapter(Context context, List<Order> Orders) {
        this.mOrders = Orders;
        this.inflater = LayoutInflater.from(context);
    }
}
