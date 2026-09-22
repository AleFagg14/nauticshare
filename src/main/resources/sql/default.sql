-- default.sql
-- Dati iniziali per sviluppo e test

-- Membri di esempio
INSERT INTO member (name, surname, email, password_hash, city, birthday, has_license)
VALUES
    ('Mario',   'Rossi',   'mario.rossi@email.it',
     'hashed_password_1', 'Firenze', '1985-03-15', TRUE),
    ('Laura',   'Bianchi', 'laura.bianchi@email.it',
     'hashed_password_2', 'Milano',  '1990-07-22', FALSE),
    ('Luca',    'Verdi',   'luca.verdi@email.it',
     'hashed_password_3', 'Roma',    '1978-11-30', TRUE),
    ('Admin',   'NauticShare', 'admin@nauticshare.it',
     'hashed_admin_pwd',  'Firenze', '1980-01-01', TRUE);

-- Barche di esempio
INSERT INTO boat (reg_num, name, type, seats, photo_url, description)
VALUES
    ('IT-FI-001', 'Luna Rossa',  'SAILBOAT',  8,
     'https://example.com/lunarossa.jpg',
     'Barca a vela d''altura, perfetta per uscite giornaliere'),
    ('IT-FI-002', 'Poseidon',    'YACHT',    12,
     'https://example.com/poseidon.jpg',
     'Yacht di lusso con cabine e cucina a bordo'),
    ('IT-FI-003', 'Freccia Blu', 'SPEEDBOAT', 4,
     'https://example.com/frecciablu.jpg',
     'Motoscafo veloce per escursioni brevi');

-- Skipper di esempio (Mario Rossi è anche skipper)
INSERT INTO skipper (member_id, boat_id, certificate, avg_rating, bio)
VALUES
    (1, 1, 'Patente Nautica Categoria A', 4.5,
     'Skipper professionista con 15 anni di esperienza in acque mediterranee'),
    (3, 2, 'Patente Nautica Categoria B', 4.2,
     'Esperto di navigazione costiera e d''altura');