/*
 * Geiger counter Adk by Uncoded based on Geiger counter by Libelium 

 *
 *  Explanation: This example shows how to get the signal from the Geiger Tube
 *  in Arduino, we use one of the Arduino interrupt pins (PIN2).
 *  We count the time (ms) between two pulses of the Geiger tube.
 *
 *  Copyright (C) 2011 Libelium Comunicaciones Distribuidas S.L.
 *  http://www.libelium.com
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 2 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *  Version:		0.3
 *  Design:		Marcos Yarza, David Gascon
 *  Implementation:	Marcos Yarza
 */

// include the library code:
#include <Wire.h>
#include <LiquidCrystal.h>

#include <Max3421e.h>
#include <Usb.h>
#include <AndroidAccessory.h>
// initialize the library with the numbers of the interface pins
LiquidCrystal lcd(3,4,5,6,7,8);


// Threshold values for the led bar
#define TH1 45
#define TH2 95
#define TH3 200
#define TH4 400
#define TH5 600

// Conversion factor - CPM to uSV/h
#define CONV_FACTOR 0.00812

// Variables
int ledArray [] = {10,11,12,13,9};
int geiger_input = 2;
long count = 0;
int count_and =0;
int seq_and=0;
long countPerMinute = 0;
long timePreviousMeassure = 0;
long timePreviousMeassure_and = 0;
long time = 0;
long countPrevious = 0;
float radiationValue = 0.0;

AndroidAccessory acc("Google, Inc.",
"DemoKit",
"DemoKit Arduino Board",
"1.0",
"http://www.android.com",
"0000000012345678");

void setup(){
  pinMode(geiger_input, INPUT);
  digitalWrite(geiger_input,HIGH);
  for (int i=0;i<5;i++){
    pinMode(ledArray[i],OUTPUT);
  }

  Serial.begin(115200);
  Serial.print("\r\nStart");
  acc.powerOn();
  //set up the LCD's number of columns and rows:
  lcd.begin(16, 2);
  lcd.clear();
  lcd.setCursor(0, 0);
  lcd.print("Radiation Sensor");
  lcd.setCursor(0,1);
  lcd.print("Board - Arduino"); 
  delay(1000);


  lcd.clear();
  lcd.setCursor(0,1);  
  lcd.print("La Cosa Radiactiva");
  delay(500);
  for (int i=0;i<5;i++){
    delay(200);  
    lcd.scrollDisplayLeft();
  }
  delay(500);

  lcd.clear();  
  lcd.setCursor(0, 0);
  lcd.print("CPM=");
  lcd.setCursor(4,0);
  lcd.print(6*count);
  lcd.setCursor(0,1);
  lcd.print(radiationValue);

  attachInterrupt(0,countPulse,FALLING);
  
}
byte msg[3];
void loop(){
  if (millis()-timePreviousMeassure > 10000){
    countPerMinute = 6*count;
    radiationValue = countPerMinute * CONV_FACTOR;
    timePreviousMeassure = millis();
    //Serial.print("cpm = "); 
    //Serial.print(countPerMinute,DEC);
    //Serial.print(" - ");
    //Serial.print("uSv/h = ");
    //Serial.println(radiationValue,4);      
    lcd.clear();    
    lcd.setCursor(0, 0);
    lcd.print("CPM=");
    lcd.setCursor(4,0);
    lcd.print(countPerMinute);
    lcd.setCursor(0,1);
    lcd.print(radiationValue,4);
    lcd.setCursor(6,1);
    lcd.print(" uSv/h");

    //led var setting  
    if(countPerMinute <= TH1) ledVar(0);
    if((countPerMinute <= TH2)&&(countPerMinute>TH1)) ledVar(1);
    if((countPerMinute <= TH3)&&(countPerMinute>TH2)) ledVar(2);
    if((countPerMinute <= TH4)&&(countPerMinute>TH3)) ledVar(3);
    if((countPerMinute <= TH5)&&(countPerMinute>TH4)) ledVar(4);
    if(countPerMinute>TH5) ledVar(5);
    
/**    if (acc.isConnected()) {
      Serial.println(radiationValue); 
      acc.beginTransmission();
      msg[0] = 0x1;
      msg[1] = int(radiationValue*1000) >> 8;
      msg[2] = int(radiationValue*1000) & 0xff;
      acc.write(msg , 3);      
      acc.endTransmission();
    }
    else{
         Serial.println("desconectado");  
    } **/
    
    count = 0;
  }
  
    if ( millis()-timePreviousMeassure_and > 500 ){
         if (acc.isConnected()) {
         //acc.beginTransmission();
            msg[0] = 0x1;
            msg[1] = int(count_and) >> 8;
            msg[2] = int(count_and) & 0xff;
            acc.write(msg , 3);      
            msg[0] = 0x2;
            msg[1] = int(seq_and) >> 8;
            msg[2] = int(seq_and) & 0xff;
            acc.write(msg , 3); 
        //acc.endTransmission();
      }
      else{
           Serial.println("desconectado"); 
           
      }
     count_and=0; 
     seq_and++;
     timePreviousMeassure_and = millis();
    }
}

void countPulse(){
  detachInterrupt(0);
  count++;
  count_and++;
  while(digitalRead(2)==0){
  }
  attachInterrupt(0,countPulse,FALLING);
}

void ledVar(int value){
  if (value > 0){
    for(int i=0;i<=value;i++){
      digitalWrite(ledArray[i],HIGH);
    }
    for(int i=5;i>value;i--){
      digitalWrite(ledArray[i],LOW);
    }
  }
  else {
    for(int i=5;i>=0;i--){
      digitalWrite(ledArray[i],LOW);
    }
  }
}

