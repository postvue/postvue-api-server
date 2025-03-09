CREATE TABLE sns_user_reports_tb (
sns_user_report_id bigint not null,
created_at timestamp(6),
last_updated_at timestamp(6),
last_updated_by bigint,
report_reason varchar(2047) not null,
user_report_reason_type varchar(255) not null check (user_report_reason_type in ('INAPPROPRIATE_CONTENT','SPAM_OR_PROMOTIONAL_CONTENT','FALSE_INFORMATION_FRAUD','PRIVACY_VIOLATION','COPYRIGHT_INFRINGEMENT','HARASSMENT_OR_BULLYING','OTHER')),
user_report_status varchar(255) not null check (user_report_status in ('PENDING','REVIEWED','RESOLVED')),
reported_user_id bigint,
reporter_user_id bigint not null,
primary key (sns_user_report_id)
);

CREATE INDEX IDX__reporter_user_id_BY_SNS_USER_REPORTS
ON sns_user_reports_tb (reporter_user_id);

CREATE INDEX IDX__reported_user_id_BY_SNS_USER_REPORTS
ON sns_user_reports_tb (reported_user_id);

ALTER TABLE if EXISTS sns_user_reports_tb
ADD CONSTRAINT FK9dt8q4x0luij0odiyhbv7jsjd
FOREIGN KEY (reported_user_id)
REFERENCES sns_users_tb;

ALTER TABLE if EXISTS sns_user_reports_tb
ADD CONSTRAINT FK6cdk3xn9atp5sgmev8a1q7ukg
FOREIGN KEY (reporter_user_id)
REFERENCES sns_users_tb;