# Important ~ Migration to Github Packages
With JFrog removing services we have migrated to using Github Packages. This would be a good [starting point](https://docs.github.com/en/packages/learn-github-packages/installing-a-package).
The packages can be found [here](https://github.com/ShimmerEngineering?tab=packages&repo_name=Shimmer-Java-Android-API).
Also to be noted, the Shimmer Java Android API repository has now been made public. Contributions are welcomed.

# Important ~ Migration to JFrog
With Bintray being sunset, we have migrated to the use of JFrog. Should you face any problems please do not hesitate to contact us. We apologize for any inconvenience caused.

# JFrog Gradle Settings
The gradle file for the example ShimmerBasicExamples have been updated accordingly to the following:-

repositories:-

```
maven {
 url 'https://shimmersensing.jfrog.io/artifactory/ShimmerAPI'
}
```
dependencies:-
```
compile(group: 'com.shimmersensing', name: 'ShimmerDriverPC', version: '0.9.4beta')
```

For further info 
https://shimmersensing.jfrog.io/ui/repos/tree/General/ShimmerAPI%2Fcom%2Fshimmersensing%2FShimmerDriverPC%2F0.9.4beta%2FShimmerDriverPC-0.9.4beta.jar

# Bintray Sunset
The following ways of getting the library is **deprecated**
repositories:-

```
 maven {
        url  "http://dl.bintray.com/shimmerengineering/Shimmer"
    }
```
dependencies:-
```
compile 'com.shimmerresearch.pcdriver:ShimmerDriverPC:0.9.3beta'
```

# Shimmer Java Examples
A number of Java examples are provided showing how to connect,configure and stream from a Bluetooth supported Shimmer device.

# Linux Bluetooth Pairing And Using the Jar Application
1) first install Bluetooth Manager : sudo apt-get install blueman
2) next open Bluetooth Manager and search for devices
3) right click on the device you want to use and choose pair, note the Bluetooth pairing key is 1234
4) once paired click setup, and connect to serial port, at this stage you should see the Bluetooth come on permanently on the Shimmer device, also you should see a pop up indicating /dev/rfcommx
5) now you can open the exported jar (*e.g. SensorMapsExample*) and connect to that port /dev/rfcommx use **sudo** when running *sudo java -jar X.jar* otherwise the application will throw an error when connecting to the Shimmer device

# Known Issues
1) Update JSSC from 2.8.0 to 2.9.1 https://github.com/java-native/jssc/releases , if the following error below is seen

 EXCEPTION_ACCESS_VIOLATION (0xc0000005) at pc=0x000000007110b5db, pid=36460, tid=30304#
 JRE version: Java(TM) SE Runtime Environment (13.0.1+9) (build 13.0.1+9)
 Java VM: Java HotSpot(TM) 64-Bit Server VM (13.0.1+9, mixed mode, sharing, tiered, compressed oops, g1 gc, windows-amd64)
 Problematic frame:
 C  [jSSC-2.8_x86_64.dll+0xb5db]


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
 
