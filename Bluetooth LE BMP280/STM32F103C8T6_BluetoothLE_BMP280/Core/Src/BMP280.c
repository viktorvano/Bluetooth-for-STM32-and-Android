/*
 * BMP280.c
 *
 *  Created on: 28. 10. 2017
 *      Author: CyberPunkTECH
 */

#include "BMP280.h"

signed long temperature_raw, pressure_raw;
unsigned short dig_T1, dig_P1;
signed short dig_T2, dig_T3, dig_P2, dig_P3, dig_P4, dig_P5, dig_P6, dig_P7, dig_P8, dig_P9;
float temperature, pressure, altitude, init_height;

uint8_t I2C_Read_Register(uint8_t device_adr, uint8_t internal_adr)
{
	uint8_t tx_buff[1];
	uint8_t rx_buff[1];

	tx_buff[0] = internal_adr;

	HAL_I2C_Master_Transmit(&hi2c1, device_adr, &tx_buff[0], 1, 10000);
	HAL_I2C_Master_Receive(&hi2c1, device_adr + 1, &rx_buff[0], 1, 10000);


	return rx_buff[0];
}

void I2C_Write_Register(uint8_t device_adr, uint8_t internal_adr, uint8_t data)
{
	uint8_t tx_buff[2];

	tx_buff[0] = internal_adr;
	tx_buff[1] = data;

	HAL_I2C_Master_Transmit(&hi2c1, device_adr, tx_buff, 2, 10000);
}

void BMP280_get_calib_values(void)
{
	uint8_t rx_buff[24], starting_address=0x88;

	HAL_I2C_Master_Transmit(&hi2c1, BMP280_dev_address, &starting_address, 1, 10000);
	HAL_I2C_Master_Receive(&hi2c1, BMP280_dev_address + 1, &rx_buff[0], 24, 10000);

	dig_T1=(rx_buff[0])+(rx_buff[1]<<8);
	dig_T2=(rx_buff[2])+(rx_buff[3]<<8);
	dig_T3=(rx_buff[4])+(rx_buff[5]<<8);
	dig_P1=(rx_buff[6])+(rx_buff[7]<<8);
	dig_P2=(rx_buff[8])+(rx_buff[9]<<8);
	dig_P3=(rx_buff[10])+(rx_buff[11]<<8);
	dig_P4=(rx_buff[12])+(rx_buff[13]<<8);
	dig_P5=(rx_buff[14])+(rx_buff[15]<<8);
	dig_P6=(rx_buff[16])+(rx_buff[17]<<8);
	dig_P7=(rx_buff[18])+(rx_buff[19]<<8);
	dig_P8=(rx_buff[20])+(rx_buff[21]<<8);
	dig_P9=(rx_buff[22])+(rx_buff[23]<<8);
}

void BMP280_init(uint8_t register_F4, uint8_t register_F5)
{
	I2C_Write_Register(BMP280_dev_address, 0xF4, register_F4);// osrs_t 010 x2, osrs_p 16 101, mode normal 11
	I2C_Write_Register(BMP280_dev_address, 0xF5, register_F5);// standby time 500ms 100, filter 16 100, SPI DIS 0

	BMP280_get_calib_values();
}

void BMP280_calc_values(void)
{
	uint8_t status, rx_buff[6], starting_address=0xF7;

	do
	{
		status=I2C_Read_Register(BMP280_dev_address, 0xF3);
	} while(((status&0b00001000)==8)||((status&0b00000001)==1));

	HAL_I2C_Master_Transmit(&hi2c1, BMP280_dev_address, &starting_address, 1, 10000);
	HAL_I2C_Master_Receive(&hi2c1, BMP280_dev_address + 1, &rx_buff[0], 6, 10000);

	volatile uint32_t temp[3];
	temp[2]=rx_buff[3];
	temp[1]=rx_buff[4];
	temp[0]=rx_buff[5];
	temperature_raw=(temp[2]<<12)+(temp[1]<<4)+(temp[0]>>4);

	temp[2]=rx_buff[0];
	temp[1]=rx_buff[1];
	temp[0]=rx_buff[2];
	pressure_raw=(temp[2]<<12)+(temp[1]<<4)+(temp[0]>>4);

	double var1, var2;
	var1=(((double)temperature_raw)/16384.0-((double)dig_T1)/1024.0)*((double)dig_T2);
	var2=((((double)temperature_raw)/131072.0-((double)dig_T1)/8192.0)*(((double)temperature_raw)/131072.0-((double)dig_T1)/8192.0))*((double)dig_T3);
	double t_fine = (int32_t)(var1+var2);
volatile	float T = (var1+var2)/5120.0;

	var1=((double)t_fine/2.0)-64000.0;
	var2=var1*var1*((double)dig_P6)/32768.0;
	var2=var2+var1*((double)dig_P5)*2.0;
	var2=(var2/4.0)+(((double)dig_P4)*65536.0);
	var1=(((double)dig_P3)*var1*var1/524288.0+((double)dig_P2)*var1)/524288.0;
	var1=(1.0+var1/32768.0)*((double)dig_P1);
volatile	double p=1048576.0-(double)pressure_raw;
	p=(p-(var2/4096.0))*6250.0/var1;
	var1=((double)dig_P9)*p*p/2147483648.0;
	var2=p*((double)dig_P8)/32768.0;
	p=p+(var1+var2+((double)dig_P7))/16.0;

	temperature=T;
	pressure=p;
	altitude=44330.0f*(1-powf(pressure/101325.0f,1.0f/5.255f));//altitude=((powf(101325.0/pressure, 1/5.257f)-1)*(temperature+273.15f))/0.0065f;
}
