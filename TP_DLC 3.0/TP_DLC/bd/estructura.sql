-- /* 
--  * To change this license header, choose License Headers in Project Properties.
--  * To change this template file, choose Tools | Templates
--  * and open the template in the editor.
--  */
-- /**
--  * Author:  dlcusr
--  * Created: May 11, 2018
--  */

DROP SEQUENCE IF EXISTS sq_palabra CASCADE;
CREATE SEQUENCE sq_palabra;
DROP TABLE IF EXISTS palabra CASCADE;
CREATE TABLE palabra (
  idPalabra             INTEGER                         NOT NULL,
  nombre                VARCHAR(128)                     NOT NULL,
  PRIMARY KEY (idPalabra),
  UNIQUE (nombre)
);

-- =============================================================================

DROP SEQUENCE IF EXISTS sq_documento CASCADE;
CREATE SEQUENCE sq_documento;
DROP TABLE IF EXISTS documento CASCADE;
CREATE TABLE documento (
  idDocumento           INTEGER                         NOT NULL,
  nombre                VARCHAR(32)                     NOT NULL,
  PRIMARY KEY (idDocumento),
  UNIQUE (nombre)
);

-- =============================================================================

DROP TABLE IF EXISTS palabraPorDocumento CASCADE;
CREATE TABLE palabraPorDocumento (
  idPalabra                INTEGER                         NOT NULL,
  idDocumento              INTEGER                         NOT NULL,
  cantidad                 INTEGER                         NOT NULL,
  PRIMARY KEY (idPalabra, idDocumento),
  FOREIGN KEY (idPalabra)
    REFERENCES palabra(idPalabra),
  FOREIGN KEY (idDocumento)
    REFERENCES documento(idDocumento)
);

COMMIT;
-- =============================================================================
