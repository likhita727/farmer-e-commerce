package org.likesyou.bensalcie.pushharder

import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date

/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment constructor() : Fragment() {
    private lateinit var btnUpdate: FloatingActionButton
    private var productName: EditText? = null
    private var productCategory: EditText? = null
    private var productQuantity: EditText? = null
    private var productPrice: EditText? = null
    private lateinit var productsList: RecyclerView
    private var mAuth: FirebaseAuth? = null
    private var userId: String? = null
    private lateinit var v: View
    private var d: Dialog? = null
    private var imageUri: Uri? = null
    private var profileImage: ImageView? = null
    private var myProductsDatabase: DatabaseReference? = null
    private var mStorageReference: StorageReference? = null
    private var pd: ProgressDialog? = null
    public override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_profile, container, false)
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        mAuth = FirebaseAuth.getInstance()
        myProductsDatabase =
            FirebaseDatabase.getInstance().getReference().child("FARM").child("Products")
        mStorageReference = FirebaseStorage.getInstance().getReference().child("FARM")
            .child("Product Images")
        btnUpdate = v.findViewById(R.id.fb)
        d = Dialog((getContext())!!)
        d!!.setContentView(R.layout.post_dialog)
        profileImage = d!!.findViewById(R.id.productImage)
        productName = d!!.findViewById(R.id.product_title)
        productCategory = d!!.findViewById(R.id.product_category)
        productQuantity = d!!.findViewById(R.id.product_quantity)
        productPrice = d!!.findViewById(R.id.product_price)
        productsList = v.findViewById(R.id.productsList)
        val gridLayoutManager: GridLayoutManager = GridLayoutManager(getContext(), 2)
        productsList.setLayoutManager(gridLayoutManager)
        userId = mAuth!!.getCurrentUser()!!.getUid()
        pd = ProgressDialog(getContext())
        pd!!.setTitle("Uploading Product")
        pd!!.setMessage("Please wait...")
        btnUpdate.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(v: View) {
                bringPostDialog()
            }
        })
        return v
    }

    private fun bringPostDialog() {
        val btnPost: Button = d!!.findViewById(R.id.btnUpload)
        profileImage!!.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(v: View) {
                val galleryIntent: Intent = Intent(Intent.ACTION_GET_CONTENT)
                galleryIntent.setType("image/*")
                startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE)
            }
        })
        btnPost.setOnClickListener(object : View.OnClickListener {
            public override fun onClick(v: View) {
                val name: String =
                    productName!!.getText().toString().trim({ it <= ' ' }).trim({ it <= ' ' })
                val category: String = productCategory!!.getText().toString().trim({ it <= ' ' })
                val quantity: String = productQuantity!!.getText().toString().trim({ it <= ' ' })
                val price: String = productPrice!!.getText().toString().trim({ it <= ' ' })
                if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(category) && !TextUtils.isEmpty(
                        quantity
                    ) && !TextUtils.isEmpty(price)
                ) {
                    if (imageUri != null) {

                        //toast("Data is ready");
                        pd!!.show()
                        postData(name, category, quantity, price, imageUri!!)
                    } else {
                        toast("Tap the avatar to select product Image...")
                    }
                } else {
                    toast("Check your Product Inputs...some fileds are blank...")
                }
            }
        })
        d!!.show()
    }

    private fun postData(
        name: String,
        category: String,
        quantity: String,
        price: String,
        myUri: Uri
    ) {
        val filePath: StorageReference =
            mStorageReference!!.child(myUri.getLastPathSegment() + ".jpg")
        filePath.putFile(myUri)
            .addOnCompleteListener(object : OnCompleteListener<UploadTask.TaskSnapshot?> {
                public override fun onComplete(task: Task<UploadTask.TaskSnapshot?>) {
                    if (task.isSuccessful()) {
                        filePath.getDownloadUrl()
                            .addOnSuccessListener(object : OnSuccessListener<Uri> {
                                public override fun onSuccess(uri: Uri) {
                                    val downloadLink: String = uri.toString()
                                    val dateFormat: DateFormat = SimpleDateFormat("HH:mm:ss ")
                                    val date: Date = Date()
                                    val time: String = dateFormat.format(date)
                                    val productKey: String? = myProductsDatabase!!.push().getKey()
                                    val newProduct: DatabaseReference =
                                        myProductsDatabase!!.child((productKey)!!)
                                    val myMap: HashMap<String, Any?> = HashMap()
                                    myMap.put("post_id", productKey)
                                    myMap.put("product_name", name)
                                    myMap.put("product_category", category)
                                    myMap.put("product_quantity", quantity)
                                    myMap.put("product_price", price)
                                    myMap.put("post_time", time)
                                    myMap.put("product_poster", userId)
                                    myMap.put("product_views", "0")
                                    myMap.put("product_likes", "0")
                                    myMap.put("post_image", downloadLink)
                                    newProduct.updateChildren(myMap)
                                        .addOnCompleteListener(object : OnCompleteListener<Void?> {
                                            public override fun onComplete(task: Task<Void?>) {
                                                if (task.isSuccessful()) {
                                                    toast("Uploaded successfully")
                                                    pd!!.dismiss()
                                                    d!!.dismiss()
                                                    productName!!.setText("")
                                                    productCategory!!.setText("")
                                                    productQuantity!!.setText("")
                                                    productPrice!!.setText("")
                                                    //imageUri=null;
                                                    profileImage!!.setImageURI(null)
                                                } else {
                                                    pd!!.dismiss()
                                                    toast(task.getException()!!.message)
                                                }
                                            }
                                        })
                                }
                            })
                    } else {
                        toast(task.getException()!!.message)
                    }
                }
            })
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (GALLERY_REQUEST_CODE == requestCode && resultCode == Activity.RESULT_OK) {
            imageUri = data.getData()
            profileImage!!.setImageURI(imageUri)
        }
    }

    private fun toast(s: String?) {
        Toast.makeText(getContext(), "Message: " + s, Toast.LENGTH_SHORT).show()
    }

    public override fun onStart() {
        super.onStart()
        myProductsDatabase!!.keepSynced(true)
        val options: FirebaseRecyclerOptions<Products> = FirebaseRecyclerOptions.Builder<Products>()
            .setQuery((myProductsDatabase)!!, Products::class.java)
            .build()
        val adapter: FirebaseRecyclerAdapter<Products, productsViewHolder> =
            object : FirebaseRecyclerAdapter<Products, productsViewHolder>(options) {
                override fun onBindViewHolder(
                    holder: productsViewHolder,
                    position: Int,
                    model: Products
                ) {
                    holder.tvName.setText(model.product_name)
                    holder.tvQty.setText(model.product_quantity + " Kgs")
                    holder.tvPrice.setText("Ksh:" + model.product_price)
                    Picasso.get().load(model.post_image).resize(180, 150)
                        .placeholder(R.drawable.ic_photo_library_black_24dp)
                        .into(holder.product_imageView)
                    holder.itemView.setOnClickListener(object : View.OnClickListener {
                        public override fun onClick(v: View) {
                            val `in`: Intent = Intent(getContext(), DetailActivity::class.java)
                            `in`.putExtra("name", model.product_name)
                            `in`.putExtra("qty", model.product_quantity)
                            `in`.putExtra("price", model.product_price)
                            `in`.putExtra("image", model.post_image)
                            `in`.putExtra("poster", model.product_poster)
                            `in`.putExtra("time", model.post_time)
                            `in`.putExtra("postId", model.post_id)
                            `in`.putExtra("category", model.product_category)
                            startActivity(`in`)
                        }
                    })
                }

                public override fun onCreateViewHolder(
                    viewGroup: ViewGroup,
                    i: Int
                ): productsViewHolder {
                    val view: View = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.single_product, viewGroup, false)
                    val viewHolder: productsViewHolder = productsViewHolder(view)
                    return viewHolder
                }
            }
        productsList!!.setAdapter(adapter)
        adapter.startListening()
        productsList!!.smoothScrollToPosition(adapter.getItemCount() + 1)
    }

    class productsViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val product_imageView: ImageView
        val tvName: TextView
        val tvPrice: TextView
        val tvQty: TextView

        init {
            product_imageView = itemView.findViewById(R.id.product_image_view)
            tvName = itemView.findViewById(R.id.name_tv)
            tvPrice = itemView.findViewById(R.id.price_tv)
            tvQty = itemView.findViewById(R.id.qty_tv)
        }
    }

    companion object {
        private val GALLERY_REQUEST_CODE: Int = 2345
    }
}