package org.naho.user.command;

public class GoogleLoginCommand {

    private final String sub;
    private final String email;
    private final String fullName;
    private final String pictureUrl;
    private final String deviceId;
    private final String userAgent;
    private final String ipAddress;

    private GoogleLoginCommand(Builder builder) {
        this.sub = builder.sub;
        this.email = builder.email;
        this.fullName = builder.fullName;
        this.pictureUrl = builder.pictureUrl;
        this.deviceId = builder.deviceId;
        this.userAgent = builder.userAgent;
        this.ipAddress = builder.ipAddress;
    }

    public String getSub() {
        return sub;
    }

    public String getEmail() {
        return email;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public boolean isDeviceIdBlank() {
        return deviceId == null || deviceId.isBlank();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String sub;
        private String email;
        private String fullName;
        private String pictureUrl;
        private String deviceId;
        private String userAgent;
        private String ipAddress;

        public Builder sub(String sub) {
            this.sub = sub;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public Builder pictureUrl(String pictureUrl) {
            this.pictureUrl = pictureUrl;
            return this;
        }

        public Builder deviceId(String deviceId) {
            this.deviceId = deviceId;
            return this;
        }

        public Builder userAgent(String userAgent) {
            this.userAgent = userAgent;
            return this;
        }

        public Builder ipAddress(String ipAddress) {
            this.ipAddress = ipAddress;
            return this;
        }

        public GoogleLoginCommand build() {
            return new GoogleLoginCommand(this);
        }
    }
}