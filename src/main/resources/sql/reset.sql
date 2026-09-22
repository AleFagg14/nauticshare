-- reset.sql
-- Elimina tutte le tabelle nell'ordine corretto
-- (le tabelle con FK vengono eliminate prima delle tabelle referenziate)

DROP TABLE IF EXISTS registration CASCADE;
DROP TABLE IF EXISTS booking CASCADE;
DROP TABLE IF EXISTS rental CASCADE;
DROP TABLE IF EXISTS skipper CASCADE;
DROP TABLE IF EXISTS boat CASCADE;
DROP TABLE IF EXISTS member CASCADE;