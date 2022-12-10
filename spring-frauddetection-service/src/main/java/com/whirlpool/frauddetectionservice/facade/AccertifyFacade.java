///**
// *
// */
//package com.whirlpool.frauddetectionservice.facade;
//
//import com.hybris.integration.accertify.core.data.CommerceAccertifyParameter;
//import com.hybris.integration.accertify.order.response.TransactionResults;
//import com.whirlpool.digitalplatform.core.model.WHRAssertifyConfigModel;
//import de.hybris.platform.core.model.order.OrderModel;
//
//import javax.annotation.Nullable;
//
///**
// * @author ohmprakash.balaiah
// *
// */
//public interface AccertifyFacade
//{
//
//	TransactionResults CallFraudCheckService(
//			CommerceAccertifyParameter commerceAccertifyParameter,
//			String ioBlackBoxVal,
//			WHRAssertifyConfigModel whrAssertifyConfigModel);
//
//	void createFraudReport(TransactionResults transactionResult, OrderModel order);
//
//	/**
//	 * Saves transactionResult if any exist
//	 */
//	void saveFraudStatusToCurrentCart(@Nullable TransactionResults transactionResult);
//}
