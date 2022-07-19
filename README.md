# ExoPlayerCompose

[![Platform](https://img.shields.io/badge/platform-android-green.svg)](http://developer.android.com/index.html)
<img src="https://img.shields.io/badge/license-MIT-green.svg?style=flat">
[![API](https://img.shields.io/badge/API-16%2B-blue.svg?style=flat)](https://android-arsenal.com/api?level=16)

## Gradle

```groovy
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

```groovy
dependencies {
    implementation 'com.github.Danila009:exo-player-compose:0.1.0'
}
```

## ExoPlayer

```kotlin
val exoParameters = exoParameters {
    url = VIDEO_URL
    useController = false
    statePlayPause = VideoPlayerPausePlayState.PAUSE
}
```

```kotlin
ExoPlayer(
    parameters = exoParameters
)
```

## Custom ExoPlayer (video item)

```kotlin
val exoParameters = exoCustomParameters {

    useController = true

    onProgressBarVisibility = {
        Log.d("onProgressBarVisibility",it.toString())
    }

    onVideoListPositionItem = {
        Log.d("onVideoListPositionItem",it.toString())
    }

    onFullscreen = {
        Log.d("onFullscreen",it.toString())
    }
}
```


```kotlin
ExoPlayerCustom(
    modifier = Modifier
        .fillMaxWidth()
        .height(300.dp),
    url = VIDEO_URL,
    parameters = exoParameters
)
```

## Custom ExoPlayer (video list)

```kotlin
ExoPlayerCustom(
    modifier = Modifier
        .fillMaxWidth()
        .height(300.dp),
    videoList = videoList,
    parameters = exoParameters
)
```

## You Tube Double Tap

[Documentation][DoubleTapPlayerView]

```kotlin
val exoParameters = exoCustomParameters {
    
    
    doubleTapParameters = doubleTapParameters {
        enabled = true

        seekSeconds = 10

        animationDuration = 650
    }
    
}
```

## Preview Seek Bar

[Documentation][PreviewSeekBar]

```kotlin
val exoParameters = exoCustomParameters {

    previewSeekBarParameters = previewSeekBarParameters {
        enabled = true
        previewUrl = "https://bitdash-a.akamaihd.net/content/MI201109210084_1/thumbnails/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.jpg"
    }
    
}
```

## Video Filters

[Documentation][ExoPlayerFilter]

```kotlin
val exoParameters = exoCustomParameters {

    exoFilterParameters = exoFilterParameters {
        type = ExoFilterType.INVERT
    }
    
}
```

## License

~~~
Copyright (C) 2022 Danila Belyakov dan.nel.89@bk.ru


Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
~~~

[DoubleTapPlayerView]: https://github.com/vkay94/DoubleTapPlayerView
[PreviewSeekBar]: https://github.com/rubensousa/PreviewSeekBar
[ExoPlayerFilter]: https://github.com/MasayukiSuda/ExoPlayerFilter