## <img src="https://user-images.githubusercontent.com/60299141/158283111-231ef8da-9d0f-4420-96bd-6fc02bf809e4.png" width="25" /> Overview project
News MVVM is an app I made with the intention to exercise and learn the most recent technologies that are used to build Android applications.  

This app uses newsapi.org to display the breaking news for the country the user has chosen. The user may open any article, read it on the app and save it in the database.
The breaking news screen may be refreshed by swiping it up. On the Favorite screen, any article saved may be deleted by swiping it to any side (left or right). 
On the search screen, it's possible to make a news research based on the country selected.



https://user-images.githubusercontent.com/60299141/158223660-abd46d30-cab4-4e0f-9bcc-e3ad28d87c90.mp4


## <img src="https://user-images.githubusercontent.com/60299141/158274121-e6f063de-29fd-4a90-b9d2-172d98ed0826.png" width="25" /> Tchnologies and libraries used
- 100% Kotlin
- MVVM architecture
- LiveData
- ViewBinding
- Single Activity with multiple fragments
- [Android Navigation components](https://developer.android.com/guide/navigation)
- [Retrofit](https://square.github.io/retrofit/)
- [Moshi](https://github.com/square/retrofit/tree/master/retrofit-converters/moshi) for JSON convertion
- [Dagger-Hilt](https://developer.android.com/training/dependency-injection/hilt-android) for dependency injection
- [Coroutines](https://developer.android.com/kotlin/coroutines)
- [Paging3](https://developer.android.com/topic/libraries/architecture/paging/v3-overview) for pagination
- [Coil](https://github.com/coil-kt/coil) for image display
- [Room](https://developer.android.com/training/data-storage/room) for database storage
- [DataStore](https://developer.android.com/topic/libraries/architecture/datastore) for preferences storage

## <img src="https://user-images.githubusercontent.com/60299141/158275533-e137b93c-fa00-4d2a-a2b5-1a0570bd0748.png" width="25" /> Instructions
Before running the app, you should make an account on https://newsapi.org/ and get your own API key. After that, go to the Constants.kt file and assign
your key to the API_KEY attribute. 

#### Attribuitions
<a href="https://www.flaticon.com/br/icones-gratis/brasil" title="brasil ícones">Flag icons used in this app were created by Freepik - Flaticon</a>
