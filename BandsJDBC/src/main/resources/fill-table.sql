INSERT INTO BAND (name, region, pricePerHour, rate) VALUES('Eufory', 3, 200000.00, 7.4);
INSERT INTO band_styles (idBand, style) VALUES((SELECT id FROM BAND WHERE name = 'Eufory'), 3), ((SELECT id FROM BAND WHERE name = 'Eufory'), 2), ((SELECT id FROM BAND WHERE name = 'Eufory'), 4);

INSERT INTO BAND (name, region, pricePerHour, rate) VALUES('Kabat', 4, 600000.00, 6.4);
INSERT INTO band_styles (idBand, style) VALUES((SELECT id FROM BAND WHERE name = 'Kabat'), 3);

INSERT INTO BAND (name, region, pricePerHour, rate) VALUES('Rybicky 48', 6, 900000.00, 1.4);
INSERT INTO band_styles (idBand, style) VALUES((SELECT id FROM BAND WHERE name = 'Rybicky 48'), 3), ((SELECT id FROM BAND WHERE name = 'Rybicky 48'), 2), ((SELECT id FROM BAND WHERE name = 'Rybicky 48'), 4);

INSERT INTO BAND (name, region, pricePerHour, rate) VALUES('Slza', 7, 100000.00, 0.4);
INSERT INTO band_styles (idBand, style) VALUES((SELECT id FROM BAND WHERE name = 'Slza'), 8), ((SELECT id FROM BAND WHERE name = 'Slza'), 2), ((SELECT id FROM BAND WHERE name = 'Slza'), 4);

INSERT INTO BAND (name, region, pricePerHour, rate) VALUES('Five Live', 1, 650000.00, 0.1);
INSERT INTO band_styles (idBand, style) VALUES((SELECT id FROM BAND WHERE name = 'Five Live'), 7), ((SELECT id FROM BAND WHERE name = 'Five Live'), 2), ((SELECT id FROM BAND WHERE name = 'Five Live'), 4);

INSERT INTO BAND (name, region, pricePerHour, rate) VALUES('Jelen', 6, 655000.00, 2.1);
INSERT INTO band_styles (idBand, style) VALUES((SELECT id FROM BAND WHERE name = 'Jelen'), 3), ((SELECT id FROM BAND WHERE name = 'Jelen'), 2), ((SELECT id FROM BAND WHERE name = 'Jelen'), 5); 

INSERT INTO BAND (name, region, pricePerHour, rate) VALUES('Helena Vondrackova', 5, 655000.00, 0.2);
INSERT INTO band_styles (idBand, style) VALUES((SELECT id FROM BAND WHERE name = 'Helena Vondrackova'), 7), ((SELECT id FROM BAND WHERE name = 'Helena Vondrackova'), 2), ((SELECT id FROM BAND WHERE name = 'Helena Vondrackova'), 8);



INSERT INTO customer (name, phoneNumber, aDdress) VALUES('Ondrej Hnedy','+421 922 222 222', 'Razusova 8');
INSERT INTO customer (name, phoneNumber, aDdress) VALUES('Petr Zeleny','+420 922 354 284', 'Hodzova 4');
INSERT INTO customer (name, phoneNumber, aDdress) VALUES('Filip Maly','+421 922 448 336', 'Bohradova 12');
INSERT INTO customer (name, phoneNumber, aDdress) VALUES('Maho Nasaku','+666 922 448 336', 'Jou 12');
INSERT INTO customer (name, phoneNumber, aDdress) VALUES('Pedro De La Vedro','+366 922 448 336', 'Jeu 12');
INSERT INTO customer (name, phoneNumber, aDdress) VALUES('Fucimu Nani','+466 922 448 336', 'Juuu 12');
INSERT INTO customer (name, phoneNumber, aDdress) VALUES('Chaldoryny Kapaloto','+000 922 448 336', 'Jau 12');
INSERT INTO customer (name, phoneNumber, aDdress) VALUES('Zdislav Uzdichcal','+123 321 123 123', 'Aau 2');

INSERT INTO lease (idBand, idCustomer, date, region, duration) VALUES(1, 3, TIMESTAMP('2016-08-08 20:00:00') , 5, 8);
INSERT INTO lease (idBand, idCustomer, date, region, duration) VALUES(4, 5, TIMESTAMP('2016-05-06 20:00:00'), 4, 7);
INSERT INTO lease (idBand, idCustomer, date, region, duration) VALUES(1, 2, TIMESTAMP('2016-09-08 20:00:00'), 3, 6);
INSERT INTO lease (idBand, idCustomer, date, region, duration) VALUES(2, 2, TIMESTAMP('2016-07-11 20:00:00'), 2, 1);
INSERT INTO lease (idBand, idCustomer, date, region, duration) VALUES(3, 4, TIMESTAMP('2016-11-01 20:00:00'), 2, 4);
INSERT INTO lease (idBand, idCustomer, date, region, duration) VALUES(1, 2, TIMESTAMP('2016-04-06 20:00:00'), 1, 10);
