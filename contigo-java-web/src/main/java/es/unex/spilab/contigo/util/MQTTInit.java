package es.unex.spilab.contigo.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONObject;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.model.LatLng;

import es.unex.spilab.contigo.model.MsgMQTT;
import es.unex.spilab.contigo.model.MsgMQTTAlert;
import es.unex.spilab.contigo.model.UserResponse;
import es.unex.spilab.contigo.service.UserService;

@Component
public class MQTTInit implements InitializingBean, DisposableBean {

	@Autowired
	private UserService userService;

	@Value("${mqtt.host.local}")
	private String mqtthostLocal;

	@Value("${mqtt.host.local.ssl}")
	private String mqtthostLocalSSL;

	@Value("${mqtt.host}")
	private String mqtthost;

	@Value("${mqtt.host.ssl}")
	private String mqtthostSSL;

	@Value("${mqtt.username}")
	private String mqttusername;

	@Value("${mqtt.password}")
	private String mqttpassword;

	@Value("${mqtt.topic}")
	private String mqtttopic;

	@Value("${mqtt.aws}")
	private Boolean mqttaws;

	@Value("${mqtt.ssl}")
	private Boolean mqttssl;

	@Value("${app.version}")
	private String appVersionAux;

	// Global responses
	public static HashMap<Integer, List<UserResponse>> globalActivities = new HashMap<Integer, List<UserResponse>>();

	// To check if connected to the broker and subscribed to the default topic
	public static boolean subscribed = false;

	// MQTT client
	public static MqttClient mqttClient;

	// Broker credentials
	private static String host;
	private static String username;
	private static String password;
	private static String topic;
	private static Boolean ssl;

	private static String appVersion;

	public static final Logger LOGGER = Logger.getLogger(MQTTInit.class.getName());
//	public static FileHandler fh;

	private void init() throws SecurityException, IOException {

		MQTTInit.appVersion = appVersionAux;

		if (!mqttaws) {
			if (mqttssl) {
				MQTTInit.host = mqtthostSSL;
			} else {
				MQTTInit.host = mqtthost;
			}
		} else {
			if (mqttssl) {
				MQTTInit.host = mqtthostLocalSSL;
			} else {
				MQTTInit.host = mqtthostLocal;
			}
		}

		System.out.println(MQTTInit.host);

		MQTTInit.username = mqttusername;
		MQTTInit.password = mqttpassword;
		MQTTInit.topic = mqtttopic;
		MQTTInit.ssl = mqttssl;

		MQTTInit.LOGGER.info(MQTTInit.appVersion + "--> STARTING MQTT (POSTCONSTRUCT) <--");
		connect();

		keepMysqlConnectionAlive();
	}

	private void keepMysqlConnectionAlive() {

		// long delay = 5000; // 5 seconds
		long delay = 18000000; // 5 hours

		Timer rotationTimer = new Timer(true);
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				MQTTInit.LOGGER.info(MQTTInit.appVersion + "--> KEEPING MYSQL SESSION ALIVE! <--");
				userService.findByUsername("user");
			}
		};
		rotationTimer.scheduleAtFixedRate(task, 1000, delay);

	}

	public static synchronized void connect() {
		if (!subscribed) {
			try {
				Resource chain = new ClassPathResource("chain.crt");
				File fChain = chain.getFile();

				String publisherId = MqttClient.generateClientId();
				mqttClient = new MqttClient(host, publisherId, new MemoryPersistence());

				final MqttConnectOptions options = new MqttConnectOptions();

				options.setUserName(username);
				options.setPassword(password.toCharArray());
				options.setAutomaticReconnect(true);
				options.setCleanSession(true);
				options.setConnectionTimeout(10);
				if (MQTTInit.ssl) {
					SocketFactory.SocketFactoryOptions socketFactoryOptions = new SocketFactory.SocketFactoryOptions();
					try {
						InputStream targetStream = new FileInputStream(fChain);
						socketFactoryOptions.withCaInputStream(targetStream);
						options.setSocketFactory(new SocketFactory(socketFactoryOptions));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				mqttClient.connect(options);
				mqttClient.setCallback(new MqttCallback() {
					@Override
					public void connectionLost(Throwable cause) {
						LOGGER.warning(MQTTInit.appVersion + "MQTT connection lost: " + cause.getMessage());
						LOGGER.warning(MQTTInit.appVersion + "MQTT reconnecting... ");
						subscribed = false;
						connect();
					}

					@Override
					public void deliveryComplete(IMqttDeliveryToken token) {
					}

					@Override
					public void messageArrived(String topic, MqttMessage message) throws Exception {
						// TODO Auto-generated method stub

					}

				});

				// System.out.println(" ** Conectado MQTT **");
				LOGGER.info(MQTTInit.appVersion + "MQTT connected");
				subscribe();

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				LOGGER.warning(MQTTInit.appVersion + "MQTT exception: " + e.getMessage());
				// e.printStackTrace();
				subscribed = false;
			}

		}
	}

	public static void publish(MsgMQTT msg) {
		if (subscribed) {

			if (!mqttClient.isConnected())
				connect();

			Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
			try {
//				if (!mqttClient.isConnected())
//					connect();
				mqttClient.publish(topic, gson.toJson(msg).getBytes(), 1, false);
				LOGGER.info(MQTTInit.appVersion + "MQTT publish: " + msg);
				// System.out.println("publico:" + msg);
				subscribed = true;
			} catch (MqttPersistenceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				LOGGER.warning(MQTTInit.appVersion + "MQTT exception: " + e.getMessage());
				// System.out.println("** Error al publicar: " + e.getMessage() + "**");
			} catch (MqttException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				LOGGER.warning(MQTTInit.appVersion + "MQTT exception: " + e.getMessage());
				// System.out.println("** Error al publicar: " + e.getMessage() + "**");
			}
		} else {
			connect();
		}
	}

	public static void publishAlert(MsgMQTTAlert msg, String uuid) {
		if (subscribed) {

			if (!mqttClient.isConnected())
				connect();

			Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
			try {
//				if (!mqttClient.isConnected())
//					connect();
				mqttClient.publish(uuid, gson.toJson(msg).getBytes(), 1, false);
				LOGGER.info(MQTTInit.appVersion + "MQTT publish: " + msg);
				// System.out.println("publico:" + msg);
				subscribed = true;
			} catch (MqttPersistenceException e) {
				// TODO Auto-generated catch block
				LOGGER.warning(MQTTInit.appVersion + "MQTT error publishing: " + e.getMessage());
				e.printStackTrace();
				// System.out.println("** Error al publicar: " + e.getMessage() + "**");
			} catch (MqttException e) {
				// TODO Auto-generated catch block
				LOGGER.warning(MQTTInit.appVersion + "MQTT error publishing: " + e.getMessage());
				// e.printStackTrace();
				// System.out.println("** Error al publicar: " + e.getMessage() + "**");
			}
		} else {
			connect();
		}
	}

	public static void publishValidateData(String msg, String uuid) {
		if (subscribed) {

			if (!mqttClient.isConnected())
				connect();

			//Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
			try {
//				if (!mqttClient.isConnected())
//					connect();
				mqttClient.publish(uuid, msg.getBytes(), 1, false);
				LOGGER.info(MQTTInit.appVersion + "MQTT publish: " + msg);
				// System.out.println("publico:" + msg);
				subscribed = true;
			} catch (MqttPersistenceException e) {
				// TODO Auto-generated catch block
				LOGGER.warning(MQTTInit.appVersion + "MQTT error publishing: " + e.getMessage());
				e.printStackTrace();
				// System.out.println("** Error al publicar: " + e.getMessage() + "**");
			} catch (MqttException e) {
				// TODO Auto-generated catch block
				LOGGER.warning(MQTTInit.appVersion + "MQTT error publishing: " + e.getMessage());
				// e.printStackTrace();
				// System.out.println("** Error al publicar: " + e.getMessage() + "**");
			}
		} else {
			connect();
		}
	}

	public static void subscribe() {
//		if (!subscribed) {
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
		try {

			if (!mqttClient.isConnected())
				connect();

			mqttClient.subscribe("monactweb", (topic, msg) -> {

				byte[] payload = msg.getPayload();

				JSONObject testV = new JSONObject(new String(payload));
				JSONObject loc = testV.getJSONObject("location");
				LatLng location = new LatLng(loc.getDouble("latitude"), loc.getDouble("longitude"));

				String str = new String(payload, StandardCharsets.UTF_8);

				LOGGER.info(MQTTInit.appVersion + "MQTT requested STR: " + str);

				UserResponse userResponse = gson.fromJson(str, UserResponse.class);
				userResponse.setLocation(location);

//				System.out.println("  |--> Leo mensaje ID req = " + userResponse.getIdRequest());
//				System.out.println("  |--> Payload: " + str);

				LOGGER.info(MQTTInit.appVersion + "MQTT requested: " + str);

				// Add the response to the array in the corresponding id request
				globalActivities.get(userResponse.getIdRequest()).add(userResponse);

			});
			subscribed = true;
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			LOGGER.warning(MQTTInit.appVersion + "MQTT error to suscribe/publish: " + e.getMessage());
			// System.out.println("** Error al suscribirse o recibir: " + e.getMessage() +
			// "**");
		}
//		} else {
//			connect();
//		}

	}

	public static synchronized int getSize() {
		return MQTTInit.globalActivities.size();
	}

	private void shutdown() {
		// TODO: destroy code
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		init();
	}

	@Override
	public void destroy() throws Exception {
		shutdown();
	}

}