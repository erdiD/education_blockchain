package de.deutschebahn.ilv.bussinesobject.delivery;

import de.deutschebahn.ilv.domain.BusinessObject;
import de.deutschebahn.ilv.domain.Delivery;
import de.deutschebahn.ilv.domain.ObjectStateTransitionAction;

import javax.annotation.Resource;
import javax.ejb.*;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Created by AlbertLacambraBasil on 15.08.2017.
 */
@Singleton
public class DeliveryTimerService {

    @Resource
    TimerService timerService;

    @Inject
    Logger logger;

    @Inject
    DeliveryFacade deliveryFacade;

    public enum Type {
        START_DELIVERY,
        END_DELIVERY
    }

    public void setDeliveryTimers(Delivery delivery) {

        /*
        Delivery is immediatly activated. In case some action should be done for the given start date, this timer
        should bereactivated
         */
        /*
        Objects.requireNonNull(delivery);
        Date startDate = delivery.getStartDate();
        setTimer(startDate, delivery, Type.START_DELIVERY);
        */

        Date endDate = delivery.getDeliveryDate();
        setTimer(endDate, delivery, Type.END_DELIVERY);
    }

    public void cancelTimers(Delivery delivery) {
        Objects.requireNonNull(delivery);
        TimerId startTimerId = new TimerId(Type.START_DELIVERY, Delivery.class.getSimpleName(), delivery.getId());
        TimerId endTimerId = new TimerId(Type.END_DELIVERY, Delivery.class.getSimpleName(), delivery.getId());
        timerService.getTimers()
                .stream()
                .filter(timer -> startTimerId.equals(new TimerId((String) timer.getInfo())) ||
                        endTimerId.equals(new TimerId((String) timer.getInfo()))
                )
                .forEach(timer -> {
                    logger.info("[cancelTimers] Cancelling timer=" + timer.getInfo());
                    timer.cancel();
                });
    }

    private void setTimer(Date expirationDate, BusinessObject businessObject, Type type) {
        TimerId timerId = new TimerId(type, businessObject.getClass().getSimpleName(), businessObject.getId());
        TimerConfig startTimerConfig = new TimerConfig();
        startTimerConfig.setInfo(timerId.getTimerIdAsString());
        logger.info("[setTimer] Creating timer " + timerId + ". Expires on " + expirationDate);
        timerService.createSingleActionTimer(expirationDate, startTimerConfig);
    }

    @Timeout
    @Transactional
    public void timeTimeout(Timer timer) {
        String info = (String) timer.getInfo();
        TimerId timerId = new TimerId(info);
        Optional<Delivery> deliveryOptional = deliveryFacade.getById(timerId.getObjectId());

        if (!deliveryOptional.isPresent()) {
            logger.info("[timeTimeout] delivers for timer " + timerId + " not found. TimeoutAction ignored");
            return;
        }

        Delivery delivery = deliveryOptional.get();
        ObjectStateTransitionAction action = null;

        if (timerId.getType() == Type.START_DELIVERY) {
            action = ObjectStateTransitionAction.ACTIVATE_DELIVERY;
        } else if (timerId.getType() == Type.END_DELIVERY) {
            action = ObjectStateTransitionAction.CLOSE_DELIVERY;
        }

        logger.info(String.format("[timeTimeout]  Action to be triggered is %s for object type %s and id %d",
                action, Delivery.class.getSimpleName(), delivery.getId())
        );

        deliveryFacade.fireAction(delivery.getId(), action);
    }

    private static class TimerId {
        private static final String SEPARATOR = "##";
        Type type;
        String className;
        String objectId;
        boolean isValid;

        public TimerId(String timerId) {
            String[] parts = timerId.split(SEPARATOR);
            if (parts.length != 3) {
                isValid = false;
                return;
            }

            type = Type.valueOf(parts[0]);
            className = parts[1];
            objectId = parts[2];
            isValid = true;
        }

        public TimerId(Type type, String className, String objectId) {
            this.type = type;
            this.className = className;
            this.objectId = objectId;
        }

        public String getTimerIdAsString() {
            return type.name() + SEPARATOR + className + SEPARATOR + String.valueOf(objectId);
        }

        public Type getType() {
            return type;
        }

        public String getClassName() {
            return className;
        }

        public String getObjectId() {
            return objectId;
        }

        public boolean isValid() {
            return isValid;
        }

        @Override
        public String toString() {
            return getTimerIdAsString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TimerId timerId = (TimerId) o;

            if (type != timerId.type) return false;
            if (className != null ? !className.equals(timerId.className) : timerId.className != null) return false;
            return objectId != null ? objectId.equals(timerId.objectId) : timerId.objectId == null;
        }

        @Override
        public int hashCode() {
            int result = type != null ? type.hashCode() : 0;
            result = 31 * result + (className != null ? className.hashCode() : 0);
            result = 31 * result + (objectId != null ? objectId.hashCode() : 0);
            return result;
        }
    }
}
