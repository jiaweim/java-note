package mjw.java.concurrency.executor;

import java.util.concurrent.CompletionService;

/**
 * @author Jiawei Mao
 * @version 0.1.0
 * @since 18 Mar 2024, 4:24 PM
 */
public class ReportRequest implements Runnable {

    private final String name;

    private final CompletionService<String> service;

    public ReportRequest(String name, CompletionService<String> service) {
        this.name = name;
        this.service = service;
    }

    @Override
    public void run() {
        ReportGenerator reportGenerator = new ReportGenerator(name,
                "Report");
        service.submit(reportGenerator);
    }
}
