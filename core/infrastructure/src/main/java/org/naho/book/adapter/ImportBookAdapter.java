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
import org.naho.i18n.message.book.BookDetailMessageKey;
import org.naho.learning.entity.LearningPathNodeEntity;
import org.naho.learning.type.NodeType;
import org.naho.question.entity.SpeakingQuestionEntity;
import org.naho.question.type.QuestionStatus;
import org.naho.shared.exception.InfrastructureException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;

@Component
@RequiredArgsConstructor
public class ImportBookAdapter implements ImportBookPort {
    private final BookJpaRepository bookJpaRepository;

    @Override
    @Transactional
    public void importBookDataFromExcel(InputStream inputStream) {
        try (Workbook workbook = WorkbookFactory.create(inputStream)) {
            double nodeGlobalOrderIndex = 1;

            // loop through each sheet
            for (int sheetNum = 0; sheetNum < workbook.getNumberOfSheets(); sheetNum++) {
                Sheet sheet = workbook.getSheetAt(sheetNum);

                Long bookId = (long) (sheetNum + 1);
                BookEntity bookEntity = bookJpaRepository
                        .findById(bookId)
                        .orElseThrow(() -> new InfrastructureException(
                                BookErrorCode.BOOK_NOT_FOUND,
                                BookDetailMessageKey.BOOK_ID_NOT_FOUND,
                                bookId
                        ));

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

                    Cell topicCell = row.getCell(1);
                    if (topicCell != null && topicCell.getCellType() != CellType.BLANK) {
                        topicEntity = TopicEntity.builder()
                                .japaneseName(topicCell.getStringCellValue().trim())
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
                                BookDetailMessageKey.BOOK_IMPORT_TOPIC_NULL
                        );
                    }

                    Cell lessonCell = row.getCell(2);
                    if (lessonCell != null && lessonCell.getCellType() != CellType.BLANK) {
                        lessonEntity = LessonEntity.builder()
                                .japaneseName(lessonCell.getStringCellValue().trim())
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
                                BookDetailMessageKey.BOOK_IMPORT_LESSON_NULL
                        );
                    }

                    Cell objectiveCell = row.getCell(3);
                    if (objectiveCell != null && objectiveCell.getCellType() != CellType.BLANK) {
                        objectiveEntity = ObjectiveEntity.builder()
                                .japaneseName(objectiveCell.getStringCellValue().trim())
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
                                BookDetailMessageKey.BOOK_IMPORT_OBJECTIVE_NULL
                        );
                    }

                    Cell nodeTypeCell = row.getCell(4);
                    if (nodeTypeCell == null || nodeTypeCell.getCellType() == CellType.BLANK) {
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
                    nodeGlobalOrderIndex += 1.0;
                    nodeOrderIndex += 1.0;

                    switch (nodeType) {
                        case SPEAKING_QUESTION -> {
                            Cell speakingQuestionCell = row.getCell(5);
                            String title = (speakingQuestionCell != null) ? speakingQuestionCell.getStringCellValue().trim() : "";
                            SpeakingQuestionEntity speakingQuestionEntity = SpeakingQuestionEntity
                                    .builder()
                                    .title(title)
                                    .titleMarkup(title)
                                    .status(QuestionStatus.PUBLISHED)
                                    .learningPathNode(learningPathNodeEntity)
                                    .build();

                            learningPathNodeEntity.setSpeakingQuestion(speakingQuestionEntity);
                        }
                        case VOCABULARY_QUESTION -> {
                            // để trống
                        }
                        case CHEST -> {
                            // để trống
                        }
                    }
                }
                bookJpaRepository.save(bookEntity);
            }
        } catch (IOException e) {
            throw new InfrastructureException(
                    BookErrorCode.BOOK_IMPORT_FAILED,
                    BookDetailMessageKey.BOOK_IMPORT_FAILED,
                    e.getMessage()
            );
        }
    }
}
