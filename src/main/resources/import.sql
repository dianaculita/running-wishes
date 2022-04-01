INSERT INTO associations(association_id, name, purpose)
VALUES (1, 'Hope', 'help children with cancer');

INSERT INTO charities(person_cnp, name, story, iban, needed_fund, raised_fund, association_id)
VALUES ('2990804673366', 'Alexandra', '', 1234, 1000, 0, 1);
INSERT INTO charities(person_cnp, name, story, iban, needed_fund, raised_fund, association_id)
VALUES ('1990804673366', 'Mihai', '', 6578, 2500, 0, 1);

INSERT INTO participants(participant_cnp, name)
VALUES ('2900110459966', 'Florina');
INSERT INTO participants(participant_cnp, name)
VALUES ('2660405234400', 'Ioana');

INSERT INTO sports(sport_id, name)
VALUES (1, 'running');

INSERT INTO competitions(competition_id, name, number_of_days, location, ticket_fee, raised_money, sport_id)
VALUES (1, 'Color Run', 1, 'Bucharest', 30, 760, 1);

INSERT INTO participates_to(participant_cnp, competition_id)
VALUES ('2900110459966', 1);
INSERT INTO participates_to(participant_cnp, competition_id)
VALUES ('2660405234400', 1);

INSERT INTO sponsors(sponsor_id, name, sponsoring_funds)
VALUES (1, 'ING', 300);
INSERT INTO sponsors(sponsor_id, name, sponsoring_funds)
VALUES (2, 'PRO TV', 400);

INSERT INTO sponsor_has(competition_id, sponsor_id)
VALUES (1, 1);
INSERT INTO sponsor_has(competition_id, sponsor_id)
VALUES (1, 2);