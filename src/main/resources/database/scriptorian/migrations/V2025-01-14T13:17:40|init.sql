create table stories(
    id binary(16) not null,
    owner_id binary(16),
    title varchar(32) not null,
    timestamp datetime not null default now(),

    constraint PK_stories
        primary key (id)
);

create table story_details(
    story_id binary(16) not null,
    content varchar(128) not null,

    constraint PK_story_content
        primary key (story_id, content),
    constraint FK_story_detail
        foreign key (story_id)
            references stories(id)
                on update cascade
                on delete cascade
);

create table contact_infos(
    id binary(16) not null,
    name varchar(64),
    email varchar(32),

    constraint PK_contact_info
        primary key (id)
);

create table addresses(
    id binary(16) not null,
    contact_info_id binary(16) not null,
    street varchar(32) not null,
    number varchar(8) not null,
    floor varchar(16),
    postal_code varchar(8),
    city varchar(32) not null,
    country varchar(16) not null,
    timestamp datetime not null default now(),

    constraint PK_addresses
        primary key (id),
    constraint FK_contact_info_address
        foreign key (contact_info_id)
            references contact_infos(id)
                on update cascade
                on delete cascade
);

create table users(
    id binary(16) not null,
    password longtext not null,
    zone_id varchar(32) not null,
    timestamp datetime not null default now(),

    constraint PK_user
        primary key (id)
);

create table user_authorities(
    user_id binary(16) not null,
    authority varchar(16),

    constraint PK_user_authority
        primary key (user_id, authority),
    constraint FK_user
        foreign key (user_id)
            references users(id)
                on update cascade
                on delete cascade
);

create table days(
    `numeric` int(1) not null,
    title enum(
        'MONDAY',
        'TUESDAY',
        'WEDNESDAY',
        'THURSDAY',
        'FRIDAY',
        'SATURDAY',
        'SUNDAY'
    ) not null,

    constraint PK_day_numeric
        primary key (`numeric`)
);

insert into days(
    title,
    `numeric`
) values (
    'MONDAY',
    1
), (
    'TUESDAY',
    2
), (
    'WEDNESDAY',
    3
), (
    'THURSDAY',
    4
), (
    'FRIDAY',
    5
), (
    'SATURDAY',
    6
), (
    'SUNDAY',
    7
);

create table opening_hours(
    id binary(16) not null,
    owner_id binary(16) not null,
    start datetime not null,
    end datetime not null,
    includingHolidays boolean not null,

    constraint PK_opening_hour
        primary key (id),
    constraint FK_opening_hour_owner
        foreign key (owner_id)
            references users(id)
                on update cascade
                on delete cascade
);

create table opening_hour_days(
    day_numeric int(1) not null,
    opening_hour_id binary(16) not null,

    constraint PK_day_numeric_opening_hour
        primary key (day_numeric, opening_hour_id),
    constraint FK_opening_hour_day
        foreign key (day_numeric)
            references days(`numeric`)
                on update cascade
                on delete cascade,
    constraint FK_owner_id_opening_hour
        foreign key (opening_hour_id)
            references opening_hours(id)
                on update cascade
                on delete cascade
);

create table bookings(
    id binary(16) not null,
    owner_id binary(16) not null,
    contact_info_id binary(16),
    booked datetime,
    start datetime not null,
    end datetime not null,
    timestamp datetime not null default now(),

    constraint PK_booking
        primary key (id),
    constraint FK_client_booking
        foreign key (owner_id)
            references users(id)
                on update cascade
                on delete cascade,
    constraint FK_contact_info_booking
        foreign key (contact_info_id)
            references contact_infos(id)
                on update cascade
                on delete set null
);
