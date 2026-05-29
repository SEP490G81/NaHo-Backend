package org.naho.speech.llm.usecase;

import org.naho.speech.llm.port.in.SuggestedTopicsInputPort;
import org.naho.speech.llm.result.SuggestedTopicsResult;
import org.naho.speech.llm.result.SuggestedTopicsResult.TopicItem;

import java.util.List;

public class SuggestedTopicsUseCase implements SuggestedTopicsInputPort {
    private static final List<TopicItem> TOPICS = List.of(
            new TopicItem("自己紹介", "Self-introduction",
                    "Giới thiệu bản thân: tên, tuổi, quốc gia, sở thích", "N5"),
            new TopicItem("毎日の生活", "Daily Life",
                    "Nói về thói quen hàng ngày: thức dậy, ăn sáng, đi làm/học", "N5"),
            new TopicItem("食べ物", "Food",
                    "Nói về món ăn yêu thích, nhà hàng, nấu ăn", "N5"),
            new TopicItem("家族", "Family",
                    "Giới thiệu về gia đình, thành viên trong nhà", "N5"),

            new TopicItem("旅行", "Travel",
                    "Nói về chuyến du lịch, địa điểm muốn đi, trải nghiệm", "N4"),
            new TopicItem("趣味", "Hobbies",
                    "Chia sẻ sở thích: thể thao, âm nhạc, đọc sách, game", "N4"),
            new TopicItem("天気と季節", "Weather & Seasons",
                    "Nói về thời tiết, mùa yêu thích, hoạt động theo mùa", "N4"),
            new TopicItem("買い物", "Shopping",
                    "Nói về mua sắm, giá cả, cửa hàng yêu thích", "N4"),

            new TopicItem("仕事と将来", "Work & Future",
                    "Nói về công việc hiện tại, mục tiêu tương lai, career", "N3"),
            new TopicItem("日本の文化", "Japanese Culture",
                    "Thảo luận về văn hóa Nhật: anime, manga, kimono, lễ hội", "N3"),
            new TopicItem("健康", "Health",
                    "Nói về sức khỏe, tập thể dục, chế độ ăn uống", "N3"),
            new TopicItem("テクノロジー", "Technology",
                    "Nói về smartphone, AI, internet, mạng xã hội", "N3"),

            new TopicItem("環境問題", "Environmental Issues",
                    "Thảo luận về ô nhiễm, biến đổi khí hậu, tái chế", "N2"),
            new TopicItem("教育", "Education",
                    "Nói về hệ thống giáo dục, cách học, trường học", "N2"),
            new TopicItem("ニュース", "News & Current Events",
                    "Thảo luận về sự kiện thời sự, tin tức nổi bật", "N2"),

            new TopicItem("社会問題", "Social Issues",
                    "Thảo luận sâu về vấn đề xã hội, bất bình đẳng, già hóa", "N1"),
            new TopicItem("哲学 và 人生", "Philosophy & Life",
                    "Nói về quan điểm sống, triết lý, ý nghĩa cuộc đời", "N1")
    );

    @Override
    public SuggestedTopicsResult getSuggestedTopics() {
        return new SuggestedTopicsResult(TOPICS);
    }
}
