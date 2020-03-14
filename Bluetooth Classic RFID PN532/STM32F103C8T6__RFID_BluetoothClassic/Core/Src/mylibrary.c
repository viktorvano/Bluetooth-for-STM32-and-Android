/*
 * mylibrary.c
 *
 *  Created on: Feb 7, 2020
 *      Author: Viktor Vano
 */
#include "mylibrary.h"

char buffer[50];
uint8_t timer_count = 0, buffer_index = 0;

uint8_t string_compare(char array1[], char array2[], uint16_t length)
{
	 uint8_t comVAR=0, i;
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

void Message_handler()
{
	if(string_compare(buffer, "LED ON", strlen("LED ON")))
	{
		HAL_GPIO_WritePin(LED_GPIO_Port, LED_Pin, RESET);
		HAL_UART_Transmit(&huart2, (uint8_t*)"LED is ON.\n", strlen("LED is ON.\n"), 500);
	}else
	if(string_compare(buffer, "LED OFF", strlen("LED OFF")))
	{
		HAL_GPIO_WritePin(LED_GPIO_Port, LED_Pin, SET);
		HAL_UART_Transmit(&huart2, (uint8_t*)"LED is OFF.\n", strlen("LED is OFF.\n"), 500);
	}else
	{
		strcat(buffer, "\n");
		HAL_UART_Transmit(&huart2, (uint8_t*)buffer, strlen(buffer), 500);
	}

	memset(buffer, 0, sizeof(buffer));
	buffer_index = 0;
	timer_count = 0;
}
