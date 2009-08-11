drop database if exists f3s;

create database f3s CHARACTER SET utf8 COLLATE utf8_general_ci;

use f3s;

drop table if exists GAME_FEEDBACK;

drop table if exists GAME_RECORD;

drop table if exists GLOBAL_CONFIG;

drop table if exists LOG_INFO;

drop table if exists PERIODLY_SUM;

drop table if exists PLAYER_PROFILE;

drop table if exists PLAYER_SCORE;

create table GAME_FEEDBACK
(
   FEEDBACK_ID          VARCHAR(100) not null,
   GAME_ID              VARCHAR(100) not null,
   NUMBER               VARCHAR(100),
   TITLE                VARCHAR(100) not null,
   DESCRIPTION          TEXT not null,
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
   GAME_TYPE            VARCHAR(100) not null,
   GAME_SETTING         TINYINT,
   WINNER_NUMBERS       VARCHAR(100),
   PLAYERS              VARCHAR(100),
   SCORE                INT,
   SYSTEM_SCORE         INT,
   INIT_POKERS          TEXT,
   RECORD               TEXT not null,
   STATUS               TINYINT,
   CREATE_TIME          DATETIME,
   CREATE_BY            VARCHAR(100),
   UPDATE_TIME          DATETIME,
   UPDATE_BY            VARCHAR(100),
   primary key (GAME_ID)
)
type = InnoDB;

create table GLOBAL_CONFIG
(
   GLOBAL_CONFIG_ID     VARCHAR(100) not null,
   NUMBER               VARCHAR(100),
   NAME                 VARCHAR(100) not null,
   VALUE                TEXT not null,
   TYPE                 VARCHAR(100),
   STATUS               TINYINT,
   CREATE_TIME          DATETIME,
   CREATE_BY            VARCHAR(100),
   UPDATE_TIME          DATETIME,
   UPDATE_BY            VARCHAR(100),
   primary key (GLOBAL_CONFIG_ID)
);

create table LOG_INFO
(
   LOG_ID               VARCHAR(100) not null,
   NUMBER               VARCHAR(100),
   CAPTION              VARCHAR(100) not null,
   KEY_CAUSE1           VARCHAR(100),
   KEY_CAUSE2           VARCHAR(100),
   KEY_CAUSE3           VARCHAR(100),
   INFO                 TEXT,
   TYPE                 VARCHAR(100),
   STATUS               TINYINT,
   CREATE_TIME          DATETIME,
   CREATE_BY            VARCHAR(100),
   UPDATE_TIME          DATETIME,
   UPDATE_BY            VARCHAR(100),
   primary key (LOG_ID)
);

create table PERIODLY_SUM
(
   PERIODLY_ID          VARCHAR(100) not null,
   PROFILE_ID           VARCHAR(100),
   NUMBER               VARCHAR(100),
   TITLE                VARCHAR(100) not null,
   START_DATE           DATE not null,
   END_DATE             DATE not null,
   WIN_TIMES            INT not null,
   WIN_SCORES           INT not null,
   LOSE_TIMES           INT not null,
   LOSE_SCORES          INT not null,
   DRAW_TIMES           INT not null,
   DRAW_SCORES          INT not null,
   TOTAL_TIMES          INT not null,
   TOTAL_SCORES         INT not null,
   TOTAL_SYSTEM_SCORE   INT not null,
   STATUS               TINYINT,
   CREATE_TIME          DATETIME,
   CREATE_BY            VARCHAR(100),
   UPDATE_TIME          DATETIME,
   UPDATE_BY            VARCHAR(100),
   primary key (PERIODLY_ID)
)
type = InnoDB;

create table PLAYER_PROFILE
(
   PROFILE_ID           VARCHAR(100) not null,
   NUMBER               VARCHAR(100),
   NAME                 VARCHAR(100),
   USER_ID              VARCHAR(16) not null,
   PASSWORD             VARCHAR(16) not null,
   CURRENT_SCORE        INT not null,
   INIT_LIMIT           INT,
   LEVEL                INT not null,
   RLS_PATH             VARCHAR(1000) not null,
   ROLE                 VARCHAR(100) not null,
   STATUS               TINYINT,
   CREATE_TIME          DATETIME,
   CREATE_BY            VARCHAR(100),
   UPDATE_TIME          DATETIME,
   UPDATE_BY            VARCHAR(100),
   primary key (PROFILE_ID)
)
type = InnoDB;

create table PLAYER_SCORE
(
   SCORE_ID             VARCHAR(100) not null,
   PROFILE_ID           VARCHAR(100) not null,
   GAME_ID              VARCHAR(100) not null,
   USER_ID              VARCHAR(100) not null,
   CURRENT_NUMBER       VARCHAR(1) not null,
   ORG_SCORE            INT not null,
   SCORE                INT not null,
   SYSTEM_SCORE         INT not null,
   STATUS               TINYINT,
   CREATE_TIME          DATETIME,
   CREATE_BY            VARCHAR(100),
   UPDATE_TIME          DATETIME,
   UPDATE_BY            VARCHAR(100),
   primary key (SCORE_ID)
)
type = InnoDB;

alter table GAME_FEEDBACK add constraint FK_GAME_FEEDBACK_TO_GAME_RECORD foreign key (GAME_ID)
      references GAME_RECORD (GAME_ID) on delete restrict on update restrict;

alter table PERIODLY_SUM add constraint FK_PERIODLY_SUM_TO_PLAYER_PROFILE foreign key (PROFILE_ID)
      references PLAYER_PROFILE (PROFILE_ID) on delete restrict on update restrict;

alter table PLAYER_SCORE add constraint FK_PLAYER_SCORE_TO_GAME_RECORD foreign key (GAME_ID)
      references GAME_RECORD (GAME_ID) on delete restrict on update restrict;

alter table PLAYER_SCORE add constraint FK_PLAYER_SCORE_TO_PLAYER_PROFILE foreign key (PROFILE_ID)
      references PLAYER_PROFILE (PROFILE_ID) on delete restrict on update restrict;
