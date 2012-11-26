# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table backup (
  id                        bigint not null,
  job_id                    bigint,
  status                    varchar(1),
  start_time                timestamp,
  end_time                  timestamp,
  constraint ck_backup_status check (status in ('S','R','E')),
  constraint pk_backup primary key (id))
;

create table backup_log_message (
  id                        bigint not null,
  backup_id                 bigint,
  timestamp                 timestamp,
  message                   varchar(255),
  level                     varchar(1),
  constraint ck_backup_log_message_level check (level in ('E','W','I')),
  constraint pk_backup_log_message primary key (id))
;

create table datastore_profile (
  id                        bigint not null,
  user_id                   bigint,
  profile_name              varchar(255),
  description               varchar(255),
  plugin_class              varchar(255),
  type                      varchar(6),
  created                   timestamp,
  modified                  timestamp,
  constraint ck_datastore_profile_type check (type in ('SINK','SOURCE')),
  constraint pk_datastore_profile primary key (id))
;

create table datastore_profile_property (
  id                        bigint not null,
  profile_id                bigint,
  property_name             varchar(255),
  property_value            varchar(255),
  constraint pk_datastore_profile_property primary key (id))
;

create table job (
  id                        bigint not null,
  user_id                   bigint,
  job_title                 varchar(255),
  source_profile_id         bigint,
  actions                   varchar(255),
  sink_profile_id           bigint,
  start                     timestamp,
  delay                     bigint,
  created                   timestamp,
  modified                  timestamp,
  constraint pk_job primary key (id))
;

create table user (
  id                        bigint not null,
  username                  varchar(255),
  email                     varchar(255),
  password                  varchar(255),
  constraint pk_user primary key (id))
;

create table user_property (
  id                        bigint not null,
  user_id                   bigint,
  key                       varchar(255),
  value                     varchar(255),
  type                      varchar(7),
  constraint ck_user_property_type check (type in ('BOOLEAN','STRING','NUMBER')),
  constraint pk_user_property primary key (id))
;

create sequence backup_seq;

create sequence backup_log_message_seq;

create sequence datastore_profile_seq;

create sequence datastore_profile_property_seq;

create sequence job_seq;

create sequence user_seq;

create sequence user_property_seq;

alter table backup add constraint fk_backup_job_1 foreign key (job_id) references job (id) on delete restrict on update restrict;
create index ix_backup_job_1 on backup (job_id);
alter table backup_log_message add constraint fk_backup_log_message_backup_2 foreign key (backup_id) references backup (id) on delete restrict on update restrict;
create index ix_backup_log_message_backup_2 on backup_log_message (backup_id);
alter table datastore_profile add constraint fk_datastore_profile_user_3 foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_datastore_profile_user_3 on datastore_profile (user_id);
alter table datastore_profile_property add constraint fk_datastore_profile_property__4 foreign key (profile_id) references datastore_profile (id) on delete restrict on update restrict;
create index ix_datastore_profile_property__4 on datastore_profile_property (profile_id);
alter table job add constraint fk_job_user_5 foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_job_user_5 on job (user_id);
alter table job add constraint fk_job_sourceProfile_6 foreign key (source_profile_id) references datastore_profile (id) on delete restrict on update restrict;
create index ix_job_sourceProfile_6 on job (source_profile_id);
alter table job add constraint fk_job_sinkProfile_7 foreign key (sink_profile_id) references datastore_profile (id) on delete restrict on update restrict;
create index ix_job_sinkProfile_7 on job (sink_profile_id);
alter table user_property add constraint fk_user_property_user_8 foreign key (user_id) references user (id) on delete restrict on update restrict;
create index ix_user_property_user_8 on user_property (user_id);



# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists backup;

drop table if exists backup_log_message;

drop table if exists datastore_profile;

drop table if exists datastore_profile_property;

drop table if exists job;

drop table if exists user;

drop table if exists user_property;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists backup_seq;

drop sequence if exists backup_log_message_seq;

drop sequence if exists datastore_profile_seq;

drop sequence if exists datastore_profile_property_seq;

drop sequence if exists job_seq;

drop sequence if exists user_seq;

drop sequence if exists user_property_seq;

