USE stfcdb;

CREATE TABLE IF NOT EXISTS session (primary_id CHAR(36) NOT NULL, session_id CHAR(36) NOT NULL,
    creation_time BIGINT NOT NULL, last_access_time BIGINT NOT NULL, max_inactive_interval INT NOT NULL,
    expiry_time BIGINT NOT NULL, principal_name VARCHAR(100),
    CONSTRAINT session_pk PRIMARY KEY (primary_id));

CREATE TABLE session_ATTRIBUTES (session_primary_id CHAR(36) NOT NULL, attribute_name VARCHAR(200) NOT NULL,
                                 attribute_bytes BLOB NOT NULL, CONSTRAINT session_attributes_pk
                                     PRIMARY KEY (session_primary_id, attribute_name), CONSTRAINT session_attributes_fk
                                     FOREIGN KEY (session_primary_id) REFERENCES session(primary_id) ON DELETE CASCADE);

CREATE UNIQUE INDEX spring_session_ix1 ON session (session_id);
CREATE INDEX spring_session_ix2 ON session (expiry_time);
CREATE INDEX spring_session_ix3 ON session (principal_name);