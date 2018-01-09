package de.deutschebahn.ilv.smartcontract.commons;

import de.deutschebahn.ilv.domain.*;

import javax.json.JsonObject;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * Created by AlbertLacambraBasil on 10.10.2017.
 */
public class EntityBuilder<T, B extends CanBuild<T>> {

    private static final String SEP = "\u0000";
    private final B builder;
    DataConverter<T> dataConverter;
    private final Supplier<EntityBuilder<T, B>> recreator;
    DataConverter.SerializeView serializeView = DataConverter.SerializeView.objectInAppToJsonToFabric;

    public EntityBuilder(DataConverter<T> dataConverter, B builder, Supplier<EntityBuilder<T, B>> recreator) {
        this.builder = builder;
        this.dataConverter = dataConverter;
        this.recreator = recreator;
        this.serializeView = serializeView;
    }

    public B getBuilder() {
        return builder;
    }

    public T getEntity() {
        return builder.build();
    }

    public JsonObject asJson() {
        return dataConverter.serialize(getEntity(), serializeView);
    }

    public String asString() {
        return asJson().toString();
    }

    public byte[] asBytes() {
        return asString().getBytes();
    }

    public EntityBuilder<T, B> makeNew() {
        return recreator.get();
    }

    public static EntityBuilder<User, UserBuilder> createUser() {
        UserBuilder builder = UserBuilder.anUser().
                withMarketRole(Arrays.asList(MarketRoleName.OFFER_CREATOR)).
                withDateCreated(new Date()).
                withLastModified(new Date()).
                withId(UUID.randomUUID().toString()).
                withFirsName("fn").
                withLastName("ln").
                withUserName("un").
                withOrganizationId("dbe_id");

        return new EntityBuilder<User, UserBuilder>(new UserDataConverter(), builder, EntityBuilder::createUser);
    }

    public static EntityBuilder<Contract, ContractBuilder> createContract() {
        ContractBuilder builder = ContractBuilder.aContract().
                withDateCreated(new Date()).
                withLastModified(new Date()).
                withId(UUID.randomUUID().toString());

        return new EntityBuilder<>(new ContractDataConverter(), builder, EntityBuilder::createContract);
    }

    public static EntityBuilder<Demand, DemandBuilder> createDemand() {

        String projectId = "P_" + UUID.randomUUID().toString();
        String demandId = projectId + SEP + "D" + SEP;
        String offerId = projectId + SEP + "O" + SEP + "1" + SEP;

        DemandBuilder builder = DemandBuilder.aDemand().
                withId(demandId).
                withProjectId(projectId).
                withDateCreated(new Date()).
                withLastModified(new Date()).
                withBudget(BigDecimal.ZERO).
                withEndDate(new Date()).
                withName("some-name").
                withMessageBoardUrl("dsaasd").
                withOrganizationId("dbe_id").
                withPriority(Priority.HIGH).
                withTargetAccount("1234").
                withCreatorId(UUID.randomUUID().toString()).
                withState(ObjectState.NOT_CREATED);

        return new EntityBuilder<>(new DemandDataConverter(), builder, EntityBuilder::createDemand);
    }

    public static EntityBuilder<Offer, OfferBuilder> createOffer() {

        String projectId = "P_" + UUID.randomUUID().toString();
        String demandId = projectId + SEP + "D" + SEP;
        String offerId = projectId + SEP + "O" + SEP + "1" + SEP;

        OfferBuilder builder = OfferBuilder.anOffer().
                withProjectId(projectId).
                withId(offerId).
                withDateCreated(new Date()).
                withLastModified(new Date()).
                withPrice(BigDecimal.ZERO).
                withState(ObjectState.OFFER_NOT_CREATED).
                withContractType(ContractType.SERVICE_CONTRACT).
                withDeliveryDate(new Date()).
                withStartDate(new Date()).
                withPaymentType(PaymentType.MONTHLY).
                withOrganizationId("dbe_id");

        return new EntityBuilder<>(new OfferDataConverter(), builder, EntityBuilder::createOffer);
    }

    public static String extractProjectId(String id) {
        return id.split(SEP)[0];
    }

}
