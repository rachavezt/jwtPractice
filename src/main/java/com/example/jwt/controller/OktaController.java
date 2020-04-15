package com.example.jwt.controller;

import com.example.jwt.config.security.AuthenticatedUser;
import com.example.jwt.config.security.annotation.CurrentUser;
import com.example.jwt.dto.OktaResponseDto;
import com.example.jwt.service.UserService;
import org.apache.xml.security.utils.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/okta", headers = "Authorization")
public class OktaController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/salesforce", method = RequestMethod.GET, produces = "application/json")
    public OktaResponseDto ssoSalesfoce(@CurrentUser AuthenticatedUser authenticatedUser) {
        String result = null;
        try {
            result = userService.samlResponse(authenticatedUser);
        } catch (Exception e) {
            e.printStackTrace();
        }

        OktaResponseDto oktaResponseDto = new OktaResponseDto();
        oktaResponseDto.setUrl("https://dev-390527.okta.com/sso/saml2/0oa91kxnxbgRWJEbN4x6");
        oktaResponseDto.setBase64Payload(Base64.encode(result.getBytes()));

        return oktaResponseDto;
    }

}
