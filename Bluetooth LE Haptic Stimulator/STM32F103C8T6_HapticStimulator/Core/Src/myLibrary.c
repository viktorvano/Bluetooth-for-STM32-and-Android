/*
 * myLibrary.c
 *
 *  Created on: Oct 6, 2021
 *      Author: vikto
 */


#include "myLibrary.h"

uint8_t value = 0;
uint8_t buffer[50];
uint8_t timeout = 0, buffer_index = 0, messageHandlerFlag = 0;

uint8_t string_compare(char array1[], char array2[], uint16_t length)
{
	 uint16_t comVAR=0, i;
	 for(i=0;i<length;i++)
	   	{
	   		  if(array1[i]==array2[i])
	   	  		  comVAR++;
	   	  	  else comVAR=0;
	   	}
	 if (comVAR==length)
		 	return 1;
	 else 	return 0;
}

int string_contains(char bufferArray[], char searchedString[], uint16_t length)
{
	uint8_t result=0;
	for(uint16_t i=0; i<length; i++)
	{
		result = string_compare(&bufferArray[i], &searchedString[0], strlen(searchedString));
		if(result == 1)
			return i;
	}
	return -1;
}

void messageHandler()
{
	__HAL_UART_DISABLE_IT(&huart1, UART_IT_RXNE);
	int position = 0;
	if((position = string_contains((char*)buffer, "value:", buffer_index)) != -1)
	{
		value = (uint8_t)atoi((char*)&buffer[position]);
	}
	clear_Buffer();
	__HAL_UART_ENABLE_IT(&huart1, UART_IT_RXNE);
}

void clear_Buffer()
{
	memset(buffer, 0, 2000);
	buffer_index = 0;
	HAL_GPIO_WritePin(LED_GPIO_Port, LED_Pin, GPIO_PIN_SET);
}
