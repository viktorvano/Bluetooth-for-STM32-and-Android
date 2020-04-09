/*
 * mylibrary.c
 *
 *  Created on: Feb 7, 2020
 *      Author: Viktor Vano
 */
#include "mylibrary.h"

char buffer[50];
uint8_t timer_count = 0, buffer_index = 0;

// reverses a string 'str' of length 'len'
void reverse(char *str, int len)
{
	int i = 0, j = len - 1, temp;
	while (i < j)
	{
		temp = str[i];
		str[i] = str[j];
		str[j] = temp;
		i++; j--;
	}
}

// Converts a given integer x to string str[].  d is the number
// of digits required in output. If d is more than the number
// of digits in x, then 0s are added at the beginning.
int intToStr(int x, char str[], int d)
{
	int i = 0;
	while (x)
	{
		str[i++] = (x % 10) + '0';
		x = x / 10;
	}

	// If number of digits required is more, then
	// add 0s at the beginning
	while (i < d)
		str[i++] = '0';

	reverse(str, i);
	str[i] = '\0';
	return i;
}

// Converts a floating point number to string.
void ftoa(float n, char *res, int afterpoint)
{
	unsigned char minus_flag = 0;
	if (n < 0)
	{
		minus_flag = 1;
		n = -n;
	}

	// Extract integer part
	int ipart = (int)n;

	// Extract floating part
	float fpart = n - (float)ipart;

	// convert integer part to string
	int i = intToStr(ipart, res, 0);

	// check for display option after point
	if (afterpoint != 0)
	{
		res[i] = '.';  // add dot

					   // Get the value of fraction part upto given no.
					   // of points after dot. The third parameter is needed
					   // to handle cases like 233.007
		fpart = fpart * pow(10, afterpoint);

		intToStr((int)fpart, res + i + 1, afterpoint);
	}

	char string[30];
	if (minus_flag == 1)
	{
		memset(string, 0, 30);
		string[0] = '-';
		if (n < 1.0f)
		{
			string[1] = '0';
			strcpy(&string[2], res);
		}
		else
			strcpy(&string[1], res);

		memset(res, 0, strlen(res));
		strcpy(res, string);
	}
	else
		if (n < 1.0f)
		{
			string[0] = '0';
			strcpy(&string[1], res);
			memset(res, 0, strlen(res));
			strcpy(res, string);
		}

}

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
	if(string_compare(buffer, "get", strlen("get")))
	{
		HAL_GPIO_WritePin(LED_GPIO_Port, LED_Pin, RESET);
		BMP280_calc_values();
		char string[50];
		memset(string, 0, sizeof(string));
		ftoa(temperature, &string[0], 3);
		strcat(string, ",");
		ftoa(pressure, &string[strlen(string)], 3);
		strcat(string, ",");
		ftoa(altitude, &string[strlen(string)], 3);
		strcat(string, "\n");
		HAL_UART_Transmit(&huart2, (uint8_t*)string, strlen(string), 500);
		HAL_GPIO_WritePin(LED_GPIO_Port, LED_Pin, SET);
	}

	memset(buffer, 0, sizeof(buffer));
	buffer_index = 0;
	timer_count = 0;
}
