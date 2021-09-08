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
Wiring diagram:  
![alt text](https://github.com/viktorvano/Bluetooth-for-STM32-and-Android/blob/master/Bluetooth%20LE%20BMP280/SchematicWiring.png?raw=true)  
  
Module wiring modification for I2C:  
![alt text](https://github.com/viktorvano/Bluetooth-for-STM32-and-Android/blob/master/Bluetooth%20LE%20BMP280/BMP280%20module%20wiring%20mod.png?raw=true)  
  
ECG tutorial video: https://www.youtube.com/watch?v=gboCV7RqehE  
  
ECG 2 wire configuration (single lead):  
![alt text](https://github.com/viktorvano/Bluetooth-for-STM32-and-Android/blob/master/Bluetooth%20LE%20ECG/ECG%20schematic%20diagram%20-%202%20lead%20ECG.png?raw=true)  
  
ECG 3 wire configuration (single lead):  
![alt text](https://github.com/viktorvano/Bluetooth-for-STM32-and-Android/blob/master/Bluetooth%20LE%20ECG/ECG%20schematic%20diagram%20-%203%20lead%20ECG.png?raw=true)  
  
ECG electrode placement and heart potertial:  
![alt text](https://github.com/viktorvano/Bluetooth-for-STM32-and-Android/blob/master/Bluetooth%20LE%20ECG/ECG-3-electrodes.png?raw=true)  
  
ECG waveform:  
![alt text](https://github.com/viktorvano/Bluetooth-for-STM32-and-Android/blob/master/Bluetooth%20LE%20ECG/PQRST.png?raw=true)  
