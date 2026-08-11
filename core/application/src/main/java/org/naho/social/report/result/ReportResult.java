package org.naho.social.report.result;

import org.naho.file.result.FileResult;
import org.naho.social.report.type.ReportType;

import java.util.List;

public class ReportResult {

    private Long id;
    private Long userId;
    private String fullName;
    private String title;
    private String description;
    private ReportType reportType;
    private Boolean isResolved;
    private Long questionId;
    private Long commentId;
    private List<FileResult> files;

    public ReportResult() {
    }

    public ReportResult(Long id, Long userId, String fullName, String title, String description, ReportType reportType, Boolean isResolved, Long questionId, Long commentId, List<FileResult> files) {
        this.id = id;
        this.userId = userId;
        this.fullName = fullName;
        this.title = title;
        this.description = description;
        this.reportType = reportType;
        this.isResolved = isResolved;
        this.questionId = questionId;
        this.commentId = commentId;
        this.files = files;
    }

    private ReportResult(Builder builder) {
        this.id = builder.id;
        this.userId = builder.userId;
        this.fullName = builder.fullName;
        this.title = builder.title;
        this.description = builder.description;
        this.reportType = builder.reportType;
        this.isResolved = builder.isResolved;
        this.questionId = builder.questionId;
        this.commentId = builder.commentId;
        this.files = builder.files;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ReportType getReportType() {
        return reportType;
    }

    public void setReportType(ReportType reportType) {
        this.reportType = reportType;
    }

    public Boolean getIsResolved() {
        return isResolved;
    }

    public void setIsResolved(Boolean isResolved) {
        this.isResolved = isResolved;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Long getCommentId() {
        return commentId;
    }

    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }

    public List<FileResult> getFiles() {
        return files;
    }

    public void setFiles(List<FileResult> files) {
        this.files = files;
    }

    public static final class Builder {
        private Long id;
        private Long userId;
        private String fullName;
        private String title;
        private String description;
        private ReportType reportType;
        private Boolean isResolved;
        private Long questionId;
        private Long commentId;
        private List<FileResult> files;

        private Builder() {
        }

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

        public Builder isResolved(Boolean isResolved) {
            this.isResolved = isResolved;
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

        public Builder files(List<FileResult> files) {
            this.files = files;
            return this;
        }

        public ReportResult build() {
            return new ReportResult(this);
        }
    }
}