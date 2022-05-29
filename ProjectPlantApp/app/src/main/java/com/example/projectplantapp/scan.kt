package com.example.projectplantapp


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_scan.*
import java.io.IOException


class scan : AppCompatActivity() {
    private lateinit var mClassifier:Classifier
    private lateinit var mBitmap: Bitmap

    private val mCameraRequestCode=0
    private val mGalleryRequestCode=2

    private val mInputSize=224
    private val mModelPath="plant_disease_model.tflite"
    private val mLabelPath="plant_labels.txt"
    private val mSamplePath="automn.jpg"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)
        requestedOrientation=ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        mClassifier=Classifier(assets,mModelPath,mLabelPath,mInputSize)

        resources.assets.open(mSamplePath).use {
            mBitmap=BitmapFactory.decodeStream(it)
            mBitmap=Bitmap.createScaledBitmap(mBitmap,mInputSize,mInputSize,true)
            mPhotoImageView.setImageBitmap(mBitmap)
        }
        cam.setOnClickListener {
            val callCameraIntent=Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(callCameraIntent,mCameraRequestCode)
        }
        select.setOnClickListener {
            val callGalleryIntent=Intent(Intent.ACTION_PICK)
            callGalleryIntent.type="image/*"
            startActivityForResult(callGalleryIntent,mGalleryRequestCode)
        }
        detect.setOnClickListener {
            val results=mClassifier.recognizeImage(mBitmap).firstOrNull()


            val intent=Intent(this,result::class.java)
            intent.putExtra("answer",results.toString())
            startActivity(intent)
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == mCameraRequestCode){
            if (resultCode == Activity.RESULT_OK && data!=null){
                mBitmap=data.extras!!.get("data") as Bitmap
                mBitmap=scaleImage(mBitmap)
                mPhotoImageView.setImageBitmap(mBitmap)
            }else{
                Toast.makeText(this,"Camera Cancel....",Toast.LENGTH_LONG).show()
            }
        }else if(requestCode == mGalleryRequestCode){
            if (data!=null){
                val uri=data.data

                try {
                    mBitmap=MediaStore.Images.Media.getBitmap(this.contentResolver,uri)
                }catch (e:IOException){
                    e.printStackTrace()
                }

                println("Sucess!")
                mBitmap=scaleImage(mBitmap)
                mPhotoImageView.setImageBitmap(mBitmap)
            }
        }else{
            Toast.makeText(this,"Unrecognized request code",Toast.LENGTH_LONG).show()
        }
    }

    private fun scaleImage(bitmap: Bitmap?): Bitmap {
        val originalWidth=bitmap!!.width
        val originalHeight=bitmap.height
        val scaleWidth=mInputSize.toFloat()/originalWidth
        val scaleHeight=mInputSize.toFloat()/originalHeight
        val matrix= Matrix()
        matrix.postScale(scaleWidth,scaleHeight)
        return Bitmap.createBitmap(bitmap,0,0,originalWidth,originalHeight,matrix,true)

    }
}