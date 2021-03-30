# RefreshKit

## YdKit通用组件库
YdKit 是一组功能丰富的 Android 通用组件。

* [LogKit](https://github.com/ydstar/LogKit) — 轻量级的 Android 日志系统。
* [RestfulKit](https://github.com/ydstar/RestfulKit) — 简洁但不简单的 Android 网络组件库。
* [CrashKit](https://github.com/ydstar/CrashKit) — 简洁易用的 Android Crash日志捕捉组件。
* [PermissionKit](https://github.com/ydstar/PermissionKit) — 简洁易用的 Android 权限请求组件。
* [RefreshKit](https://github.com/ydstar/RefreshKit) — 简洁易用的 Android 下拉刷新和上拉加载组件。
* [AdapterKit](https://github.com/ydstar/AdapterKit) — 简洁易用的 Android 列表组件。
* [BannerKit](https://github.com/ydstar/BannerKit) — 简洁易用的 Android 无限轮播图组件。
* [TabBottomKit](https://github.com/ydstar/TabBottomKit) — 简洁易用的 Android 底部导航组件。

## 效果预览
<img src="https://github.com/ydstar/RefreshKit/blob/main/preview/show.gif" alt="动图演示效果" width="250px">

轻量级下拉刷新和上拉加载组件

## 导入方式

仅支持`AndroidX`
```
dependencies {
      implementation 'com.android.ydkit:refresh-kit:1.0.1'
      implementation 'com.android.ydkit:adapter-kit:1.0.1'//可选项(加载更多需搭配使用)
}
```

## 使用方法

#### 只是使用下拉刷新
#### 1.在XML布局文件中添加 RefreshLayout
```java
<?xml version="1.0" encoding="utf-8"?>
<com.refresh.kit.core.RefreshLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/refresh_layout"
    android:layout_width="match_parent"
    android:layout_height="match_parent">


    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/recycler_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>
</com.refresh.kit.core.RefreshLayout>
```

#### 2.在 Activity 或者 Fragment 中添加代码
```java
val refreshLayout = findViewById<IRefreshLayout>(R.id.refresh_layout)
//自定义下拉头
val lottieOverView = LottieOverView(this)
refreshLayout.setRefreshOverView(lottieOverView)

refreshLayout.setRefreshListener(object : IRefresh.IRefreshListener {
      //下拉刷新的时候会调用下面这个方法,一般情况下都是下拉刷新之后去请求数据,然后刷新列表
      override fun onRefresh() {

      }
      override fun enableRefresh(): Boolean {
          return true
      }
})
// 刷新时是否禁止滚动,默认是false
refreshLayout.setDisableRefreshScroll(true)
//设置recyclerView
initRecycleView()
```

#### 下拉刷新和加载更多,建议参考demo


## License
```text
Copyright [2021] [ydStar]

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
