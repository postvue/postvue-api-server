alter table if exists sns_scraps_tb
add column deleted_at timestamp(6);

alter table if exists sns_tag_follows_tb
drop constraint if exists UK6y30ef12h1svmakrhkpj9057s;

alter table if exists sns_tag_follows_tb
add constraint UK6y30ef12h1svmakrhkpj9057s unique (sns_user_id, sns_tag_id);

-- # Sns User Message Table
-- 기존 인덱스 제거
DROP INDEX IF EXISTS IDX__SOURCE_TARGET_MSG_READ_BY_SNS_USER_MESSAGES;

ALTER TABLE SNS_USER_MESSAGES_TB
DROP COLUMN msg_type;

ALTER TABLE SNS_USER_MESSAGES_TB
DROP COLUMN msg_content;

ALTER TABLE SNS_USER_MESSAGES_TB
ADD COLUMN msg_text_content VARCHAR(2048);

ALTER TABLE SNS_USER_MESSAGES_TB
ADD COLUMN msg_media_type VARCHAR(255)
CHECK (msg_media_type IN ('VIDEO', 'IMAGE'));

ALTER TABLE SNS_USER_MESSAGES_TB
ADD COLUMN msg_media_content VARCHAR(2048);

-- 새로운 인덱스 생성
CREATE INDEX IDX__SOURCE_TARGET_MSG_READ_BY_SNS_USER_MESSAGES
ON SNS_USER_MESSAGES_TB (source_user_id, sns_user_message_room_id, created_at);

ALTER TABLE IF EXISTS sns_user_messages_tb
ADD COLUMN IF NOT EXISTS msg_meta_info jsonb DEFAULT '{"ogTitle": "", "ogImage": "", "ogDescription": ""}'::jsonb NOT NULL;

ALTER TABLE sns_posts_tb
ALTER COLUMN post_body_text TYPE VARCHAR(1024);

ALTER TABLE sns_posts_tb
ALTER COLUMN post_body_text TYPE VARCHAR(1024);

ALTER TABLE if EXISTS sns_posts_tb
ALTER COLUMN address SET DATA TYPE VARCHAR(511);

ALTER TABLE IF EXISTS sns_posts_tb
ADD COLUMN IF NOT EXISTS build_name VARCHAR(511);

CREATE INDEX IDX__REACTION_COUNT_BY_SNS_POSTS
ON sns_posts_tb (reaction_count);

-- build_name에 대한 pgroonga 인덱스 생성
CREATE INDEX IDX__PGROONGA__BUILD_NAME_BY_SNS_POSTS
ON sns_posts_tb
USING pgroonga (build_name);

CREATE INDEX IDX__PGROONGA__ADDRESS_BY_SNS_POSTS
ON sns_posts_tb
USING pgroonga (address);

-- 유저 id 유니크
alter table if exists sns_user_follow_statistics_tb
drop constraint if exists IDX__USER_ID_UNIQUE_BY_SNS_USER_FOLLOW_STATISTICS;

alter table if exists sns_user_follow_statistics_tb
add constraint IDX__USER_ID_UNIQUE_BY_SNS_USER_FOLLOW_STATISTICS unique (sns_user_id);

-- 스크랩
CREATE INDEX IDX__SNS_SCRAP_BOARD_ID_BY_SNS_SCRAPS
ON sns_scraps_tb (sns_scrap_board_id);

CREATE INDEX IDX__SNS_POST_ID_BY_SNS_SCRAPS
ON sns_scraps_tb (sns_post_id);

CREATE INDEX IDX__SNS_USER_ID_BY_SNS_SCRAPS
ON sns_scraps_tb (sns_user_id);

CREATE INDEX IDX__SCRAP_BOARD_ID_AND_SNS_POST_ID_BY_SNS_SCRAPS
ON sns_scraps_tb (sns_scrap_board_id, sns_post_id);

-- 포스트
alter table if exists SNS_POSTS_TB
DROP column post_caption_content;

-- 포스트 리액션
alter table if exists sns_post_user_reactions_tb
drop constraint if exists IDX_UNIQUE_USER_POST_ID_BY_SNS_POST_USER_REACTIONS;

alter table if exists sns_post_user_reactions_tb
add constraint IDX_UNIQUE_USER_POST_ID_BY_SNS_POST_USER_REACTIONS unique (sns_post_id, sns_user_id);

-- 포스트 댓글 리액션
DROP INDEX IDX__SOURCE_COMMENTS_BY_SNS_POST_COMMENT_REACTIONS;

alter table if exists SNS_POST_COMMENT_REACTIONS_TB
DROP column is_commented;

CREATE INDEX IDX__SOURCE_COMMENTS_BY_SOURCE_COMMENT_ID
ON sns_post_comment_reactions_tb (source_comment_id);

-- 기존 CHECK 제약 조건 삭제 (제약 조건 이름 필요)
ALTER TABLE sns_post_comment_reactions_tb DROP CONSTRAINT sns_post_comment_reactions_tb_comment_media_type_check;

-- 새로운 CHECK 제약 조건 추가
ALTER TABLE sns_post_comment_reactions_tb
ADD CONSTRAINT sns_post_comment_reactions_tb_comment_media_type_check
CHECK (comment_media_type IN ('IMAGE', 'VIDEO', 'NONE'));

-- tag
alter table if exists sns_tags_tb
add column deleted_at timestamp(6);
