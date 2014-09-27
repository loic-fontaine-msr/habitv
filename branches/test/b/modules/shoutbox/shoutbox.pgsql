CREATE TABLE shoutbox (
  shout_id serial NOT NULL,
  uid int NOT NULL default '0',
  nick varchar(32) NOT NULL default '',
  shout varchar(255) NOT NULL default '',
  url varchar(100) NOT NULL default '',
  status smallint NOT NULL default '0',
  moderate int NOT NULL default '0',
  created int NOT NULL default '0',
  changed int NOT NULL default '0',
  hostname varchar(255) NOT NULL default '',
  PRIMARY KEY (shout_id)
);


CREATE TABLE shoutbox_moderation (
  moderation_id serial NOT NULL,
  shout_id int default '0',
  uid int default '0',
  vote smallint default '0',
  timestamp int default '0',
  PRIMARY KEY (moderation_id)
);
