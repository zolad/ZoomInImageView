ZoomInImageView
==============
A  zoomable ImageView  for Android, can be used in AdapterView and Recyclerview. 一个可拉伸拖动的ImageView ,可在AdapterView和RecyclerView中使用。

![screenshot1~](https://raw.github.com/zolad/ZoomInImageView/master/screenshot/screenshot_1.gif)

Features
==============
- Zooming, using multi-touch.
- Can be used in AdapterView and Recyclerview.
- Easily attach and detach ImageView.
- Anim zooming After release

Dependency
==============
### Add this in your build.gradle file 
```gradle
// appcompat-v7 is required
compile 'com.zolad:zoominimageview:1.0.0'
```


Usage
==============
### 1.attach ImageView

```java
 <com.zolad.zoominimageview.ZoomInImageView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content" />
```

or

```java
ZoomInImageViewAttacher mIvAttacter = new ZoomInImageViewAttacher();
mIvAttacter.attachImageView(imageview);
```

or

```java
ZoomInImageViewAttacher mIvAttacter = new ZoomInImageViewAttacher(imageview);
```

### 2.other settings

set Zoomable

```java
mIvAttacter.setZoomable(zoomable);
```

detach ImageView

```java
mIvAttacter.detach();
```

release anim setting

```java
 mIvAttacter.setZoomReleaseAnimDuration(duration);
 mIvAttacter.setZoomReleaseAnimInterpolator(interpolator);
```


License
==============

    Copyright 2018 Zolad

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
