package com.whirlpool.frauddetectionservice.service;

import com.whirlpool.frauddetectionservice.order.request.TransactionRequest;
import com.whirlpool.frauddetectionservice.order.response.ObjectFactory;
import com.whirlpool.frauddetectionservice.order.response.TransactionResults;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import okhttp3.Response;
import org.apache.commons.net.util.Base64;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.util.Objects;
import ch.qos.logback.classic.Logger;

/**
 * Default Whirlpool Accertify Call Service
 */
@ComponentScan("com.whirlpool")
@Service
public class DefaultAccertifyCallService {
    private static final String SEPARATOR = ":";

    Logger logger = (Logger) LoggerFactory.getLogger(DefaultAccertifyCallService.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private Environment environment;
    /**
     * CallFraudCheckService to make the actual call.
     */
    public TransactionResults callFraudCheckService(final TransactionRequest transactionRequest)  throws JAXBException  {

        if (Boolean.FALSE.equals(environment.getProperty("frauddetection.enabled", Boolean.class))) {
            return getBypassResponse(transactionRequest);
        } else {
            final HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_XML);
            headers.add("Accept", MediaType.APPLICATION_XML.toString());
            final String authorization = environment.getProperty("accertify.user.whirlpool-us") + SEPARATOR + environment.getProperty("accertify.password.whirlpool-us");
            final String base64Creds = new String(Base64.encodeBase64(authorization.getBytes()));
            headers.set("Authorization", "Basic " + base64Creds);

            final String url = environment.getProperty("accertify.preauth.endpoint.url.whirlpool-us");

            final HttpEntity<TransactionRequest> requestEntity = new HttpEntity<TransactionRequest>(transactionRequest, headers);

            logger.info("[frauddetection-service] Accertify request: " + toXml(transactionRequest));

            final ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

            logger.info("[frauddetection-service] Accertify response: " + response);

            final JAXBContext jc = JAXBContext.newInstance(TransactionResults.class);
            final Unmarshaller unmar = jc.createUnmarshaller();
            return (TransactionResults) unmar.unmarshal(new StringReader(Objects.requireNonNull(response.getBody())));
        }
    }

    /***
     * Providing a String to print the resquest.
     *
     * @param request
     * @return
     */
    public String toXml(final TransactionRequest request) {
        try {
            final JAXBContext jc = JAXBContext.newInstance(request.getClass());
            final Marshaller marshaller = jc.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            marshaller.marshal(request, baos);
            return baos.toString();
        } catch (final Exception e) {
            logger.error(e.getMessage(), e);
        }
        return "";
    }

    public TransactionResults getBypassResponse(TransactionRequest transactionRequest) {
        ObjectFactory objectFactory = new ObjectFactory();
        TransactionResults transactionResults = objectFactory.createTransactionResults();
        transactionResults.setTransactionId(transactionRequest.getOrderInformation().getTransactionID());
        transactionResults.setTotalScore("0");
        transactionResults.setRecommendationCode("ACCEPT");
        return transactionResults;
    }
}
