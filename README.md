# smarty
This is the source code to my custom version of BTNotification app called 'smarty' 
first posted in [this XDA thread](https://forum.xda-developers.com/showpost.php?p=76227730)

while i do not intend to work on this app anymore, i saw that people are interested in contributing 
and taking the project further so i have uploaded the code base as is.

> Smarty - Custom BTNotification app for MTK smartwatches (tested with DZ09)
> so i decompiled the original BTNotification app and after a lot of cleaning up and debugging and reverse engineering their systems, i have finally been able to cobble together a functional version of the app.

>This is a customized version of the smartwatch companion app for MTK smartwatches with a few improvements !

>No spyware !
>The original app had a lot of spyware / analytics in it and i did my best to try and remove all of it
>(if you notice any please let me know !)

>Better notifications !
>The icon that comes with notifications now has black background instead of white , gives better look to watches with black UI
>more detailed notifications with more actionable information (like message text etc.) and notifications works with more apps !
>can use standard notifications by enabling privacy mode, works exactly like the BTNotification app

>Can use front camera for your smartwatch remote camera
>just dial a call to the number 00*001 from your smartwatch or phone (the call is automatically blocked )
>just call it again to switch camera to rear camera again.

>Can open any HTML app / link on your smartwatch with the remote camera
>just need to dial a call to the number 00*002 from your smartwatch or phone
>call 00*003 to stop opening the app and switch to camera again.
>[ by default this opens an analog watch, change the link in the app to open something else]

>codes supported currently
>switch camera = 00*001
>open HTML mode = 00*002
>camera mode =00*003

>find my phone feature
>the phone now vibrates and even if silent the phone will ring !
