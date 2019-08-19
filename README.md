# Test_Overlay

Test_Overlay is an Android application that uses Android’s built in accessibility feature to record all information about the state transitions that occur in the device's UI. This means the app is able to identify almost everything the user is doing on their device;  in this case it would be: 

* The current app the user is on  

* Which activity the user is currently viewing 

* What component on that activity the user is currently interacting with

On top of the accessibility services used, the app uses an overlay feature to display start, stop and reset buttons over the device screen to allow users to have full control over exactly what interactions are being recorded. Once the user has completed their run through of the app, they can generate a test file in the device by simply clicking a button which they can then email to QA via the app.


