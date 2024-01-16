package com.example.campustransaction.ui.main

import com.example.campustransaction.R

class BannerDataBean {
    var imageRes: Int? = null
    var imageUrl: String? = null
    var title: String?
    var viewType: Int

    constructor(imageRes: Int?, title: String?, viewType: Int) {
        this.imageRes = imageRes
        this.title = title
        this.viewType = viewType
    }

    constructor(imageUrl: String?, title: String?, viewType: Int) {
        this.imageUrl = imageUrl
        this.title = title
        this.viewType = viewType
    }

    companion object {
        //测试数据，如果图片链接失效请更换
        val testData3: List<BannerDataBean>
            get() {
                val list: MutableList<BannerDataBean> = ArrayList()
                list.add( BannerDataBean(R.drawable.image1, null, 1) )
                list.add( BannerDataBean(R.drawable.image2, null, 1) )
                list.add( BannerDataBean(R.drawable.image3, null, 1) )
                return list
            }
    }
}