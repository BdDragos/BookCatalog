package com.mobilelab.artyomska.bookdeposit.listAdapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobilelab.artyomska.bookdeposit.MainActivity;
import com.mobilelab.artyomska.bookdeposit.model.Book;
import com.mobilelab.artyomska.bookdeposit.listHolder.BookHolder;
import com.mobilelab.artyomska.bookdeposit.utils.EndlessRecyclerViewScrollListener;

import java.util.ArrayList;



public class BookAdapter extends RecyclerView.Adapter<BookHolder> {

    private ArrayList<Book> bookList;
    private Context context;
    private int itemResource;

    public BookAdapter(Context context, int itemResource) {

        // 1. Initialize our adapter
        this.context = context;
        this.itemResource = itemResource;
        this.bookList = new ArrayList<>();
    }

    // 2. Override the onCreateViewHolder method
    @Override
    public BookHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        // 3. Inflate the view and return the new ViewHolder
        View view = LayoutInflater.from(parent.getContext()).inflate(this.itemResource, parent, false);
        return new BookHolder(view,context,this);
    }

    // 4. Override the onBindViewHolder method
    @Override
    public void onBindViewHolder(BookHolder holder, int position) {

        Book book = this.bookList.get(position);
        holder.bindBook(book);
    }

    @Override
    public int getItemCount() {

        return this.bookList.size();
    }


    public void changeFragment(String ID, String ISBN, int i)
    {
        if(context instanceof MainActivity) {
            ((MainActivity) context).changeFragment(ID, ISBN, i);
        }
    }

    public void addNewDataPage(ArrayList<Book> data)
    {
        final int size = bookList.size() + 1;
        bookList.addAll(data);
        notifyItemRangeInserted(size, data.size());
    }

}
