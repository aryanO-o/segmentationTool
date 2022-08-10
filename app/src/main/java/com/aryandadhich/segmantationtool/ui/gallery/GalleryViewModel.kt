package com.aryandadhich.segmantationtool.ui.gallery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GalleryViewModel : ViewModel() {

    private var _imgWidth = MutableLiveData<Int>()
    val imgWidth: LiveData<Int>
    get() = _imgWidth

    private var _imgHeight = MutableLiveData<Int>()
    val imgHeight: LiveData<Int>
    get() = _imgHeight

    private var _imgUrl = MutableLiveData<String>()
    val imgUrl: LiveData<String>
    get() = _imgUrl

    fun setData(imgWidht: Int, imgHeight: Int, imgUrl: String){
        _imgWidth.value = imgWidht;
        _imgHeight.value = imgHeight;
        _imgUrl.value = imgUrl;
    }
}