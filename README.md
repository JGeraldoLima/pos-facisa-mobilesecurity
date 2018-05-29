# pos-facisa-mobilesecurity

This repo is only for study purposes. It was used as FACISA Post-degree Mobile Security Module Final Project App.
As REST API, I used a node.js jwt server based (implemented by prof. Ruan Pierre de Oliveira).

# Required features

- Must use native login feature
- Must connect jwt server, get token, and use it to call any protected endpoint (in my case, random-quote)

# Instructions

Just run server "npm start" and open the app. 

Only the main flow was implemented, so to be able to test, 
you can login with google just one time per server instance and then call the random-quote service.
Why? When user do login successfully with google, the app calls a service to register him and then 
returns a token, that will be used for the random-quote calls. If you login again before restart 
the server, it will return an error because the user has already been registered. 

All alternative flows will be handled in the future.


# Credits

- [RetroFit](https://github.com/square/retrofit)
- [OkHttp](https://github.com/square/okhttp)
- [Google Gson](https://github.com/google/gson)
- Android Support: ConstraintLayout
- Android DataBinding

# License

You may use this software under the MIT License
