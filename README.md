# Bluetooth-for-STM32-and-Android
 Bluetooth Classic and Bluetooth LE examples for STM32 and Android

Bluetooth Classic tutorial: https://www.youtube.com/watch?v=TtpLcsQ4nMw  
Works with HC-06 or HC-05 modules. Other Bluetooth classic modules should work too.   
   
     
Bluetooth LE tutorial: https://www.youtube.com/watch?v=zRJctDUt-Zg  

Works with JDY-08 module. For other Bluetooth LE modules, you need to modify UUIDs of service and characteristic which has read and write descriptor.  It is located in BluetoothLeService.java file.

  
For RFID I have used this library, which I have modified: https://github.com/soonuse/pn532-lib/blob/master/examples/stm32/stm32.7z  
RFID project example uses PN532 to read UID from RFID card and then send it as a string to the Android app via Bluetooth.  
RFID Tutorial: https://www.youtube.com/watch?v=ARtuPrKluho  
  
   
Displaying data from BMP280 in a chart: https://www.youtube.com/watch?v=MlOEx_Op3w8  
For this project I have used this library via Gradle - those are amazing charts: https://github.com/PhilJay/MPAndroidChart
