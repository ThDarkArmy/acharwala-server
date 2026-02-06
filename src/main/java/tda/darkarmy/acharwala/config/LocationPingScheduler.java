package tda.darkarmy.acharwala.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tda.darkarmy.acharwala.model.DidiProfile;
import tda.darkarmy.acharwala.repository.DidiProfileRepository;
import tda.darkarmy.acharwala.enums.Role;
import tda.darkarmy.acharwala.repository.UserRepository;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class LocationPingScheduler {

    private final DidiProfileRepository didiProfileRepository;
    private final UserRepository userRepository;

    /**
     * Scheduled task to remind Didis to send location pings
     * Runs at 8:00 AM, 12:00 PM, and 6:00 PM every day
     */
    @Scheduled(cron = "0 0 8 * * *") // 8:00 AM
    public void locationPingReminder8AM() {
        sendLocationPingReminder("Morning");
    }

    @Scheduled(cron = "0 0 12 * * *") // 12:00 PM
    public void locationPingReminder12PM() {
        sendLocationPingReminder("Afternoon");
    }

    @Scheduled(cron = "0 0 18 * * *") // 6:00 PM
    public void locationPingReminder6PM() {
        sendLocationPingReminder("Evening");
    }

    private void sendLocationPingReminder(String timeOfDay) {
        try {
            List<DidiProfile> approvedDidis = didiProfileRepository
                    .findByApprovalStatus(DidiProfile.ApprovalStatus.APPROVED);

            log.info("Sending {} location ping reminders to {} Didis", timeOfDay, approvedDidis.size());

            for (DidiProfile didi : approvedDidis) {
                // TODO: Implement notification service to send SMS/WhatsApp reminder
                // notificationService.sendLocationPingReminder(didi.getUser());
                log.debug("Location ping reminder sent to Didi ID: {}", didi.getId());
            }
        } catch (Exception e) {
            log.error("Error sending location ping reminders", e);
        }
    }
}
