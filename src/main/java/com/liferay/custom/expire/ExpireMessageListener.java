package com.liferay.custom.expire;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.messaging.BaseMessageListener;
import com.liferay.portal.kernel.messaging.DestinationNames;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.scheduler.*;
import com.liferay.portal.kernel.util.GetterUtil;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;

import java.util.Date;
import java.util.Map;

/**
 * @author jverweij
 */
@Component(
        immediate = true,
        property = {
                "cron.expression= 0 0/05 1/1 1/1 * ?"   // scheduler runs every 5 min.
        },
        service = ExpireMessageListener.class
)
public class ExpireMessageListener extends BaseMessageListener {

        @Override
        protected void doReceive(Message message) throws Exception {
                _log.info("Scheduled task executed... put your logic here or separate class");

                ExpiredUsers expiredUsers = new ExpiredUsers();
                expiredUsers.update();
        }

        @Activate
        @Modified
        protected void activate(Map<String, Object> properties) throws SchedulerException {

                try {
                        String cronExpression = GetterUtil.getString(properties.get("cron.expression"), "cronExpression");
                        _log.info("activating with cronExpression: " + cronExpression);

                        String listenerClass = getClass().getName();
                        Trigger jobTrigger = TriggerFactoryUtil.createTrigger(listenerClass, listenerClass, new Date(), null, cronExpression);

                        SchedulerEntryImpl schedulerEntryImpl = new SchedulerEntryImpl(listenerClass,jobTrigger,"expire users... and more??");

                        SchedulerEngineHelperUtil.register(this, schedulerEntryImpl, DestinationNames.SCHEDULER_DISPATCH);

                } catch (Exception e) {
                        _log.error(e);
                }
        }

        @Deactivate
        protected void deactivate() {
                SchedulerEngineHelperUtil.unregister(this);
        }

        private static final Log _log = LogFactoryUtil.getLog(ExpireMessageListener.class);
}
