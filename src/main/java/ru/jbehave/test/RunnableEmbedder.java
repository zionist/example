package ru.jbehave.test;

import org.jbehave.core.InjectableEmbedder;
import org.jbehave.core.annotations.Configure;
import org.jbehave.core.annotations.UsingEmbedder;
import org.jbehave.core.annotations.UsingSteps;
import org.jbehave.core.configuration.AnnotationBuilder;
import org.jbehave.core.io.StoryFinder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


@Configure(storyReporterBuilder = MyReportBuilder.class)
@UsingEmbedder(ignoreFailureInStories = true, ignoreFailureInView = true, verboseFailures = true)
@UsingSteps(instances = { SshSteps.class })
public class RunnableEmbedder extends InjectableEmbedder implements Runnable {
    private static final AnnotationBuilder builder = new AnnotationBuilder(RunnableEmbedder.class);
    private static final ApplicationContext springContext = new ClassPathXmlApplicationContext("beans.xml");
    private String testStoriesPath = springContext.getBean("testStoriesPath", String.class);

    public void run() {
        injectedEmbedder().runStoriesAsPaths(getStories());
    }

    public static void main(String[] args) {
        RunnableEmbedder runnableEmbedder = new RunnableEmbedder();
        runnableEmbedder.useEmbedder(builder.buildEmbedder());
            runnableEmbedder.run() ;
    }

    // list of stories files from ClassPathResource
    public List<String> getStories() {
        File storiesDir = null;
        try {
            storiesDir = new ClassPathResource(testStoriesPath).getFile();
        } catch (IOException e) {
            System.err.println("Wrong path to stories");
            e.printStackTrace();
        }
        if (!storiesDir.isDirectory()) {
            System.err.println("Stories path is not a directory");
            System.exit(1);
        }
        if (storiesDir.listFiles().length == 0 ) {
            System.err.println("Stories path has no any story file");
            System.exit(1);
        }
        List<String> paths = new LinkedList<String>();
        for (File file : storiesDir.listFiles()) {
            Path p =  Paths.get(testStoriesPath, file.getName());
            paths.add(p.toString());
        }
        return paths;
    }
}
