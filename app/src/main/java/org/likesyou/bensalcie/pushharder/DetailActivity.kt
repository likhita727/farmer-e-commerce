package org.likesyou.bensalcie.pushharder

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date

class DetailActivity() : AppCompatActivity() {
    private var name: String? = null
    private var qty: String? = null
    private var price: String? = null
    private var image: String? = null
    private var poster: String? = null
    private var time: String? = null
    private var postId: String? = null
    private var category: String? = null
    private lateinit var tvDetailTitle: TextView
    private var tvDetailViews: TextView? = null
    private lateinit var tvDetailLikes: TextView
    private lateinit var tvDetailQty: TextView
    private lateinit var tvDetailCategory: TextView
    private lateinit var tvDetailPoster: TextView
    private lateinit var tvDetailPrice: TextView
    private lateinit var tvDetailTime: TextView
    private var tvPosterPhone: TextView? = null
    private var tvPosterCounty: TextView? = null
    private var tvPosterSubcounty: TextView? = null
    private var tvPosterScaleType: TextView? = null
    private var ivImage: ImageView? = null
    private var myUsersDatabase: DatabaseReference? = null
    private var mAuth: FirebaseAuth? = null
    private var userId: String? = null
    private var myProductsDatabase: DatabaseReference? = null
    private var myCartDatabase: DatabaseReference? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        tvDetailTitle = findViewById(R.id.tvDetailTitle)
        tvDetailQty = findViewById(R.id.tvDetailQty)
        tvDetailCategory = findViewById(R.id.tvDetailCategory)
        tvDetailPoster = findViewById(R.id.tvDetailPoster)
        tvDetailPrice = findViewById(R.id.tvDetailPrice)
        tvDetailTime = findViewById(R.id.tvDetailTime)
        tvPosterPhone = findViewById(R.id.tvDetailPosterPhone)
        tvPosterCounty = findViewById(R.id.tvDetailPosterCounty)
        tvPosterSubcounty = findViewById(R.id.tvDetailPosterSubcounty)
        tvPosterScaleType = findViewById(R.id.tvDetailPosterScaletype)
        tvDetailViews = findViewById(R.id.tvDetailViews)
        tvDetailLikes = findViewById(R.id.tvDetailLikes)
        mAuth = FirebaseAuth.getInstance()
        myCartDatabase = FirebaseDatabase.getInstance().reference.child("FARM").child("Cart")
        myUsersDatabase =
            FirebaseDatabase.getInstance().reference.child("FARM").child("Users")
        myProductsDatabase =
            FirebaseDatabase.getInstance().reference.child("FARM").child("Products")
        userId = mAuth!!.currentUser!!.uid
        ivImage = findViewById(R.id.ivDetailImage)
        name = intent.getStringExtra("name")
        qty = intent.getStringExtra("qty")
        price = intent.getStringExtra("price")
        image = intent.getStringExtra("image")
        poster = intent.getStringExtra("poster")
        time = intent.getStringExtra("time")
        postId = intent.getStringExtra("postId")
        category = intent.getStringExtra("category")
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setTitle(name)
        tvDetailTitle.setText("" + name)
        tvDetailQty.setText("$qty Kgs ")
        tvDetailPrice.setText("Ksh: $price")
        tvDetailTime.setText("Uploaded at $time")
        tvDetailCategory.setText(category)
        Picasso.get().load(image).placeholder(R.drawable.ic_photo_library_black_24dp).into(ivImage)
        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener { view -> sendProductToCart(view, name, qty, price, poster, postId) }
        setupUploader()
        setUpViewsandLikes()
        tvDetailLikes.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                myProductsDatabase!!.child((postId)!!).child("product_likes").child(userId!!)
                    .setValue("1").addOnCompleteListener(
                    OnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(
                                this@DetailActivity,
                                "You Liked this product...",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
            }
        })
    }

    private fun sendProductToCart(
        view: View,
        name: String?,
        qty: String?,
        price: String?,
        poster: String?,
        postId: String?
    ) {
        val newCartID = myCartDatabase!!.child((userId)!!).push().key
        val newCart = myCartDatabase!!.child((userId)!!).child((newCartID)!!)
        val dateFormat: DateFormat = SimpleDateFormat("HH:mm:ss ")
        val date = Date()
        val tim = dateFormat.format(date)
        val myMap = HashMap<String, Any?>()
        myMap["name"] = name
        myMap["qty"] = qty
        myMap["price"] = price
        myMap["poster_id"] = poster
        myMap["post_id"] = postId
        myMap["time"] = tim
        myMap["post_image"] = image
        newCart.updateChildren(myMap).addOnCompleteListener(object : OnCompleteListener<Void?> {
            override fun onComplete(task: Task<Void?>) {
                if (task.isSuccessful) {
                    Snackbar.make(view, "You added $name to your cart. ", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()
                }
            }
        })
    }

    private fun setUpViewsandLikes() {
        myProductsDatabase!!.child((postId)!!).child("product_views")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val views = dataSnapshot.childrenCount
                        tvDetailViews!!.text = "" + views
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
        myProductsDatabase!!.child((postId)!!).child("product_likes")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val likes = dataSnapshot.childrenCount
                        tvDetailLikes!!.text = "" + likes
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
    }

    private fun setupUploader() {
        myUsersDatabase!!.child((poster)!!).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val name = dataSnapshot.child("name").value.toString()
                    val phon = dataSnapshot.child("phone").value.toString()
                    val coun = dataSnapshot.child("county").value.toString()
                    val subcou = dataSnapshot.child("subcounty").value.toString()
                    val scalet = dataSnapshot.child("type_scale").value.toString()
                    tvDetailPoster!!.text = "Product Sold  By: $name"
                    tvPosterPhone!!.text = phon
                    tvPosterCounty!!.text = coun
                    tvPosterSubcounty!!.text = subcou
                    tvPosterScaleType!!.text = scalet
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    override fun onStart() {
        super.onStart()
        myProductsDatabase!!.child((postId)!!).child("product_views").child((userId)!!)
            .setValue("1").addOnCompleteListener(object : OnCompleteListener<Void?> {
            override fun onComplete(task: Task<Void?>) {
                if (task.isSuccessful) {
                    // Toast.makeText(DetailActivity.this, "You Liked this product...", Toast.LENGTH_SHORT).show();
                }
            }
        })
    }
}