/*
 * mylibrary.c
 *
 *  Created on: Feb 7, 2020
 *      Author: Viktor Vano
 */
#include "mylibrary.h"

uint8_t stream_index = 0, array_index = 0, send_flag = 0;
uint32_t ADC_value;
uint16_t ADC_Values[4][64];
