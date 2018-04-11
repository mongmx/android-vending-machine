/* Copyright 2012 Google Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 * Project home page: http://code.google.com/p/usb-serial-for-android/
 */

// Sample Arduino sketch for use with usb-serial-for-android.
// Prints an ever-increasing counter, and writes back anything
// it receives.

#include "Adafruit_Thermal.h"
#include "SoftwareSerial.h"

SoftwareSerial softSerial(10, 11); // RX, TX
Adafruit_Thermal printer(&softSerial);

static int counter = 0;
int tick = 0;
int amount = 0;

String str;

String content = "";
char character;

char c[255];
char* message;
int i = 0;
//This is a quick way of ensuring the ParseJSON function is only called once (until Reset() is called)
int runonce = 0;

void setup() {
  Serial.begin(115200);
  softSerial.begin(19200);
//  printer.begin();
//  printer.sleep();
  pinMode(12, INPUT_PULLUP);
}

void loop() {
////  Serial.print("Tick #");
////  Serial.print(counter++, DEC);
////  Serial.print("\n");

//  int buttonState = digitalRead(pushButton);
//  if (buttonState) {
//    Serial.print("C1");
//  }
//  
//  if (Serial.peek() != -1) {
//    Serial.print("Read: ");
//    do {
//      Serial.print((char) Serial.read());
//    } while (Serial.peek() != -1);
//    Serial.print("\n");
//  }
//  
////  delay(1000);
//  delay(250);

//  tick = digitalRead(12);
//  if((tick == 0) && (amount != 0)){
//    amount = amount - 1;
//    Serial.println(amount);
//    delay(75);
//    if(amount == 0) {
//      Serial.println("Completed");
//    }   
//  }

  tick = digitalRead(12);
  if(tick == 0){
    Serial.print("C1");
    delay(75); 
  }

//  if(Serial.available()){
//    softSerial.println(Serial.read());
//  }

//  if(Serial.available()){
//    if((Serial.read() != 13) || (Serial.read() != 10)){
//      printer.println(Serial.read());
//    }
//  }

    if(Serial.available() > 0) {
      int inChar = Serial.read();
      str = Serial.readStringUntil('\n');
//      printer.println(str);
      if(inChar != 240) {
        softSerial.println(str);
        softSerial.println("");
      }
    }

//    while(Serial.available()) {
//      character = Serial.read();
//      content.concat(character);
//    }
//
//    if (content != "") {
//      printer.println(content);
//      content = "";
//    }

//  if (Serial.available() > 0) {
//    char data;
//    data = Serial.read();
//    delay(10);
//    c[i] = data;
//    i++;
//  } else if (c[0] !=0 && runonce == 0) {
//    runonce = 1;
//    message = c;
//    softSerial.println(message);
//    Reset();
////    ParseJSON(message);
//  }
  
//  if(Serial.read() == 13){
//    printer.wake();
//    
//    printer.justify('C');
//    printer.setSize('S');
//    printer.println(F("Android Vending Machine"));
//    printer.setDefault();
//    
//    printer.println(F("--------------------------------"));
//    
//    printer.justify('C');
//    printer.setSize('L');
//    printer.println("no. 10");
//    printer.setDefault();
//
//    printer.println(F("--------------------------------"));
//    printer.println("\n");
//    printer.println("\n");
//    
//    printer.sleep();
//  }
}

void Reset() {
  runonce = 0;
  i = 0;
  message = "";
  c[0] = 0;
}
