# My Android App
A Chat app Android project

This is an Android application I have started a while ago as a side project when I first dived into the Kotlin language.
I have had the most fun diving into Android Apps world with its rich and always up to date documentation, the Android Jetpack library suite, 
and its overwhelmingly helpful and growing community

I have also developed a server API for this App, based on the NodeJS JavaScript runtime and the various libraries it offers.

_See the code base for the [NodeJS-API server](https://github.com/vbounyasit/NodeJS-API)_

_See my [Trello board](https://trello.com/b/0NH0WcIo/android-app) with the technical business rules I implemented_

# Architecture choices

# App components

# Libraries

## General
- [Retrofit](https://square.github.io/retrofit/) is used for the **Network layer**, to allow the App to query and retrieve the relevant data from **the remote API server**
- [Moshi](https://github.com/square/moshi) allows us to parse various **JSON responses** from the remote API, or to serialize and **pass data between various components** in the App
- [Room](https://developer.android.com/jetpack/androidx/releases/room) is a database library used for **persisting data locally** in various structured tables
- [Kotlin coroutines](https://developer.android.com/kotlin/coroutines) allows us to use **Kotlin coroutines** for launching asynchronous work on various threads
- [Glide](https://github.com/bumptech/glide) is an open source media management. It allows us to **load images or other medias efficiently** with intelligent memory and disk caching, with additional features such as image cropping/resizing/etc
- [Hilt](https://developer.android.com/training/dependency-injection/hilt-android) is a **dependency injection** library. It is used to inject various components into the main parts of our App such as Fragments, ViewModels, Activities, etc.
  It allows us to **obtain an available instance** for our local database, network component, DAO, dispatchers or other components without the need to instantiate each of them as Singletons manually which can become **very tedious**
- [WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager) allows us to use Android's WorkManager component, in order to perform various **background tasks** (See [guide to background work](https://developer.android.com/guide/background) for more details)
- [SocketIO](https://socket.io/fr/blog/native-socket-io-and-android/) allows us to open a **Socket connection** between our device and the remote server for **real time executions** such as instant chat messaging, chat/posts/comments notifications, etc

## UI
- [Material Components](https://material.io/develop/android/docs/getting-started) allows the various UI elements of our App to **inherit from a common design system** known as **Material design**, created by Google
- [Navigation](https://developer.android.com/guide/navigation/navigation-getting-started) allows us to use the Navigation component to navigate between different destination fragments

## Testing
- [MockK](https://mockk.io/) allows us to generate mock instances of various Application components that would otherwise be difficult to instantiate for **unit tests**
- [KoTest](https://kotest.io/) is a flexible multi-platform **assertion Framework** allowing us to write simple, clean and elegant test cases
- [JUnit](https://kotlinlang.org/docs/jvm-test-using-junit.html) is a popular **unit testing Framework**



**Under construction...**
