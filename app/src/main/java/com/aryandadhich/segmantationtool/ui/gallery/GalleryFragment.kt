package com.aryandadhich.segmantationtool.ui.gallery

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.aryandadhich.segmantationtool.databinding.FragmentGalleryBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition

class GalleryFragment : Fragment() {

    private var _binding: FragmentGalleryBinding? = null

    private val binding get() = _binding!!

    private lateinit var galleryViewModel: GalleryViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        galleryViewModel =
            ViewModelProvider(this).get(GalleryViewModel::class.java)

        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.viewModel = galleryViewModel;
        binding.setLifecycleOwner(this)

        binding.previewBtn.setOnClickListener {
            galleryViewModel.setData(0, 0, binding.imgUrlEditTxt.text.toString())
            loadImage()
        }

        binding.startBtn.setOnClickListener {
            onStartClicked()
        }
        return root
    }

    private fun onStartClicked() {
        galleryViewModel.setData(binding.widthOfImageEditTxt.text.toString().toInt(), binding.heightOfImageEditTxt.text.toString().toInt(), binding.imgUrlEditTxt.text.toString())
        navigateToSegmentationFragment()
    }

    private fun navigateToSegmentationFragment() {
        findNavController().navigate(GalleryFragmentDirections.actionNavGalleryToDrawFragment().setBackgroundUrl(galleryViewModel.imgUrl.value).setRequiredImgHeight(galleryViewModel.imgHeight.value!!).setRequiredImgWidth(galleryViewModel.imgWidth.value!!))
    }

    private fun loadImage() {
        val imgUri = galleryViewModel.imgUrl?.value?.toUri()?.buildUpon()?.scheme("https")?.build()

        context?.let {
            Glide.with(it.applicationContext)
                .asBitmap()
                .load(imgUri)
                .into(object : SimpleTarget<Bitmap?>() {
                    override fun onResourceReady(bitmap: Bitmap, transition: Transition<in Bitmap?>?) {
                        val w = bitmap.width
                        val h = bitmap.height
                        binding.widthOfImageEditTxt.setText(w.toString())
                        binding.heightOfImageEditTxt.setText(h.toString())
                        binding.previewImg.setImageBitmap(bitmap)
                    }

                })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}