package com.mobilelab.artyomska.bookdeposit.listHolder

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.support.constraint.ConstraintLayout
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.util.Base64
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import com.mobilelab.artyomska.bookdeposit.R
import com.mobilelab.artyomska.bookdeposit.listAdapter.BookAdapter
import com.mobilelab.artyomska.bookdeposit.listAdapter.ReviewsOfUserAdapter
import com.mobilelab.artyomska.bookdeposit.model.Book
import com.mobilelab.artyomska.bookdeposit.model.Review
import com.mobilelab.artyomska.bookdeposit.utils.CheckNetwork
import java.io.UnsupportedEncodingException
import java.net.URLEncoder

class BookHolderUserReview(itemView: View, private val context: Context?, private val mAdapter: ReviewsOfUserAdapter) : HolderAbstractClass(itemView), View.OnClickListener {

    private val thumbNail: ImageView
    private val btnBookOption: ImageView
    private val title: TextView
    private val rating: TextView
    private val genre: TextView
    private val author: TextView
    private val year: TextView
    private var book: Book? = null
    private val layout: ConstraintLayout

    init {
        thumbNail = itemView.findViewById(R.id.thumbnail)
        title = itemView.findViewById(R.id.title)
        rating = itemView.findViewById(R.id.rating)
        genre = itemView.findViewById(R.id.genre)
        author = itemView.findViewById(R.id.author)
        year = itemView.findViewById(R.id.releaseYear)
        btnBookOption = itemView.findViewById(R.id.btnBookOption)
        layout = itemView.findViewById(R.id.relativeLayoutBookDetail)
        layout.setOnClickListener(this)
        btnBookOption.setOnClickListener(this)

    }

    @SuppressLint("SetTextI18n")
    override fun bindType(rev: Review) {

        val bookRev : Book = rev.book
        val x = Base64.decode(bookRev.bookPic, Base64.DEFAULT)
        val bmp = BitmapFactory.decodeByteArray(x, 0, x.size)
        var genreForText = ""
        var authorForText = ""
        for (g in bookRev.genre) {
            genreForText = genreForText + g.genreName + ". "
        }
        for (a in bookRev.author) {
            authorForText = authorForText + a.authorName + ". "
        }
        this.book = bookRev
        this.title.text = bookRev.getTitle()

        if (bookRev.genre.size == 0)
            this.author.text = "No known authors"
        else
            this.author.text = authorForText
        if (bookRev.genre.size == 0)
            this.genre.text = "No known genres"
        else
            this.genre.text = genreForText
        this.thumbNail.setImageBitmap(bmp)
        this.rating.text = java.lang.Double.toString(bookRev.rating)

        this.year.text = bookRev.releaseDate.toString()
        this.btnBookOption.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        val context = itemView.context
        val bookC = this.book
        when (v.id) {
            R.id.relativeLayoutBookDetail -> {

                mAdapter.changeFragment(Integer.toString(bookC!!.id), Integer.toString(0), 2)
                if (CheckNetwork.isNetworkConnected(context)) {
                    val popup = PopupMenu(context, v)
                    popup.menuInflater.inflate(R.menu.clipboard_popup, popup.menu)
                    popup.show()
                    popup.setOnMenuItemClickListener { item ->
                        when (item.itemId) {

                            R.id.openWiki -> {

                                var query: String? = null
                                try {
                                    query = URLEncoder.encode(bookC.getTitle(), "utf-8")
                                } catch (e: UnsupportedEncodingException) {
                                    e.printStackTrace()
                                }

                                val url = "http://www.google.com/search?q=" + query!!
                                val intent = Intent(Intent.ACTION_VIEW)
                                intent.data = Uri.parse(url)
                                context.startActivity(intent)
                            }

                            R.id.bookdepository -> {

                                val bookTitle = bookC.getTitle().replace(" ".toRegex(), "+").toLowerCase()
                                val urlS = "https://www.bookdepository.com/search?searchTerm=$bookTitle &search=Find+book"
                                val intentS = Intent(Intent.ACTION_VIEW)
                                intentS.data = Uri.parse(urlS)
                                context.startActivity(intentS)
                            }

                            else -> {
                            }
                        }
                        true
                    }
                } else {
                    val alertDialog = AlertDialog.Builder(context, R.style.AppTheme_PopupOverlay).create()
                    alertDialog.setTitle("Warning")
                    alertDialog.setMessage("No internet connection. You won't be able to edit the items")
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK"
                    ) { dialog, which -> dialog.dismiss() }
                    alertDialog.show()
                }
            }

            R.id.btnBookOption -> if (CheckNetwork.isNetworkConnected(context)) {
                val popup = PopupMenu(context, v)
                popup.menuInflater.inflate(R.menu.clipboard_popup, popup.menu)
                popup.show()
                popup.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.openWiki -> {
                            var query: String? = null
                            try {
                                query = URLEncoder.encode(bookC!!.getTitle(), "utf-8")
                            } catch (e: UnsupportedEncodingException) {
                                e.printStackTrace()
                            }

                            val url = "http://www.google.com/search?q=" + query!!
                            val intent = Intent(Intent.ACTION_VIEW)
                            intent.data = Uri.parse(url)
                            context.startActivity(intent)
                        }
                        R.id.bookdepository -> {
                            val bookTitle = bookC!!.getTitle().replace(" ".toRegex(), "+").toLowerCase()
                            val urlS = "https://www.bookdepository.com/search?searchTerm=$bookTitle &search=Find+book"
                            val intentS = Intent(Intent.ACTION_VIEW)
                            intentS.data = Uri.parse(urlS)
                            context.startActivity(intentS)
                        }
                        else -> {
                        }
                    }
                    true
                }
            } else {
                val alertDialog = AlertDialog.Builder(context, R.style.AppTheme_PopupOverlay).create()
                alertDialog.setTitle("Warning")
                alertDialog.setMessage("No internet connection. You won't be able to edit the items")
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK") { dialog, which -> dialog.dismiss() }
                alertDialog.show()
            }

            else -> {
            }
        }
    }
}