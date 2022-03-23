INSERT INTO ASSOCIATIONS(association_id, name, purpose) VALUES (1, 'Hope', 'help children with cancer');

INSERT INTO CHARITIES(person_cnp, name, age, gender, story, iban, association_id) VALUES (2990804673366, 'Alexandra', 5, 'F', '?', 1234, 1);
INSERT INTO CHARITIES(person_cnp, name, age, gender, story, iban, association_id) VALUES (1990804673366, 'Mihai', 15, 'M', '', 6578, 1);

INSERT INTO USERS(user_cnp, name, age, gender) VALUES (2900110459966, 'Florina', 31, 'F');
INSERT INTO USERS(user_cnp, name, age, gender) VALUES (2660405234400, 'Ioana', 56, 'F');

INSERT INTO SPORTS(sport_id, sport_name) VALUES (1, 'running');

INSERT INTO COMPETITIONS(competition_id, name, number_of_days, location, sport_id) VALUES (1, 'Color Run', 1, 'Bucharest', 1);

INSERT INTO PARTICIPATES_TO(user_cnp, competition_id) VALUES (2900110459966, 1);
INSERT INTO PARTICIPATES_TO(user_cnp, competition_id) VALUES (2660405234400, 1);

INSERT INTO SPONSORS(sponsor_id, name) VALUES (1, 'ING');
INSERT INTO SPONSORS(sponsor_id, name) VALUES (2, 'PRO TV');

INSERT INTO SPONSOR_HAS(competition_id, sponsor_id) VALUES (1, 1);
INSERT INTO SPONSOR_HAS(competition_id, sponsor_id) VALUES (1, 2);

INSERT INTO DONATIONS(donation_id, person_cnp, sponsor_id, total_funds) VALUES (1, 2990804673366, 1, 20);
INSERT INTO DONATIONS(donation_id, person_cnp, sponsor_id, total_funds) VALUES (2, 2990804673366, 1, 20);
INSERT INTO DONATIONS(donation_id, person_cnp, sponsor_id, total_funds) VALUES (3, 1990804673366, 2, 30);