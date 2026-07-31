package org.naho.user.controller.v1;

import lombok.RequiredArgsConstructor;
import org.naho.file.command.UploadFileCommand;
import org.naho.file.port.out.FileValidatorPort;
import org.naho.i18n.message.user.UserDetailMessageKey;
import org.naho.i18n.message.user.UserTitleMessageKey;
import org.naho.shared.annotation.ApiResponseMessage;
import org.naho.shared.exception.PresentationException;
import org.naho.user.command.RegisterCommand;
import org.naho.user.command.UpdateUserInfoCommand;
import org.naho.user.dto.mapper.RegisterRequestMapper;
import org.naho.user.dto.mapper.RegisterResponseMapper;
import org.naho.user.dto.mapper.UserResponseMapper;
import org.naho.user.dto.request.RegisterRequest;
import org.naho.user.dto.request.UpdateStatusRequest;
import org.naho.user.dto.request.UpdateUserInfoRequest;
import org.naho.user.dto.response.RegisterResponse;
import org.naho.user.dto.response.UserResponse;
import org.naho.user.exception.UserErrorCode;
import org.naho.user.port.in.CrudUserInputPort;
import org.naho.user.port.in.GetUserInputPort;
import org.naho.user.port.in.RegisterInputPort;
import org.naho.user.port.in.UpdateUserInputPort;
import org.naho.user.result.AccessTokenPayload;
import org.naho.user.result.RegisterResult;
import org.naho.user.result.UserResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    public static final String AVATAR_FILE_FOLDER = "avatars";

    private final GetUserInputPort getUserInputPort;
    private final UpdateUserInputPort updateUserInputPort;
    private final UserResponseMapper userResponseMapper;
    private final CrudUserInputPort crudUserInputPort;

    private final RegisterInputPort registerInputPort;
    private final RegisterResponseMapper registerResponseMapper;
    private final RegisterRequestMapper registerRequestMapper;
    private final FileValidatorPort fileValidatorPort;

    @ApiResponseMessage(message = UserDetailMessageKey.USER_GET_SUCCESSFULLY)
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentLoggedUser(
            @AuthenticationPrincipal AccessTokenPayload payload
    ) {
        UserResult result = crudUserInputPort.findUserById(payload.userId());
        UserResponse response = userResponseMapper.resultToResponse(result);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    @ApiResponseMessage(message = UserDetailMessageKey.USER_REGISTER_SUCCESSFULLY)
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request) {
        RegisterCommand command = registerRequestMapper.requestToCommand(request);
        RegisterResult result = registerInputPort.register(command);
        RegisterResponse response = registerResponseMapper.resultToResponse(result);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
        UserResult result = getUserInputPort.getUserById(id);
        return ResponseEntity.ok(userResponseMapper.resultToResponse(result));
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getListUser(
            @RequestParam(value = "userNameOrEmail", required = false) String userNameOrEmail,
            @RequestParam(value = "role", required = false) String role,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "jlptLevel", required = false) String jlptLevel
    ) {
        List<UserResult> results = getUserInputPort.searchUsers(userNameOrEmail, role, status, jlptLevel);
        return ResponseEntity.ok(results.stream().map(userResponseMapper::resultToResponse).toList());
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<UserResponse> updateStatus(
            @PathVariable Long id,
            @RequestBody UpdateStatusRequest request
    ) {
        UserResult user = updateUserInputPort.updateStatus(id, request.newStatus());
        UserResponse response = userResponseMapper.resultToResponse(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @ApiResponseMessage(message = UserDetailMessageKey.USER_UPDATE_INFO_SUCCESSFULLY)
    @PatchMapping(value = "/info", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserResponse> updateUserInfo(
            @AuthenticationPrincipal AccessTokenPayload payload,
            @ModelAttribute UpdateUserInfoRequest request,
            @RequestPart(value = "avatarFile", required = false) MultipartFile avatarFile
    ) {
        try {
            UploadFileCommand avatarUploadFileCommand = null;
            if (avatarFile != null && !avatarFile.isEmpty()) {
                // check định dạng của file có phải là ảnh không?
                String contentType = fileValidatorPort
                        .validateImageFile(avatarFile.getInputStream());

                avatarUploadFileCommand = UploadFileCommand.builder()
                        .folderName(AVATAR_FILE_FOLDER)
                        .originalName(avatarFile.getOriginalFilename())
                        .inputStream(avatarFile.getInputStream())
                        .contentType(contentType)
                        .size(avatarFile.getSize())
                        .build();
            }

            UpdateUserInfoCommand command = UpdateUserInfoCommand.builder()
                    .id(payload.userId())
                    .username(request.username())
                    .avatarFile(avatarUploadFileCommand)
                    .fullName(request.fullName())
                    .gender(request.gender())
                    .dob(request.dob())
                    .build();

            UserResult result = crudUserInputPort.updateUserInfo(command);
            UserResponse response = userResponseMapper.resultToResponse(result);

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            throw new PresentationException(
                    UserErrorCode.USER_PERSIST_FAILED,
                    UserTitleMessageKey.USER_PERSIST_FAILED_TITLE,
                    e.getMessage()
            );
        }
    }
}
