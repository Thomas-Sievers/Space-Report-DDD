package br.com.spacereport.domain.service;

import br.com.spacereport.domain.model.EventType;
import br.com.spacereport.domain.model.SpaceEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SpaceEventBSTTest {

    private SpaceEventBST bst;

    @BeforeEach
    void setUp() {
        bst = new SpaceEventBST();
    }

    private SpaceEvent event(long id, double intensity) {
        return new SpaceEvent(id, EventType.CME, null, intensity, LocalDate.now(), "test");
    }

    @Test
    void shouldReturnEmptyWhenNoEventsInserted() {
        assertThat(bst.inOrder()).isEmpty();
        assertThat(bst.findAboveThreshold(100)).isEmpty();
    }

    @Test
    void shouldReturnEventsInAscendingOrderByIntensity() {
        bst.insert(event(1L, 500.0));
        bst.insert(event(2L, 200.0));
        bst.insert(event(3L, 800.0));
        bst.insert(event(4L, 100.0));

        List<SpaceEvent> inOrder = bst.inOrder();

        assertThat(inOrder).hasSize(4);
        assertThat(inOrder.get(0).getIntensity()).isEqualTo(100.0);
        assertThat(inOrder.get(1).getIntensity()).isEqualTo(200.0);
        assertThat(inOrder.get(2).getIntensity()).isEqualTo(500.0);
        assertThat(inOrder.get(3).getIntensity()).isEqualTo(800.0);
    }

    @Test
    void shouldReturnOnlyEventsAboveThreshold() {
        bst.insert(event(1L, 100.0));
        bst.insert(event(2L, 500.0));
        bst.insert(event(3L, 900.0));
        bst.insert(event(4L, 300.0));

        List<SpaceEvent> above500 = bst.findAboveThreshold(500.0);

        assertThat(above500).hasSize(1);
        assertThat(above500.get(0).getIntensity()).isEqualTo(900.0);
    }

    @Test
    void shouldReturnAllEventsWhenThresholdIsZero() {
        bst.insert(event(1L, 100.0));
        bst.insert(event(2L, 200.0));
        bst.insert(event(3L, 50.0));

        assertThat(bst.findAboveThreshold(0.0)).hasSize(3);
    }

    @Test
    void shouldReturnNothingWhenAllEventsBelowThreshold() {
        bst.insert(event(1L, 100.0));
        bst.insert(event(2L, 200.0));

        assertThat(bst.findAboveThreshold(1000.0)).isEmpty();
    }
}
