create table admin_service_adjustments_tb (
    admin_service_adjustment_id bigint not null,
    created_at timestamp(6),
    last_updated_at timestamp(6),
    last_updated_by bigint,
    prop_long1 bigint,
    prop_long2 bigint,
    prop_long3 bigint,
    prop_long4 bigint,
    prop_string1 varchar(512),
    prop_string2 varchar(512),
    prop_string3 varchar(512),
    prop_string4 varchar(512),
    service_type varchar(255),
    primary key (admin_service_adjustment_id)
);
    
create table sns_block_users_tb (
    sns_block_user_id bigint not null,
    is_blocked_at timestamp(6),
    sns_blocked_user_id bigint,
    sns_blocker_user_id bigint,
    primary key (sns_block_user_id)
);

create table sns_notifications_tb (
    sns_notification_id bigint not null,
    created_at timestamp(6),
    last_updated_at timestamp(6),
    last_updated_by bigint,
    notification_content_user_id bigint not null,
    notification_content_user_profile_path varchar(255) not null,
    notification_content_username varchar(255) not null,
    notification_count integer not null,
    sns_notification_contents jsonb default '[]' not null,
    notification_type varchar(255) not null check (notification_type in ('MESSAGE_NOTIFICATION','POST_LIKE_NOTIFICATION','POST_CLIP_NOTIFICATION','POST_COMMENT_NOTIFICATION','POST_COMMENT_LIKE_NOTIFICATION','POST_COMMENT_REPLY_NOTIFICATION','USER_FOLLOWER_NOTIFICATION')),
    username varchar(255) not null,
    sns_follower_id bigint,
    sns_post_id bigint,
    sns_user_id bigint not null,
    primary key (sns_notification_id)
);

create table sns_post_comment_likes_tb (
    sns_post_comment_like_id bigint not null,
    created_at timestamp(6),
    last_updated_at timestamp(6),
    last_updated_by bigint,
    is_liked boolean default false not null,
    is_liked_at timestamp(6),
    sns_post_id bigint not null,
    sns_post_comment_reaction_id bigint,
    sns_user_id bigint not null,
    primary key (sns_post_comment_like_id)
);

create table sns_post_comment_reactions_tb (
sns_post_comment_reaction_id bigint not null,
created_at timestamp(6),
last_updated_at timestamp(6),
last_updated_by bigint,
comment_media_content varchar(255),
comment_media_type varchar(255) check (comment_media_type in ('IMAGE','VIDEO')),
comment_msg varchar(2048),
deleted_at timestamp(6),
is_commented boolean,
is_source boolean default false not null,
comment_user_id bigint not null,
sns_post_id bigint not null,
source_comment_id bigint,
primary key (sns_post_comment_reaction_id)
);

create table sns_post_reports_tb (
    sns_post_report_id bigint not null,
    created_at timestamp(6),
    last_updated_at timestamp(6),
    last_updated_by bigint,
    post_report_reason_type varchar(255) not null check (post_report_reason_type in ('DISLIKE','INACCURATE_LOCATION','SPAM_OR_SCAM','SENSITIVE_CONTENT','HARMFUL_OR_ABUSIVE','OTHER')),
    post_report_status varchar(255) not null check (post_report_status in ('PENDING','REVIEWED','RESOLVED')),
    report_reason varchar(2047) not null,
    reported_user_id bigint,
    reporter_user_id bigint not null,
    sns_post_id bigint,
    sns_post_comment_reaction_id bigint,
    primary key (sns_post_report_id)
);

create table sns_post_user_reactions_tb (
    sns_post_user_reaction_id bigint not null,
    created_at timestamp(6),
    last_updated_at timestamp(6),
    last_updated_by bigint,
    is_clipped boolean default false not null,
    is_clipped_at timestamp(6),
    is_liked boolean default false not null,
    is_liked_at timestamp(6),
    is_reposted boolean default false not null,
    is_reposted_at timestamp(6),
    is_shown boolean default true not null,
    not_shown_at timestamp(6),
    sns_post_id bigint not null,
    sns_user_id bigint not null,
    primary key (sns_post_user_reaction_id)
);

create table sns_posts_tb (
    sns_post_id bigint not null,
    created_at timestamp(6),
    last_updated_at timestamp(6),
    last_updated_by bigint,
    address varchar(255),
    deleted_at timestamp(6),
    is_exposed boolean default true not null,
    is_repost boolean default false,
    is_show_address boolean default true,
    latitude float4,
    longitude float4,
    post_body_text varchar(255),
    post_caption_content varchar(255),
    post_content_business_type varchar(255) default 'BUSINESS_DAILY_TYPE' check (post_content_business_type in ('BUSINESS_GOOD_PLACE_TYPE','BUSINESS_CAFE_TYPE','BUSINESS_ATTRACTION_TYPE','BUSINESS_PARK_TYPE','BUSINESS_DAILY_TYPE')),
    post_title varchar(255),
    reaction_count integer default 1,
    sns_post_contents jsonb default '[]' not null,
    tags jsonb default '[]' not null,
    tgt_aud_type varchar(255) default 'PUBLIC_SCOPE' check (tgt_aud_type in ('PUBLIC_SCOPE','FOLLOWERS_SCOPE','PRIVATE_SCOPE')),
    repost_origin_id bigint,
    sns_user_id bigint,
    primary key (sns_post_id)
);

create table sns_scrap_boards_tb (
    sns_scrap_board_id bigint not null,
    created_at timestamp(6),
    last_updated_at timestamp(6),
    last_updated_by bigint,
    deleted_at timestamp(6),
    scrap_name varchar(255) not null,
    target_audience varchar(255) not null check (target_audience in ('PUBLIC_AUDIENCE','PRIVATE_AUDIENCE','PROTECTED_AUDIENCE')),
    sns_user_id bigint,
    primary key (sns_scrap_board_id)
);

create table sns_scraps_tb (
    sns_scrap_id bigint not null,
    created_at timestamp(6),
    last_updated_at timestamp(6),
    last_updated_by bigint,
    sns_post_id bigint,
    sns_scrap_board_id bigint,
    sns_user_id bigint,
    primary key (sns_scrap_id)
);

create table sns_tag_follows_tb (
    sns_tag_follow_id bigint not null,
    created_at timestamp(6),
    last_updated_at timestamp(6),
    last_updated_by bigint,
    tag_name varchar(255),
    sns_tag_id bigint,
    sns_user_id bigint,
    primary key (sns_tag_follow_id)
);

create table sns_tag_posts_tb (
    sns_tag_post_id bigint not null,
    sns_post_id bigint,
    sns_tag_id bigint,
    primary key (sns_tag_post_id)
);

create table sns_tags_tb (
    sns_tag_id bigint not null,
    created_at timestamp(6),
    last_updated_at timestamp(6),
    last_updated_by bigint,
    is_exposed boolean default true,
    tag_name varchar(255) not null,
    tag_reps_batch_content varchar(255),
    tag_reps_batch_content_type varchar(255) check (tag_reps_batch_content_type in ('IMAGE','TEXTFIELD','VIDEO')),
    primary key (sns_tag_id)
);

create table sns_user_favorite_term_bookmarks_tb (
    sns_user_favorite_term_bookmark_id bigint not null,
    favorite_term_content varchar(255),
    favorite_term_content_type varchar(255) check (favorite_term_content_type in ('IMAGE','TEXTFIELD','VIDEO')),
    favorite_term_name varchar(255),
    sns_tag_follow_id bigint,
    sns_user_id bigint,
    primary key (sns_user_favorite_term_bookmark_id)
);

create table sns_user_follow_statistics_tb (
    sns_user_follow_statistic_id bigint not null,
    follower_num integer,
    following_num integer,
    sns_user_id bigint not null,
    primary key (sns_user_follow_statistic_id)
);

create table sns_user_follows_tb (
    sns_user_follow_id bigint not null,
    created_at timestamp(6),
    last_updated_at timestamp(6),
    last_updated_by bigint,
    follower_id bigint,
    following_id bigint,
    primary key (sns_user_follow_id)
);

create table sns_user_message_reactions_tb (
sns_user_message_reaction_id bigint not null,
has_msg_reaction boolean default false not null,
is_read boolean default false not null,
reacted_at timestamp(6),
read_at timestamp(6),
msg_reaction_type varchar(255) default 'NOT_REACTION' check (msg_reaction_type in ('NOT_REACTION','REACTION_LIKE','REACTION_HEART','REACTION_LAUGH','REACTION_SURPRISE','REACTION_SAD','REACTION_ANGRY')),
sns_user_message_id bigint not null,
primary key (sns_user_message_reaction_id)
);

create table sns_user_message_room_members_tb (
    sns_user_message_room_member_id bigint not null,
    created_at timestamp(6),
    last_updated_at timestamp(6),
    last_updated_by bigint,
    is_blocked boolean default false not null,
    is_hidden boolean default false not null,
    msg_room_type varchar(255) not null check (msg_room_type in ('DIRECT_MESSAGE_ROOM_TYPE','GROUP_MESSAGE_ROOM_TYPE')),
    read_at timestamp(6),
    sns_user_message_room_id bigint not null,
    source_user_id bigint not null,
    target_user_id bigint,
    primary key (sns_user_message_room_member_id)
);

create table sns_user_message_rooms_tb (
    sns_user_message_room_id bigint not null,
    created_at timestamp(6),
    last_updated_at timestamp(6),
    last_updated_by bigint,
    msg_room_type varchar(255) not null check (msg_room_type in ('DIRECT_MESSAGE_ROOM_TYPE','GROUP_MESSAGE_ROOM_TYPE')),
    primary key (sns_user_message_room_id)
);

create table sns_user_messages_tb (
    sns_user_message_id bigint not null,
    created_at timestamp(6),
    last_updated_at timestamp(6),
    last_updated_by bigint,
    deleted_at timestamp(6),
    msg_content varchar(2048) not null,
    msg_type varchar(255) not null check (msg_type in ('IMAGE','TEXT','EMOTICON')),
    sns_user_message_room_id bigint not null,
    source_user_id bigint not null,
    primary key (sns_user_message_id)
);

create table sns_users_tb (
    sns_user_id bigint not null,
    created_at timestamp(6),
    last_updated_at timestamp(6),
    last_updated_by bigint,
    birth_date date,
    deleted_at timestamp(6),
    email varchar(512),
    has_follower_notification boolean default true not null,
    hash_pw varchar(255),
    is_private_profile boolean default false not null,
    nickname varchar(30) not null,
    profile_path varchar(512),
    refresh_token varchar(255),
    sign_up_type varchar(255) check (sign_up_type in ('KAKAO','NAVER','GOOGLE','APPLE','EMAIL')),
    signup_email varchar(512),
    sns_app_role varchar(255) check (sns_app_role in ('ROLE_USER','ROLE_ADMIN')),
    sns_user_gender varchar(255) check (sns_user_gender in ('MALE','FEMALE','OTHERS')),
    sns_user_state varchar(255) check (sns_user_state in ('ACTIVE','DORMANT','SUSPENDED','PENDING','DELETED','FULL_DELETED')),
    social_id varchar(255),
    user_description varchar(1024),
    user_link varchar(1024),
    username varchar(18) not null,
    primary key (sns_user_id)
);

alter table if exists sns_block_users_tb
drop constraint if exists UKfatomlpd9japkrmjbbrn356u9;

alter table if exists sns_block_users_tb
add constraint UKfatomlpd9japkrmjbbrn356u9 unique (sns_blocker_user_id, sns_blocked_user_id);

create index IDX__USER_BY_SNS_NOTIFICATIONS on sns_notifications_tb (sns_user_id);

create index IDX__sns_post_comment_reaction_id_BY_SNS_POST_COMMENT_LIKES
on sns_post_comment_likes_tb (sns_post_comment_reaction_id, is_liked);

alter table if exists sns_post_comment_likes_tb
drop constraint if exists UK82sc2byv4ds2wc87dwax9lpu1;

alter table if exists sns_post_comment_likes_tb
add constraint UK82sc2byv4ds2wc87dwax9lpu1 unique (sns_post_comment_reaction_id, sns_user_id);

create index IDX__SOURCE_COMMENTS_BY_SNS_POST_COMMENT_REACTIONS
on sns_post_comment_reactions_tb (source_comment_id, is_commented);

create index IDX__reporter_user_id_BY_SNS_POST_REPORTS
on sns_post_reports_tb (reporter_user_id);

create index IDX__reported_user_id_BY_SNS_POST_REPORTS
on sns_post_reports_tb (reported_user_id);

create index IDX__sns_post_id_BY_SNS_POST_REPORTS
on sns_post_reports_tb (sns_post_id);

create index IDX__USER_BY_SNS_POST_USER_REACTIONS
on sns_post_user_reactions_tb (sns_user_id);

create index IDX__USER_BY_SNS_POSTS
on sns_posts_tb (sns_user_id);

create index IDX__LATITUDE_BY_SNS_POSTS
on sns_posts_tb (latitude);

alter table if exists sns_scraps_tb
drop constraint if exists UK6al5inu4n1x54kpe0s6nwwjy4;

alter table if exists sns_scraps_tb
add constraint UK6al5inu4n1x54kpe0s6nwwjy4 unique (sns_scrap_board_id, sns_user_id, sns_post_id);

create index IDX__TAG_BY_SNS_TAG_FOLLOWS
on sns_tag_follows_tb (sns_tag_id);

create index IDX__TAG_BY_SNS_TAG_POSTS
on sns_tag_posts_tb (sns_tag_id);

create index IDX__POST_BY_SNS_TAG_POSTS
on sns_tag_posts_tb (sns_post_id);

alter table if exists sns_tags_tb
drop constraint if exists UK2ilvya62qau6q64bga2407qur;

alter table if exists sns_tags_tb
add constraint UK2ilvya62qau6q64bga2407qur unique (tag_name);

create index IDX__USER_TERM_NAME_BY_SNS_USER_FAVORITE_TERM_BOOKMARKS
on sns_user_favorite_term_bookmarks_tb (sns_user_id, favorite_term_name);

alter table if exists sns_user_favorite_term_bookmarks_tb
drop constraint if exists UKcosfawj33ucu3f00t9qj0hyxv;

alter table if exists sns_user_favorite_term_bookmarks_tb
add constraint UKcosfawj33ucu3f00t9qj0hyxv unique (sns_user_id, favorite_term_name);

create index IDX__FOLLOWER_BY_SNS_USER_FOLLOWS
on sns_user_follows_tb (follower_id);

alter table if exists sns_user_follows_tb
drop constraint if exists UK1huub6qnitiknaqqcwebqcs0c;

alter table if exists sns_user_follows_tb
add constraint UK1huub6qnitiknaqqcwebqcs0c unique (following_id, follower_id);

create index IDX__ROOM_BY_SNS_USER_MESSAGE_ROOM_MEMBERS
on sns_user_message_room_members_tb (sns_user_message_room_id);

create index IDX__SOURCE_TARGET_USER_BY_SNS_USER_MESSAGE_ROOM_MEMBERS
on sns_user_message_room_members_tb (source_user_id, target_user_id, sns_user_message_room_id);

alter table if exists sns_user_message_room_members_tb
drop constraint if exists UKdu7r8fwd40waoa4jyj4od5kjg;

alter table if exists sns_user_message_room_members_tb
add constraint UKdu7r8fwd40waoa4jyj4od5kjg unique (source_user_id, target_user_id);

create index IDX__SOURCE_TARGET_MSG_READ_BY_SNS_USER_MESSAGES
on sns_user_messages_tb (source_user_id, sns_user_message_room_id, msg_type, msg_content, created_at);

alter table if exists sns_users_tb
drop constraint if exists UKlrjuv95fgeo0l516w961fcp19;

alter table if exists sns_users_tb
add constraint UKlrjuv95fgeo0l516w961fcp19 unique (signup_email);

alter table if exists sns_users_tb
drop constraint if exists UKj3ekdwvhxqyledtbc3e315cpj;

alter table if exists sns_users_tb
add constraint UKj3ekdwvhxqyledtbc3e315cpj unique (username);

alter table if exists sns_block_users_tb
add constraint FKqwasndattg2gslxa1yhh2y5ou
foreign key (sns_blocked_user_id)
references sns_users_tb;

alter table if exists sns_block_users_tb
add constraint FKp4ohtsekpjeal5252d63ti1dn
foreign key (sns_blocker_user_id)
references sns_users_tb;

alter table if exists sns_notifications_tb
add constraint FKdmq05s7ghe3rq469nf9stmqfm
foreign key (sns_follower_id)
references sns_users_tb;

alter table if exists sns_notifications_tb
add constraint FKk9pvsci97fecew241waepn8kn
foreign key (sns_post_id)
references sns_posts_tb;

alter table if exists sns_notifications_tb
add constraint FKc5hp9voaidvlbwfayai51it1c
foreign key (sns_user_id)
references sns_users_tb;

alter table if exists sns_post_comment_likes_tb
add constraint FK411mi11vbe37suy0763qhcm93
foreign key (sns_post_id)
references sns_posts_tb;

alter table if exists sns_post_comment_likes_tb
add constraint FKf8mqv1s35p8sn9bo1x8g1hc3b
foreign key (sns_post_comment_reaction_id)
references sns_post_comment_reactions_tb;

alter table if exists sns_post_comment_likes_tb
add constraint FKhi1xxpl4kvpah1g2fbmev88bn
foreign key (sns_user_id)
references sns_users_tb;

alter table if exists sns_post_comment_reactions_tb
add constraint FKdeqdq1xcssv4lv35iksaism2p
foreign key (comment_user_id)
references sns_users_tb;

alter table if exists sns_post_comment_reactions_tb
add constraint FKcmsfx3wh8o9ebowvjc1clyo2g
foreign key (sns_post_id)
references sns_posts_tb;

alter table if exists sns_post_comment_reactions_tb
add constraint FK1yp90nnihcb86lv3gi0ly37fb
foreign key (source_comment_id)
references sns_post_comment_reactions_tb;

alter table if exists sns_post_reports_tb
add constraint FKf3oofnhejpsi5e2yap7nncvw5
foreign key (reported_user_id)
references sns_users_tb;

alter table if exists sns_post_reports_tb
add constraint FKkr8cdwyqr8axlvqlg2nm1qo2i
foreign key (reporter_user_id)
references sns_users_tb;

alter table if exists sns_post_reports_tb
add constraint FKeerc5v85a8m9lswtqvsjjrv8r
foreign key (sns_post_id)
references sns_posts_tb;

alter table if exists sns_post_reports_tb
add constraint FK60nvd08wivqxb26034owj4edw
foreign key (sns_post_comment_reaction_id)
references sns_post_comment_reactions_tb;

alter table if exists sns_post_user_reactions_tb
add constraint FK69p4s2nj7dd4l86mm42ye23d9
foreign key (sns_post_id)
references sns_posts_tb;

alter table if exists sns_post_user_reactions_tb
add constraint FKq7f1qloagblhit0bwfg8spjbq
foreign key (sns_user_id)
references sns_users_tb;

alter table if exists sns_posts_tb
add constraint FK135cq3g19sm1bcix57jq8f974
foreign key (repost_origin_id)
references sns_posts_tb;

alter table if exists sns_posts_tb
add constraint FKn2m1j2lvikpx1kk7nw3fkwswg
foreign key (sns_user_id)
references sns_users_tb;

alter table if exists sns_scrap_boards_tb
add constraint FK510luk81fpw1x3jreh3m5l3d7
foreign key (sns_user_id)
references sns_users_tb;

alter table if exists sns_scraps_tb
add constraint FKgwwptj1ehy8dve58ml0fbrs0w
foreign key (sns_post_id)
references sns_posts_tb;

alter table if exists sns_scraps_tb
add constraint FKthwwmy8tlmk9jx01qpea7exv6
foreign key (sns_scrap_board_id)
references sns_scrap_boards_tb;

alter table if exists sns_scraps_tb
add constraint FK29b13sifnvaurcgxpe9f51f1m
foreign key (sns_user_id)
references sns_users_tb;

alter table if exists sns_tag_follows_tb
add constraint FKdikxastp0thy0mw9dg4xevlr1
foreign key (sns_tag_id)
references sns_tags_tb;

alter table if exists sns_tag_follows_tb
add constraint FKf5n3cjiu26o813jlnwvgmxecb
foreign key (sns_user_id)
references sns_users_tb;

alter table if exists sns_tag_posts_tb
add constraint FK7e6m47ma25vit05bux7dd4rbo
foreign key (sns_post_id)
references sns_posts_tb;

alter table if exists sns_tag_posts_tb
add constraint FK5v0ij9krwow52b22w3ki0h5f
foreign key (sns_tag_id)
references sns_tags_tb;

alter table if exists sns_user_favorite_term_bookmarks_tb
add constraint FK830ltcvpxfka7neh3asqatc06
foreign key (sns_tag_follow_id)
references sns_tag_follows_tb;

alter table if exists sns_user_favorite_term_bookmarks_tb
add constraint FKtbrnq42qe01x6itenjxy4lln1
foreign key (sns_user_id)
references sns_users_tb;

alter table if exists sns_user_follow_statistics_tb
add constraint FKod38hyoda880o220hsaafhk7g
foreign key (sns_user_id)
references sns_users_tb;

alter table if exists sns_user_follows_tb
add constraint FKnm7fh03iewnk5f3880q2kevnb
foreign key (follower_id)
references sns_users_tb;

alter table if exists sns_user_follows_tb
add constraint FKm56jhaauo19w7nt9oastfj9js
foreign key (following_id)
references sns_users_tb;

alter table if exists sns_user_message_reactions_tb
add constraint FKl165jxqk7ygh5xlk9mwmufnkg
foreign key (sns_user_message_id)
references sns_user_messages_tb;

alter table if exists sns_user_message_room_members_tb
add constraint FK5a8a105phjoegebvyduyiwbwe
foreign key (sns_user_message_room_id)
references sns_user_message_rooms_tb;

alter table if exists sns_user_message_room_members_tb
add constraint FKioxevrpss5smbaj75lpil4tbt
foreign key (source_user_id)
references sns_users_tb;

alter table if exists sns_user_message_room_members_tb
add constraint FK1n951qesqvxs23yct89cev2od
foreign key (target_user_id)
references sns_users_tb;

alter table if exists sns_user_messages_tb
add constraint FKec46fb6gkq1verim9yoei7qib
foreign key (sns_user_message_room_id)
references sns_user_message_rooms_tb;

alter table if exists sns_user_messages_tb
add constraint FKlbl3scqj84c904b9ae71529mx
foreign key (source_user_id)
references sns_users_tb;