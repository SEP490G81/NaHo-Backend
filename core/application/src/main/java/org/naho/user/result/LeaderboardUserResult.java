package org.naho.user.result;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardUserResult {
    private Long id;
    private Long leagueId;
    private Integer rank;
    private String username;
    private String email;
    private String fullName;
    private String avatarObjectKey;
    private List<String> oAuthAvatarUrl = new ArrayList<String>();
    private Double totalPoint;

    public LeaderboardUserResult() {
    }

    public LeaderboardUserResult(Long id, Long leagueId, Integer rank, String username, String email, String fullName, String avatarObjectKey, List<String> oAuthAvatarUrl, Double totalPoint) {
        this.id = id;
        this.leagueId = leagueId;
        this.rank = rank;
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.avatarObjectKey = avatarObjectKey;
        this.oAuthAvatarUrl = oAuthAvatarUrl;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public List<String> getoAuthAvatarUrl() {
        return oAuthAvatarUrl;
    }

    public void setoAuthAvatarUrl(List<String> oAuthAvatarUrl) {
        this.oAuthAvatarUrl = oAuthAvatarUrl;
    }

    public Double getTotalPoint() {
        return totalPoint;
    }

    public void setTotalPoint(Double totalPoint) {
        this.totalPoint = totalPoint;
    }
}
