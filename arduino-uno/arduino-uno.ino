/*
 keyestudio smart turtle robot
 lesson 11
 Tracking turtle
 http://www.keyestudio.com
*/
int left_ctrl = 4;//define direction control pin of A motor
int left_pwm = 6;//define PWM control pin of A motor
int right_ctrl = 2;//define direction control pin of B motor
int right_pwm = 5;//define PWM control pin of B motor
int sensor_L = 11;//define the pin of left line tracking sensor
int sensor_M = 7;//define the pin of middle line tracking sensor
int sensor_R = 8;//define the pin of right line tracking sensor
int L_val, M_val, R_val;//define these variables

const bool SENSOR_BLACK_IS_HIGH = false; // Set to true if your sensor returns 1 on black.
const int SPEED_FRONT = 160;
const int SPEED_TURN_FAST = 160;
const int SPEED_TURN_SLOW = 130;

void setup() {
  Serial.begin(9600);//start serial monitor and set baud rate to 9600
  pinMode(left_ctrl, OUTPUT);//set direction control pin of A motor to OUTPUT
  pinMode(left_pwm, OUTPUT);//set PWM control pin of A motor to OUTPUT
  pinMode(right_ctrl, OUTPUT);//set direction control pin of B motor to OUTPUT
  pinMode(right_pwm, OUTPUT);//set PWM control pin of B motor to OUTPUT
  pinMode(sensor_L, INPUT);//set the pins of left line tracking sensor to INPUT
  pinMode(sensor_M, INPUT);//set the pins of middle line tracking sensor to INPUT
  pinMode(sensor_R, INPUT);//set the pins of right line tracking sensor to INPUT
}

void loop()
{
  tracking(); //run main program
}

void tracking()
{
  L_val = digitalRead(sensor_L);//read the value of left line tracking sensor
  M_val = digitalRead(sensor_M);//read the value of middle line tracking sensor
  R_val = digitalRead(sensor_R);//read the value of right line tracking sensor

  bool L_black = SENSOR_BLACK_IS_HIGH ? (L_val == HIGH) : (L_val == LOW);
  bool M_black = SENSOR_BLACK_IS_HIGH ? (M_val == HIGH) : (M_val == LOW);
  bool R_black = SENSOR_BLACK_IS_HIGH ? (R_val == HIGH) : (R_val == LOW);

  static unsigned long lastPrintMs = 0;
  if (millis() - lastPrintMs >= 120) {
    Serial.print("L=");
    Serial.print(L_val);
    Serial.print(" M=");
    Serial.print(M_val);
    Serial.print(" R=");
    Serial.print(R_val);
    Serial.print(" | black=");
    Serial.print(L_black);
    Serial.print(M_black);
    Serial.println(R_black);
    lastPrintMs = millis();
  }

  if (M_black) {//if the middle sensor detects black line

    if (L_black && !R_black) { //If a black line is detected on the left, but not on the right, turn left
      left();
    }
    else if (!L_black && R_black) { //Otherwise, if a black line is detected on the right and not on the left, turn right
      right();
    }
    else { //Otherwise,forward
      front();
    }
  }
  else { //No black lines detected in the middle
    if (L_black && !R_black) { //If a black line is detected on the left, but not on the right, turn left
      left();
    }
    else if (!L_black && R_black) { //Otherwise, if a black line is detected on the right and not on the left, turn right
      right();
    }
    else { //Otherwise,stop
      Stop();
    }
  }
}

void front()//define the status of going forward
{
  digitalWrite(left_ctrl, HIGH);
  analogWrite(left_pwm, SPEED_FRONT);
  digitalWrite(right_ctrl, HIGH);
  analogWrite(right_pwm, SPEED_FRONT);
}

void back()//define the state of going back
{
  digitalWrite(left_ctrl, LOW);
  analogWrite(left_pwm, 155);
  digitalWrite(right_ctrl, LOW);
  analogWrite(right_pwm, 155);
}

void left()//define the left-turning state
{
  digitalWrite(left_ctrl, LOW);
  analogWrite(left_pwm, SPEED_TURN_FAST);
  digitalWrite(right_ctrl, HIGH);
  analogWrite(right_pwm, SPEED_TURN_SLOW);
}

void right()//define the right-turning state
{
  digitalWrite(left_ctrl, HIGH);
  analogWrite(left_pwm, SPEED_TURN_SLOW);
  digitalWrite(right_ctrl, LOW);
  analogWrite(right_pwm, SPEED_TURN_FAST);
}

void Stop()//define the state of stop
{
  digitalWrite(left_ctrl, LOW);
  analogWrite(left_pwm, 0);
  digitalWrite(right_ctrl, LOW);
  analogWrite(right_pwm, 0);
}