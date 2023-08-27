
INSERT INTO users (name, password,created_at) VALUES ('Admin', '$2y$10$ne8RCW6ifp815fvKPmk2eu/rDbsdvQkaIwtLHGVkiHFoWTC.s/BZO','2023-08-23 17:08:50.926856');
INSERT INTO users (name, password,created_at) VALUES ('John Doe', '$2y$10$gnGFQFZ42xlhjj09HxP8Qerll/GT5RZtvP5CHBa9YibFREB/3brWu','2023-08-23 17:08:50.926856');

INSERT INTO authorities (role_name, user_id,created_at) VALUES ('ADMIN', 1,'2023-08-23 17:08:50.926856');
INSERT INTO authorities (role_name, user_id,created_at) VALUES ('USER', 2,'2023-08-23 17:08:50.926856');

INSERT INTO books (id,title,author,price,stock_quantity,created_at)
VALUES ('d215b5f8-0249-4dc5-89a3-51fd148cfb41','The Bathysphere Book: Effects of the Luminous Ocean Depths','Brad Fox','12.99','4','2023-08-23 17:08:50.926856');


INSERT INTO books (id,title,author,price,stock_quantity,created_at)
VALUES ('62d497ca-4035-4389-ae1d-ec0dafe3cf73','The Heaven & Earth Grocery Store: A Novel','James McBride','6.99','5','2023-08-23 17:07:15.410846');


INSERT INTO books (id,title,author,price,stock_quantity,created_at)
VALUES ('c3bded73-c426-4816-86d6-205f1f5c2f3a','The Underworld: Journeys to the Depths of the Ocean','Susan Caseye','4.99','2','2023-08-23 17:08:20.102918');

INSERT INTO books (id,title,author,price,stock_quantity,created_at)
VALUES ('6cb307ec-3ec3-42ea-80ab-eda7fa5b7e9b','The Water Knife','Paolo Bacigalupi','8.99','3','2023-08-23 17:24:57.19972');

INSERT INTO books (id,title,author,price,stock_quantity,created_at)
VALUES ('83dfaae9-fdd1-495f-80a6-ebed815734a4','American Journey: On the Road with Henry Ford, Thomas Edison, and John Burroughs','Wes Davis','35.99','7','2023-08-23 17:09:17.852471');


INSERT INTO books (id,title,author,price,stock_quantity,created_at)
VALUES ('f845505a-40a3-4045-9e02-62c5fb37ad27','Cadillac Desert: The American West and Its Disappearing Water, Revised Edition','Marc Reisner','22.99','8','2023-08-23 17:16:28.963928');