package club.ryans.utility;


import club.ryans.views.Utility;
import com.sun.management.UnixOperatingSystemMXBean;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.lang.management.*;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class MetricsLogger {
    private CpuInfo previousCpuInfo;

    @Scheduled(fixedDelay = 10, timeUnit = TimeUnit.MINUTES)
    public void log() {
        LOGGER.info("Logging metics: {}", constructMemoryMetrics() + constructCpuString());
    }

    private static String constructMemoryMetrics() {
        MemoryMXBean memory = ManagementFactory.getMemoryMXBean();
        return constructMemorayUsageString("heap", memory.getHeapMemoryUsage())
                + constructMemorayUsageString("non-heap", memory.getNonHeapMemoryUsage());
    }

    private static String constructMemorayUsageString(final String name, final MemoryUsage usage) {
        return String.format("[%s %s/%s/%s/%s]", name, Utility.scoplify(usage.getInit()),
            Utility.scoplify(usage.getUsed()), Utility.scoplify(usage.getCommitted()),
            Utility.scoplify(usage.getMax()));
    }

    private String constructCpuString() {
        ThreadMXBean thread = ManagementFactory.getThreadMXBean();
        final String threadInfo = String.format("[threads %d]", thread.getThreadCount());
        final String cpuInfo = constructCpuInfoString();
        return threadInfo + cpuInfo;
    }

    private String constructCpuInfoString() {
        CpuInfo cpuInfo = new CpuInfo();
        String infoString = String.format("[cpu: up %d, cpu %d]", cpuInfo.getUpTime(), cpuInfo.getCpuTime());

        if (previousCpuInfo == null) {
            previousCpuInfo = cpuInfo;
            return infoString;
        }

        long dCpu = cpuInfo.getCpuTime() - previousCpuInfo.getCpuTime();
        long dUpTime = cpuInfo.getUpTime() - previousCpuInfo.getUpTime();
        double usage = dCpu / (100f * dUpTime);
        infoString+= String.format("[usage %.2f%%/%d]", usage, cpuInfo.getProcessors());
        previousCpuInfo = cpuInfo;
        return infoString;
    }

    @Data
    private static class CpuInfo {
        private int processors;
        long upTime;
        long cpuTime;

        public CpuInfo() {
            OperatingSystemMXBean operatingSystem = ManagementFactory.getOperatingSystemMXBean();
            RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
            this.processors = operatingSystem.getAvailableProcessors();
            this.upTime = runtime.getUptime();
            this.cpuTime = ((com.sun.management.OperatingSystemMXBean)operatingSystem).getProcessCpuTime();
        }
    }
}
