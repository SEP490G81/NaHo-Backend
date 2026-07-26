package org.naho.user.dto.mapper;

import jakarta.servlet.http.HttpServletRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.naho.user.command.VerifyEmailCommand;
import org.naho.user.dto.request.VerifyEmailRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Mapper(componentModel = "spring")
public interface VerifyEmailRequestMapper {

    @Mapping(target = "deviceId", expression = "java(getDeviceId(request))")
    @Mapping(target = "userAgent", expression = "java(getUserAgent(request))")
    @Mapping(target = "ipAddress", expression = "java(getIpAddress(request))")
    VerifyEmailCommand toCommand(VerifyEmailRequest request);

    default String getDeviceId(VerifyEmailRequest request) {
        HttpServletRequest httpRequest = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String deviceId = httpRequest.getHeader("Device-ID");
        return deviceId != null ? deviceId : "UNKNOWN";
    }

    default String getUserAgent(VerifyEmailRequest request) {
        HttpServletRequest httpRequest = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        return httpRequest.getHeader("User-Agent");
    }

    default String getIpAddress(VerifyEmailRequest request) {
        HttpServletRequest httpRequest = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String xfHeader = httpRequest.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return httpRequest.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
