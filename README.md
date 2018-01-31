# UnifyIDChallenge

An app for an Android challenge to be considered for the Android developer position.

How to run:
1. Use android studio 3.0.0 and above
2. Import the project as android project
3. Sync gradle dependencies, build the project
4. On the main screen you will see a FAB. Click on it to start the scanning process. 
The photos will be stored in .UnifyIdChallenge folder, which is not available for users. 

Further Considerations: 
Also internal storage could be used to hide the photos. So the photos will be private to our application.
Hiding the folder doesn't guarantee us a security, since it can be opened on some other file managers or even Windows file system.
Some encryption algorithm should be implemented to safely store our photos.
