package org.naho.user.controller.v1;

import jakarta.validation.Valid;
import org.naho.shared.annotation.ApiResponseMessage;
import org.naho.user.command.RegisterCommand;
import org.naho.user.constant.UserApplicationMessageKey;
import org.naho.user.dto.request.RegisterRequest;
import org.naho.user.port.in.RegisterInputPort;
import org.naho.user.result.RegisterResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final RegisterInputPort registerInputPort;

    public UserController(RegisterInputPort registerInputPort) { this.registerInputPort = registerInputPort; }

    @PostMapping("/register")
    @ApiResponseMessage(message = UserApplicationMessageKey.USER_REGISTER_SUCCESSFULLY)
    public RegisterResult register(@RequestBody RegisterRequest request){
        RegisterCommand command = new RegisterCommand(
                request.username(),
                request.password(),
                request.email()
        );

        return registerInputPort.register(command);
    }
}
