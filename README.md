
## Gradle

~~~
allprojects {
    repositories {
		maven { url 'https://jitpack.io' }
	}
}
~~~

~~~
dependencies {
    implementation 'com.github.Danila009:exo-player-compose:0.0.2'
}
~~~

## ExoPlayer

~~~
val exoParameters = exoParameters {
    url = VIDEO_URL
    useController = false
    statePlayPause = VideoPlayerPausePlayState.PAUSE
}
~~~

~~~
ExoPlayer(
    parameters = exoParameters
)
~~~

## Custom ExoPlayer (video item)

~~~
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
~~~


~~~
ExoPlayerCustom(
    modifier = Modifier
        .fillMaxWidth()
        .height(300.dp),
    url = VIDEO_URL,
    parameters = exoParameters
)
~~~

## Custom ExoPlayer (video list)

~~~
ExoPlayerCustom(
    modifier = Modifier
        .fillMaxWidth()
        .height(300.dp),
    videoList = videoList,
    parameters = exoParameters
)
~~~

## License

~~~
Copyright (C) 2022 Danila Belyakov dan.nel.89@bk.ru

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
~~~