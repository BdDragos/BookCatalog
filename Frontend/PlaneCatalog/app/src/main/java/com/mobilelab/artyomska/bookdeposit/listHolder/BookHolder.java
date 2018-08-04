package com.mobilelab.artyomska.bookdeposit.listHolder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.mobilelab.artyomska.bookdeposit.listAdapter.BookAdapter;
import com.mobilelab.artyomska.bookdeposit.R;
import com.mobilelab.artyomska.bookdeposit.model.Author;
import com.mobilelab.artyomska.bookdeposit.model.Book;
import com.mobilelab.artyomska.bookdeposit.model.Genre;
import com.mobilelab.artyomska.bookdeposit.utils.CheckNetwork;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class BookHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private ImageView thumbNail;
    private ImageView btnBookOption;
    private TextView title;
    private TextView rating;
    private TextView genre;
    private TextView author;
    private TextView year;
    private Context context;
    private Book book;
    private BookAdapter mAdapter;
    private ConstraintLayout layout;

    public BookHolder(View itemView, Context context,BookAdapter mAdapter)
    {

        super(itemView);

        this.mAdapter = mAdapter;
        this.context = context;
        thumbNail = itemView.findViewById(R.id.thumbnail);
        title = itemView.findViewById(R.id.title);
        rating = itemView.findViewById(R.id.rating);
        genre = itemView.findViewById(R.id.genre);
        author = itemView.findViewById(R.id.author);
        year = itemView.findViewById(R.id.releaseYear);
        btnBookOption = itemView.findViewById(R.id.btnBookOption);
        layout = itemView.findViewById(R.id.relativeLayoutBookDetail);
        layout.setOnClickListener(this);
        btnBookOption.setOnClickListener(this);

    }

    @SuppressLint("SetTextI18n")
    public void bindBook(Book book) {

        byte[] x = Base64.decode(book.getBookPic(), Base64.DEFAULT);
        Bitmap bmp= BitmapFactory.decodeByteArray(x,0,x.length);
        String genreForText = "";
        String authorForText = "";
        for (Genre g : book.getGenre())
        {
            genreForText = genreForText + g.getGenreName() + ". ";
        }
        for (Author a : book.getAuthor())
        {
            authorForText = authorForText + a.getAuthorName() + ". ";
        }
        this.book = book;
        this.title.setText(book.getTitle());

        if (book.getAuthor().size() == 0)
            this.author.setText("No known authors");
        else
            this.author.setText(authorForText);

        if (book.getGenre().size() == 0)
            this.genre.setText("No known genres");
        else
            this.genre.setText(genreForText);
        this.thumbNail.setImageBitmap(bmp);
        this.rating.setText(Double.toString(book.getRating()));

        this.year.setText(book.getReleaseDate().toString());
        this.btnBookOption.setOnClickListener(this);
    }

    @Override
    public void onClick(final View v)
    {
        final Context context = itemView.getContext();
        final Book bookC = this.book;
        switch (v.getId()) {
            case R.id.relativeLayoutBookDetail:

                mAdapter.changeFragment(Integer.toString(bookC.getID()), Integer.toString(0), 2);

            case R.id.btnBookOption:
                if (CheckNetwork.isNetworkConnected(context))
                {
                    PopupMenu popup = new PopupMenu(context, v);
                    popup.getMenuInflater().inflate(R.menu.clipboard_popup, popup.getMenu());
                    popup.show();
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {

                            switch (item.getItemId()) {

                                case R.id.openWiki:

                                    String query = null;
                                    try {
                                        query = URLEncoder.encode(bookC.getTitle(), "utf-8");
                                    } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    }
                                    String url = "http://www.google.com/search?q=" + query;
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setData(Uri.parse(url));
                                    context.startActivity(intent);

                                    break;

                                case R.id.bookdepository:

                                    String bookTitle = bookC.getTitle().replaceAll(" ", "+").toLowerCase();
                                    String urlS = "https://www.bookdepository.com/search?searchTerm=" + bookTitle +" &search=Find+book";
                                    Intent intentS = new Intent(Intent.ACTION_VIEW);
                                    intentS.setData(Uri.parse(urlS));
                                    context.startActivity(intentS);
                                    break;

                                default:
                                    break;
                            }
                            return true;
                        }
                    });
                    break;
                }
                else
                {
                    AlertDialog alertDialog = new AlertDialog.Builder(context, R.style.AppTheme_PopupOverlay).create();
                    alertDialog.setTitle("Warning");
                    alertDialog.setMessage("No internet connection. You won't be able to edit the items");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                    break;
                }

            default:
                break;
        }
    }
}
