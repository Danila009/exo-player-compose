
~~~
allprojects {
    repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
~~~

~~~
dependencies {
    implementation 'com.google.android.exoplayer:exoplayer:2.18.0'
    implementation 'com.github.Danila009:exo-player-compose:0.0.2'
}
~~~