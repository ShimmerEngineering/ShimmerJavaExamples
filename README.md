# Important ~ Migration to JFrog
With Bintray being sunset, we are currently in the process of moving to the use of JFrog. As a test, this is a prelimeneary branch of which you can use to test your applications. Please note this is not the final version as we will be doing more refactoring and updates that have long been scheduled. Should you face any problem please do not hesitate to contact us. Eventually the goal will have the master branch updated and using JFrog. We apologize for any inconvenience caused.

# Shimmer Java Examples
A number of Java examples are provided showing how to connect,configure and stream from a Bluetooth supported Shimmer device.

# Importing Via Gradle
The Shimmer API used by this Java Examples can be imported using gradle, please include the following repository in your build.gradle file 
```
allprojects {
    repositories {
        jcenter()
        maven {
            url  "http://dl.bintray.com/shimmerengineering/Shimmer"
        }
    }
}
```
Most recent uploaded library can be found here:-
https://bintray.com/shimmerengineering/Shimmer/shimmerdriverpc

# Linux Bluetooth Pairing And Using the Jar Application
1) first install Bluetooth Manager : sudo apt-get install blueman
2) next open Bluetooth Manager and search for devices
3) right click on the device you want to use and choose pair, note the Bluetooth pairing key is 1234
4) once paired click setup, and connect to serial port, at this stage you should see the Bluetooth come on permanently on the Shimmer device, also you should see a pop up indicating /dev/rfcommx
5) now you can open the exported jar (*e.g. SensorMapsExample*) and connect to that port /dev/rfcommx use **sudo** when running *sudo java -jar X.jar* otherwise the application will throw an error when connecting to the Shimmer device

# The following license applies to the source code within this repository/project.
Copyright (c) 2017, Shimmer Research, Ltd. All rights reserved

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright
   notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above
   copyright notice, this list of conditions and the following
   disclaimer in the documentation and/or other materials provided
   with the distribution.
 * Neither the name of Shimmer Research, Ltd. nor the names of its
   contributors may be used to endorse or promote products derived
   from this software without specific prior written permission.
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 
