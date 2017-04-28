//define variables for rpm
volatile byte rev;
unsigned int rpm=150;
unsigned int timeold;

//define variables to be used for rest of the code
int num_spokes= 102;
int p= num_spokes/ 3;
int d_time;
int num_led= 13;
int light[102][13];
int q=0;

//define the pins where leds are attached
int pcb1[13]={51,49,47,45,43,41,53,29,31,33,39,37,35};
int pcb2[13]= {2,16,13,12,11,10,3,4,5,6,9,8,7};
int pcb3[13]= {52,A7,A6,A5,A4,A3,50,48,46,44,A2,A1,A0};

//declare variables for bluetooth

int i_bl= 0, i_bl_count= 0, ind1, ind2, check= 1;

// take some other variable

int i;

void setup() {

  //write serial commands for bluetooth communication
  Serial3.begin(9600);
  Serial.begin(9600);
  
  // define attachInterrupt pin for rpm measurement
  attachInterrupt(digitalPinToInterrupt(19), rpm_fn, RISING); //do this for all three sensors and update the same variable using same function
  attachInterrupt(digitalPinToInterrupt(20), rpm_fn, RISING);
  attachInterrupt(digitalPinToInterrupt(21), rpm_fn, RISING);
  
  //define intial values
  rev= 0;
  timeold= 0;

  //set pins of pcb to output
  for(i= 0; i< num_led; i++){
    pinMode(pcb1[i], OUTPUT);
    pinMode(pcb2[i], OUTPUT);
    pinMode(pcb3[i], OUTPUT);
  }
}

void loop() {

  //recieve complete array from blueooth
  if(i_bl< 1326){
    bluetooth_read();
  }
  //if array is recieved, change counter to indicate that whole array has been received
  else{
    i_bl= 0;
    i_bl_count= 1500;
  }

  //code to glow LEDs
  if(i_bl_count== 1500){

    //below code is for one complete rpm
    for(int i= 0; i< num_spokes; i++){
      
      //this updates rpm
      if(rev>= 1){
        rpm= (60*1000)/ (millis()- timeold);
        rpm= rpm*rev;
        rpm= rpm/3;
        if(rpm>150){
          rpm=150;
        }
        // update old time
        timeold= millis();
        rev= 0;
      }
      
      //set the time delay for an arc
      d_time= (60*1000)/(rpm*num_spokes);

      //glow LEDs as per need
     for(int j= 0; j< 13; j++){
      if(light[i][j]== 1) digitalWrite(pcb1[j], HIGH);
      else digitalWrite(pcb1[j], LOW);
      
      if(light[(p+i)%num_spokes][j]== 1) digitalWrite(pcb2[j], HIGH);
      else digitalWrite(pcb2[j], LOW);
      
      if(light[((2*p)+ i)%num_spokes][j]== 1) digitalWrite(pcb3[j], HIGH);
      else digitalWrite(pcb3[j], LOW);
     }
     delay(d_time);
    }    
  }
}


//this updates rpm counter
void rpm_fn(){
  rev++;
}

//this recieve data from blutooth
void bluetooth_read(){
  while(Serial3.available() > 0) 
    {
      if(check== 0){}
      else{
        ind1= i_bl/13;
        ind2= i_bl%13;
        if(i_bl< 1326){
          light[ind1][ind2]= Serial3.read()-'0';  
        }

        //update counters to check how many entries have been recieved
        i_bl++;
        i_bl_count= i_bl;
      }
      check= 1;  
     }
}

