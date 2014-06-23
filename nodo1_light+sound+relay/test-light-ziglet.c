/*
 * This file is part of the Contiki operating system.
 *
 */

/**
 * \file
 *         A quick program for testing the light ziglet driver in the Z1 platform
 * \author
 *         Antonio Lignan <alinan@zolertia.com>
 */

#include <stdio.h>
#include "contiki.h"
#include "dev/i2cmaster.h"

/* Specifici del sensore */
#include "dev/light-ziglet.h"

#if 1
#define PRINTF(...) printf(__VA_ARGS__)
#else
#define PRINTF(...)
#endif


#if 0
#define PRINTFDEBUG(...) printf(__VA_ARGS__)
#else
#define PRINTFDEBUG(...)
#endif


#define SENSOR_READ_INTERVAL (CLOCK_SECOND)

PROCESS(test_process, "Test light ziglet process");
AUTOSTART_PROCESSES(&test_process);
/*---------------------------------------------------------------------------*/
static struct etimer et;

PROCESS_THREAD(test_process, ev, data)
{
  PROCESS_BEGIN();

  uint16_t light;

  light_ziglet_init();

  while(1) {
    etimer_set(&et, SENSOR_READ_INTERVAL);
    PROCESS_WAIT_EVENT_UNTIL(etimer_expired(&et));

    light = light_ziglet_read();
    PRINTF("Light = %u\n", light);
  }
  PROCESS_END();
}
