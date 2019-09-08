REPLACE INTO USERS (id, username, password, active) VALUES 
(1, "admin",SHA2("password", 256) , 1 ), 
(2, "user1", SHA2("password", 256) , 1 ),
(3, "user2", SHA2("password", 256) , 1 )
;


REPLACE INTO USER_ROLES (id, user_id, role_name) VALUES 
(1, 1, "ROLE_ADMIN"),
(2, 1, "ROLE_USER"),
(3, 2, "ROLE_USER"),
(4, 3, "ROLE_USER")
;