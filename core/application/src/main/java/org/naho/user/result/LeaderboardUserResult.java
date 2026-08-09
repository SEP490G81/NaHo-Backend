package org.naho.user.result;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardUserResult {
    private Long id;
    private Long leagueId;
    private Integer rank;
    private String fullName;
    private String avatarObjectKey;
    private List<String> authAvatarUrl = new ArrayList<String>();
    private Double totalPoint;

    public LeaderboardUserResult() {
    }

    public LeaderboardUserResult(Long id, Long leagueId, Integer rank, String fullName, String avatarObjectKey, List<String> authAvatarUrl, Double totalPoint) {
        this.id = id;
        this.leagueId = leagueId;
        this.rank = rank;
        this.fullName = fullName;
        this.avatarObjectKey = avatarObjectKey;
        this.authAvatarUrl = authAvatarUrl;
        this.totalPoint = totalPoint;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getLeagueId() {
        return leagueId;
    }

    public void setLeagueId(Long leagueId) {
        this.leagueId = leagueId;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAvatarObjectKey() {
        return avatarObjectKey;
    }

    public void setAvatarObjectKey(String avatarObjectKey) {
        this.avatarObjectKey = avatarObjectKey;
    }

    public List<String> getAuthAvatarUrl() {
        return authAvatarUrl;
    }

    public void setAuthAvatarUrl(List<String> authAvatarUrl) {
        this.authAvatarUrl = authAvatarUrl;
    }

    public Double getTotalPoint() {
        return totalPoint;
    }

    public void setTotalPoint(Double totalPoint) {
        this.totalPoint = totalPoint;
    }
}
