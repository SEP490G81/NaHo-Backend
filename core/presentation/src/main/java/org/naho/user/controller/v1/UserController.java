package org.naho.user.controller.v1;

import lombok.RequiredArgsConstructor;
import org.naho.file.constant.FileAccessStatus;
import org.naho.file.constant.FileFolderConstant;
import org.naho.file.exception.FileErrorCode;
import org.naho.file.port.out.FileStorageServicePort;
import org.naho.file.port.out.FileValidatorPort;
import org.naho.file.result.StoredFile;
import org.naho.i18n.message.file.FileDetailMessageKey;
import org.naho.i18n.message.user.UserDetailMessageKey;
import org.naho.pagination.PageData;
import org.naho.shared.annotation.ApiResponseMessage;
import org.naho.shared.exception.PresentationException;
import org.naho.user.command.RegisterCommand;
import org.naho.user.command.UpdateUserAvatarCommand;
import org.naho.user.command.UpdateUserInfoCommand;
import org.naho.user.command.UserQueryCommand;
import org.naho.user.dto.mapper.RegisterRequestMapper;
import org.naho.user.dto.mapper.RegisterResponseMapper;
import org.naho.user.dto.mapper.UserRequestMapper;
import org.naho.user.dto.mapper.UserResponseMapper;
import org.naho.user.dto.request.RegisterRequest;
import org.naho.user.dto.request.UpdateUserInfoRequest;
import org.naho.user.dto.request.UserQueryRequest;
import org.naho.user.dto.response.RegisterResponse;
import org.naho.user.dto.response.UserResponse;
import org.naho.user.port.in.CrudUserInputPort;
import org.naho.user.port.in.GetUserInputPort;
import org.naho.user.port.in.RegisterInputPort;
import org.naho.user.port.in.UpdateUserInputPort;
import org.naho.user.result.AccessTokenPayload;
import org.naho.user.result.RegisterResult;
import org.naho.user.result.UserResult;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final GetUserInputPort getUserInputPort;
    private final UpdateUserInputPort updateUserInputPort;
    private final UserResponseMapper userResponseMapper;
    private final CrudUserInputPort crudUserInputPort;

    private final RegisterInputPort registerInputPort;
    private final RegisterResponseMapper registerResponseMapper;
    private final RegisterRequestMapper registerRequestMapper;
    private final FileValidatorPort fileValidatorPort;
    private final FileStorageServicePort fileStorageServicePort;
    private final UserRequestMapper userRequestMapper;

    /**
     * Lấy thông tin chi tiết của người dùng đang đăng nhập
     *
     * @param payload chứa user id của tài khoản đang đăng nhập thông qua JWT
     * @return UserResponse
     */
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

    /**
     * API để cho role Admin lấy ra danh sách người dùng
     * Có kèm thêm chức năng search, filter, sort.
     *
     * @param request chứa các field, page, sort column...
     * @return PageData<UserResponse>
     */
    @ApiResponseMessage(message = UserDetailMessageKey.USER_GET_SUCCESSFULLY)
    @PostMapping("/all")
    public ResponseEntity<PageData<UserResponse>> findAllUsers(
            @RequestBody UserQueryRequest request
    ) {
        UserQueryCommand command = userRequestMapper.requestToCommand(request);
        PageData<UserResult> result = crudUserInputPort.findAllUsers(command);

        PageData<UserResponse> responsePageData = PageData.<UserResponse>builder()
                .pageMeta(result.getPageMeta())
                .data(result.getData()
                        .stream()
                        .map(userResponseMapper::resultToResponse)
                        .toList())
                .build();

        return ResponseEntity.ok(responsePageData);
    }

    /**
     * Cập nhật trạng thái của người dùng: ACTIVE, UNACTIVE
     * Nếu đang là ACTIVE => UNACTIVE và ngược lại
     *
     * @param id user id
     * @return UserResponse
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<UserResponse> updateStatus(@PathVariable Long id) {
        UserResult user = updateUserInputPort.updateStatus(id);
        UserResponse response = userResponseMapper.resultToResponse(user);
        return ResponseEntity.ok(response);
    }

    /**
     * Cập nhật thông tin cơ bản của người dùng:
     * username, fullName, gender, dob
     *
     * @param payload chứa userId của người dùng đang đăng nhập, tự sinh từ JWT
     * @param request bao gồm: username, full name, gender, dob
     * @return UserResponse
     */
    @ApiResponseMessage(message = UserDetailMessageKey.USER_UPDATE_INFO_SUCCESSFULLY)
    @PatchMapping(value = "/info")
    public ResponseEntity<UserResponse> updateUserInfo(
            @AuthenticationPrincipal AccessTokenPayload payload,
            @RequestBody UpdateUserInfoRequest request
    ) {
        UpdateUserInfoCommand command = UpdateUserInfoCommand.builder()
                .id(payload.userId())
                .username(request.username())
                .fullName(request.fullName())
                .gender(request.gender())
                .dob(request.dob())
                .build();

        UserResult result = crudUserInputPort.updateUserInfo(command);
        UserResponse response = userResponseMapper.resultToResponse(result);

        return ResponseEntity.ok(response);
    }

    /**
     * Cập nhật avatar của người dùng
     *
     * @param payload    chứa userId của người dùng đang đăng nhập, tự sinh từ JWT
     * @param avatarFile file avatar mới của người dùng upload lên
     * @return UserResponse
     */
    @ApiResponseMessage(message = UserDetailMessageKey.USER_UPDATE_AVATAR_SUCCESSFULLY)
    @PatchMapping("/avatar")
    public ResponseEntity<UserResponse> updateUserAvatar(
            @AuthenticationPrincipal AccessTokenPayload payload,
            @RequestPart("avatar") MultipartFile avatarFile
    ) {
        try {
            // kiểm tra xem có phải file ảnh không
            fileValidatorPort.validateImageFile(avatarFile.getBytes());

            // tạm thời lưu file vào trong local
            StoredFile storedFile = fileStorageServicePort.saveFileToLocal(
                    avatarFile,
                    FileFolderConstant.AVATARS,
                    FileAccessStatus.PRIVATE
            );

            UpdateUserAvatarCommand command = new UpdateUserAvatarCommand(
                    payload.userId(),
                    storedFile
            );

            UserResult result = crudUserInputPort.updateUserAvatar(command);
            UserResponse response = userResponseMapper.resultToResponse(result);

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            throw new PresentationException(
                    FileErrorCode.FILE_UPLOAD_FAILED,
                    FileDetailMessageKey.FILE_UPLOAD_FAILED,
                    e.getMessage()
            );
        }
    }
}
