#include <Blink.h>

void setup() {
    analogRead(0);
    millis();
}

char Str1[15];
char Str2[8] = {'a', 'r', 'd', 'u', 'i', 'n', 'o'};
char Str3[8] = {'a', 'r', 'd', 'u', 'i', 'n', 'o', '\0'};
char Str5[8] = "arduino";
char Str6[15] = "arduino";
char Str4[] = "arduino";

char* myStrings[]={"This is String 1", "This is String 2", "This is String 3",
"This is String 4", "This is String 5","This is String 6"};
String stringOne = "Hello String";                                     // using a constant String
String stringOne1 =  String('a');                                          // converting a constant char into a String
String stringOne2 =  String();                                          // converting a constant char into a String
String stringTwo =  String("This is a string");                 // converting a constant string into a String object
String stringOne3 =  String(stringTwo + " with more"); // concatenating two strings
String stringOne4 =  String(13);                                          // using a constant integer
String stringOne5 =  String(analogRead(0), DEC);          // using an int and a base
String stringOne6 =  String(45, HEX);                                // using an int and a base (hexadecimal)
String stringOne7 =  String(255, BIN);                               // using an int and a base (binary)
String stringOne8 =  String(millis(), DEC);                        // using a long and a base
String stringOne9 =  String(5.698, 3);
int mySensVals[6] = {2, 4, -8, 3, 2};
int myPins[] = {2, 4, 8, 3, 6};
char message[6] = "hello";
int myInts[6];
bool bool_var = true;
boolean boolean_var = false;
byte byte_var = 7;
char char_var = 'A';
char char_var2 = 65;      // both are equivalent
double double_var = 12.4;
float float_var = 12.3;
int int_var = -12;
long long_var = -12423423543534;
short short_var = 123;
unsigned char char_myChar = 240;
unsigned int int_ledPin = 13;
unsigned long unsigned_long_var = 124344243534;
word word_var = 10000;

void onStart() {
	Serial.begin(9600);
	tracer.setOutput(Serial);
}
