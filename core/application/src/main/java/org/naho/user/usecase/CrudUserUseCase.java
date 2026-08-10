package org.naho.user.usecase;

import org.naho.file.constant.FileAccessStatus;
import org.naho.file.exception.FileErrorCode;
import org.naho.file.model.File;
import org.naho.file.port.in.AsyncCrudFileInputPort;
import org.naho.file.port.in.UploadFileInputPort;
import org.naho.file.port.out.FileRepositoryPort;
import org.naho.file.result.FileResult;
import org.naho.file.result.StoredFile;
import org.naho.i18n.message.file.FileDetailMessageKey;
import org.naho.i18n.message.user.UserDetailMessageKey;
import org.naho.pagination.PageData;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.port.out.TransactionPort;
import org.naho.user.command.UpdateUserAvatarCommand;
import org.naho.user.command.UpdateUserInfoCommand;
import org.naho.user.command.UserQueryCommand;
import org.naho.user.exception.UserErrorCode;
import org.naho.user.mapper.UserResultMapper;
import org.naho.user.model.User;
import org.naho.user.port.in.CrudUserInputPort;
import org.naho.user.port.out.UserRepositoryPort;
import org.naho.user.result.UserResult;

public class CrudUserUseCase implements CrudUserInputPort {
    private final UserRepositoryPort userRepositoryPort;
    private final UserResultMapper userResultMapper;
    private final FileRepositoryPort fileRepositoryPort;
    private final UploadFileInputPort uploadFileInputPort;
    private final TransactionPort transactionPort;
    private final AsyncCrudFileInputPort asyncCrudFileInputPort;

    public CrudUserUseCase(
            UserRepositoryPort userRepositoryPort,
            UserResultMapper userResultMapper,
            FileRepositoryPort fileRepositoryPort,
            UploadFileInputPort uploadFileInputPort,
            TransactionPort transactionPort,
            AsyncCrudFileInputPort asyncCrudFileInputPort
    ) {
        this.userRepositoryPort = userRepositoryPort;
        this.userResultMapper = userResultMapper;
        this.fileRepositoryPort = fileRepositoryPort;
        this.uploadFileInputPort = uploadFileInputPort;
        this.transactionPort = transactionPort;
        this.asyncCrudFileInputPort = asyncCrudFileInputPort;
    }

    @Override
    public UserResult findUserById(Long userId) {
        if (userId == null) {
            throw new ApplicationException(
                    UserErrorCode.USER_NOT_FOUND,
                    UserDetailMessageKey.USER_ID_NULL
            );
        }

        User user = userRepositoryPort.findById(userId)
                .orElseThrow(() -> new ApplicationException(
                        UserErrorCode.USER_NOT_FOUND,
                        UserDetailMessageKey.USER_ID_NOT_FOUND,
                        userId
                ));
        return userResultMapper.domainToResult(user);
    }

    /**
     * Cập nhật các thông tin cơ bản của người dùng như:
     * username, full name, gender, dob
     *
     * @param command chứa các dữ liệu người dùng muốn update
     * @return UserResult
     */
    @Override
    public UserResult updateUserInfo(UpdateUserInfoCommand command) {
        if (command == null) {
            throw new ApplicationException(
                    UserErrorCode.USER_NOT_FOUND,
                    UserDetailMessageKey.USER_ID_NULL
            );
        }
        if (command.id() == null) {
            throw new ApplicationException(
                    UserErrorCode.USER_NOT_FOUND,
                    UserDetailMessageKey.USER_ID_NULL
            );
        }
        User savedUser = userRepositoryPort.updateUserInfo(command);
        return userResultMapper.domainToResult(savedUser);
    }


    /**
     * Method cập nhật avatar của người dùng
     * Sau đó upload lên cloud và xoá avatar cũ trên cloud
     *
     * @param command chứa user id và stored file của avatar mới
     * @return UserResult
     */
    @Override
    public UserResult updateUserAvatar(UpdateUserAvatarCommand command) {
        StoredFile storedFile = command.storedFile();
        Long userId = command.userId();

        if (userId == null) {
            throw new ApplicationException(
                    UserErrorCode.USER_NOT_FOUND,
                    UserDetailMessageKey.USER_ID_NULL
            );
        }

        if (storedFile == null) {
            throw new ApplicationException(
                    FileErrorCode.FILE_NOT_VALID,
                    FileDetailMessageKey.FILE_NOT_VALID
            );
        }

        // lấy avatar hiện tại của người dùng nếu có
        // kể cả nếu avatar hiện tại của người dùng ở trạng thái đang upload thì cũng lấy ra
        File currentAvatarFile = fileRepositoryPort
                .findAvatarFileByUserId(userId)
                .orElse(null);

        // 1 transaction
        UserResult userResult = transactionPort.execute(() -> doUploadUserAvatar(
                userId,
                storedFile,
                currentAvatarFile
        ));

        // nếu các thao tác với db thành công thì mới upload avatar file mới lên cloud
        FileResult fileResult = uploadFileInputPort.uploadFileToCloud(command.storedFile());

        userResult.setAvatarFile(fileResult);

        if (currentAvatarFile != null) {
            // async xóa thử xóa file trên cloud
            // nếu thất bại thì schedule sẽ xóa sau
            asyncCrudFileInputPort.deleteFileInCloudAsync(currentAvatarFile.getObjectKey());
        }

        return userResult;
    }

    private UserResult doUploadUserAvatar(Long userId, StoredFile storedFile, File currentAvatarFile) {
        // nếu người dùng có avatar từ trước
        if (currentAvatarFile != null) {
            // đánh dấu là đã xóa
            currentAvatarFile.markDeleted();

            // đánh dấu là đang xóa
            currentAvatarFile.markProcessing();

            // set lại retry count = 0
            // và next retry at thành null
            currentAvatarFile.resetRetry();

            // lưu lại trạng thái
            fileRepositoryPort.save(currentAvatarFile);
        }

        // tạo avatar mới trong db với trạng thái đang upload
        File file = fileRepositoryPort.createNewForUpload(storedFile, FileAccessStatus.PRIVATE);

        // liên kết file avatar mới đó với user
        User savedUser = userRepositoryPort.updateUserAvatar(userId, file);

        return userResultMapper.domainToResult(savedUser);
    }

    @Override
    public PageData<UserResult> findAllUsers(UserQueryCommand command) {
        PageData<User> pageData = userRepositoryPort.findAllUsers(command);
        return PageData.<UserResult>builder()
                .pageMeta(pageData.getPageMeta())
                .data(pageData.getData()
                        .stream()
                        .map(userResultMapper::domainToResult)
                        .toList()
                )
                .build();
    }
}
