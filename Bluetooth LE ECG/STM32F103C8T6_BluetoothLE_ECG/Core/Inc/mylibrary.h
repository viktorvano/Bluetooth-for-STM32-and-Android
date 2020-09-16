/*
 * mylibrary.h
 *
 *  Created on: Feb 7, 2020
 *      Author: Viktor Vano
 */

#ifndef INC_MYLIBRARY_H_
#define INC_MYLIBRARY_H_

#include "main.h"
#include <string.h>
#include <stdlib.h>

extern uint8_t stream_index, array_index, send_flag;
extern uint32_t ADC_value;
extern UART_HandleTypeDef huart2;
extern uint16_t ADC_Values[4][64];
extern ADC_HandleTypeDef hadc1;

#endif /* INC_MYLIBRARY_H_ */
