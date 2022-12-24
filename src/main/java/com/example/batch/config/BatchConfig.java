package com.example.batch.config;

import com.example.batch.writer.mapper.ProductFieldSetMapper;
import com.example.batch.writer.ProductJdbcItemWriter;
import com.example.batch.listener.JobListener;
import com.example.model.Product;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import javax.sql.DataSource;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.zip.ZipInputStream;

@Configuration
@EnableBatchProcessing(modular = true)
public class BatchConfig {
    private static final String JOB_NAME = "importProducts";
    private static final String DECOMPRESS_NAME = "decompress";
    private static final String JOB_MASTER_STEP = "masterStep";

    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private DataSource dataSource;

    @Bean
    public Job job() {
        return jobBuilderFactory.get(JOB_NAME)
                .listener(jobListener())
                .start(decompress())
                .next(masterStep())
                .build();
    }

    @Bean
    public JobExecutionListener jobListener() {
        return new JobListener(dataSource);
    }

    @Bean
    public Step decompress() {
        return stepBuilderFactory.get(DECOMPRESS_NAME)
                .tasklet(decompressTasklet(null))
                .build();
    }

    @Bean
    public Step masterStep() {
        return stepBuilderFactory.get(JOB_MASTER_STEP)
                .chunk(10)
                .reader(reader(null))
                .writer(writer())
                .faultTolerant()
                .skipPolicy(skipPolicy())
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<Product> reader(@Value("#{jobParameters}") Map<String, Object> jobParameters) {
        String targetDirectory = (String) jobParameters.get("targetDirectory");
        String targetFile = (String) jobParameters.get("targetFile");
        ResourceLoader resourceLoader = new DefaultResourceLoader();

        Resource resource = resourceLoader.getResource("file:./" + targetDirectory + targetFile);

        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setNames("ID", "NAME", "DESCRIPTION", "PRICE");

        DefaultLineMapper<Product> lineMapper = new DefaultLineMapper<Product>();
        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(new ProductFieldSetMapper());

        FlatFileItemReader<Product> reader = new FlatFileItemReader<Product>();
        reader.setLinesToSkip(1);
        reader.setResource(resource);
        reader.setLineMapper(lineMapper);
        return reader;
    }

    @Bean
    public ItemWriter writer() {
        return new ProductJdbcItemWriter(dataSource);
    }

    @Bean
    public SkipPolicy skipPolicy() {
        return (t, sc) -> {
            if (t instanceof FlatFileParseException) {
                return true;
            }
            return false;
        };
    }

    @Bean
    @StepScope
    public Tasklet decompressTasklet(@Value("#{jobParameters}") Map<String, Object> jobParameters) {
        Resource inputResource = new ClassPathResource((String) jobParameters.get("inputResource"));
        String targetDirectory = (String) jobParameters.get("targetDirectory");
        String targetFile = (String) jobParameters.get("targetFile");
        return (c, cc) -> {
            ZipInputStream zis = new ZipInputStream(new BufferedInputStream(inputResource.getInputStream()));
            File targetDirectoryAsFile = new File(targetDirectory);
            if (!targetDirectoryAsFile.exists()) {
                FileUtils.forceMkdir(targetDirectoryAsFile);
            }
            File target = new File(targetDirectory, targetFile);
            BufferedOutputStream dest = null;
            while (zis.getNextEntry() != null) {
                if (!target.exists()) {
                    target.createNewFile();
                }
                FileOutputStream fos = new FileOutputStream(target);
                dest = new BufferedOutputStream(fos);
                IOUtils.copy(zis, dest);
                dest.flush();
                dest.close();
            }
            zis.close();
            if (!target.exists()) {
                throw new IllegalStateException("Could not decompress anything from the archive!");
            }
            return RepeatStatus.FINISHED;
        };
    }
}
