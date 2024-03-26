
# Contigo

**Contigo** is a real-time monitoring system that, using Federated Learning (FL) techniques, is capable of predicting possible pathologies in individuals. Its objective is to serve as a complementary tool for healthcare staff, who must always validate the data and predictions made. 

Contigo is based on two fundamental parts: a local part consisting of end devices such as smartphones to collect and process data from individuals using FL algorithms to generate personalized models, and a cloud part responsible for aggregating these personalized models and generating an enriched global model.

Below, the main parts of Contigo are detailed.

## Neural network

A Neural Network was designed in Python through TensorFlow to process users' medical data on smartphones through the mobile application. The variables used are those identified in a public dataset widely used to predict heart attacks. From these variables, a selection has been made that corresponds to the aspects covered by Contigo. This selection is described below, by indicating the variables, the descriptions, and the types.

| Variable   | Description | Type |
| :--------- | :----------- | :-- |
|Age|Age of the person (>0)|Personal data|
|Sex|Sex of the person (0 = male; 1 = female)|Personal data|
|Trestbps|Blood pressure|Health data|
|FBS|Fasting Blood Sugar > 120 mg/dl (0 = no; 1 = yes)|Health data|
|Thalach|Maximum recorded heart rate level|Health data|
|Thal|Risk of pathology (0 = normal; 1 = defect fixed; 2 = reversible defect)|Health data|
|Target|The intended purpose of the measurement. the predicted value is based on the probability of a heart attack occurring: 0 <= 50\% diameter narrowing in any major vessel; 1 >= 50\% diameter narrowing in any major vessel.|Health data|
|Prediction|Accuracy of the prediction|FL inference|
|Timestamp|Date and time of the measurement|Info|



## Web platform

The web platform allows healthcare staff, upon registration, to monitor mobile application users in real-time, aggregating and displaying data on an interactive map to identify normal and abnormal behavior. To do this, searches can be performed based on parameters such as radius and minimum activity, with results grouped for easy viewing and heat maps highlighting potential anomalies.

### Requirements

The web platform for Contigo is developed with Spring MVC, Spring Security, Spring Data JPA, Maven, JSP, MySQL.

- JDK 1.8 or later
- Maven 3 or later
- MySQL 5.6 or later

### Installation
Install `contigo-java-web`. First, clone the repository contigo-java-web:

```bash
  git clone ...
```

Second, import the project as a Java Maven project into your favourite IDE.

Thrid, set the properties file: "src/main/resources/application.properties":

```
# Database
jdbc.driverClassName=com.mysql.jdbc.Driver
jdbc.url=jdbc:mysql://localhost:3306/accounts?useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false
jdbc.username=db_username
jdbc.password=db_password

#MQTT
mqtt.host.local=tcp://localhost:1883
mqtt.host.local.ssl=ssl://mqtt_server:8883
mqtt.host=tcp://mqtt_server:1883
mqtt.host.ssl=ssl://mqtt_server:8883
mqtt.username=mqtt_username
mqtt.password=mqtt_password
mqtt.topic=monact/device
mqtt.ssl=true	# Set true to use port 8883, else 1883
mqtt.aws=false	# For testing in AWS
mqtt.test=false	# Set true to use example data in the web platform

# Mail
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.protocol=smtp
spring.mail.username=your_email@gmail.com
spring.mail.password=your_access_key

# Recaptcha
recaptcha.secret=your_recaptcha_key

# General
app.version=CONTIGOFL | # The header for the debugger
```

The data above is just an example. Please, set your own data.

Forth, run the project in Tomcat. It can be accesed in the browser:

```bash
  http://localhost:8080/contigo
```

#### Google Recaptcha

Also, you must create your Google Recaptcha key. To do this, go to the following link:

```bash
  https://www.google.com/recaptcha/about/
```

You will have to create a new project to obtain the access key. Set this access key in the `recaptcha.secret`.

### Usage

#### Searching

In this interface, searches can be performed through different parameters, allowing users to be identified within an operational range using an interactive map

![App Screenshot](https://spilapps.unex.es/resources/Contigo_web-Leftbar-map-aux.png)

#### Data validation

Users' data must be manually validated by healthcare experts according to their criteria. This validation allows mobile devices to continue retraining the personalized model with new data to enrich the global model and obtain more precise predictions.

![App Screenshot](https://spilapps.unex.es/resources/Contigo_web-MedicalDataChart.png)

![App Screenshot](https://spilapps.unex.es/resources/Contigo_web-MedicalDataTable.png)


## Mobile application

The mobile application is responsible for the passive monitoring of the users' status. To do this, the application stores the time with the screen active (the user is actively using the phone), the time that the user is active without using the phone (sitting, standing up, walking, etc.), and the health parameters. These activities are identified using the accelerometer and gyroscope sensors. The sum of the times is called activity time, which will be used to determine whether a person is active. Personal information is also stored in the mobile application in case it is necessary to contact the user directly.

### Requirements

- Android 6.0 or later.

### Installation

The mobile application is currently developed only for Android (Android 6.0 or later). It could be downloaded at:

```bash
  https://spilapps.unex.es/resources/contigo.apk
```

If you need to set the MQTT parameters, please clone the repository `contigo-android-app` and change the needed parameters.

### Usage
The application is designed so that all processing is done in the background and no user intervention is required. The data remains on the device at all times to safeguard your privacy.

![App Screenshot](https://spilapps.unex.es/resources/Contigo_app.png)

#### Registration and preferences data

Once the application is installed, the first step is for the user to register with their information. From this moment on, the application will monitor their activity

#### Main screen

The main screen shows their activity in the last few hours.

## API Reference
A Python API has also been developed to interact with the model in the Cloud. This API contains the necessary endpoints to download the global model, upload the custom model, and retrieve the current version of the global model. The endpoints developed are the following:


- Get and post users files on the servers. These files correspond to the ckpt model and csv raw data about their profile. With the .ckpt files, the global model will be aggregated to be later downloaded.

```bash
  GET /data
```

| Parameter | Type     | Description                                               |
| :-------- | :------- | :-------------------------------------------------------- |
| `file`    | `string` | **Optional**. Get the file. Only .ckpt and .csv allowed   |


```bash
  POST /data
```

| Parameter | Type              | Description                                              |
| :-------- | :-----------------| :------------------------------------------------------- |
| `file`    | `string($binary)` | **Required**. Set the file. Only .ckpt and .csv allowed  |
| `user`    | `string`          | **Required**. Set the username/id                        |

- Get the aggregated model file (E.g.: global-model1.tflite -File-)

```bash
  GET /model
```

- Get the aggregated model filename (E.g.: global-model1.tflite -String-)

```bash
  GET /model-filename
```

### Installation
Clone the repository `contigo-api` and deploy it as a Python project. The file `main.py` contains the API methods while the `generate_fl_model.py` is in charge of aggregating the custom models generated in the smarphones. Set this file to be automatically triggered every day, month,...

## MQTT Configuration
Make sure you configure the MQTT server correctly in both the web platform and the mobile application. To do so, you will need these three parameters

 - Broker URL
 - Username
 - Password

For the mobile application, you must set this configuration in the following file (note that a secure connection is established through port 8883):

```bash
  contigo-android-app\app\src\main\java\com\spilab\monact\services\MQTTConfiguration.java
```

```bash
  public class MQTTConfiguration {
    public static String MQTT_BROKER_URL = "ssl://mqtt_server:8883";
    public static String USER = "mqtt_username";
    public static String PASSWORD = "mqtt_password";
}
```

For the web platform, you should have already set this configuration in the `application.properties` file.

## SSL
Contigo makes use of SSL certificates to secure communication on both the web platform and the mobile application. To properly run Contigo, you must place your certificate in the following paths. Note that the format of the certificate varies from the web platform to the mobile application even though it is the same certificate:

- Web platform
```bash
  contigo-java-web/src/main/resources/chain.pem
```
 - Mobile application:
```bash
  contigo-android-app/app/src/main/res/raw/spilapps.crt
```

Also note that your server where you deploy the web application must also have the certificate installed. In our case, Apache is in charge of managing the web platform and pointing to the domain name.

## Demo

A full video of Contigo is available [here](https://www.youtube.com/watch?v=qd6SOXiWiDU).

![App Screenshot](https://spilapps.unex.es/resources/Contigo_Gif_compressed.gif)

In addition, you can directly test the platform on our server. You will need to request prior access through the form:

```bash
  https://spilapps.unex.es/contigo
```

## Authors

- Daniel Flores-Martin: dfloresm@unex.es
- Sergio Laso: slasom@unex.es
- Javier Berrocal: jberolm@unex.es

