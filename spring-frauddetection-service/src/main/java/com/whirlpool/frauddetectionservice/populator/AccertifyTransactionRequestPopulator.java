//package com.whirlpool.frauddetectionservice.populator;
//
//import com.whirlpool.frauddetectionservice.order.request.TransactionRequest;
//
//import java.math.BigDecimal;
//import java.text.*;
//import java.util.Date;
//import java.util.List;
//import java.util.Locale;
//
//
//public class AccertifyTransactionRequestPopulator {
//
//	public void populate(CommerceAccertifyParameter source, TransactionRequest  target) throws ConversionException {
//
//		composeFraudCheckRequest(source,target);
//	}
//
//	/**
//	 * Create request parameters for fraud check service
//	 *
//	 * @param commerceAccertifyParameter
//	 * @param ioBlackBoxVal
//	 * @return TransactionRequestType
//	 */
//	public TransactionRequest composeFraudCheckRequest(final CommerceAccertifyParameter commerceAccertifyParameter, TransactionRequest  request)
//	{
//		final ObjectFactory objectFactory = new ObjectFactory();
//		final String validateStr = validateParameter(commerceAccertifyParameter);
//		if (StringUtils.isNotEmpty(validateStr))
//		{
//			LOGGER.warn(validateStr);
//			return request;
//		}
//
//		final CartData cart = commerceAccertifyParameter.getCart();
//		final CCPaymentInfoData paymentInfo = cart.getPaymentInfo();
//		final AddressData billingAddress = paymentInfo.getBillingAddress();
//		final AddressData deliveryAddress = cart.getDeliveryAddress();
//		final String ipAddress = commerceAccertifyParameter.getIpAddress();
//		final List<OrderEntryData> entries = cart.getEntries();
//
//		final TransactionRequest.OrderInformation orderInformation = objectFactory.createTransactionRequestOrderInformation();
//		// Order code use since cart might contain other code then placed order
//        final String transactionID = StringUtils.isNotBlank(cart.getOrderCode()) ? cart.getOrderCode() : cart.getCode();
//		orderInformation.setTransactionID(transactionID);
//		orderInformation.setOperation("checkout");
//		orderInformation.setStore(commerceAccertifyParameter.getStoreName());
//		final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		orderInformation.setOrderDateTime(df.format(new Date()));
//		//check if french canada order if so reformat the price according to english canada format
//		LanguageModel languageModel = getCommonI18NService().getCurrentLanguage();
//		if(CANADA_BASE_STORE.equalsIgnoreCase(commerceAccertifyParameter.getStoreName()) && FRENCH_CANADA_ISOCODE.equalsIgnoreCase(languageModel.getIsocode()))
//		{
//			//reformat priceValues before setting to the Request
//			orderInformation.setOrderTotalTax(formatPrice(cart.getTotalTax().getValue(),"CAD","en_CA"));
//			orderInformation.setOrderTotalShipping(formatPrice(getDeliveryCost(cart),"CAD","en_CA"));
//			orderInformation.setOrderTotalAmount(formatPrice(cart.getTotalPrice().getValue(),"CAD","en_CA"));
//
//		}
//		else
//		{
//			orderInformation.setOrderTotalTax(cart.getTotalTax().getFormattedValue());
//			orderInformation.setOrderTotalShipping(formatPrice(getDeliveryCost(cart), cart.getTotalPrice().getCurrencyIso()));
//			orderInformation.setOrderTotalAmount(cart.getTotalPrice().getFormattedValue());
//		}
//
//		orderInformation.setOrderCurrencyCode(cart.getTotalPrice().getCurrencyIso());
//
//		orderInformation.setOrderIpAddress(ipAddress);
//		orderInformation.setIovationBlackBox(commerceAccertifyParameter.getIoBlackBoxVal());
//		request.setOrderInformation(orderInformation);
//
//		final TransactionRequest.BillingInformation billingInformation = objectFactory.createTransactionRequestBillingInformation();
//		billingInformation.setBillingFirstName(billingAddress.getFirstName());
//		billingInformation.setBillingLastName(billingAddress.getLastName());
//		billingInformation.setBillingAddress(billingAddress.getLine1());
//		billingInformation.setBillingAddress2(billingAddress.getLine2());
//		billingInformation.setBillingCity(billingAddress.getTown());
//		billingInformation
//				.setBillingRegion(billingAddress.getRegion() != null ? billingAddress.getRegion().getIsocodeShort() : null);
//		billingInformation.setBillingPostalCode(billingAddress.getPostalCode());
//		billingInformation.setBillingCountry(billingAddress.getCountry() != null ? billingAddress.getCountry().getIsocode() : null);
//
//		if (WhrCoreConstants.CustomerEmail.DUMMY_EMAIL.equals(billingAddress.getEmail())) {
//			billingInformation.setBillingEmail(cart.getOrderEmail());
//		} else {
//			billingInformation.setBillingEmail(billingAddress.getEmail());
//		}
//
//		billingInformation.setBillingPhone(billingAddress.getPhone());
//		request.setBillingInformation(billingInformation);
//
//		final TransactionRequest.MemberInformation memberInformation = objectFactory.createTransactionRequestMemberInformation();
//		memberInformation.setMemberID(cart.getUser() != null ? cart.getUser().getUid() : null);
//		memberInformation.setMembershipDate(cart.getSaveTime() != null ? df.format(cart.getSaveTime()) : null);
//		request.setMemberInformation(memberInformation);
//
//		final TransactionRequest.ShippingInformation shippingInformationType = objectFactory
//				.createTransactionRequestShippingInformation();
//		shippingInformationType.setShippingFirstName(deliveryAddress.getFirstName());
//		//Update the last name if available
//		if(StringUtils.isNotBlank(deliveryAddress.getLastName()))
//		{
//			shippingInformationType.setShippingLastName(deliveryAddress.getLastName());
//		}
//		shippingInformationType.setShippingAddress(deliveryAddress.getLine1());
//		shippingInformationType.setShippingAddress2(deliveryAddress.getLine2());
//		shippingInformationType.setShippingCity(deliveryAddress.getTown());
//		shippingInformationType
//				.setShippingRegion(deliveryAddress.getRegion() != null ? deliveryAddress.getRegion().getIsocodeShort() : null);
//		shippingInformationType.setShippingPostalCode(deliveryAddress.getPostalCode());
//		shippingInformationType
//				.setShippingCountry(deliveryAddress.getCountry() != null ? deliveryAddress.getCountry().getIsocode() : null);
//		shippingInformationType.setShippingEmail(deliveryAddress.getEmail());
//		shippingInformationType.setShippingPhone(deliveryAddress.getPhone());
//		request.setShippingInformation(shippingInformationType);
//
//		final TransactionRequest.PaymentDetailInformation paymentDetailInformation = objectFactory
//				.createTransactionRequestPaymentDetailInformation();
//		final TransactionRequest.PaymentDetailInformation.PaymentSource paymentSource = objectFactory
//				.createTransactionRequestPaymentDetailInformationPaymentSource();
//		paymentDetailInformation.setPaymentSource(paymentSource);
//
//		paymentSource.setPaymentCardNumber(paymentInfo.getCardNumber());
//		paymentSource.setPaymentCreditCardFullName(paymentInfo.getAccountHolderName());
//		paymentSource.setPaymentCardType(paymentInfo.getCardType());
//		paymentSource.setPaymentCardExpireDate(paymentInfo.getExpiryYear() + "-" + paymentInfo.getExpiryMonth());
//		paymentSource.setPaymentCardBilledAmount(cart.getTotalPrice().getFormattedValue());
//		request.setPaymentDetailInformation(paymentDetailInformation);
//
//		final TransactionRequest.OrderDetailInformation orderDetailInformation = objectFactory
//				.createTransactionRequestOrderDetailInformation();
//		final List<TransactionRequest.OrderDetailInformation.OrderItem> orderItems = orderDetailInformation.getOrderItem();
//
//		for (final OrderEntryData entry : entries)
//		{
//			final TransactionRequest.OrderDetailInformation.OrderItem orderItem1 = objectFactory
//					.createTransactionRequestOrderDetailInformationOrderItem();
//			orderItem1.setItemNumber(entry.getProduct().getCode());
//			orderItem1.setItemDescription(entry.getProduct().getDescription());
//			orderItem1.setItemQuantity(entry.getQuantity().toString());
//			orderItem1.setItemPrice(entry.getTotalPrice().getFormattedValue());
//			orderItem1.setShippingMethod(entry.getDeliveryMode() != null ? entry.getDeliveryMode().getCode() : null);//TODO
//			orderItems.add(orderItem1);
//		}
//
//		request.setOrderDetailInformation(orderDetailInformation);
//		return request;
//	}
//
//    private BigDecimal getDeliveryCost(final CartData cart) {
//        final PriceData shippingCost = cart.getDeliveryCost();
//        final PriceData homeDeliveryCost = cart.getDeliveryHomeCost();
//        final BigDecimal deliveryCost = getPriceValue(shippingCost)
//                .add(getPriceValue(homeDeliveryCost)).setScale(2, BigDecimal.ROUND_HALF_UP);
//        return deliveryCost;
//    }
//
//    @Nonnull
//    private BigDecimal getPriceValue(final PriceData priceData) {
//	    return (priceData != null && priceData.getValue() != null) ? priceData.getValue() : BigDecimal.ZERO;
//    }
//
//	private String validateParameter(final CommerceAccertifyParameter commerceAccertifyParameter)
//	{
//
//		if (commerceAccertifyParameter == null)
//		{
//			return "Parameter commerceAccertifyParameter can not be null";
//		}
//
//		final CartData cart = commerceAccertifyParameter.getCart();
//		if (cart == null)
//		{
//			return "Parameter cart can not be null";
//		}
//
//		final CCPaymentInfoData paymentInfo = cart.getPaymentInfo();
//		if (paymentInfo == null)
//		{
//			return "Parameter paymentInfo can not be null";
//		}
//
//		final AddressData billingAddress = paymentInfo.getBillingAddress();
//		if (null == billingAddress || StringUtils.isEmpty(billingAddress.getFirstName())
//				|| StringUtils.isEmpty(billingAddress.getLine1())
//				|| StringUtils.isEmpty(billingAddress.getTown()) || null == billingAddress.getRegion()
//				|| StringUtils.isEmpty(billingAddress.getPostalCode()) || null == billingAddress.getCountry())
//		{
//			return "Parameter billingAddress can not be null or it's properties can not be null";
//		}
//
//		final AddressData deliveryAddress = cart.getDeliveryAddress();
//		if (null == deliveryAddress || StringUtils.isEmpty(deliveryAddress.getFirstName()) || StringUtils.isEmpty(deliveryAddress.getLine1())
//				|| StringUtils.isEmpty(deliveryAddress.getTown()) || null == deliveryAddress.getRegion()
//				|| StringUtils.isEmpty(deliveryAddress.getPostalCode()) || null == deliveryAddress.getCountry())
//		{
//			return "Parameter deliveryAddress can not be null or it's properties can not be null";
//		}
//
//		if (!WhrCoreConstants.PAYPAL_PAYMENT_TYPE.equals(paymentInfo.getPaymentType())
//				&& (StringUtils.isEmpty(paymentInfo.getCardNumber())
//					|| StringUtils.isEmpty(paymentInfo.getAccountHolderName())
//					|| null == paymentInfo.getCardType())) {
//			return "Parameter creditCardPaymentInfoModel can not be null or it's properties can not be null";
//		}
//
//		final List<OrderEntryData> entries = cart.getEntries();
//		for (final OrderEntryData entry : entries)
//		{
//			if (null == entry.getProduct() || StringUtils.isEmpty(entry.getProduct().getCode()) || null == entry.getQuantity()
//					|| null == entry.getTotalPrice())
//			{
//				return "Parameter cart's entry can not be null or it's properties can not be null";
//			}
//		}
//
//		return "";
//	}
//
//	/*
//	 * Formats the priceValue according to current language Model and given currency
//	 */
//	public String formatPrice(final BigDecimal value, final String currencyIso)
//	{
//		final String currentLanguageIsoCode = getCommonI18NService().getCurrentLanguage().getIsocode();
//		return formatPrice(value, currencyIso, currentLanguageIsoCode);
//	}
//
//	/*
//	 * Formats the priceValue according to given language Model and currency
//	 */
//	public String formatPrice(BigDecimal value, String currencyIso, String isoCode)
//	{
//		//obtain locale for given isoCode
//		final Locale locale = new Locale(isoCode);
//
//		//obtain Currency
//		final CurrencyModel currenyModel = getCommonI18NService().getCurrency(currencyIso);
//
//
//		final NumberFormat currencyFormat = createCurrencyFormat(locale, currenyModel);
//		return currencyFormat.format(value);
//	}
//
//
//	protected NumberFormat createNumberFormat(final Locale locale, final CurrencyModel currency)
//	{
//		final DecimalFormat currencyFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(locale);
//		adjustDigits(currencyFormat, currency);
//		adjustSymbol(currencyFormat, currency);
//		return currencyFormat;
//	}
//
//	/**
//	 * @param locale
//	 * @param currency
//	 * @return A clone of {@link NumberFormat} from the instance in the local cache, if the cache does not contain an
//	 *         instance of a NumberFormat for a given locale and currency one would be added.
//	 */
//	protected NumberFormat createCurrencyFormat(final Locale locale, final CurrencyModel currency)
//	{
//		final String key = locale.getISO3Country() + "_" + currency.getIsocode();
//     	final NumberFormat currencyFormat = createNumberFormat(locale, currency);
//
//		// don't allow multiple references
//		return currencyFormat;
//	}
//
//
//	protected DecimalFormat adjustDigits(final DecimalFormat format, final CurrencyModel currencyModel)
//	{
//		final int tempDigits = currencyModel.getDigits() == null ? 0 : currencyModel.getDigits().intValue();
//		final int digits = Math.max(0, tempDigits);
//
//		format.setMaximumFractionDigits(digits);
//		format.setMinimumFractionDigits(digits);
//		if (digits == 0)
//		{
//			format.setDecimalSeparatorAlwaysShown(false);
//		}
//
//		return format;
//	}
//
//	protected DecimalFormat adjustSymbol(final DecimalFormat format, final CurrencyModel currencyModel)
//	{
//		final String symbol = currencyModel.getSymbol();
//		if (symbol != null)
//		{
//			final DecimalFormatSymbols symbols = format.getDecimalFormatSymbols(); // does cloning
//			final String iso = currencyModel.getIsocode();
//			boolean changed = false;
//			if (!iso.equalsIgnoreCase(symbols.getInternationalCurrencySymbol()))
//			{
//				symbols.setInternationalCurrencySymbol(iso);
//				changed = true;
//			}
//			if (!symbol.equals(symbols.getCurrencySymbol()))
//			{
//				symbols.setCurrencySymbol(symbol);
//				changed = true;
//			}
//			if (changed)
//			{
//				format.setDecimalFormatSymbols(symbols);
//			}
//		}
//		return format;
//	}
//}
