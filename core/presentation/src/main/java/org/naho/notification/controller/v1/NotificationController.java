package org.naho.notification.controller.v1;

import lombok.RequiredArgsConstructor;
import org.naho.i18n.message.notification.NotificationDetailMessageKey;
import org.naho.notification.command.GetListNotificationCommand;
import org.naho.notification.command.MarkNotificationAsReadCommand;
import org.naho.notification.dto.mapper.NotificationResponseMapper;
import org.naho.notification.dto.response.NotificationResponse;
import org.naho.notification.port.in.CountUnreadNotificationInputPort;
import org.naho.notification.port.in.GetListNotificationByUserInputPort;
import org.naho.notification.port.in.MarkAllNotificationsAsReadInputPort;
import org.naho.notification.port.in.MarkNotificationAsReadInputPort;
import org.naho.notification.result.NotificationResult;
import org.naho.shared.annotation.ApiResponseMessage;
import org.naho.user.result.AccessTokenPayload;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final GetListNotificationByUserInputPort getListNotificationByUserInputPort;
    private final CountUnreadNotificationInputPort countUnreadNotificationInputPort;
    private final MarkNotificationAsReadInputPort markNotificationAsReadInputPort;
    private final MarkAllNotificationsAsReadInputPort markAllNotificationsAsReadInputPort;
    private final NotificationResponseMapper notificationResponseMapper;

    @GetMapping
    @ApiResponseMessage(message = NotificationDetailMessageKey.NOTIFICATION_GET_LIST_SUCCESS)
    public ResponseEntity<List<NotificationResponse>> getList(
            @AuthenticationPrincipal AccessTokenPayload payload,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "0") int offset) {

        GetListNotificationCommand command = new GetListNotificationCommand(payload.userId(), limit, offset);
        List<NotificationResult> results = getListNotificationByUserInputPort.getListByUserId(command);

        List<NotificationResponse> responses = results.stream()
                .map(notificationResponseMapper::resultToResponse)
                .toList();

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/unread-count")
    @ApiResponseMessage(message = NotificationDetailMessageKey.NOTIFICATION_COUNT_UNREAD_SUCCESS)
    public ResponseEntity<Long> countUnread(@AuthenticationPrincipal AccessTokenPayload payload) {
        long count = countUnreadNotificationInputPort.countUnreadByUserId(payload.userId());
        return ResponseEntity.ok(count);
    }

    @PatchMapping("/{id}/read")
    @ApiResponseMessage(message = NotificationDetailMessageKey.NOTIFICATION_MARK_READ_SUCCESS)
    public ResponseEntity<NotificationResponse> markAsRead(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal AccessTokenPayload payload) {

        MarkNotificationAsReadCommand command = new MarkNotificationAsReadCommand(id, payload.userId());
        NotificationResult result = markNotificationAsReadInputPort.markAsRead(command);

        return ResponseEntity.ok(notificationResponseMapper.resultToResponse(result));
    }

    @PatchMapping("/read-all")
    @ApiResponseMessage(message = NotificationDetailMessageKey.NOTIFICATION_MARK_ALL_READ_SUCCESS)
    public ResponseEntity<Void> markAllAsRead(@AuthenticationPrincipal AccessTokenPayload payload) {
        markAllNotificationsAsReadInputPort.markAllAsRead(payload.userId());
        return ResponseEntity.noContent().build();
    }
}
