package ru.jbehave.test;

import org.jbehave.core.reporters.StoryReporterBuilder;

import static org.jbehave.core.reporters.Format.*;

public class MyReportBuilder extends StoryReporterBuilder {
    public MyReportBuilder() {
        this.withFormats(CONSOLE).withDefaultFormats();
    }
}
