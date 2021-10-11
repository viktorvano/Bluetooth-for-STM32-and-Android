/*
 * myLibrary.h
 *
 *  Created on: Oct 6, 2021
 *      Author: vikto
 */

#ifndef INC_MYLIBRARY_H_
#define INC_MYLIBRARY_H_

#include "main.h"
#include <stdio.h>
#include <string.h>
#include <stdlib.h>

extern TIM_HandleTypeDef htim2;
extern UART_HandleTypeDef huart1;

extern uint8_t value;
extern uint8_t buffer[50];
extern uint8_t timeout, buffer_index, messageHandlerFlag;

uint8_t string_compare(char array1[], char array2[], uint16_t length);
int string_contains(char bufferArray[], char searchedString[], uint16_t length);
void messageHandler();
void clear_Buffer();

#endif /* INC_MYLIBRARY_H_ */
