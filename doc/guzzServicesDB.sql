
CREATE TABLE gs_configuration (
  id varchar(64) NOT NULL,
  groupId varchar(64) NOT NULL,
  parameter varchar(64) NOT NULL,
  name varchar(64) default NULL,
  value varchar(128) default NULL,
  type varchar(8) NOT NULL,
  validValues varchar(64) default NULL,
  description text,
  createdTime datetime NOT NULL,
  PRIMARY KEY  (id),
  KEY idx_conf_gid (groupId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE gs_configuration_group (
  id varchar(64) NOT NULL default '',
  userId int(11) NOT NULL,
  name varchar(64) NOT NULL,
  version int(11) NOT NULL default '0',
  createdTime datetime NOT NULL,
  PRIMARY KEY  (id),
  KEY idx_cg_uid (userId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE gs_filter_word (
  id int(11) NOT NULL auto_increment,
  groupId varchar(64) NOT NULL,
  word varchar(32) NOT NULL,
  level int(11) NOT NULL,
  createdTime datetime NOT NULL,
  PRIMARY KEY  (id)
) ENGINE=InnoDB AUTO_INCREMENT=74 DEFAULT CHARSET=utf8;


CREATE TABLE gs_filter_word_group (
  id varchar(64) NOT NULL,
  userId int(11) NOT NULL,
  name varchar(32) NOT NULL,
  color varchar(16) NOT NULL,
  description varchar(255) default NULL,
  createdTime datetime NOT NULL,
  PRIMARY KEY  (id),
  KEY idx_fwg_uid (userId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE gs_user (
  id int(11) NOT NULL auto_increment,
  email varchar(64) NOT NULL,
  password varchar(64) NOT NULL,
  nickName varchar(32) NOT NULL,
  admin bit(1) NOT NULL,
  status tinyint(4) NOT NULL,
  createdTime datetime NOT NULL,
  PRIMARY KEY  (id),
  UNIQUE KEY idx_u_email (email),
  UNIQUE KEY idx_u_nickName (nickName)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;


CREATE TABLE tb_guzz_su (
  gu_id bigint(20) NOT NULL auto_increment,
  gu_tab_name varchar(64) NOT NULL,
  gu_inc_col varchar(64) NOT NULL,
  gu_tab_pk_col varchar(64) NOT NULL,
  gu_tab_pk_val varchar(64) NOT NULL,
  gu_inc_count int(11) NOT NULL,
  gu_db_group varchar(32) NOT NULL default 'default',
  PRIMARY KEY  (gu_id)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

CREATE TABLE gs_banned_top_record (
  id bigint(20) NOT NULL auto_increment,
  groupId int(11) NOT NULL,
  objectId varchar(64) NOT NULL,
  objectTitle varchar(128) NOT NULL,
  objectURL varchar(255),
  lastHitTime datetime default NULL,
  createdTime datetime NOT NULL,
  PRIMARY KEY  (id),
  KEY idx_gid_oid (groupId, objectId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE gs_stat_item (
  id int(11) NOT NULL auto_increment,
  userId int(11) NOT NULL,
  groupId int(11) not null,
  name varchar(64) NOT NULL,
  authKey varchar(128) NOT NULL,
  programId varchar(64) NOT NULL,
  dataProviderUrl varchar(128) NOT NULL,
  encoding varchar(16) NOT NULL,
  dataPublisherUrl varchar(128) NOT NULL,
  lastDataLoadTime datetime,
  timePointPrecision int(11) NOT NULL,
  autoPublish bit(11) default 0,
  cronExpression varchar(16) not null,
  fetchSize int(11) not null,
  publishSize int(11) not null,
  recordCheating bit(1) default 1,
  statBeforeMinutes int(11) default 0,
  recordEditable bit(1) default 0,
  description varchar(255),
  templateContent text,
  errorInfo text,
  createdTime timestamp default current_timestamp(),
  PRIMARY KEY  (id),
  KEY idx_gid (groupId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE gs_stat_item_group (
  id int(11) NOT NULL auto_increment,
  name varchar(64) NOT NULL,
  userId int(11) NOT NULL,
  createdTime datetime NOT NULL,
  PRIMARY KEY  (id),
  KEY idx_uid (userId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE gs_stat_log (
  id int(11) NOT NULL auto_increment,
  userId int(11) NOT NULL,
  statId int(11) NOT NULL,
  statExecuteTime datetime NOT NULL,
  type int(11) NOT NULL,
  result text,
  PRIMARY KEY  (id),
  KEY idx_sid (statId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE gs_top_record (
  id bigint(20) NOT NULL auto_increment,
  groupId int(11) not null,
  statId int(11) not null,
  banned bit(1) default 1,  
  objectId varchar(64) not null,
  objectOrder int(11) default 0,  
  objectTitle varchar(128) NOT NULL,
  objectURL varchar(255),
  objectCreatedTime datetime NOT NULL,
  opTimes int(11) not null,
  extra1 varchar(255),
  extra2 varchar(255),
  extra3 varchar(255),  
  PRIMARY KEY  (id),
  KEY idx_sid (statId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

	
CREATE TABLE gs_authed_service (
  id int(11) NOT NULL auto_increment,
  email varchar(64) not null,
  serviceName varchar(32) default 0,  
  serviceKey varchar(64),
  createdTime datetime,
  owner bit(1) default 0,
  PRIMARY KEY  (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE gs_task_group (
  id int(11) NOT NULL auto_increment,
  name varchar(64) NOT NULL,
  userId int(11) NOT NULL,
  createdTime datetime NOT NULL,
  PRIMARY KEY  (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE gs_task (
  id int(11) NOT NULL auto_increment,
  name varchar(64) NOT NULL,
  userId int(11) NOT NULL,
  groupId int(11) NOT NULL,
  errorCode int(11) default 0,
  authKey varchar(128),
  remoteUrl varchar(255) NOT NULL,
  cronExpression varchar(64) NOT NULL,
  lastExecuteTime datetime,
  lastSucessTime datetime,
  PRIMARY KEY  (id),
  KEY idx_uid (groupId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


--#################2011-07-25


CREATE TABLE gs_log_app (
  id int(11) NOT NULL auto_increment,
  appName varchar(64) NOT NULL,
  secureCode varchar(64) NOT NULL,
  description varchar(255),
  recordsCount int(11) default 0,
  createdTime datetime,
  PRIMARY KEY  (id),
  KEY idx_scode (secureCode)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

	
CREATE TABLE gs_log_custom_property (
  id int(11) NOT NULL auto_increment,
  appId int(11) not null,
  propName varchar(32) NOT NULL,
  colName varchar(32) NOT NULL,
  displayName varchar(32) NOT NULL,
  dataType varchar(32) NOT NULL,
  createdTime datetime,
  PRIMARY KEY  (id),
  KEY idx_appId (appId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


--log record基础表结构和表名

create database guzzServicesAppLog default character set utf8 ;
use guzzServicesAppLog ;

CREATE TABLE gs_log_record_ (
  id bigint(20) NOT NULL auto_increment,
  userId int(11) not null,
  appId int(11) not null,
  appIP varchar(32) not null,
  createdTime datetime not null,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE gs_log_record_2 (
  id bigint(20) NOT NULL auto_increment,
  userId int(11) not null,
  appId int(11) not null,
  appIP varchar(32) not null,
  userNick varchar(64) not null,
  postId bigint(20) not null,
  title varchar(128) not null,
  createdTime datetime not null,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;







