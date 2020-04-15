package com.example.jwt.service.impl;

import com.example.jwt.config.security.AuthenticatedUser;
import com.example.jwt.dao.RoleDao;
import com.example.jwt.dao.UserDao;
import com.example.jwt.dto.SignUpRequestDto;
import com.example.jwt.entity.Role;
import com.example.jwt.entity.User;
import com.example.jwt.service.UserService;
import org.apache.xml.security.utils.Base64;
import org.joda.time.DateTime;
import org.opensaml.DefaultBootstrap;
import org.opensaml.common.SAMLVersion;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.Attribute;
import org.opensaml.saml2.core.AttributeStatement;
import org.opensaml.saml2.core.AttributeValue;
import org.opensaml.saml2.core.Audience;
import org.opensaml.saml2.core.AudienceRestriction;
import org.opensaml.saml2.core.AuthnContext;
import org.opensaml.saml2.core.AuthnContextClassRef;
import org.opensaml.saml2.core.AuthnStatement;
import org.opensaml.saml2.core.Conditions;
import org.opensaml.saml2.core.Issuer;
import org.opensaml.saml2.core.NameID;
import org.opensaml.saml2.core.NameIDType;
import org.opensaml.saml2.core.Response;
import org.opensaml.saml2.core.Status;
import org.opensaml.saml2.core.StatusCode;
import org.opensaml.saml2.core.Subject;
import org.opensaml.saml2.core.SubjectConfirmation;
import org.opensaml.saml2.core.SubjectConfirmationData;
import org.opensaml.saml2.core.impl.AssertionBuilder;
import org.opensaml.saml2.core.impl.AttributeBuilder;
import org.opensaml.saml2.core.impl.AttributeStatementBuilder;
import org.opensaml.saml2.core.impl.AudienceBuilder;
import org.opensaml.saml2.core.impl.AudienceRestrictionBuilder;
import org.opensaml.saml2.core.impl.AuthnContextBuilder;
import org.opensaml.saml2.core.impl.AuthnContextClassRefBuilder;
import org.opensaml.saml2.core.impl.AuthnStatementBuilder;
import org.opensaml.saml2.core.impl.ConditionsBuilder;
import org.opensaml.saml2.core.impl.IssuerBuilder;
import org.opensaml.saml2.core.impl.NameIDBuilder;
import org.opensaml.saml2.core.impl.ResponseBuilder;
import org.opensaml.saml2.core.impl.ResponseMarshaller;
import org.opensaml.saml2.core.impl.StatusBuilder;
import org.opensaml.saml2.core.impl.StatusCodeBuilder;
import org.opensaml.saml2.core.impl.SubjectBuilder;
import org.opensaml.saml2.core.impl.SubjectConfirmationBuilder;
import org.opensaml.saml2.core.impl.SubjectConfirmationDataBuilder;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.schema.XSString;
import org.opensaml.xml.schema.impl.XSStringBuilder;
import org.opensaml.xml.security.credential.Credential;
import org.opensaml.xml.security.x509.BasicX509Credential;
import org.opensaml.xml.signature.KeyInfo;
import org.opensaml.xml.signature.Signature;
import org.opensaml.xml.signature.SignatureConstants;
import org.opensaml.xml.signature.Signer;
import org.opensaml.xml.signature.X509Data;
import org.opensaml.xml.signature.impl.KeyInfoBuilder;
import org.opensaml.xml.signature.impl.SignatureBuilder;
import org.opensaml.xml.signature.impl.X509CertificateBuilder;
import org.opensaml.xml.signature.impl.X509DataBuilder;
import org.opensaml.xml.signature.X509Certificate;
import org.opensaml.xml.util.XMLHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.w3c.dom.Element;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.util.HashSet;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleDao roleDao;

    @Override
    public User addUser(SignUpRequestDto signUpRequestDto) {

        Set<Role> roles = new HashSet<>();
        roles.add(roleDao.findByName(signUpRequestDto.getRoleName()));

        User user = new User();
        user.setFirstName(signUpRequestDto.getFirstName());
        user.setLastName(signUpRequestDto.getLastName());
        user.setMobile(signUpRequestDto.getMobile());
        user.setUsername(signUpRequestDto.getUsername());
        user.setEmail(signUpRequestDto.getEmail());
        user.setPassword(passwordEncoder.encode(signUpRequestDto.getPassword()));
        user.setRoles(roles);

        return userDao.save(user);
    }

    @Override
    public Boolean exists(String username, String email) {
        return userDao.existsByUsernameOrEmail(username, email);
    }

    @Override
    public String samlResponse(AuthenticatedUser authenticatedUser) throws Exception {

        try {
            DefaultBootstrap.bootstrap();
        } catch (ConfigurationException ce) {
           ce.printStackTrace();
        }

        DateTime dateTimeNow = DateTime.now();
        DateTime dateTimeInAnHour = DateTime.now().plusHours(1);

        BasicX509Credential credential = initializeCredentials();

        Issuer issuer = new IssuerBuilder().buildObject();
        issuer.setValue("urn:example:idp");

        Issuer issuerTop = new IssuerBuilder().buildObject();
        issuerTop.setValue("urn:example:idp");

        X509Certificate x509Certificate = new X509CertificateBuilder().buildObject();
        x509Certificate.setValue(Base64.encode(credential.getEntityCertificate().getEncoded()));

        X509Data x509Data = new X509DataBuilder().buildObject();
        x509Data.getX509Certificates().add(x509Certificate);

        KeyInfo keyInfo = new KeyInfoBuilder().buildObject();
        keyInfo.getX509Datas().add(x509Data);

        SignatureBuilder builder = new SignatureBuilder();
        Signature signature = builder.buildObject();
        signature.setSigningCredential(credential);
        signature.setSignatureAlgorithm(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256);
        signature.setCanonicalizationAlgorithm(SignatureConstants.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);
        signature.setKeyInfo(keyInfo);

        X509Certificate x509CertificateTop = new X509CertificateBuilder().buildObject();
        x509CertificateTop.setValue(Base64.encode(credential.getEntityCertificate().getEncoded()));

        X509Data x509DataTop = new X509DataBuilder().buildObject();
        x509DataTop.getX509Certificates().add(x509CertificateTop);

        KeyInfo keyInfoTop = new KeyInfoBuilder().buildObject();
        keyInfoTop.getX509Datas().add(x509DataTop);

        Signature signatureTop = builder.buildObject();
        signatureTop.setSigningCredential(credential);
        signatureTop.setSignatureAlgorithm(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256);
        signatureTop.setCanonicalizationAlgorithm(SignatureConstants.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);
        signatureTop.setKeyInfo(keyInfoTop);

        StatusCode statusCode = new StatusCodeBuilder().buildObject();
        statusCode.setValue(StatusCode.SUCCESS_URI);

        Status status = new StatusBuilder().buildObject();
        status.setStatusCode(statusCode);

        NameID nameID = new NameIDBuilder().buildObject();
        nameID.setFormat(NameIDType.EMAIL);
        nameID.setValue("rchavez@itexico.com");

        SubjectConfirmation subjectConfirmation = new SubjectConfirmationBuilder().buildObject();
        subjectConfirmation.setMethod(SubjectConfirmation.METHOD_BEARER);

        SubjectConfirmationData subjectConfirmationData = new SubjectConfirmationDataBuilder().buildObject();
        subjectConfirmationData.setNotOnOrAfter(dateTimeInAnHour);
        subjectConfirmationData.setRecipient("https://dev-390527.okta.com/sso/saml2/0oa91kxnxbgRWJEbN4x6");

        subjectConfirmation.setSubjectConfirmationData(subjectConfirmationData);

        Subject subject = new SubjectBuilder().buildObject();
        subject.setNameID(nameID);
        subject.getSubjectConfirmations().add(subjectConfirmation);

        Audience audience = new AudienceBuilder().buildObject();
        audience.setAudienceURI("https://www.okta.com/saml2/service-provider/spwpkwwthkagxzdgrjye");

        AudienceRestriction audienceRestriction = new AudienceRestrictionBuilder().buildObject();
        audienceRestriction.getAudiences().add(audience);

        Conditions conditions = new ConditionsBuilder().buildObject();
        conditions.setNotBefore(dateTimeNow);
        conditions.setNotOnOrAfter(dateTimeInAnHour);
        conditions.getAudienceRestrictions().add(audienceRestriction);

        AuthnContextClassRef authnContextClassRef = new AuthnContextClassRefBuilder().buildObject();
        authnContextClassRef.setAuthnContextClassRef(AuthnContext.PPT_AUTHN_CTX);

        AuthnContext authnContext = new AuthnContextBuilder().buildObject();
        authnContext.setAuthnContextClassRef(authnContextClassRef);

        AuthnStatement authnStatement = new AuthnStatementBuilder().buildObject();
        authnStatement.setAuthnInstant(dateTimeNow);
        authnStatement.setSessionIndex("2143702971");
        authnStatement.setAuthnContext(authnContext);

        XSStringBuilder stringBuilder = new XSStringBuilder();

        XSString idValue = stringBuilder.buildObject(AttributeValue.DEFAULT_ELEMENT_NAME, XSString.TYPE_NAME);
        idValue.setValue("00u5h9hml5ycoljad4x1");

        Attribute id = new AttributeBuilder().buildObject();
        id.setNameFormat(Attribute.BASIC);
        id.setName("id");
        id.getAttributeValues().add(idValue);

        XSString fnValue = stringBuilder.buildObject(AttributeValue.DEFAULT_ELEMENT_NAME, XSString.TYPE_NAME);
        fnValue.setValue(authenticatedUser.getFirstName());

        Attribute firstName = new AttributeBuilder().buildObject();
        firstName.setNameFormat(Attribute.BASIC);
        firstName.setName("firstName");
        firstName.getAttributeValues().add(fnValue);

        XSString lnValue = stringBuilder.buildObject(AttributeValue.DEFAULT_ELEMENT_NAME, XSString.TYPE_NAME);
        lnValue.setValue(authenticatedUser.getLastName());

        Attribute lastName = new AttributeBuilder().buildObject();
        lastName.setNameFormat(Attribute.BASIC);
        lastName.setName("lastName");
        lastName.getAttributeValues().add(lnValue);

        XSString dnValue = stringBuilder.buildObject(AttributeValue.DEFAULT_ELEMENT_NAME, XSString.TYPE_NAME);
        dnValue.setValue(authenticatedUser.getFirstName() + " " + authenticatedUser.getLastName());

        Attribute displayName = new AttributeBuilder().buildObject();
        displayName.setNameFormat(Attribute.BASIC);
        displayName.setName("displayName");
        displayName.getAttributeValues().add(dnValue);

        XSString emailValue = stringBuilder.buildObject(AttributeValue.DEFAULT_ELEMENT_NAME, XSString.TYPE_NAME);
        emailValue.setValue(authenticatedUser.getEmail());

        Attribute email = new AttributeBuilder().buildObject();
        email.setNameFormat(Attribute.BASIC);
        email.setName("email");
        email.getAttributeValues().add(emailValue);

        XSString mobilePhoneValue = stringBuilder.buildObject(AttributeValue.DEFAULT_ELEMENT_NAME, XSString.TYPE_NAME);
        mobilePhoneValue.setValue(authenticatedUser.getMobile());

        Attribute mobilePhone = new AttributeBuilder().buildObject();
        mobilePhone.setNameFormat(Attribute.BASIC);
        mobilePhone.setName("mobilePhone");
        mobilePhone.getAttributeValues().add(mobilePhoneValue);

        XSString group1Value = stringBuilder.buildObject(AttributeValue.DEFAULT_ELEMENT_NAME, XSString.TYPE_NAME);
        group1Value.setValue("Simple IdP Users");

        XSString group2Value = stringBuilder.buildObject(AttributeValue.DEFAULT_ELEMENT_NAME, XSString.TYPE_NAME);
        group2Value.setValue("West Coast Users");

        XSString group3Value = stringBuilder.buildObject(AttributeValue.DEFAULT_ELEMENT_NAME, XSString.TYPE_NAME);
        group3Value.setValue("Cloud Users");

        Attribute groups = new AttributeBuilder().buildObject();
        groups.setNameFormat(Attribute.BASIC);
        groups.setName("groups");
        groups.getAttributeValues().add(group1Value);
        groups.getAttributeValues().add(group2Value);
        groups.getAttributeValues().add(group3Value);

        XSString userTypeValue = stringBuilder.buildObject(AttributeValue.DEFAULT_ELEMENT_NAME, XSString.TYPE_NAME);
        userTypeValue.setValue("admin");

        Attribute userType = new AttributeBuilder().buildObject();
        userType.setNameFormat(Attribute.BASIC);
        userType.setName("userType");
        userType.getAttributeValues().add(userTypeValue);

        AttributeStatement attributeStatement = new AttributeStatementBuilder().buildObject();
        attributeStatement.getAttributes().add(id);
        attributeStatement.getAttributes().add(firstName);
        attributeStatement.getAttributes().add(lastName);
        attributeStatement.getAttributes().add(displayName);
        attributeStatement.getAttributes().add(email);
        attributeStatement.getAttributes().add(mobilePhone);
        attributeStatement.getAttributes().add(groups);
        attributeStatement.getAttributes().add(userType);

        Assertion assertion = new AssertionBuilder().buildObject();
        assertion.setIssuer(issuer);
        assertion.setSignature(signature);
        assertion.setSubject(subject);
        assertion.setIssueInstant(dateTimeNow);
        assertion.setConditions(conditions);
        assertion.getAuthnStatements().add(authnStatement);
        assertion.getAttributeStatements().add(attributeStatement);

        Response response = new ResponseBuilder().buildObject();

        //Response response = (Response) builderFactory.getBuilder(Subject.DEFAULT_ELEMENT_NAME);
        response.setID("_1143b842dba3f8fd8143");
        response.setDestination("https://dev-390527.okta.com/sso/saml2/0oa91kxnxbgRWJEbN4x6");
        response.setVersion(SAMLVersion.VERSION_20);
        response.setIssueInstant(dateTimeNow);
        response.setIssuer(issuerTop);
        response.setSignature(signatureTop);
        response.setStatus(status);
        response.getAssertions().add(assertion);

        ResponseMarshaller responseMarshaller = new ResponseMarshaller();
        Element plain = responseMarshaller.marshall(response);

        Signer.signObject(signature);
        Signer.signObject(signatureTop);

        String samlResponse = XMLHelper.nodeToString(plain);

        return samlResponse;
    }

    private BasicX509Credential initializeCredentials() throws Exception {

        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());

        ClassLoader classLoader = getClass().getClassLoader();
        InputStream keyFileIS = classLoader.getResourceAsStream("okta.jks");
        ks.load(keyFileIS, "123456".toCharArray());
        keyFileIS.close();

        KeyStore.PrivateKeyEntry pkEntry = (KeyStore.PrivateKeyEntry) ks.getEntry("alias_name", new KeyStore.PasswordProtection("123456".toCharArray()));

        PrivateKey pk = pkEntry.getPrivateKey();

        java.security.cert.X509Certificate certificate = (java.security.cert.X509Certificate) pkEntry.getCertificate();
        BasicX509Credential credential = new BasicX509Credential();
        credential.setEntityCertificate(certificate);
        credential.setPrivateKey(pk);
        return credential;
    }
}
