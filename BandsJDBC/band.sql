/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * Author:  Tomáš
 * Created: 12.4.2016
 */

CREATE TABLE band (
	id PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
	name VARCHAR(40),
	styles VARCHAR(50),
	region INT,
	pricePerHour Double,
	rate Double
);

CREATE TABLE customer (
	id PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
	name VARCHAR(50),
	phoneNumber VARCHAR(20),
	adress VARCHAR(50)
);

CREATE TABLE order (
	id PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
	idBand BIGINT REFERENCES band(id),
	idCustomer BIGINT REFERENCES customer(id),
	date VARCHAR(30)      
	region VARCHAR(30)   
	duration INT
);

CREATE TABLE band_styles (
	id PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
	idBand BIGINT REFERENCES band(id),
	style INT
);