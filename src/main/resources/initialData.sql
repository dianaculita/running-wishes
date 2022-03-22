INSERT INTO ASSOCIATIONS(association_id, name, purpose) VALUES (1, 'Hope', 'help children with cancer');

INSERT INTO CHARITIES(person_cnp, name, age, gender, story, iban, association_id) VALUES (2990804673366, 'Alexandra', 5, 'F', '', 1234, 1);
INSERT INTO CHARITIES(person_cnp, name, age, gender, story, iban, association_id) VALUES (1990804673366, 'Mihai', 15, 'M', '', 6578, 1);

INSERT INTO USERS(user_cnp, name, age, gender) VALUES (2900110459966, 'Florina', 31, 'F');
INSERT INTO USERS(user_cnp, name, age, gender) VALUES (2660405234400, 'Ioana', 56, 'F');

INSERT INTO DONATIONS(donation_id, user_cnp, person_cnp, total_funds) VALUES (1, 2900110459966, 2990804673366, 20);
INSERT INTO DONATIONS(donation_id, user_cnp, person_cnp, total_funds) VALUES (2, 2900110459966, 1990804673366, 20);
INSERT INTO DONATIONS(donation_id, user_cnp, person_cnp, total_funds) VALUES (3, 2660405234400, 1990804673366, 30);

INSERT INTO SPORTS(sport_id, sport_name, donation_amount) VALUES (1, 'jogging', 5);
INSERT INTO SPORTS(sport_id, sport_name, donation_amount) VALUES (2, 'walking', 10);
INSERT INTO SPORTS(sport_id, sport_name, donation_amount) VALUES (3, 'running', 5);

INSERT INTO USERS_PRACTICE_SPORTS(sport_id, user_cnp, burned_calories, raised_sum) VALUES (1, 2900110459966, 1000, 10);
INSERT INTO USERS_PRACTICE_SPORTS(sport_id, user_cnp, burned_calories, raised_sum) VALUES (3, 2900110459966, 1000, 10);
INSERT INTO USERS_PRACTICE_SPORTS(sport_id, user_cnp, burned_calories, raised_sum) VALUES (2, 2660405234400, 3000, 30);