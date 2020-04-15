package io.dawn.ivrauto.config;

import io.dawn.ivrauto.model.Candidate;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

@Configuration
@EnableBatchProcessing
public class CandidatesBatchConfig {

  @Value("${import.data.candidates.csv.file}")
  String csvResource;

  @Bean
  public Job job(
      JobBuilderFactory jobBuilderFactory,
      StepBuilderFactory stepBuilderFactory,
      ItemReader<Candidate> itemReader,
      ItemProcessor<Candidate, Candidate> itemProcessor,
      ItemWriter<Candidate> itemWriter) {

    Step step =
        stepBuilderFactory
            .get("Candidate-CSV-file-load")
            .<Candidate, Candidate>chunk(100)
            .reader(itemReader)
            .processor(itemProcessor)
            .writer(itemWriter)
            .build();

    return jobBuilderFactory
        .get("CandidateCSVLoad")
        .incrementer(new RunIdIncrementer())
        .start(step)
        .build();
  }

  @Bean
  public FlatFileItemReader<Candidate> itemReader() {
    FlatFileItemReader<Candidate> flatFileItemReader = new FlatFileItemReader<>();
    flatFileItemReader.setResource(new FileSystemResource(csvResource));
    flatFileItemReader.setName("Candidate-CSV-Reader");
    flatFileItemReader.setLinesToSkip(1);
    flatFileItemReader.setLineMapper(lineMapper());
    return flatFileItemReader;
  }

  private LineMapper<Candidate> lineMapper() {
    DefaultLineMapper<Candidate> defaultLineMapper = new DefaultLineMapper<>();
    DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();

    lineTokenizer.setDelimiter(",");
    lineTokenizer.setStrict(false);
    lineTokenizer.setNames(
        "name", "mobile_number", "email", "skills", "years_of_exp", "spoc", "delivery_lead");

    BeanWrapperFieldSetMapper<Candidate> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
    fieldSetMapper.setTargetType(Candidate.class);

    defaultLineMapper.setLineTokenizer(lineTokenizer);
    defaultLineMapper.setFieldSetMapper(fieldSetMapper);

    return defaultLineMapper;
  }
}
