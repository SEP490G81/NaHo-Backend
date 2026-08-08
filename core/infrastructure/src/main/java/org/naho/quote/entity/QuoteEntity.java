package org.naho.quote.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "quote")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuoteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "kanji", nullable = false, length = 500)
    private String kanji;

    @Column(name = "hiragana", nullable = false, length = 500)
    private String hiragana;

    @Column(name = "romaji", nullable = false, length = 500)
    private String romaji;

    @Column(name = "translation", nullable = false, columnDefinition = "TEXT")
    private String translation;

    @Column(name = "kanji_detail", columnDefinition = "TEXT")
    private String kanjiDetail;
}
