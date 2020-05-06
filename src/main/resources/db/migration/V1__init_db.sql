create table player
(
    id                       bigint primary key auto_increment not null,
    name                     varchar(255) unique not null,
    bud                      varchar(255),
    games                    integer default 0,
    wins                     integer default 0,
    password                 varchar(255)        not null,
    status                   varchar(255) check (status in ('FREE', 'IN_SEARCH', 'IN BATTLE')),
    profile_image_name       varchar(255),
    profile_image_borders    varchar(255),
    profile_image_background varchar(255),
    profile_image_color      varchar(255)
);

create index playerName on player (name);

create table unit
(
    id         bigint primary key auto_increment not null,
    name       varchar(255) not null,
    level      integer default 1,
    experience integer default 0,
    health     integer default 0,
    type       varchar(255) check (type in ('FIGHTER', 'RANGER', 'SAGE')),
    status     varchar(255) check (status in ('IN_POOL', 'IN_BATTLE', 'IN_SEARCH')),
    player_id  bigint       not null,

    foreign key (player_id) references player (id)
);

create index unitOwner on unit (player_id);

create table advertisement
(
    id       bigint primary key auto_increment not null,
    customer varchar(255)        not null,
    name     varchar(255) unique not null,
    link     varchar(255) unique not null,
    rate     float default 1.0
);

insert into advertisement
values (1, 'the_battle', 'no_add_1', 'res/ad/no-add_1.jpg', 1);
insert into advertisement
values (2, 'the_battle', 'new_version_soon', 'res/ad/new_version_coming_soon.gif', 1);

create table avatar_image
(
    id    bigint primary key auto_increment not null,
    image varchar(255) unique not null
);

insert into avatar_image (image) values ('dragon1');
insert into avatar_image (image) values ('dragon2');
insert into avatar_image (image) values ('dragon3');
insert into avatar_image (image) values ('ff_cactuar');
insert into avatar_image (image) values ('ff_chocobo_1');
insert into avatar_image (image) values ('ff_chocobo_2');
insert into avatar_image (image) values ('ff_chocobo_3');
insert into avatar_image (image) values ('ff_chocobo_4');
insert into avatar_image (image) values ('ff_mage');
insert into avatar_image (image) values ('ff_mog');
insert into avatar_image (image) values ('ff_torn');
insert into avatar_image (image) values ('fury1');
insert into avatar_image (image) values ('fury2');
insert into avatar_image (image) values ('pony1');
insert into avatar_image (image) values ('pony2');
insert into avatar_image (image) values ('pony3');
insert into avatar_image (image) values ('skylanders');
insert into avatar_image (image) values ('spyro1');
insert into avatar_image (image) values ('spyro2');