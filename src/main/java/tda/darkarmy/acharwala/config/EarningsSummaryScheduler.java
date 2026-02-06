package tda.darkarmy.acharwala.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tda.darkarmy.acharwala.model.DidiProfile;
import tda.darkarmy.acharwala.repository.DidiProfileRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class EarningsSummaryScheduler {

    private final DidiProfileRepository didiProfileRepository;

    /**
     * Scheduled task to generate daily earnings summary for Didis
     * Runs at 11:59 PM every day
     */
    @Scheduled(cron = "0 59 23 * * *")
    public void generateDailyEarningsSummary() {
        try {
            List<DidiProfile> approvedDidis = didiProfileRepository
                    .findByApprovalStatus(DidiProfile.ApprovalStatus.APPROVED);

            log.info("Generating daily earnings summary for {} Didis", approvedDidis.size());

            for (DidiProfile didi : approvedDidis) {
                // TODO: Implement earnings calculation based on orders delivered today
                // Calculate earnings from orders with status DELIVERED for today
                // Update didi.totalEarnings
                // Send earnings notification via SMS/WhatsApp

                log.debug("Earnings summary generated for Didi ID: {}", didi.getId());
            }
        } catch (Exception e) {
            log.error("Error generating earnings summary", e);
        }
    }

    /**
     * Scheduled task to send weekly earnings report
     * Runs at 9:00 AM every Monday
     */
    @Scheduled(cron = "0 0 9 ? * MON")
    public void generateWeeklyEarningsReport() {
        try {
            List<DidiProfile> approvedDidis = didiProfileRepository
                    .findByApprovalStatus(DidiProfile.ApprovalStatus.APPROVED);

            log.info("Generating weekly earnings report for {} Didis", approvedDidis.size());

            for (DidiProfile didi : approvedDidis) {
                // TODO: Calculate earnings for the past week
                // Send comprehensive weekly report via email/WhatsApp
                log.debug("Weekly earnings report generated for Didi ID: {}", didi.getId());
            }
        } catch (Exception e) {
            log.error("Error generating weekly earnings report", e);
        }
    }
}
