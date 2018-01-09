package de.deutschebahn.ilv.smartcontract.business;

import de.deutschebahn.ilv.domain.*;
import de.deutschebahn.ilv.smartcontract.commons.DataConverter;
import de.deutschebahn.ilv.smartcontract.commons.DemandDataConverter;
import de.deutschebahn.ilv.smartcontract.commons.OfferDataConverter;
import de.deutschebahn.ilv.smartcontract.commons.UserDataConverter;
import org.hyperledger.fabric.shim.ledger.CompositeKey;

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
                withOrganizationId(UUID.randomUUID().toString());

        return new EntityBuilder<User, UserBuilder>(new UserDataConverter(), builder, EntityBuilder::createUser);
    }

    public static EntityBuilder<Demand, DemandBuilder> createDemand() {
        DemandBuilder builder = DemandBuilder.aDemand().
                withId(IdUtils.generateDemandKey().toString()).
                withDateCreated(new Date()).
                withLastModified(new Date()).
                withBudget(BigDecimal.ZERO).
                withEndDate(new Date()).
                withName("some-name").
                withMessageBoardUrl("dsaasd").
                withOrganizationId(UUID.randomUUID().toString()).
                withPriority(Priority.HIGH).
                withTargetAccount("1234").
                withCreatorId(UUID.randomUUID().toString()).
                withState(ObjectState.NOT_CREATED);

        return new EntityBuilder<>(new DemandDataConverter(), builder, EntityBuilder::createDemand);
    }

    public static EntityBuilder<Offer, OfferBuilder> createOffer() {

        CompositeKey demandKey = IdUtils.generateDemandKey();
        String projectId = IdUtils.getProjectId(demandKey);
        CompositeKey offerKey = IdUtils.generateOfferKey(demandKey, 1);

        OfferBuilder builder = OfferBuilder.anOffer().
                withProjectId(projectId).
                withId(offerKey.toString()).
                withDateCreated(new Date()).
                withLastModified(new Date()).
                withPrice(BigDecimal.ZERO).
                withState(ObjectState.OFFER_NOT_CREATED).
                withContractType(ContractType.SERVICE_CONTRACT).
                withDeliveryDate(new Date()).
                withStartDate(new Date()).
                withPaymentType(PaymentType.MONTHLY).
                withOrganizationId(UUID.randomUUID().toString());

        return new EntityBuilder<>(new OfferDataConverter(), builder, EntityBuilder::createOffer);
    }

}
