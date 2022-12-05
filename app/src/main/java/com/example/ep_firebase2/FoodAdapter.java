package com.example.ep_firebase2;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FoodAdapter extends
        RecyclerView.Adapter<FoodAdapter.ViewHolder>{

    // возвращает объект ViewHolder, который будет хранить данные по одному объекту Food
    @Override
    public FoodAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // надуваем данными из XML-файла объекты View
        // 1 параметр - идентификатор ресурса разметки
        // 2 параметр - корневой компонент
        // 3 параметр - нужно ли привязывать надутые объекты к корневому элементы
        View contactView = inflater.inflate(R.layout.recyclerview_item_layout, parent, false);

        // возвращаем новый элемент ViewHolder
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }


    // выполняет привязку объекта ViewHolder к объекту Food по определенной позиции
    // заполняет элементы данными через holder
    @Override
    public void onBindViewHolder(FoodAdapter.ViewHolder holder, int position) {
        // Получаем данные из определенной позиции
        Food Food = mFoods.get(position);

        // Установка значений элементам
        TextView textView = holder.nameTextView;
        textView.setText(Food.getName());
        TextView textView1 = holder.priceTextView;
        textView1.setText(Integer.toString(Food.getPrice()));


        // Класс ViewHolder имеет поле itemView, которое представляет интерфейс
        // для одного объекта в списке и фактически объект View.
        // А у этого объекта есть метод setOnClickListener(),
        // через который можно подлючить стандартный слушатель нажатия OnClickListener
        // и в его методе onClick() вызвать метод нашего интерфейса, передав ему
        // необходимые данные - выбранный объект State и его позицию в списке

        // обработка нажатия
        holder.addFoodButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                // вызываем метод слушателя, передавая ему данные
                onClickListener.onFoodClick(Food, holder.getAdapterPosition());

                TextView textView2 = holder.countTextView;
                textView2.setText(Integer.toString(Integer.parseInt(textView2.getText().toString())+1));
            }
        });

    }


    // возвращает количество объектов в списке
    @Override
    public int getItemCount() {
        return mFoods.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        // описывает и предоставляет доступ ко всем представлениям в каждой строке элемента
        public TextView nameTextView;
        public TextView priceTextView;
        public Button addFoodButton;
        public TextView countTextView;
        public Button deleteFoodButton;

        public ViewHolder(View itemView) {
            super(itemView);

            nameTextView = (TextView) itemView.findViewById(R.id.foodName_TextView);
            priceTextView = (TextView) itemView.findViewById(R.id.price_TextView);
            addFoodButton=(Button) itemView.findViewById(R.id.addFood_Button);
            countTextView=itemView.findViewById(R.id.count_TextView);
            countTextView.setText("0");
        }
    }

    // интерфейс слушателя события нажатия
    interface OnFoodClickListener{
        void onFoodClick(Food Food, int position);
    }

    // переменная для хранения объекта интерфейса и получение для нее значения в конструкторе
    private final OnFoodClickListener onClickListener;
    private final LayoutInflater inflater;
    private final List<Food> mFoods;

    FoodAdapter(Context context, List<Food> Foods, OnFoodClickListener onClickListener) {
        this.onClickListener = onClickListener;
        this.mFoods = Foods;
        this.inflater = LayoutInflater.from(context);
    }
}
