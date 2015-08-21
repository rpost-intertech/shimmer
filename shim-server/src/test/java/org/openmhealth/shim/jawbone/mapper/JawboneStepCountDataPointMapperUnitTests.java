package org.openmhealth.shim.jawbone.mapper;

import com.google.common.collect.Maps;
import org.openmhealth.schema.domain.omh.*;
import org.springframework.core.io.ClassPathResource;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;


/**
 * @author Chris Schaefbauer
 */
public class JawboneStepCountDataPointMapperUnitTests extends JawboneDataPointMapperUnitTests<StepCount> {

    private JawboneStepCountDataPointMapper mapper = new JawboneStepCountDataPointMapper();

    @BeforeTest
    public void initializeResponseNode() throws IOException {

        ClassPathResource resource =
                new ClassPathResource("org/openmhealth/shim/jawbone/mapper/jawbone-moves.json");
        responseNode = objectMapper.readTree(resource.getInputStream());
    }

    @Test
    public void asDataPointsShouldReturnCorrectNumberOfDataPoints() {

        List<DataPoint<StepCount>> dataPoints = mapper.asDataPoints(singletonList(responseNode));
        assertThat(dataPoints.size(), equalTo(2));
    }

    @Test
    public void asDataPointsShouldReturnCorrectDataPointsForSingleTimeZone() {

        List<DataPoint<StepCount>> dataPoints = mapper.asDataPoints(singletonList(responseNode));

        //Test first data point
        StepCount expectedStepCount = new StepCount.Builder(197).setEffectiveTimeFrame(
                TimeInterval.ofStartDateTimeAndEndDateTime(OffsetDateTime.parse("2015-08-10T09:16:00-06:00"),
                        OffsetDateTime.parse("2015-08-10T11:43:00-06:00"))).build();
        assertThat(dataPoints.get(0).getBody(), equalTo(expectedStepCount));

        Map<String, Object> testProperties = Maps.newHashMap();
        testProperties.put(HEADER_SCHEMA_ID_KEY, StepCount.SCHEMA_ID);
        testProperties.put(HEADER_SOURCE_UPDATE_KEY, "2015-08-18T03:11:44Z");
        testProperties.put(HEADER_SENSED_KEY, DataPointModality.SENSED);
        testProperties.put(HEADER_EXTERNAL_ID_KEY,"QkfTizSpRdvMvnHFctzItGNZMT-1F5vw");
        testDataPointHeader(dataPoints.get(0).getHeader(), testProperties);

    }

    @Test
    public void asDataPointsShouldUseCorrectTimeZoneWhenMultipleTimeZonesOnSingleDay() {

        List<DataPoint<StepCount>> dataPoints = mapper.asDataPoints(singletonList(responseNode));

        StepCount expectedStepCount = new StepCount.Builder(593).setEffectiveTimeFrame(
                TimeInterval.ofStartDateTimeAndEndDateTime(OffsetDateTime.parse("2015-08-05T00:00:00-04:00"),OffsetDateTime.parse("2015-08-05T06:42:00-06:00"))).build();
        assertThat(dataPoints.get(1).getBody(),equalTo(expectedStepCount));
    }

}
