# Nabto Android JCenter Demo

Simple app that demonstrates how to use the Nabto Client SDK to do P2P RPC invocations and establish tunnels. It uses the [Nabto Android Client API](https://github.com/nabto/android-client-api) from [JCenter](https://bintray.com/nabto/android/com.nabto.android%3Anabto-api).

## Notes

To keep the app simple, device ids are hard coded in the source code to public Nabto demos. Also, there is no device pairing - meaning that the target device must have access control disabled. 

You can replace the hard coded device ids with ids of your own devices, obtained through the [Nabto Cloud Console](https://console.cloud.nabto.com). As device applications to run on your own, use the Nabto [weather station demo](https://github.com/nabto/unabto/tree/master/apps/weather_station) and the [device tunnel application](https://github.com/nabto/unabto/tree/master/apps/tunnel). They both support disabled access control and can be used as-is with this demo.

For production, of course access control must be enabled - read more about access control in [this article](https://www.nabto.com/pairing-and-access-control-part-1-intro-and-device/). For a more advanced demo that includes full access control, see the [Nabto Heat Control](https://github.com/nabto/ios-starter-nabto) demo.
