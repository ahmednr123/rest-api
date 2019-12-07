create table users (id INT(6) AUTO_INCREMENT PRIMARY KEY, name VARCHAR(30) NOT NULL, age INT(3) NOT NULL, about VARCHAR(100) NOT NULL);
create table apps (user_id INT(6) NOT NULL, id INT(6) AUTO_INCREMENT PRIMARY KEY, name VARCHAR(30) NOT NULL, description VARCHAR(100) NOT NULL, FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE);
create table activities (app_id INT(6) NOT NULL, id INT(6) AUTO_INCREMENT PRIMARY KEY, name VARCHAR(30) NOT NULL, description VARCHAR(100) NOT NULL, FOREIGN KEY (app_id) REFERENCES apps(id) ON DELETE CASCADE);

INSERT INTO activities (app_id, name, description) SELECT apps.id, 'name', 'desc' FROM apps WHERE apps.id = app_id AND app.user_id = user_id;

CREATE TABLE admins (username VARCHAR(30) PRIMARY KEY, password VARCHAR(30) NOT NULL);
CREATE TABLE jwt_keys (username VARCHAR(30) NOT NULL, auth_key TEXT NOT NULL, FOREIGN KEY (username) REFERENCES admins(username) ON DELETE CASCADE);