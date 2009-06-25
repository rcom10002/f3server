drop database if exists f3s;

create database f3s CHARACTER SET utf8 COLLATE utf8_general_ci;

use f3s;

drop table if exists GAME_FEEDBACK;

drop table if exists GAME_RECORD;

drop table if exists GLOBAL_CONFIG;

drop table if exists PERIODLY_SUM;

drop table if exists PLAYER_PROFILE;

drop table if exists PLAYER_SCORE;

drop table if exists RECHARGE_HISTORY;

create table GAME_FEEDBACK
(
   FEEDBACK_ID          VARCHAR(100) not null,
   GAME_ID              VARCHAR(100),
   NUMBER               VARCHAR(100),
   CHEAT_DESC           VARCHAR(2000),
   STATUS               TINYINT,
   CREATE_TIME          DATETIME,
   CREATE_BY            VARCHAR(100),
   UPDATE_TIME          DATETIME,
   UPDATE_BY            VARCHAR(100),
   primary key (FEEDBACK_ID)
);

create table GAME_RECORD
(
   GAME_ID              VARCHAR(100) not null,
   GAME_TYPE            VARCHAR(100),
   GAME_SETTING         TINYINT,
   WINNER_NUMBERS       VARCHAR(100),
   PLAYERS              VARCHAR(100),
   SCORE                INT,
   SYSTEM_SCORE         INT,
   INIT_POKERS          TEXT,
   RECORD               TEXT,
   STATUS               TINYINT,
   CREATE_TIME          DATETIME,
   CREATE_BY            VARCHAR(100),
   UPDATE_TIME          DATETIME,
   UPDATE_BY            VARCHAR(100),
   primary key (GAME_ID)
);

create table GLOBAL_CONFIG
(
   GLOBAL_CONFIG_ID     VARCHAR(100) not null,
   NUMBER               VARCHAR(100),
   NAME                 VARCHAR(100),
   VALUE                TEXT,
   STATUS               TINYINT,
   CREATE_TIME          DATETIME,
   CREATE_BY            VARCHAR(100),
   UPDATE_TIME          DATETIME,
   UPDATE_BY            VARCHAR(100),
   primary key (GLOBAL_CONFIG_ID)
);

create table PERIODLY_SUM
(
   PERIODLY_ID          VARCHAR(100) not null,
   PROFILE_ID           VARCHAR(100),
   NUMBER               VARCHAR(100),
   START_DATE           DATE,
   END_DATE             DATE,
   SCORE                INT,
   SYSTEM_SCORE         INT,
   STATUS               TINYINT,
   CREATE_TIME          DATETIME,
   CREATE_BY            VARCHAR(100),
   UPDATE_TIME          DATETIME,
   UPDATE_BY            VARCHAR(100),
   primary key (PERIODLY_ID)
);

create table PLAYER_PROFILE
(
   PROFILE_ID           VARCHAR(100) not null,
   NUMBER               VARCHAR(100),
   NAME                 VARCHAR(100),
   USER_ID              VARCHAR(16),
   PASSWORD             VARCHAR(16),
   CURRENT_SCORE        INT,
   INIT_LIMIT           INT,
   LEVEL                INT,
   RLS_PATH             VARCHAR(1000),
   ROLE                 TINYINT,
   STATUS               TINYINT,
   CREATE_TIME          DATETIME,
   CREATE_BY            VARCHAR(100),
   UPDATE_TIME          DATETIME,
   UPDATE_BY            VARCHAR(100),
   primary key (PROFILE_ID)
);

create table PLAYER_SCORE
(
   SCORE_ID             VARCHAR(100) not null,
   PROFILE_ID           VARCHAR(100),
   GAME_ID              VARCHAR(100),
   USER_ID              VARCHAR(100),
   CURRENT_NUMBER       VARCHAR(1),
   SCORE                INT,
   SYSTEM_SCORE         INT,
   STATUS               TINYINT,
   CREATE_TIME          DATETIME,
   CREATE_BY            VARCHAR(100),
   UPDATE_TIME          DATETIME,
   UPDATE_BY            VARCHAR(100),
   primary key (SCORE_ID)
);

create table RECHARGE_HISTORY
(
   RECHARGE_ID          VARCHAR(100) not null,
   PROFILE_ID           VARCHAR(100),
   OPERATOR             VARCHAR(100),
   SCORE                INT,
   BIZ_DATE             DATE,
   CREATE_TIME          DATETIME,
   CREATE_BY            VARCHAR(100),
   UPDATE_TIME          DATETIME,
   UPDATE_BY            VARCHAR(100),
   primary key (RECHARGE_ID)
);

alter table GAME_FEEDBACK add constraint FK_GAME_FEEDBACK_TO_GAME_RECORD foreign key (GAME_ID)
      references GAME_RECORD (GAME_ID) on delete restrict on update restrict;

alter table PERIODLY_SUM add constraint FK_PERIODLY_SUM_TO_PLAYER_PROFILE foreign key (PROFILE_ID)
      references PLAYER_PROFILE (PROFILE_ID) on delete restrict on update restrict;

alter table PLAYER_SCORE add constraint FK_PLAYER_SCORE_TO_PLAYER_PROFILE foreign key (PROFILE_ID)
      references PLAYER_PROFILE (PROFILE_ID) on delete restrict on update restrict;

alter table PLAYER_SCORE add constraint FK_PLAYER_SCORE_TO_GAME_RECORD foreign key (GAME_ID)
      references GAME_RECORD (GAME_ID) on delete restrict on update restrict;

alter table RECHARGE_HISTORY add constraint FK_RECHARGE_HISTORY_TO_PLAYER_PROFILE foreign key (PROFILE_ID)
      references PLAYER_PROFILE (PROFILE_ID) on delete restrict on update restrict;