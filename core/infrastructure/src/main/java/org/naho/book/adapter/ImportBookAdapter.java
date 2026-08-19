package org.naho.book.adapter;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.naho.book.entity.BookEntity;
import org.naho.book.entity.LessonEntity;
import org.naho.book.entity.ObjectiveEntity;
import org.naho.book.entity.TopicEntity;
import org.naho.book.exception.BookErrorCode;
import org.naho.book.port.out.ImportBookPort;
import org.naho.book.repository.BookJpaRepository;
import org.naho.book.type.TopicStatus;
import org.naho.chest.entity.ChestEntity;
import org.naho.chest.exception.ChestErrorCode;
import org.naho.chest.repository.ChestJpaRepository;
import org.naho.grammar.entity.GrammarEntity;
import org.naho.i18n.message.book.BookDetailMessageKey;
import org.naho.i18n.message.chest.ChestDetailMessageKey;
import org.naho.i18n.message.question.VocabularyQuestionDetailMessageKey;
import org.naho.learning.entity.LearningPathNodeEntity;
import org.naho.learning.type.NodeType;
import org.naho.question.entity.SpeakingQuestionEntity;
import org.naho.question.entity.VocabularyQuestionEntity;
import org.naho.question.exception.VocabularyQuestionErrorCode;
import org.naho.question.repository.GrammarJpaRepository;
import org.naho.question.repository.VocabularyQuestionJpaRepository;
import org.naho.question.type.QuestionStatus;
import org.naho.shared.exception.InfrastructureException;
import org.naho.vocabulary.entity.VocabularyEntity;
import org.naho.vocabulary.repository.VocabularyJpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ImportBookAdapter implements ImportBookPort {
    private final BookJpaRepository bookJpaRepository;
    private final VocabularyQuestionJpaRepository vocabularyQuestionJpaRepository;
    private final ChestJpaRepository chestJpaRepository;
    private final GrammarJpaRepository grammarJpaRepository;
    private final VocabularyJpaRepository vocabularyJpaRepository;

    @Override
    @Transactional
    public void importBookDataFromExcel(InputStream inputStream) {
        try (Workbook workbook = WorkbookFactory.create(inputStream)) {
            double nodeGlobalOrderIndex = 1;

            // loop through each sheet
            for (int sheetNum = 0; sheetNum < workbook.getNumberOfSheets(); sheetNum++) {
                Sheet sheet = workbook.getSheetAt(sheetNum);

                // get current book (each sheet is a book)
                Long bookId = (long) (sheetNum + 1);
                BookEntity bookEntity = bookJpaRepository
                        .findById(bookId)
                        .orElseThrow(() -> new InfrastructureException(
                                BookErrorCode.BOOK_NOT_FOUND,
                                BookDetailMessageKey.BOOK_ID_NOT_FOUND,
                                bookId));

                // loop through each row
                TopicEntity topicEntity = null;
                LessonEntity lessonEntity = null;
                ObjectiveEntity objectiveEntity = null;

                double topicOrderIndex = 1;
                double lessonOrderIndex = 1;
                double objectiveOrderIndex = 1;
                double nodeOrderIndex = 1;

                for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
                    Row row = sheet.getRow(rowNum);
                    if (row == null) {
                        continue;
                    }

                    // nếu gặp row là 1 topic mới
                    // Lấy cell 1 (cột 1) => Topic
                    Cell topicCell = row.getCell(1);
                    if (topicCell != null && topicCell.getCellType() != CellType.BLANK) {
                        // Lấy cell 2 (cột 2) => Topic Markup
                        Cell topicMarkupCell = row.getCell(2);

                        String topicMarkup = topicMarkupCell != null ?
                                topicMarkupCell.getStringCellValue().trim() :
                                "";

                        Cell topicDescriptionVietnameseCell = row.getCell(3);
                        String topicDescriptionVietnamese = topicDescriptionVietnameseCell != null ?
                                topicDescriptionVietnameseCell.getStringCellValue().trim() :
                                "";

                        Cell topicDescriptionEnglishCell = row.getCell(4);
                        String topicDescriptionEnglish = topicDescriptionEnglishCell != null ?
                                topicDescriptionEnglishCell.getStringCellValue().trim() :
                                "";

                        Cell topicDescriptionJapaneseCell = row.getCell(5);
                        String topicDescriptionJapanese = topicDescriptionJapaneseCell != null ?
                                topicDescriptionJapaneseCell.getStringCellValue().trim() :
                                "";

                        Cell topicDescriptionJapaneseMarkupCell = row.getCell(6);
                        String topicDescriptionJapaneseMarkup = topicDescriptionJapaneseMarkupCell != null ?
                                topicDescriptionJapaneseMarkupCell.getStringCellValue().trim() :
                                "";

                        topicEntity = TopicEntity.builder()
                                .japaneseName(topicCell.getStringCellValue().trim())
                                .japaneseNameMarkup(topicMarkup)
                                .vietnameseDescription(topicDescriptionVietnamese)
                                .englishDescription(topicDescriptionEnglish)
                                .japaneseDescription(topicDescriptionJapanese)
                                .japaneseDescriptionMarkup(topicDescriptionJapaneseMarkup)
                                .status(TopicStatus.PUBLISHED)
                                .orderIndex(topicOrderIndex)
                                .book(bookEntity)
                                .build();

                        bookEntity.getTopics().add(topicEntity);
                        topicOrderIndex += 1.0;

                        lessonOrderIndex = 1;
                        objectiveOrderIndex = 1;
                        nodeOrderIndex = 1;
                    }

                    if (topicEntity == null) {
                        throw new InfrastructureException(
                                BookErrorCode.BOOK_IMPORT_FAILED,
                                BookDetailMessageKey.BOOK_IMPORT_TOPIC_NULL);
                    }

                    // lesson
                    Cell lessonCell = row.getCell(7);
                    if (lessonCell != null && lessonCell.getCellType() != CellType.BLANK) {

                        Cell lessonMarkupCell = row.getCell(8);
                        String lessonMarkup = lessonMarkupCell != null ?
                                lessonMarkupCell.getStringCellValue().trim() :
                                "";

                        lessonEntity = LessonEntity.builder()
                                .japaneseName(lessonCell.getStringCellValue().trim())
                                .japaneseNameMarkup(lessonMarkup)
                                .status(TopicStatus.PUBLISHED)
                                .orderIndex(lessonOrderIndex)
                                .topic(topicEntity)
                                .build();

                        topicEntity.getLessons().add(lessonEntity);
                        lessonOrderIndex += 1.0;

                        objectiveOrderIndex = 1;
                        nodeOrderIndex = 1;
                    }

                    if (lessonEntity == null) {
                        throw new InfrastructureException(
                                BookErrorCode.BOOK_IMPORT_FAILED,
                                BookDetailMessageKey.BOOK_IMPORT_LESSON_NULL);
                    }

                    // objective
                    Cell objectiveCell = row.getCell(9);
                    if (objectiveCell != null && objectiveCell.getCellType() != CellType.BLANK) {

                        Cell objectiveMarkupCell = row.getCell(10);
                        String objectiveMarkup = objectiveMarkupCell != null ?
                                objectiveMarkupCell.getStringCellValue().trim() :
                                "";

                        objectiveEntity = ObjectiveEntity.builder()
                                .japaneseName(objectiveCell.getStringCellValue().trim())
                                .japaneseNameMarkup(objectiveMarkup)
                                .status(TopicStatus.PUBLISHED)
                                .orderIndex(objectiveOrderIndex)
                                .lesson(lessonEntity)
                                .build();

                        lessonEntity.getObjectives().add(objectiveEntity);
                        objectiveOrderIndex += 1.0;

                        nodeOrderIndex = 1;
                    }

                    if (objectiveEntity == null) {
                        throw new InfrastructureException(
                                BookErrorCode.BOOK_IMPORT_FAILED,
                                BookDetailMessageKey.BOOK_IMPORT_OBJECTIVE_NULL);
                    }

                    // 3 types of a node: speaking question, vocabulary question, chest
                    Cell nodeTypeCell = row.getCell(11);
                    if (nodeTypeCell == null ||
                            nodeTypeCell.getCellType() == CellType.BLANK ||
                            nodeTypeCell.getStringCellValue().isBlank()) {
                        continue;
                    }

                    NodeType nodeType = NodeType.valueOf(nodeTypeCell.getStringCellValue().trim());
                    LearningPathNodeEntity learningPathNodeEntity = LearningPathNodeEntity.builder()
                            .globalOrderIndex(nodeGlobalOrderIndex)
                            .orderIndex(nodeOrderIndex)
                            .nodeType(nodeType)
                            .objective(objectiveEntity)
                            .build();

                    objectiveEntity.getLearningPathNodes().add(learningPathNodeEntity);

                    // set first and last node global order index for entities
                    if (objectiveEntity.getFirstNodeGlobalOrderIndex() == null) {
                        objectiveEntity.setFirstNodeGlobalOrderIndex(nodeGlobalOrderIndex);
                    }
                    objectiveEntity.setLastNodeGlobalOrderIndex(nodeGlobalOrderIndex);

                    if (lessonEntity.getFirstNodeGlobalOrderIndex() == null) {
                        lessonEntity.setFirstNodeGlobalOrderIndex(nodeGlobalOrderIndex);
                    }
                    lessonEntity.setLastNodeGlobalOrderIndex(nodeGlobalOrderIndex);

                    if (topicEntity.getFirstNodeGlobalOrderIndex() == null) {
                        topicEntity.setFirstNodeGlobalOrderIndex(nodeGlobalOrderIndex);
                    }
                    topicEntity.setLastNodeGlobalOrderIndex(nodeGlobalOrderIndex);

                    if (bookEntity.getFirstNodeGlobalOrderIndex() == null) {
                        bookEntity.setFirstNodeGlobalOrderIndex(nodeGlobalOrderIndex);
                    }
                    bookEntity.setLastNodeGlobalOrderIndex(nodeGlobalOrderIndex);

                    nodeGlobalOrderIndex += 1.0;
                    nodeOrderIndex += 1.0;

                    switch (nodeType) {
                        case SPEAKING_QUESTION -> {
                            // tiêu đề gốc của speaking question
                            Cell speakingQuestionCell = row.getCell(12);
                            String japaneseName = (speakingQuestionCell != null)
                                    ? speakingQuestionCell.getStringCellValue().trim()
                                    : "";

                            // bản markup của tiêu đề của speaking question
                            Cell speakingQuestionMarkupCell = row.getCell(13);
                            String japaneseMarkup = speakingQuestionMarkupCell != null ?
                                    speakingQuestionMarkupCell.getStringCellValue().trim() :
                                    "";

                            // Bản dịch tiếng việt của tiêu đề
                            Cell vietnameseSpeakingQuestionCell = row.getCell(14);
                            String vietnameseName = vietnameseSpeakingQuestionCell != null ?
                                    vietnameseSpeakingQuestionCell.getStringCellValue().trim() :
                                    "";

                            // câu trả lời mẫu (tiếng Nhật)
                            Cell japaneseSampleAnswerCell = row.getCell(17);
                            String japaneseSampleAnswer = japaneseSampleAnswerCell != null ?
                                    japaneseSampleAnswerCell.getStringCellValue().trim() :
                                    "";

                            // câu trả lời mẫu bản markup
                            Cell japaneseSampleAnswerMarkupCell = row.getCell(18);
                            String japaneseSampleAnswerMarkup = japaneseSampleAnswerMarkupCell != null ?
                                    japaneseSampleAnswerMarkupCell.getStringCellValue().trim() :
                                    "";

                            // câu trả lời mẫu tiếng Việt
                            Cell vietnameseSampleAnswerCell = row.getCell(19);
                            String vietnameseSampleAnswer = vietnameseSampleAnswerCell != null ?
                                    vietnameseSampleAnswerCell.getStringCellValue().trim() :
                                    "";

                            // câu trả lời mẫu tiếng Anh
                            Cell englishSampleAnswerCell = row.getCell(20);
                            String englishSampleAnswer = englishSampleAnswerCell != null ?
                                    englishSampleAnswerCell.getStringCellValue().trim() :
                                    "";

                            SpeakingQuestionEntity speakingQuestionEntity = SpeakingQuestionEntity
                                    .builder()
                                    .japaneseName(japaneseName)
                                    .japaneseNameMarkup(japaneseMarkup)
                                    .vietnameseName(vietnameseName)
                                    .status(QuestionStatus.ACTIVE)
                                    .learningPathNode(learningPathNodeEntity)
                                    .japaneseSampleAnswer(japaneseSampleAnswer)
                                    .japaneseSampleAnswerMarkup(japaneseSampleAnswerMarkup)
                                    .vietnameseSampleAnswer(vietnameseSampleAnswer)
                                    .englishSampleAnswer(englishSampleAnswer)
                                    .build();

                            // add grammars to speaking question
                            Cell grammarCell = row.getCell(15);
                            if (grammarCell != null && grammarCell.getCellType() != CellType.BLANK) {
                                List<Long> grammarIds = Arrays
                                        .stream(grammarCell.getStringCellValue().trim().split(","))
                                        .map(Long::parseLong)
                                        .toList();

                                List<GrammarEntity> grammarEntityList = grammarJpaRepository.findAllByIdIn(grammarIds);
                                speakingQuestionEntity.setGrammars(grammarEntityList);
                            }

                            // add vocabularies to speaking question
                            Cell vocabularyCell = row.getCell(16);
                            if (vocabularyCell != null && vocabularyCell.getCellType() != CellType.BLANK) {
                                List<Long> vocabularyIds = Arrays
                                        .stream(vocabularyCell.getStringCellValue().trim().split(","))
                                        .map(Long::parseLong)
                                        .toList();

                                List<VocabularyEntity> vocabularyEntityList = vocabularyJpaRepository
                                        .findAllByIdIn(vocabularyIds);
                                speakingQuestionEntity.setVocabularies(vocabularyEntityList);
                            }

                            learningPathNodeEntity.setSpeakingQuestion(speakingQuestionEntity);
                        }
                        case VOCABULARY_QUESTION -> {
                            Cell vocabularyQuestionCell = row.getCell(12);
                            Long vocabularyQuestionId = (long) vocabularyQuestionCell.getNumericCellValue();
                            VocabularyQuestionEntity vocabularyQuestionEntity = vocabularyQuestionJpaRepository
                                    .findById(vocabularyQuestionId)
                                    .orElseThrow(() -> new InfrastructureException(
                                            VocabularyQuestionErrorCode.VOCABULARY_QUESTION_NOT_FOUND,
                                            VocabularyQuestionDetailMessageKey.VOCABULARY_QUESTION_NOT_FOUND,
                                            vocabularyQuestionId));

                            // add vocabularies to vocabulary question
                            Cell vocabularyCell = row.getCell(16);
                            if (vocabularyCell != null && vocabularyCell.getCellType() != CellType.BLANK) {
                                List<Long> vocabularyIds = Arrays
                                        .stream(vocabularyCell.getStringCellValue().trim().split(","))
                                        .map(Long::parseLong)
                                        .toList();

                                List<VocabularyEntity> vocabularyEntityList = vocabularyJpaRepository
                                        .findAllByIdIn(vocabularyIds);
                                vocabularyQuestionEntity.setVocabularies(new ArrayList<>(vocabularyEntityList));
                            }

                            learningPathNodeEntity.setVocabularyQuestion(vocabularyQuestionEntity);
                        }
                        case CHEST -> {
                            Cell chestCell = row.getCell(12);
                            Long chestId = (long) chestCell.getNumericCellValue();
                            ChestEntity chestEntity = chestJpaRepository.findById(chestId)
                                    .orElseThrow(() -> new InfrastructureException(
                                            ChestErrorCode.CHEST_NOT_FOUND,
                                            ChestDetailMessageKey.CHEST_NOT_FOUND,
                                            chestId
                                    ));

                            learningPathNodeEntity.setChest(chestEntity);
                        }
                    }
                }
                bookJpaRepository.save(bookEntity);
            }
        } catch (IOException e) {
            throw new InfrastructureException(
                    BookErrorCode.BOOK_IMPORT_FAILED,
                    BookDetailMessageKey.BOOK_IMPORT_FAILED,
                    e.getMessage());
        }
    }
}
