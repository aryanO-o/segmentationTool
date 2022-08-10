package com.aryandadhich.segmantationtool.ui.drawing

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.aryandadhich.segmantationtool.R
import com.aryandadhich.segmantationtool.databinding.FragmentDrawBinding
import com.bumptech.glide.Glide
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream


class DrawFragment : Fragment() {

    private lateinit var _binding: FragmentDrawBinding
    val binding get() = _binding!!

    private val imageFile = context?.let { ImageFile(it) }

    var imgHeight: Int = 0;
    var imgWidth: Int = 0;

    private var permissions = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        _binding = FragmentDrawBinding.inflate(inflater, container, false)

        val args = DrawFragmentArgs.fromBundle(arguments!!)
        setData(args)

        initButtons()
        initPaintMode()

        return binding.root
    }

    private fun setData(args: DrawFragmentArgs) {
        if(args.requiredImgHeight != 0)
        imgHeight = args.requiredImgHeight
        if(args.requiredImgWidth != 0)
            imgWidth = args.requiredImgWidth
        if(args.backgroundUrl != null)
            setBackgroundImageUsingUrl(args.backgroundUrl!!)
    }

    private fun setBackgroundImageUsingUrl(backgroundUrl : String) {
        val imgUri = backgroundUrl.toUri().buildUpon()?.scheme("https")?.build()
        binding.fragDrawMainView.alpha = 0.5F
        context?.let { Glide.with(it).load(imgUri).into(binding.fragDrawBackground) }
    }

    private fun initPaintMode() {
        val color: Int = Color.WHITE
        binding.fragDrawMainView.setBackgroundColor(color)
        binding.colorPickerFab.backgroundTintList = ColorStateList.valueOf(color)
    }

    private fun initButtons() {
        binding.brushFab.setOnClickListener{
            showBrushSizeChooserDialog()
        }

        binding.galaryFab.setOnClickListener{
            navigateToImgUrlFragment()
        }

        binding.hideImageFab.setOnClickListener{
            removeBackgroundImage()
        }

        binding.clearFab.setOnClickListener{
            binding.fragDrawMainView.clearDrawingBoard()
        }

        binding.redoFab.setOnClickListener{
            binding.fragDrawMainView.redo()
        }

        binding.undoFab.setOnClickListener{
            binding.fragDrawMainView.undo()
        }

        binding.colorPickerFab.setOnClickListener{
            setBrushColor()
        }

        binding.saveFab.setOnClickListener{
            if (checkPermission()) {
                saveImage()
            } else {
                requestPermission();
            }
        }
        binding.shareFab.setOnClickListener{
            val bitmapImage = loadBitmapFromView(binding.fragDrawCanvasContainer)
            sharePalette(bitmapImage);
        }
    }

    private fun navigateToImgUrlFragment() {
        findNavController().navigate(DrawFragmentDirections.actionDrawFragmentToNavGallery())
    }

    private fun setBrushColor() {
        ColorPickerDialogBuilder
            .with(context)
            .setTitle("Choose color")
            .initialColor(binding.fragDrawMainView.getBrushColor() )
            .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
            .density(12)
            .setPositiveButton(
                "ok"
            ) { dialog, selectedColor, allColors -> binding.fragDrawMainView.setBrushColor(selectedColor) }
            .setNegativeButton(
                "cancel"
            ) { dialog, which -> }
            .build()
            .show()
    }

    private fun removeBackgroundImage() {
        binding.fragDrawBackground.visibility = View.INVISIBLE
        binding.fragDrawMainView.alpha = 1F
    }

    /**
     * upload image from storage
     */
    private
    val replaceImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                binding.fragDrawBackground.visibility = View.VISIBLE
                binding.fragDrawMainView.alpha = 0.5F

//                context?.let {
//                    ContextCompat.getColor(
//                        it, R.color.black)
//                }?.let { binding.fragDrawBackground.setBackgroundColor(it) }

                binding.fragDrawBackground.setImageURI(uri)
            }
        }

    private fun getIMGSize(uri: Uri) {
        try {
            context!!.contentResolver.openInputStream(uri).use { inputStream ->
                val exif = ExifInterface(inputStream!!)
                val height = exif.getAttribute(ExifInterface.TAG_X_RESOLUTION)
                val width = exif.getAttribute(ExifInterface.TAG_PIXEL_Y_DIMENSION)
                if (height != null) {
                    imgHeight = height.toInt()
                }
                if (width != null) {
                    imgWidth = width.toInt()
                }
                Toast.makeText(context, "$height, $width", Toast.LENGTH_SHORT).show()
                val orientation =
                    exif.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL
                    )
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun showBrushSizeChooserDialog() {
        val brushDialog = context?.let { Dialog(it) }
        if (brushDialog != null) {
            brushDialog.setContentView(R.layout.dialog_brush_size)
        }

        val brushSize10: ImageButton = brushDialog!!.findViewById(R.id.brush_IBTN_size10)
        val brushSize15: ImageButton = brushDialog.findViewById(R.id.brush_IBTN_size15)
        val brushSize20: ImageButton = brushDialog.findViewById(R.id.brush_IBTN_size20)
        val brushSize25: ImageButton = brushDialog.findViewById(R.id.brush_IBTN_size25)
        val brushSize30: ImageButton = brushDialog.findViewById(R.id.brush_IBTN_size30)


        brushSize10.setOnClickListener {
            changeBrushSize(10, brushDialog!!)
        }
        brushSize15.setOnClickListener {
            changeBrushSize(15, brushDialog!!)
        }
        brushSize20.setOnClickListener {
            changeBrushSize(20, brushDialog!!)
        }
        brushSize25.setOnClickListener {

            changeBrushSize(25, brushDialog!!)
        }
        brushSize30.setOnClickListener {
            changeBrushSize(30, brushDialog!!)
        }

        brushDialog.show()
    }

    private fun changeBrushSize(brushSize: Int, brushDialog: Dialog) {
        binding.fragDrawMainView?.setSizeForBrush(brushSize)
        brushDialog.dismiss()
    }

    /**
     * check if permission is granted
     */
    private fun checkPermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) { // if android version is 11 or above
            Environment.isExternalStorageManager()
            return true;
        } else {
            val readCheck =
                context?.let {
                    ContextCompat.checkSelfPermission(
                        it,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                }
            val writeCheck =
                context?.let {
                    ContextCompat.checkSelfPermission(
                        it,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                }
            return readCheck == PackageManager.PERMISSION_GRANTED && writeCheck == PackageManager.PERMISSION_GRANTED

        }
    }

    /**
     * request permission for android 11 and below
     */
    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                var intent = Intent()
                intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                val uri = Uri.fromParts("package", getActivity()?.getPackageName(), null);
                intent.data = uri;
                activityResultLauncher.launch(intent)
            } catch (e: Exception) {
                var intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                activityResultLauncher.launch(intent)

            }
        } else {
            //below android 11
            requestStoragePermissionLauncher.launch(permissions)
        }
    }

    //check storage permission
    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    Toast.makeText(context, "Granted", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "Denied", Toast.LENGTH_LONG).show()
                }
            }
        }

    /**
     * request Storage Permission Launcher
     */
    private val requestStoragePermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            var readPer = false
            var writePer = false

            permissions.entries.forEach {
                val permissionName = it.key
                if (permissionName == Manifest.permission.READ_EXTERNAL_STORAGE) {
                    readPer = it.value
                } else if (permissionName == Manifest.permission.WRITE_EXTERNAL_STORAGE) {
                    writePer = it.value
                }
            }
            if (readPer && writePer) {
                saveImage()
                Toast.makeText(
                    context,
                    "Permission Granted",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                showRationaleDialog(
                    "Drawing App", "Drawing App " +
                            "needs to Access Your External Storage"
                )
                Toast.makeText(
                    context,
                    "Permission Denied",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }

    private fun showRationaleDialog(title: String, message: String) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
        builder.create().show()
    }

    private fun saveImage() {
        lifecycleScope.launch {
            saveBitmapFile(loadBitmapFromView(binding.fragDrawCanvasContainer!!))
        }
    }

//    private fun getBitmapFromView(view: View): Bitmap {
//        val returnedBitmap =
//            Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
//
//        lateinit var canvas: Canvas
//
//        if(imgWidth !=0 && imgHeight!= 0)
//        {
//            val b = resize(returnedBitmap, imgWidth,imgHeight);
//
//            canvas = b?.let { Canvas(it) }!!
//
//        }
//        else {
//            canvas = Canvas(returnedBitmap)
//        }
//        val bgDrawable = view.background
//        if (bgDrawable != null) {
//            bgDrawable.draw(canvas)
//        } else {
//            canvas.drawColor(Color.WHITE)
//        }
//        view.draw(canvas)
//
//        return returnedBitmap
//    }

    private suspend fun saveBitmapFile(mBitmap: Bitmap): String {
        var result = ""
        withContext(Dispatchers.IO) {
            if (mBitmap != null) {
                try {
                    //Generating a file name
                    val filename = "${System.currentTimeMillis()}.jpg"

                    //Output stream
                    var fos: OutputStream? = null

                    //For devices running android >= Q
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        //getting the contentResolver
                        context?.contentResolver?.also { resolver ->

                            //Content resolver will process the contentvalues
                            val contentValues = ContentValues().apply {

                                //putting file information in content values
                                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                            }

                            //Inserting the contentValues to contentResolver and getting the Uri
                            val imageUri: Uri? =
                                resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

                            //Opening an outputstream with the Uri that we got
                            fos = imageUri?.let { resolver.openOutputStream(it) }
                        }
                    } else {
                        //These for devices running on android < Q
                        //So I don't think an explanation is needed here
                        val imagesDir =
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                        val image = File(imagesDir, filename)
                        fos = FileOutputStream(image)
                    }

                    fos?.use {

                            //Finally writing the bitmap to the output stream that we opened
                            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
                            Toast.makeText(context, "saved to photos", Toast.LENGTH_SHORT).show()

                    }
                } catch (e: java.lang.Exception) {
                    result = ""
                    e.printStackTrace()
                }
            }
        }

        return result
    }

    fun loadBitmapFromView(v: View): Bitmap {
        val b = Bitmap.createBitmap(
            v.width,
            v.height,
            Bitmap.Config.ARGB_8888
        )
        val c = Canvas(b)
        v.layout(v.left, v.top, v.right, v.bottom)
        v.draw(c)

        if(imgHeight!=0 && imgWidth !=0){

            val newB = Bitmap.createScaledBitmap(b, imgWidth, imgHeight, false)
            return newB
        }

        return b
    }

    fun resize(imaged: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap? {
        var image = imaged
        if (maxHeight > 0 && maxWidth > 0) {
            val width = image.width
            val height = image.height
            val ratioBitmap = width.toFloat() / height.toFloat()
            val ratioMax = maxWidth.toFloat() / maxHeight.toFloat()
            var finalWidth = maxWidth
            var finalHeight = maxHeight
            if (ratioMax > 1) {
                finalWidth = Math.round(maxHeight.toFloat() * ratioBitmap)
            } else {
                finalHeight = Math.round(maxWidth.toFloat() / ratioBitmap)
            }
            return Bitmap.createScaledBitmap(image, finalWidth, finalHeight, false)
                .also { image = it }
        }
        return image
    }

    private fun showProgressDialog(customProgressDialog: Dialog) {
        customProgressDialog.setContentView(R.layout.dialog_custom_progress)
        customProgressDialog.show()
    }

    private fun cancelProgressDialog(customProgressDialog: Dialog) {
        customProgressDialog.dismiss()
    }

    private fun sharePalette(bitmap: Bitmap) {
        val bitmapPath = MediaStore.Images.Media.insertImage(
            activity?.contentResolver,
            bitmap,
            "palette",
            "share palette"
        )
        val bitmapUri = Uri.parse(bitmapPath)
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "image/png"
        intent.putExtra(Intent.EXTRA_STREAM, bitmapUri)
        startActivity(Intent.createChooser(intent, "Share"))
    }



    override fun onDestroy() {
        super.onDestroy()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    }
}