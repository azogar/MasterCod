#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "dev/sky-sensors.h"
#include "contiki.h"
#include "contiki-net.h"	//aggiunto

/* Specifici del sensore */
#include "dev/light-ziglet.h"
#include "relay-phidget.c"
#include "sound-sensor.c"

//Librerie per il protocollo COAP
#include "erbium.h"
#include "er-coap-13.h"

static int valLight;
static int valSound;


PROCESS(nodo1_rest_server, "Nodo1 REST server");
AUTOSTART_PROCESSES(&nodo1_rest_server);

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//DEFINIZIONE DELLE RISORSE CHE SONO ESPOSTE DAL SERVER

//Risorsa osservabile periodica con periodo fisso predefinito (acquisizione del movimento)
PERIODIC_RESOURCE(light, METHOD_GET, "sensors/periodic_light", "title=\"Periodic_light\"; Observable resource", 3 * CLOCK_SECOND);

//Risorsa osservabile periodica con periodo fisso predefinito (acquisizione del suono)
PERIODIC_RESOURCE(sound, METHOD_GET, "sensors/periodic_sound", "title=\"Periodic_sound\"; Observable resource", 3 * CLOCK_SECOND);

//Risorsa semplice (gestione della ventola): PUT che necessita del colore passato attraverso la query e il mode come attributo(?)
RESOURCE(fan, METHOD_PUT, "actuators/fan", "title=\"Fan, PUT mode=on|off\"");
//RESOURCE(fan, METHOD_PUT, "actuators/fan", "title=\"Fan:?color=r|g|b, PUT mode=on|off\"");


/* RISORSA MOVIMENTO*/
//Risorsa osservabile periodica con periodo fisso predefinito (acquisizione del movimento) per una richiesta GET semplice
/* 
 * standard handler 
 * */

void
light_handler(void* request, void* response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset)
{
	printf("Parsing SIMPLE GET on LIGHT resource:\n");
	valLight = light_ziglet_read();
	static char content[100];
	snprintf(content, sizeof(content), "{\"sample\":[{\"type\":\"periodic_light\",\"value\":%d}]}", valLight);
	const char *msg = content;

  	REST.set_header_content_type(response, REST.type.TEXT_PLAIN); // CAMBIARE IL TYPE IN JSON (o SenML?)
  	REST.set_response_payload(response, (uint8_t *)msg, strlen(msg));
}
	

//Risorsa osservabile periodica con periodo fisso predefinito (acquisizione del movimento) per una richiesta GET OBSERVING
/*
 * observe handler 
 * */
 
void
light_periodic_handler(resource_t *r)
{
	//printf("Parsing GET OBSERVING on PERIODIC TEMPERATURE resouce\n");
	static char content[100];
    	printf("Parsing OBSERVING on PERIODIC LIGHT resource:\n");
	valLight = light_ziglet_read();
	
  	//Costruisco la notifica per inviare il messaggio di notifica ai nodi registrati per questa risorsa
  	coap_packet_t notification[1]; //This way the packet can be treated as pointer as usual. */
  	coap_init_message(notification, COAP_TYPE_CON, REST.status.OK, 0);
  	coap_set_payload(notification, content, snprintf(content, sizeof(content), "{\"sample\":[{\"type\":\"periodic_light\",\"value\":%d}]}", valLight));

	//Notify the registered observers with the given message type, observe option, and payload
	// Manda la notifica a tutti i client CoAP che hanno sottoscritto questa risorsa
  	REST.notify_subscribers(r, valLight, notification);
	
}

/*RISORSA SUONO*/
//Risorsa osservabile periodica con periodo fisso predefinito (acquisizione del suono) per una richiesta GET semplice
/* 
 * standard handler 
 * */

void
sound_handler(void* request, void* response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset)
{
	printf("Parsing SIMPLE GET on SOUND resource:\n");
	valSound = sound_sensor.value(0);;
	static char content[100];
	snprintf(content, sizeof(content), "{\"sample\":[{\"type\":\"periodic_sound\",\"value\":%d}]}", valSound);
	const char *msg = content;

  	REST.set_header_content_type(response, REST.type.TEXT_PLAIN); // CAMBIARE IL TYPE IN JSON (o SenML?)
  	REST.set_response_payload(response, (uint8_t *)msg, strlen(msg));
}
	

//Risorsa osservabile periodica con periodo fisso predefinito (acquisizione del suono) per una richiesta GET OBSERVING
/*
 * observe handler 
 * */
 
void
sound_periodic_handler(resource_t *r)
{
	//printf("Parsing GET OBSERVING on PERIODIC TEMPERATURE resouce\n");
	static char content[100];
    	printf("Parsing OBSERVING on PERIODIC SOUND resource:\n");
	valSound = sound_sensor.value(0);
	
  	//Costruisco la notifica per inviare il messaggio di notifica ai nodi registrati per questa risorsa
  	coap_packet_t notification[1]; //This way the packet can be treated as pointer as usual. */
  	coap_init_message(notification, COAP_TYPE_CON, REST.status.OK, 0);
  	coap_set_payload(notification, content, snprintf(content, sizeof(content), "{\"sample\":[{\"type\":\"periodic_sound\",\"value\":%d}]}", valSound));

	//Notify the registered observers with the given message type, observe option, and payload
	// Manda la notifica a tutti i client CoAP che hanno sottoscritto questa risorsa
  	REST.notify_subscribers(r, valSound, notification);
	
}

/*RISORSA VENTOLA*/
//Risorsa semplice (gestione della ventola)
void
fan_handler(void* request, void* response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset)
{
	REST.set_header_content_type(response, REST.type.TEXT_PLAIN);
  	const char *msg = "Command fan received";
  	REST.set_response_payload(response, (uint8_t *)msg, strlen(msg));

	printf("Parsing PUT on FAN resource\n");
  	size_t len = 0;
  	const char *mode = NULL;
  	int success = 1;
	
	//Parsing della variable post (e' scritta in outgoing in Copper)
	if (success && (len=REST.get_post_variable(request, "mode", &mode))) 
	{
    		//printf("mode %s\n", mode);
    		if (strncmp(mode, "on", len) == 0)
				relay_on();
		else 
			if (strncmp(mode, "off", len) == 0)
      				relay_off();
			else
      				success = 0;
	}	
	else
		success = 0;

  	if (!success)
		REST.set_response_status(response, REST.status.BAD_REQUEST);
}


PROCESS_THREAD(nodo1_rest_server, ev, data)
{
  static struct etimer et;
  
  PROCESS_BEGIN();
  
  printf("Starting Nodo1 REST server\n");

  	/* Initialize the REST engine. */
  	rest_init_engine();
	light_ziglet_init();

	relay_enable(7); //il relay è collegato alla ventola (fan)
	
	//Attivo le risorse che voglio esporre 	
  	rest_activate_periodic_resource(&periodic_resource_light);
  	rest_activate_periodic_resource(&periodic_resource_sound);
	rest_activate_resource(&resource_fan);

  etimer_set(&et, CLOCK_SECOND);

  SENSORS_ACTIVATE(sound_sensor);


  while(1) {
    PROCESS_WAIT_EVENT_UNTIL(ev == PROCESS_EVENT_TIMER);

/* 
 * commentato perchè spostato in readTemperature()
 * 
        val = temperature_sensor.value(0); 	// valore del sensore letto dal registro
      // uso la funzione di trasferimento per ottenere i gradi (attenzione a Vref e Avss che sono selezionate con S_REF1. vedi registro micro
      
      // N.B. uso 4095 e non 4096 come a volte si vede nei datasheet perchè voglio che al valore max del registro (4095) devo dividere per un valore che mi dia 1
      // in modo da moltiplicare dopo per il Vref ed avere il valore in Volt
      //
      temp_volt=val*2.5/4095.0;				

		//stampo i valori per verificare
	  printf("val %d volt %d.%d\n", val, (int)(temp_volt), (int)((temp_volt -(int)(temp_volt))*100));	// valore del registro e valore in Volt
      printf("%d.%d\n",(int)((temp_volt-0.986)/0.00355),(int)((temp_volt-0.986)/0.00355*100));			// valore in gradi
*/

/* 
    	//Stampo a video il valore di temperatura che ho acquisito
    	//struct struct_temp tempValue = readTemperature();
    	//printf("Temperature = %c%d.%04d\n", tempValue.minus, tempValue.tempint, tempValue.tempfrac);
*/
      etimer_reset(&et);
    
  }

  PROCESS_END();
}


