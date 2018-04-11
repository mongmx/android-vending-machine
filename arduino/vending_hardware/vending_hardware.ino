//#include "Adafruit_Thermal.h"
#include "SoftwareSerial.h"

SoftwareSerial softSerial(10, 11); // RX, TX
//Adafruit_Thermal printer(&softSerial);

int tick = 0;
String str;

void setup() {
  Serial.begin(115200);
  softSerial.begin(19200);
//  printer.begin();
//  printer.sleep();
  pinMode(12, INPUT_PULLUP);
}

void loop() {
  tick = digitalRead(12);
  if(tick == 0) {
    Serial.print("C1");
    delay(75); 
  }
  if(Serial.available() > 0) {
    int inChar = Serial.read();
    str = Serial.readStringUntil('\n');
    if(inChar != 240) {
      softSerial.println(str);
//      printer.println(str);
    }
  }
}
