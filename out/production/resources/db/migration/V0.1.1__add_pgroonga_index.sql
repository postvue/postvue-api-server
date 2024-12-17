-- postTitle에 대한 pgroonga 인덱스 생성
CREATE INDEX IDX__PGROONGA__POST_TITLE_BY_SNS_POSTS
ON sns_posts_tb
USING pgroonga (post_title);

-- postBodyText에 대한 pgroonga 인덱스 생성
CREATE INDEX IDX__PGROONGA__POST_BODY_TEXT_BY_SNS_POSTS
ON sns_posts_tb
USING pgroonga (post_body_text);

-- tag 이름에 대한 pgroonga 인덱스 생성
CREATE INDEX IDX__PGROONGA__TAG_NAME_BY_SNS_TAGS
ON SNS_TAGS_TB
USING pgroonga (tag_name);

-- scrap 이름에 대한 pgroonga 인덱스 생성
CREATE INDEX IDX__PGROONGA__SCRAP_NAME_BY_SNS_SCRAP_BOARDS ON
SNS_SCRAP_BOARDS_TB
USING pgroonga (scrap_name);