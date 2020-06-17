# Communication Protocol

## Android => Server

### Socket Connection

* Socket Connection
  * /ID:ANDROIDtest@kim.com IN
* Socket disconnection
  * /ID:ANDROIDtest@kim.com OUT

### Data

* Window
  * /ANDROID>/WINDOW ON
  * /ANDROID>/WINDOW OFF
* Airpurifier
  * /ANDROID>/AIRPURIFIER ON
  * /ANDROID>/AIRPURIFIER OFF
* Airconditioner
  * /ANDROID>/AIRCONDITIONER ON
  * /ANDROID>/AIRCONDITIONER OFF

## Server => Android

* JSON Object

## Server => Latte

* ON
* OFF

## Latte => Server

### Sensor Data

#### Air Purifier (AP)
* /ID:AIRPURIFIER IN
* /AIRPURIFIER
* /airpurifierStatus : (1 : on, 0 : off)
* /dust25 :
* /dust10 :
* /gasStatus : (ppm)
* ex) /AIRPURIFIER /airpurifierStatus:1 /dust25:4 /dust10:10 /gasStatus:554
#### Air Conditioner(AC)
* /ID:AIRCONDITIONER IN
* /AIRCONDITIONER
* /airconditionerStatus: (1 : on, 0 : off)
* /temp :
* /airconditionerMode: (COLD:1, DRY:2)
* /airconditionerTemp: 
* /airconditionerSpeed: (speed : 1, 2, 3)
* ex) /AIRCONDITIONER /airconditionerStatus:1 /temp:25 /airconditionerMode:1 /airconditionerTemp: 25 /airconditionerSpeed:3
#### Windows (WD)
* /ID:WINDOW IN
* /WINDOW
* /windowStatus: (1 : on, 0 : off)
ex) /WINDOW /windowStatus:1

## Data Share

### Sensor Data Send Cycle

* Latte Panda => Server
  * 10s

### Server => Android

* Every time sensor data receive

## Server => Matlab
* /FACE
## Matlab => Server
### 얼굴 찾음
* /FACE 0.9854 0.045 0.345 0.345
* ex) 0.9854 0.045 0.345 0.345
* %로 출력
### 얼굴 못 찾음
* /FACE NULL





# MODE

### SMART

* 공기기청정기 작동
* 에어컨 설정온도보다 실내온도가 높을 경우 에어컨 작동
* 실내 pm10이 35 이상일 경우 창문 열기
  * 비, 눈, 안개가 낀 경우 열지 않기
  * 외부 pm10이 81이상인 경우 열지 않기
  * 창문이 열릴 경우 공기청정기 끄기
  * 창문 연 후에 10후에 자동으로 닫히기

* setMode("SMART")

### SLEEP

* 불 점등

* setMode(SLEEP)
* WINDOW LIGHTOFF

### VENTILATION

* 창문 열기, 공기청정기 끄기, 에어컨 끄기
* setMode(VENTILATION)
* WINDOW ON
* AIRPURIFIER OFF
* AIRCONDITIONER OFF

### OUTING

* 에어컨 끄기, 불 점등
* AIRCONDITIONER OFF
* WINDOW LIGHTOFF






