-- default.sql
-- Dati iniziali per sviluppo e test

-- Membri di esempio
INSERT INTO member (name, surname, email, password_hash, city, birthday, has_license)
VALUES
    ('Mario',   'Rossi',       'mario.rossi@email.it',       'hashed_password_1',  'Firenze',   '1985-03-15', TRUE),
    ('Laura',   'Bianchi',     'laura.bianchi@email.it',     'hashed_password_2',  'Milano',    '1990-07-22', FALSE),
    ('Luca',    'Verdi',       'luca.verdi@email.it',        'hashed_password_3',  'Roma',      '1978-11-30', TRUE),
    ('Admin',   'NauticShare', 'admin@nauticshare.it',       'hashed_admin_pwd',   'Firenze',   '1980-01-01', TRUE),
    ('Elena',   'Neri',        'elena.neri@email.it',        'hashed_password_5',  'Genova',    '1992-04-10', TRUE),
    ('Giovanni','Russo',       'giovanni.russo@email.it',    'hashed_password_6',  'Napoli',    '1983-09-18', FALSE),
    ('Sofia',   'Esposito',    'sofia.esposito@email.it',    'hashed_password_7',  'Bari',      '1995-12-05', FALSE),
    ('Marco',   'Romano',      'marco.romano@email.it',      'hashed_password_8',  'Livorno',   '1988-02-28', TRUE),
    ('Francesca','Gallo',      'francesca.gallo@email.it',   'hashed_password_9',  'Venezia',   '1991-06-14', FALSE),
    ('Alessandro','Costa',     'alessandro.costa@email.it',   'hashed_password_10', 'Pisa',      '1975-08-20', TRUE),
    ('Giulia',  'Fontana',     'giulia.fontana@email.it',     'hashed_password_11', 'Firenze',   '1998-01-11', FALSE),
    ('Matteo',  'Conti',       'matteo.conti@email.it',       'hashed_password_12', 'Bologna',   '1987-05-03', TRUE),
    ('Chiara',  'Marini',      'chiara.marini@email.it',      'hashed_password_13', 'Rimini',    '1993-10-25', FALSE),
    ('Davide',  'Giordano',    'davide.giordano@email.it',    'hashed_password_14', 'Palermo',   '1982-11-12', TRUE),
    ('Sara',    'Rizzo',       'sara.rizzo@email.it',         'hashed_password_15', 'Cagliari',  '1996-03-30', FALSE),
    ('Andrea',  'Lombardi',    'andrea.lombardi@email.it',    'hashed_password_16', 'La Spezia', '1989-07-08', TRUE),
    ('Federica','Moretti',     'federica.moretti@email.it',   'hashed_password_17', 'Milano',    '1994-09-17', FALSE),
    ('Roberto', 'Barbieri',    'roberto.barbieri@email.it',   'hashed_password_18', 'Ancona',    '1979-12-01', TRUE),
    ('Valentina','Santoro',    'valentina.santoro@email.it',  'hashed_password_19', 'Trieste',   '1997-04-22', FALSE),
    ('Simone',  'Ricci',       'simone.ricci@email.it',       'hashed_password_20', 'Firenze',   '1990-08-05', TRUE);

-- Barche di esempio
INSERT INTO boat (reg_num, name, type, seats, photo_url, description)
VALUES
    ('IT-FI-001', 'Luna Rossa',     'SAILBOAT',  8,  'https://example.com/lunarossa.jpg', 'Barca a vela d''altura, perfetta per uscite giornaliere'),
    ('IT-FI-002', 'Poseidon',       'YACHT',     12, 'https://example.com/poseidon.jpg', 'Yacht di lusso con cabine e cucina a bordo'),
    ('IT-FI-003', 'Freccia Blu',    'SPEEDBOAT', 4,  'https://example.com/frecciablu.jpg', 'Motoscafo veloce per escursioni brevi'),
    ('IT-LI-004', 'Odissea',        'SAILBOAT',  10, 'https://example.com/odissea.jpg',   'Sloop classico con ampi spazi interni e pozzetto comodo'),
    ('IT-GE-005', 'Nautilus',       'YACHT',     14, 'https://example.com/nautilus.jpg',  'Yacht d''epoca restaurato con finiture in teak'),
    ('IT-SP-006', 'Orizzonte',      'SAILBOAT',  6,  'https://example.com/orizzonte.jpg', 'Barca agilissima ideale per regate e weekend'),
    ('IT-NA-007', 'Sirena',         'SPEEDBOAT', 6,  'https://example.com/sirena.jpg',    'Gozzo sorrentino a motore moderno con ampio prendisole'),
    ('IT-VE-008', 'Vento del Nord', 'SAILBOAT',  8,  'https://example.com/ventonord.jpg', 'Cabrera 38 stabilissima anche con mare formato'),
    ('IT-BA-009', 'Olimpo',         'YACHT',     16, 'https://example.com/olimpo.jpg',    'Flybridge di oltre 18 metri per feste ed eventi aziendali'),
    ('IT-PA-010', 'Onda Chiara',    'SPEEDBOAT', 5,  'https://example.com/ondachiara.jpg','Gommone da 250 cavalli per sci nautico e calette'),
    ('IT-FI-011', 'Stella Maris',   'SAILBOAT',  8,  'https://example.com/stellamaris.jpg','Bavaria Cruiser ideale per la navigazione arcipelago'),
    ('IT-LI-012', 'Brezza Marina',  'SAILBOAT',  6,  'https://example.com/brezza.jpg',    'Piccolo cabinato perfetto per coppie o famiglie'),
    ('IT-GE-013', 'Tritone',        'YACHT',     10, 'https://example.com/tritone.jpg',   'Motor yacht con plancetta poppiera idraulica'),
    ('IT-SP-014', 'Fulmine',        'SPEEDBOAT', 4,  'https://example.com/fulmine.jpg',   'Runabout in legno d''epoca per uscite eleganti'),
    ('IT-NA-015', 'Nettuno',        'SAILBOAT',  12, 'https://example.com/nettuno.jpg',   'Catamare spazioso ad alta stabilità per crociera'),
    ('IT-VE-016', 'Azzurra',        'SAILBOAT',  8,  'https://example.com/azzurra.jpg',   'Barca da regata performante e divertente'),
    ('IT-BA-017', 'Smeralda',       'YACHT',     8,  'https://example.com/smeralda.jpg',  'Yacht d''altura con due motori turbodiesel'),
    ('IT-PA-018', 'Lampo',          'SPEEDBOAT', 6,  'https://example.com/lampo.jpg',     'Open sportivo con tendalino rollbar e stereo bluetooth'),
    ('IT-FI-019', 'Calypso',        'SAILBOAT',  10, 'https://example.com/calypso.jpg',   'Barca con pilotaggio interno ed esterno'),
    ('IT-LI-020', 'Pegaso',         'YACHT',     12, 'https://example.com/pegaso.jpg',    'Yacht moderno ad altissima efficienza di consumo');

-- Skipper di esempio (Mario Rossi è anche skipper)
INSERT INTO skipper (member_id, boat_id, certificate, avg_rating, bio)
VALUES
    (1,  1,  'Patente Nautica Categoria A', 4.80, 'Skipper professionista con 15 anni di esperienza in acque mediterranee'),
    (3,  2,  'Patente Nautica Categoria B', 4.20, 'Esperto di navigazione costiera e d''altura'),
    (5,  4,  'Master Yacht Captain 200GT',  4.95, 'Specializzata in crociere tra Elba, Corsica e Sardegna'),
    (8,  3,  'Patente Nautica Categoria A', 4.60, 'Appassionato di motoscafi veloci e immersioni subacquee'),
    (10, 5,  'STCW95 / RYA Yachtmaster',    5.00, 'Comandante navale in pensione, istruttore di vela'),
    (12, 6,  'Patente Nautica Categoria A', 4.30, 'Skipper solare, ama organizzare aperitivi al tramonto'),
    (14, 7,  'Patente Categoria B e BLSD',  4.70, 'Esperto conoscitore di grotte e insenature della costa'),
    (16, 8,  'Patente Nautica Categoria A', 4.10, 'Navigatore d''altura con partecipazione a diverse Barcolane'),
    (18, 9,  'Yachtmaster Ocean RYA',       4.85, 'Oltre 30.000 miglia nautiche percorse in tutto il mondo'),
    (20, 11, 'Patente Nautica Categoria A', 4.40, 'Specializzato in uscite didattiche di vela per principianti'),
    (1,  19, 'Istruttore FIV II Livello',    4.90, 'Regatista professionista e consulente di manovra'),
    (3,  13, 'Patente Categoria B',          3.90, 'Particolarmente attento alla sicurezza di bordo'),
    (5,  15, 'Master Yachtmaster 500GT',     4.80, 'Esperta di multiscafo e navigazione oceanica'),
    (8,  10, 'Patente Nautica Categoria A', 4.50, 'Guida naturalistica marina per l''Arcipelago Toscano'),
    (10, 17, 'Patente Categoria B e OSD',    4.65, 'Esperienza decennale nel charter di lusso'),
    (12, 12, 'Patente Nautica Categoria A', 4.15, 'Ama far provare la timoneria agli ospiti in totale sicurezza'),
    (14, 18, 'Patente Categoria A',          4.35, 'Conosce tutti i migliori ristoranti sul mare del Tirreno'),
    (16, 16, 'Istruttore FIV I Livello',     4.75, 'Esperto di regolazione vele e meteorologia marina'),
    (18, 20, 'Yachtmaster Offshore RYA',     4.90, 'Specialista in trasferimenti barche e crociere lunghe'),
    (20, 14, 'Patente Categoria A e Rescue', 4.50, 'Skipper dinamico per uscite di pesca o snorkeling');

-- Noleggi di esempio
INSERT INTO rental (member_id, boat_id, start_date, end_date, num_participants, total_price)
VALUES
    (1,  3,  '2026-05-10', '2026-05-12', 3, 450.00),
    (3,  6,  '2026-05-15', '2026-05-18', 5, 900.00),
    (5,  12, '2026-06-01', '2026-06-03', 4, 600.00),
    (8,  10, '2026-06-10', '2026-06-11', 2, 350.00),
    (10, 18, '2026-06-15', '2026-06-20', 5, 1800.00),
    (12, 11, '2026-07-01', '2026-07-05', 6, 1500.00),
    (14, 14, '2026-07-08', '2026-07-10', 3, 550.00),
    (16, 16, '2026-07-12', '2026-07-19', 7, 2400.00),
    (18, 17, '2026-08-01', '2026-08-07', 6, 3200.00),
    (20, 19, '2026-08-10', '2026-08-15', 8, 2100.00),
    (1,  8,  '2026-08-20', '2026-08-25', 6, 1900.00),
    (3,  4,  '2026-09-01', '2026-09-05', 8, 1600.00),
    (5,  13, '2026-09-10', '2026-09-12', 4, 1100.00),
    (8,  1,  '2026-09-15', '2026-09-18', 6, 1050.00),
    (10, 2,  '2026-09-20', '2026-09-27', 10, 4500.00),
    (12, 20, '2026-10-01', '2026-10-04', 8, 2800.00),
    (14, 15, '2026-10-10', '2026-10-15', 10, 2900.00),
    (16, 6,  '2026-10-20', '2026-10-22', 4, 500.00),
    (18, 5,  '2026-11-01', '2026-11-05', 12, 3800.00),
    (20, 3,  '2026-11-10', '2026-11-12', 3, 400.00);

-- Prenotazioni di esempio
INSERT INTO booking (member_id, boat_id, skipper_id, date, seats_booked, total_price, reg_type)
VALUES
    (2,  1,  1,  '2026-06-01', 2, 180.00, 'INDIVIDUAL'),
    (6,  2,  2,  '2026-06-05', 4, 480.00, 'FAMILY'),
    (7,  4,  3,  '2026-06-12', 2, 220.00, 'INDIVIDUAL'),
    (9,  3,  4,  '2026-06-18', 3, 270.00, 'INDIVIDUAL'),
    (11, 5,  5,  '2026-06-25', 6, 720.00, 'FAMILY'),
    (13, 6,  6,  '2026-07-02', 2, 160.00, 'INDIVIDUAL'),
    (15, 7,  7,  '2026-07-09', 4, 360.00, 'FAMILY'),
    (17, 8,  8,  '2026-07-15', 2, 200.00, 'INDIVIDUAL'),
    (19, 9,  9,  '2026-07-22', 8, 960.00, 'FAMILY'),
    (2,  11, 10, '2026-07-29', 2, 190.00, 'INDIVIDUAL'),
    (4,  19, 11, '2026-08-03', 4, 400.00, 'FAMILY'),
    (6,  13, 12, '2026-08-11', 2, 250.00, 'INDIVIDUAL'),
    (7,  15, 13, '2026-08-18', 6, 660.00, 'FAMILY'),
    (9,  10, 14, '2026-08-24', 2, 180.00, 'INDIVIDUAL'),
    (11, 17, 15, '2026-09-01', 4, 520.00, 'FAMILY'),
    (13, 12, 16, '2026-09-08', 2, 150.00, 'INDIVIDUAL'),
    (15, 18, 17, '2026-09-14', 3, 270.00, 'INDIVIDUAL'),
    (17, 16, 18, '2026-09-21', 4, 380.00, 'FAMILY'),
    (19, 20, 19, '2026-09-28', 5, 650.00, 'FAMILY'),
    (2,  14, 20, '2026-10-05', 2, 220.00, 'INDIVIDUAL');

-- Registrazioni di esempio
INSERT INTO registration (member_id, type, year)
VALUES
    (1,  'FAMILY',     2026),
    (2,  'INDIVIDUAL', 2026),
    (3,  'INDIVIDUAL', 2026),
    (4,  'FAMILY',     2026),
    (5,  'INDIVIDUAL', 2026),
    (6,  'FAMILY',     2026),
    (7,  'INDIVIDUAL', 2026),
    (8,  'INDIVIDUAL', 2026),
    (9,  'FAMILY',     2026),
    (10, 'INDIVIDUAL', 2026),
    (11, 'INDIVIDUAL', 2026),
    (12, 'FAMILY',     2026),
    (13, 'INDIVIDUAL', 2026),
    (14, 'FAMILY',     2026),
    (15, 'INDIVIDUAL', 2026),
    (16, 'INDIVIDUAL', 2026),
    (17, 'FAMILY',     2026),
    (18, 'INDIVIDUAL', 2026),
    (19, 'FAMILY',     2026),
    (20, 'INDIVIDUAL', 2026);