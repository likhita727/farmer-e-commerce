package org.likesyou.bensalcie.pushharder

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

/**
 * A simple [Fragment] subclass.
 */
class UsersFragment : Fragment() {
    lateinit  var  v: View
    private lateinit var cartList: RecyclerView
    private var myCartDatabase: DatabaseReference? = null
    private var mAuth: FirebaseAuth? = null
    private var userId: String? = null
    private var myUsersDatabase: DatabaseReference? = null
    private val CALL_PERMISSION_CODE = 345
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_users, container, false)
        cartList = v.findViewById(R.id.cartList)
        mAuth = FirebaseAuth.getInstance()
        userId = mAuth!!.currentUser!!.uid
        myCartDatabase =
            FirebaseDatabase.getInstance().reference.child("FARM").child("Cart").child(
                userId!!
            )
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        cartList.setLayoutManager(linearLayoutManager)
        return v
    }

    override fun onStart() {
        super.onStart()
        val options = FirebaseRecyclerOptions.Builder<Cart>()
            .setQuery(myCartDatabase!!, Cart::class.java)
            .build()
        val adapter: FirebaseRecyclerAdapter<Cart, CartViewHolder> =
            object : FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
                override fun onBindViewHolder(holder: CartViewHolder, position: Int, model: Cart) {
                    holder.cart_tvName.text = model.name
                    holder.cart_tvQty.text = model.qty + " Kgs "
                    holder.cart_tvPrice.text = "Ksh :" + model.price
                    Picasso.get().load(model.post_image)
                        .placeholder(R.drawable.ic_photo_library_black_24dp)
                        .into(holder.cart_product_imageView)
                    myUsersDatabase =
                        FirebaseDatabase.getInstance().reference.child("FARM").child("Users")
                    myUsersDatabase!!.child(model.poster_id!!)
                        .addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                val name = dataSnapshot.child("name").value.toString()
                                val phone = dataSnapshot.child("phone").value.toString()
                                val county = dataSnapshot.child("county").value.toString()
                                val subCounty = dataSnapshot.child("subcounty").value.toString()
                                holder.tvCounty.text = county
                                holder.tvSuCounty.text = subCounty
                                holder.tvProductUploader.text = "Sold by: $name"
                                holder.btnCall.setOnClickListener(object : View.OnClickListener {
                                    override fun onClick(v: View) {
                                        //Toast.makeText(getContext(), "Call: "+phone, Toast.LENGTH_SHORT).show();
                                        if (isCallAllowed) {
                                            dial(phone)
                                        } else {
                                            requestCallPermission()
                                        }
                                    }
                                })
                            }

                            override fun onCancelled(databaseError: DatabaseError) {}
                        })
                }

                override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): CartViewHolder {
                    val view = LayoutInflater.from(viewGroup.context)
                        .inflate(R.layout.single_cart, viewGroup, false)
                    return CartViewHolder(view)
                }
            }
        cartList!!.adapter = adapter
        adapter.startListening()
        adapter.notifyDataSetChanged()
    }

    private fun dial(phone: String) {
        val callIntent = Intent(Intent.ACTION_CALL)
        callIntent.data = Uri.parse("tel:$phone")
        startActivity(callIntent)
    }

    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cart_product_imageView: ImageView
        val cart_tvName: TextView
        val cart_tvPrice: TextView
        val cart_tvQty: TextView
        val tvCounty: TextView
        val tvSuCounty: TextView
        val tvProductUploader: TextView
        val btnCall: Button

        init {
            cart_product_imageView = itemView.findViewById(R.id.cart_product_image_view)
            cart_tvName = itemView.findViewById(R.id.cart_name_tv)
            cart_tvPrice = itemView.findViewById(R.id.cart_price_tv)
            cart_tvQty = itemView.findViewById(R.id.cart_qty_tv)
            tvCounty = itemView.findViewById(R.id.cart_county)
            tvSuCounty = itemView.findViewById(R.id.cart_sub_county)
            btnCall = itemView.findViewById(R.id.btnCall)
            tvProductUploader = itemView.findViewById(R.id.product_cart_uploader)
        }
    }

    private fun requestCallPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                activity!!,
                Manifest.permission.CALL_PHONE
            )
        ) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }

        //And finally ask for the permission
        ActivityCompat.requestPermissions(
            activity!!,
            arrayOf(Manifest.permission.CALL_PHONE),
            CALL_PERMISSION_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CALL_PERMISSION_CODE) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                Toast.makeText(context, "Oops you just denied the permission", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private val isCallAllowed: Boolean
        private get() {
            //Getting the permission status
            val result =
                ContextCompat.checkSelfPermission(context!!, Manifest.permission.CALL_PHONE)

            //If permission is granted returning true
            return if (result == PackageManager.PERMISSION_GRANTED) true else false

            //If permission is not granted returning false
        }
}