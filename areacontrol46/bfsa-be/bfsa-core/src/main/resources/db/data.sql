SELECT 'Hello, World!' AS greeting;

-- INSERT INTO roles (id, role) VALUES(uuid_generate_v4(), 'ADMIN');
-- INSERT INTO roles (id, role) VALUES(uuid_generate_v4(), 'USER');
--
-- INSERT INTO users (id, email, full_name, username, password, enabled, last_modified_date) VALUES(uuid_generate_v4(), 'admin@domain.xyz', 'Admin Admin', 'admin', '{bcrypt}' || crypt('admin', gen_salt('bf')), true, now());
-- INSERT INTO users (id, email, full_name, username, password, enabled, last_modified_date) VALUES(uuid_generate_v4(), 'user@domain.xyz', 'User User', 'user', '{noop}user', true, now());
--
-- INSERT INTO user_role VALUES((SELECT id from users where username = 'admin'), (SELECT id from roles where role = 'ADMIN'));
-- INSERT INTO user_role VALUES((SELECT id from users where username = 'admin'), (SELECT id from roles where role = 'USER'));
-- INSERT INTO user_role VALUES((SELECT id from users where username = 'user'), (SELECT id from roles where role = 'USER'));