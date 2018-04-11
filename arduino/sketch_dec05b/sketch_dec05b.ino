#include <ArduinoJson.h>


/*

This is a test script which:
* Waits for the user to input a JSON string into the serial window
* Collects the string 
* Parses the string 
* Outputs the details of the string (as per the standalone example)

NB: There is no error checking on the input. Incorrectly formated strings will still be parsed,
but will result in a nonsensical output

aJson_False is type 0
aJson_True is type 1
aJson_NULL is type 2
aJson_Int is type 3
aJson_Float is type 4
aJson_String is type 5
aJson_Array is type 6
aJson_Object is type 7
aJson_IsReference is type 128

*/

//#include <aJSON.h>

char c[255];
char* message;
//aJsonObject* root = aJson.createObject();
//aJsonObject* parm = aJson.createArray();
int i = 0;
//This is a quick way of ensuring the ParseJSON function is only called once (until Reset() is called)
int runonce = 0; 


void setup()
{
  Serial.begin(9600);
}


void loop()
{
  if (Serial.available() > 0) {
    char data;
    data = Serial.read();
    delay(10);
    c[i] = data;
    i++;
  } else if (c[0] !=0 && runonce == 0) {
    runonce = 1;
    message = c;
    Serial.println(message);
    Reset();
//    ParseJSON(message);
  }
}

void Reset()
{
  runonce = 0;
  i = 0;
  message ="";
  c[0] = 0;
}

//void ParseJSON(char* aJsonString)
//{
//  Serial.print("Starting to Parse: ");
//  Serial.println(aJsonString);
//
//  root = aJson.parse(aJsonString);
//
//  aJsonObject* method = aJson.getObjectItem(root, "method");
//  aJsonObject* params = aJson.getObjectItem(root, "params");
//  aJsonObject* id = aJson.getObjectItem(root, "id");
//  int ArraySize = aJson.getArraySize(params);
//
//  int methodtype = method->type;
//  int paramtype = params->type;
//  int idtype = id->type;
//  Serial.println(methodtype);
//  Serial.println(paramtype);
//  Serial.println(idtype);
//
//  Serial.println("");
//  Serial.println("** Printing the Parameters **");
//  Serial.print("Method: ");
//  Serial.println(method->valuestring);
//  Serial.print("Params Array Size: ");
//  Serial.println(ArraySize);
//  for (int i = 0; i < ArraySize; i++){
//    aJsonObject* test = aJson.getArrayItem(params, i);
//    Serial.print("Parameter ");
//    Serial.print(i);
//    Serial.print(": ");
//    Serial.println(test->valuestring);
//  }
//  Serial.print("Id: ");
//  Serial.println(id->valuestring);
//  Serial.println("");
//
//  if(method->valuestring = "echo"){
//    Serial.println("This demonstrates how to use the JSON output in an IF statement \n");
//  }
//  Reset();
//}

