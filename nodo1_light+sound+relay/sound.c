#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "dev/sky-sensors.h"
#include "contiki.h"

/* Specifici del sensore */
#include "sound-sensor.c"

static int val;

PROCESS(temperature_example, "Temperature Server");
AUTOSTART_PROCESSES(&temperature_example);

PROCESS_THREAD(temperature_example, ev, data)
{
  PROCESS_BEGIN();

  printf("Starting Temperature Example\n");

  static struct etimer et;
  etimer_set(&et, CLOCK_SECOND/2);

  SENSORS_ACTIVATE(sound_sensor);


  while(1) {
    PROCESS_WAIT_EVENT_UNTIL(ev == PROCESS_EVENT_TIMER);
      val = sound_sensor.value(0);
	
      printf("Actual val : %d\n", val);
 
      etimer_reset(&et);
    
  }

  PROCESS_END();
}
