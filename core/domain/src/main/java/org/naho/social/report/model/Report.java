package org.naho.social.report.model;

import org.naho.file.model.File;
import org.naho.i18n.message.social.ReportDetailMessageKey;
import org.naho.shared.exception.DomainException;
import org.naho.social.report.exception.ReportDomainErrorCode;
import org.naho.social.report.type.ReportType;

import java.util.List;

public class Report {

    private final Long id;
    private final Long userId;
    private final String fullName;
    private final Long questionId;
    private final Long commentId;
    private final String title;
    private final String description;
    private final ReportType reportType;
    private final boolean isResolved;
    private final String adminReply;
    private List<File> files;

    private Report(Builder builder) {
        this.id = builder.id;
        this.userId = builder.userId;
        this.fullName = builder.fullName;
        this.questionId = builder.questionId;
        this.commentId = builder.commentId;
        this.title = builder.title;
        this.description = builder.description;
        this.reportType = builder.reportType;
        this.isResolved = builder.isResolved;
        this.adminReply = builder.adminReply;
        this.files = builder.files != null ? builder.files : List.of();
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getFullName() {
        return fullName;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public Long getCommentId() {
        return commentId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public ReportType getReportType() {
        return reportType;
    }

    public boolean isResolved() {
        return isResolved;
    }

    public String getAdminReply() {
        return adminReply;
    }

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }

    public Report updateStatus(boolean isResolved, String adminReply) {
        if (this.isResolved) {
            throw new DomainException(
                    ReportDomainErrorCode.REPORT_ALREADY_RESOLVED,
                    ReportDetailMessageKey.REPORT_ALREADY_RESOLVED
            );
        }
        if (!isResolved) {
            throw new DomainException(
                    ReportDomainErrorCode.REPORT_STATUS_CANNOT_BE_UNRESOLVED,
                    ReportDetailMessageKey.REPORT_STATUS_CANNOT_BE_UNRESOLVED
            );
        }
        return builder()
                .id(this.id)
                .userId(this.userId)
                .fullName(this.fullName)
                .questionId(this.questionId)
                .commentId(this.commentId)
                .title(this.title)
                .description(this.description)
                .reportType(this.reportType)
                .isResolved(isResolved)
                .adminReply(adminReply)
                .files(this.files)
                .build();
    }

    public static class Builder {

        private Long id;
        private Long userId;
        private String fullName;
        private Long questionId;
        private Long commentId;
        private String title;
        private String description;
        private ReportType reportType;
        private boolean isResolved;
        private String adminReply;
        private List<File> files;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public Builder questionId(Long questionId) {
            this.questionId = questionId;
            return this;
        }

        public Builder commentId(Long commentId) {
            this.commentId = commentId;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder reportType(ReportType reportType) {
            this.reportType = reportType;
            return this;
        }

        public Builder isResolved(boolean isResolved) {
            this.isResolved = isResolved;
            return this;
        }

        public Builder adminReply(String adminReply) {
            this.adminReply = adminReply;
            return this;
        }

        public Builder files(List<File> files) {
            this.files = files;
            return this;
        }

        public Report build() {

            if (userId == null) {
                throw new DomainException(
                        ReportDomainErrorCode.REPORT_USER_ID_NOT_VALID,
                        ReportDetailMessageKey.REPORT_USER_ID_BLANK
                );
            }

            if (reportType == null) {
                throw new DomainException(
                        ReportDomainErrorCode.REPORT_TYPE_NOT_VALID,
                        ReportDetailMessageKey.REPORT_TYPE_BLANK
                );
            }

            if (title == null || title.isBlank()) {
                throw new DomainException(
                        ReportDomainErrorCode.REPORT_TITLE_NOT_VALID,
                        ReportDetailMessageKey.REPORT_TITLE_BLANK
                );
            }

            if (description == null || description.isBlank()) {
                throw new DomainException(
                        ReportDomainErrorCode.REPORT_DESCRIPTION_NOT_VALID,
                        ReportDetailMessageKey.REPORT_DESCRIPTION_BLANK
                );
            }

            if (ReportType.QUESTION.equals(reportType) && questionId == null) {
                throw new DomainException(
                        ReportDomainErrorCode.REPORT_QUESTION_ID_NOT_VALID,
                        ReportDetailMessageKey.REPORT_QUESTION_ID_BLANK
                );
            }

            if (ReportType.COMMENT.equals(reportType) && commentId == null) {
                throw new DomainException(
                        ReportDomainErrorCode.REPORT_COMMENT_ID_NOT_VALID,
                        ReportDetailMessageKey.REPORT_COMMENT_ID_BLANK
                );
            }

            return new Report(this);
        }
    }
}
