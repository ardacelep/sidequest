package com.tablefour.sidequest.core.results;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class BaseResponseHelpers {

    public <D> BaseResponse<D> createBaseResponse(HttpStatus httpStatus, MessageType messageType, String message,
            WebRequest webRequest, D data) {
        BaseResponse<D> response = new BaseResponse<>();

        response.setHttpStatus(httpStatus);
        response.setHttpStatusCode(httpStatus.value());
        response.setMessageType(messageType);
        response.setCreatedAt(LocalDateTime.now());
        response.setHostName(getHostName());
        response.setPath(webRequest.getDescription(false).substring(4));
        response.setMessage(message);

        // Always try to get the request type from ServletWebRequest
        if (webRequest instanceof ServletWebRequest) {
            ServletWebRequest servletWebRequest = (ServletWebRequest) webRequest;
            response.setRequestType(servletWebRequest.getHttpMethod().name());
        } else {
            // Fallback to GET if not a ServletWebRequest
            response.setRequestType("GET");
        }

        if (data != null) {
            String dataKey = getDataKeyName(data);
            response.addData(dataKey, data);
        }

        return response;
    }

    // Sınıf adını küçük harfle başlatıp key olarak kullanmak için
    private <D> String getDataKeyName(D data) {
        // Eğer data bir koleksiyon ise (List, Set, vb.), key ismi "data" olsun
        if (data instanceof Iterable) {
            return "data";
        }

        // Koleksiyon değilse sınıf adını kullan
        String simpleName = data.getClass().getSimpleName(); // Örn: UserDto
        return Character.toLowerCase(simpleName.charAt(0)) + simpleName.substring(1); // -> userDto
    }

    public BaseResponse<Void> createBaseResponse(HttpStatus httpStatus, MessageType messageType, String message,
            WebRequest webRequest) {
        return createBaseResponse(httpStatus, messageType, message, webRequest, null);
    }

    public String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            System.out.println("hata oluştu " + e.getMessage());
        }
        return null;
    }

    public List<String> addMapValue(List<String> list, String newValue) {
        list.add(newValue);
        return list;
    }
}
