# Electrical Vehicle Demonstrator GUI
The Electric Vehicle Demonstrator is a safe and interactive display that shows how an electric vehicle works. The Demonstrator consists of a motor from a decommissioned electric vehicle, user control interface, and a visual display showing current and historic values for battery voltage, current draw, and RPM.

![Screenshot of application][screenshot]

## Directions
This project comes with an [Ant](http://ant.apache.org/) build script. To build the project, just run `ant` from the project's root directory. The provided Ant build targets are:
* `ant build`: Builds the project. This is the default action taken when no target is specified.
* `ant clean`: Deletes build artifacts. This is useful if `ant build` isn't updating an existing .class file for some reason.
* `ant run`: Run the project. This requires you to have built the project first!


[screenshot]: http://i.imgur.com/kM73pG1.png "Screenshot of v0.1.1 window"