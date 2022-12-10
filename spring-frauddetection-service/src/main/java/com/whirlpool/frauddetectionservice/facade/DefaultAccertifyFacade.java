///**
// *
// */
//package com.whirlpool.frauddetectionservice.facade;
//
//import com.hybris.integration.accertify.core.data.CommerceAccertifyParameter;
//import com.hybris.integration.accertify.order.response.TransactionResults;
//import com.hybris.integration.accertify.services.AccertifyCallService;
//import com.whirlpool.digitalplatform.core.model.WHRAssertifyConfigModel;
//import com.whirlpool.digitalplatform.core.services.order.WhirlpoolCartService;
//import de.hybris.platform.core.model.order.CartModel;
//import de.hybris.platform.core.model.order.OrderModel;
//import org.apache.log4j.Logger;
//import org.springframework.beans.factory.annotation.Required;
//
//import javax.annotation.Nullable;
//import javax.annotation.Resource;
//import javax.xml.bind.JAXBException;
//import javax.xml.xpath.XPathExpressionException;
//import java.util.function.Consumer;
//import java.util.stream.Stream;
//
//import static com.whirlpool.digitalplatform.core.constants.WhrCoreConstants.Accertify.ACCEPT;
//import static com.whirlpool.digitalplatform.core.constants.WhrCoreConstants.Accertify.REVIEW;
//import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;
//
//
///**
// * @author ohmprakash.balaiah
// * DefaultAccertifyFacade to call the Accertify services.
// *
// */
//public class DefaultAccertifyFacade implements AccertifyFacade {
//
//	private static final Logger LOG = Logger.getLogger(DefaultAccertifyFacade.class);
//
//	@Resource(name = "accertifyCallService")
//	private AccertifyCallService accertifyCallService;
//
//	@Resource(name = "whirlpoolCartService")
//	private WhirlpoolCartService cartService;
//
//	public TransactionResults CallFraudCheckService(final CommerceAccertifyParameter commerceAccertifyParameter, final String ioBlackBoxVal,final WHRAssertifyConfigModel whrAssertifyConfigModel)
//	{
//		TransactionResults transactionResults = null;
//		try
//		{
//			LOG.debug("Inside DefaultAccertifyFacade.CallFraudCheckService ==");
//			transactionResults = accertifyCallService.CallFraudCheckService(commerceAccertifyParameter, ioBlackBoxVal,whrAssertifyConfigModel);
//			LOG.info("Accertify Result: " + transactionResults.getRecommendationCode());
//		}
//		catch (XPathExpressionException | JAXBException e)
//		{
//			LOG.error("Error Occured DefaultAccertifyFacade.CallFraudCheckService " + e.getLocalizedMessage(), e);
//		}
//		logAccertifyResult(transactionResults);
//		return transactionResults;
//	}
//
//	public void createFraudReport(final TransactionResults transactionResult, OrderModel order) {
//		try {
//			accertifyCallService.createFraudReport(transactionResult, order);
//			emptyIfNull(order.getChildren()).
//					forEach(child -> accertifyCallService.createFraudReport(transactionResult, child));
//		}
//		catch (final Exception e)
//		{
//			LOG.error("Error Occured createFraudReport::=" + e.getMessage(), e);
//		}
//	}
//
//	@Override
//	public void saveFraudStatusToCurrentCart(@Nullable final TransactionResults transactionResult) {
//
//		if (transactionResult == null) {
//			LOG.info("TransactionResult was not saved to the cart 'cause it's not exist");
//			return;
//		}
//
//		final CartModel sessionCart = cartService.getSessionCart();
//		accertifyCallService.saveFraudStatusToOrder(transactionResult, sessionCart);
//	}
//
//    private static void logAccertifyResult(@Nullable final TransactionResults transactionResults) {
//        if (transactionResults == null) {
//            return;
//        }
//        final Consumer<String> log = getLogger(transactionResults);
//        log.accept("Accertify Transaction ID: " + transactionResults.getTransactionId());
//        log.accept("Accertify Cross Reference: " + transactionResults.getCrossReference());
//        log.accept("Accertify Recommendation Code: " + transactionResults.getRecommendationCode());
//        log.accept("Accertify Remarks: " + transactionResults.getRemarks());
//        log.accept("Accertify Rules tripped: " + transactionResults.getRulesTripped());
//        log.accept("Accertify Total Score: " + transactionResults.getTotalScore());
//    }
//
//    private static Consumer<String> getLogger(final TransactionResults transactionResults) {
//        return Stream.of(ACCEPT, REVIEW)
//                .noneMatch(transactionResults.getRecommendationCode()::equalsIgnoreCase) ? LOG::info : LOG::debug;
//
//    }
//
//    @Required
//	public void setAccertifyCallService(final AccertifyCallService accertifyCallService)
//	{
//		this.accertifyCallService = accertifyCallService;
//	}
//}
